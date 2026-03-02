package mx.gob.pjpuebla.trials.config.sendPulse;

import mx.gob.pjpuebla.trials.util.enums.EstadoEnvioCorreo;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;

@Component
public class EmailStatusPollingJob {

    private static final int MAX_INTENTOS = 10;

    private final EmailLogRepository emailLogRepository;
    private final SendPulseClient sendPulseClient;

    public EmailStatusPollingJob(EmailLogRepository emailLogRepository, SendPulseClient sendPulseClient) {
        this.emailLogRepository = emailLogRepository;
        this.sendPulseClient = sendPulseClient;
    }

    /**
     * Corre cada minuto, pero cada email decide su siguiente verificación con T_PROXIMA_VERIFICACION.
     */
    @Scheduled(fixedDelayString = "PT1M")
    @Transactional
    public void poll() {

        LocalDateTime now = LocalDateTime.now();

        List<EmailLog> batch = emailLogRepository.findBatchToVerify(
                List.of(
                        EstadoEnvioCorreo.ENVIADO,          // ya mandado, esperando resultado
                        EstadoEnvioCorreo.RECIBIDO,        // si quieres seguir hasta leído
                        EstadoEnvioCorreo.PENDIENTE_ENVIO   // opcional (si lo usas como cola)
                ),
                now
        );

        for (EmailLog log : batch) {

            // Si no hay messageId, no podemos consultar
            if (log.getProviderMessageId() == null || log.getProviderMessageId().isBlank()) {
                continue;
            }

            int intentos = log.getIntentosVerificacion() == null ? 0 : log.getIntentosVerificacion();

            if (intentos >= MAX_INTENTOS) {
                marcarFinalPorMaxIntentos(log, now);
                continue;
            }

            try {
                SendPulseEmailInfoResponse info = sendPulseClient.getEmailInfo(log.getProviderMessageId());

                // Guardamos “crudo” lo que nos regresó SendPulse
                log.setSmtpAnswerCode(info.smtpAnswerCode());
                log.setSmtpAnswerSubcode(info.smtpAnswerSubcode());
                log.setSmtpAnswerData(info.smtpAnswerData());
                log.setUltimaVerificacion(now);

                // 1) Si falla entrega (smtp 4xx/5xx) -> NO_ENTREGADO
                if (info.smtpAnswerCode() != null && info.smtpAnswerCode() >= 400) {
                    if (log.getEstado() != EstadoEnvioCorreo.NO_ENTREGADO) {
                        log.setEstado(EstadoEnvioCorreo.NO_ENTREGADO);
                    }
                    log.setProximaVerificacion(null); // estado final
                    continue;
                }

                // 2) Si ya hay smtp 2xx -> ENTREGADO
                if (info.smtpAnswerCode() != null && info.smtpAnswerCode() >= 200 && info.smtpAnswerCode() < 300) {

                    if (log.getEstado() != EstadoEnvioCorreo.RECIBIDO) {
                        log.setEstado(EstadoEnvioCorreo.RECIBIDO);
                    }

                    // Fecha entrega (solo la 1era vez)
                    if (log.getFechaEntrega() == null) {
                        // Preferimos send_date si viene; si no, now()
                        LocalDateTime delivered = parseSendPulseDateOrNull(info.sendDate());
                        log.setFechaEntrega(delivered != null ? delivered : now);
                    }
                }

                // 3) Si tracking.open > 0 => LEIDO
                LocalDateTime openedAt = extractFirstOpenDate(info);
                if (openedAt != null) {

                    if (log.getEstado() != EstadoEnvioCorreo.LEIDO) {
                        log.setEstado(EstadoEnvioCorreo.LEIDO);
                    }

                    if (log.getFechaLectura() == null) {
                        log.setFechaLectura(openedAt);
                    }

                    log.setProximaVerificacion(null); // final
                    continue;
                }

                // Si aún no es final, reprogramamos
                intentos++;
                log.setIntentosVerificacion(intentos);
                log.setProximaVerificacion(now.plusSeconds(nextDelaySeconds(intentos)));

            } catch (Exception ex) {
                // Falló la consulta: reintenta con backoff
                intentos++;
                log.setIntentosVerificacion(intentos);
                log.setUltimaVerificacion(now);
                log.setProximaVerificacion(now.plusSeconds(nextDelaySeconds(intentos)));
            }
        }

        /**
         * IMPORTANTÍSIMO:
         * No ves emailLogRepository.save(...) aquí a propósito.
         * Con @Transactional, los EmailLog que te regresó el repository están "managed" por el EntityManager.
         * Al final del método, Hibernate hace flush/commit y guarda los cambios automáticamente.
         */
    }

    private void marcarFinalPorMaxIntentos(EmailLog log, LocalDateTime now) {
        log.setUltimaVerificacion(now);
        log.setProximaVerificacion(null);
        if (log.getEstado() != EstadoEnvioCorreo.NO_ENTREGADO) {
            log.setEstado(EstadoEnvioCorreo.NO_ENTREGADO);
        }
    }

    private long nextDelaySeconds(int intentos) {
        return switch (intentos) {
            case 1 -> 60;     // 1 min
            case 2 -> 120;    // 2 min
            case 3 -> 300;    // 5 min
            case 4 -> 600;    // 10 min
            case 5 -> 1800;   // 30 min
            default -> 3600;  // 60 min
        };
    }

    /**
     * send_date en doc viene como string.
     * En tu cuenta puede venir como timestamp o como formato tipo "2026-03-02 11:45:00".
     * Ajusta el patrón si tu respuesta viene distinta.
     */
    private LocalDateTime parseSendPulseDateOrNull(String sendDate) {
        if (sendDate == null || sendDate.isBlank()) return null;

        // Si viene como epoch seconds, prueba:
        // return LocalDateTime.ofInstant(Instant.ofEpochSecond(Long.parseLong(sendDate)), ZoneId.systemDefault());

        // Si viene como "YYYY-MM-DD HH:mm:ss"
        try {
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            return LocalDateTime.parse(sendDate, fmt);
        } catch (Exception ignore) {
            return null;
        }
    }

    /**
     * Toma la primera apertura (más antigua) de tracking.client_info[].action_date
     * Si tracking.open > 0 pero no hay lista, regresamos now() como fallback (opcional).
     */
    private LocalDateTime extractFirstOpenDate(SendPulseEmailInfoResponse info) {

        if (info == null || info.tracking() == null) return null;

        Integer openCount = info.tracking().open();
        if (openCount == null || openCount <= 0) return null;

        List<SendPulseEmailInfoResponse.ActionInfo> actions = info.tracking().clientInfo();
        if (actions == null || actions.isEmpty()) {
            // fallback: sabemos que abrió, pero no tenemos fecha exacta
            return LocalDateTime.now();
        }

        return actions.stream()
                .map(SendPulseEmailInfoResponse.ActionInfo::actionDate)
                .map(this::parseActionDateOrNull)
                .filter(d -> d != null)
                .min(Comparator.naturalOrder())
                .orElse(LocalDateTime.now());
    }

    /**
     * action_date en doc se ve como texto.
     * En tu UI aparece "Marzo 2, 2026 11:45 AM" (eso es UI, no necesariamente API).
     * La API normalmente devuelve timestamp o un formato ISO/DB.
     *
     * Aquí ponemos un parser base. Ajusta cuando veas un ejemplo real del JSON.
     */
    private LocalDateTime parseActionDateOrNull(String actionDate) {
        if (actionDate == null || actionDate.isBlank()) return null;

        // Caso común: "2026-03-02 11:45:00"
        try {
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            return LocalDateTime.parse(actionDate, fmt);
        } catch (Exception ignore) { }

        // Caso ISO: "2026-03-02T11:45:00Z" o con offset
        try {
            return ZonedDateTime.parse(actionDate).withZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime();
        } catch (Exception ignore) { }

        return null;
    }
}