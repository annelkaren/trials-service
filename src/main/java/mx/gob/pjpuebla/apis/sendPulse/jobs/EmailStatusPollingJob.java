package mx.gob.pjpuebla.apis.sendPulse.jobs;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import mx.gob.pjpuebla.apis.sendPulse.SendPulseClient;
import mx.gob.pjpuebla.apis.sendPulse.records.SendPulseEmailInfoResponse;
import mx.gob.pjpuebla.trials.util.enums.EstadoEnvioCorreo;
import mx.gob.pjpuebla.trials.workflow.emailLogs.EmailLogs;
import mx.gob.pjpuebla.trials.workflow.emailLogs.EmailLogsRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Job de sondeo (polling) para actualizar el estado de correos enviados vía SendPulse.
 *
 * <p>¿Qué problema resuelve?
 * <ul>
 *   <li>Consultar a SendPulse (en bulk) para actualizar estados del correo en nuestro EmailLogs.</li>
 *   <li>Registrar fechas: entrega, lectura (open), click (descarga/vínculo).</li>
 *   <li>Aplicar reglas de negocio: si pasan 7 días sin lectura => NO_LEIDO.</li>
 *   <li>Evitar rate-limit: usar backoff con proximaVerificacion y ejecutar cada hora.</li>
 * </ul>
 *
 * <p>Conceptos clave:
 * <ul>
 *   <li><b>proximaVerificacion</b>: controla cuándo volver a consultar. Si está en el futuro, la query no lo trae.</li>
 *   <li><b>Duration</b>: representa una cantidad de tiempo (ej. 7 días, 6 horas). Se usa para comparar ventanas y sumar backoff.</li>
 *   <li><b>Zona horaria</b>: normalizamos a America/Mexico_City; si SendPulse manda timestamps en UTC/ISO, los convertimos.</li>
 * </ul>
 */
@Component
public class EmailStatusPollingJob {

    // ====== Configuración general ======

    /** Tamaño máximo del lote a procesar por corrida. */
    private static final int TAMANIO_LOTE = 200;

    /** Longitud máxima permitida para errorEnvioDetalle. */
    private static final int MAX_ERROR_DETALLE = 1000;

    // ====== Reglas de negocio: ventana de "no leído" ======

    /**
     * Duration:
     * <p>Duration es una clase de java.time que representa un "delta" de tiempo (segundos + nanos).
     * Ejemplos:
     * <ul>
     *   <li>Duration.ofDays(7) => 7 días</li>
     *   <li>Duration.ofHours(6) => 6 horas</li>
     * </ul>
     * Se puede comparar con compareTo() y sumarse a fechas con LocalDateTime.plus(Duration).
     */
    private static final Duration VENTANA_NO_LEIDO = Duration.ofDays(7);

    /** Primeras 24 horas tras el envío: hacemos polling más frecuente. */
    private static final Duration VENTANA_PRIMER_DIA = Duration.ofHours(24);

    /** Backoff (reintento) durante el primer día. */
    private static final Duration BACKOFF_PRIMER_DIA = Duration.ofHours(1);

    /** Backoff (reintento) del día 2 al día 7. */
    private static final Duration BACKOFF_DIA_2_A_7 = Duration.ofHours(6);

    // ====== Zonas horarias / formatos ======

    private static final ZoneId ZONA_MEXICO = ZoneId.of("America/Mexico_City");
    private static final ZoneId ZONA_UTC = ZoneId.of("UTC");

    /**
     * Formato "legacy" frecuente cuando APIs mandan fecha sin offset:
     * yyyy-MM-dd HH:mm:ss
     *
     * Si llega así (sin Z / sin offset), la asumimos UTC y la convertimos a México.
     */
    private static final DateTimeFormatter FMT_SENDPULSE = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final EmailLogsRepository emailLogsRepo;
    private final SendPulseClient sendPulseClient;
    private final ObjectMapper objectMapper;

    public EmailStatusPollingJob(
            EmailLogsRepository emailLogsRepo,
            SendPulseClient sendPulseClient,
            ObjectMapper objectMapper
    ) {
        this.emailLogsRepo = emailLogsRepo;
        this.sendPulseClient = sendPulseClient;
        this.objectMapper = objectMapper;
    }

    /**
     * Ejecuta cada hora.
     *
     * <p>Ojo: aunque el job corra cada hora, NO significa que procese todos los correos cada hora.
     * Solo procesa los que cumplan:
     * <ul>
     *   <li>estado en {PENDIENTE_ENVIO, ENVIADO, LEIDO}</li>
     *   <li>proximaVerificacion es null o <= now</li>
     *   <li>si está LEIDO: solo si aún no hay click (para permitir capturar click posterior)</li>
     * </ul>
     */
    @Scheduled(cron = "0 0 * * * *")
    @Transactional
    public void ejecutarSondeo() {

        // ZonedDateTime.now(ZONA_MEXICO) sí respeta zona; luego lo bajamos a LocalDateTime.
        LocalDateTime ahoraMx = ZonedDateTime.now(ZONA_MEXICO).toLocalDateTime();

        List<EmailLogs> lote = emailLogsRepo.findBatchToVerify(
                List.of(EstadoEnvioCorreo.PENDIENTE_ENVIO, EstadoEnvioCorreo.ENVIADO, EstadoEnvioCorreo.LEIDO),
                ahoraMx,
                PageRequest.of(0, TAMANIO_LOTE)
        );

        if (lote.isEmpty()) {
            return;
        }

        // Separación:
        // 1) logs sin providerMessageId (no consultables en SendPulse)
        // 2) logs agrupados por providerMessageId (consultables en bulk)
        List<EmailLogs> sinProviderId = new ArrayList<>();
        Map<String, List<EmailLogs>> porProviderId = new HashMap<>();

        for (EmailLogs log : lote) {

            // Caso final: LEIDO + click ya registrado => no volver a consultar
            if (estaLeidoConClick(log)) {
                log.setUltimaVerificacion(ahoraMx);
                log.setProximaVerificacion(null);
                continue;
            }

            // Regla de negocio: 7 días sin lectura => NO_LEIDO (final)
            if (marcarNoLeidoSiExpira(log, ahoraMx)) {
                continue;
            }

            String providerId = log.getProviderMessageId();
            if (providerId == null || providerId.isBlank()) {
                sinProviderId.add(log);
            } else {
                porProviderId.computeIfAbsent(providerId, __ -> new ArrayList<>()).add(log);
            }
        }

        // Manejo de los que no tienen providerId: solo reprogramamos con backoff.
        for (EmailLogs log : sinProviderId) {
            registrarIntentoVerificacion(log, ahoraMx);
            log.setErrorEnvioDetalle(null);
            log.setProximaVerificacion(calcularBackoffRegular(log, ahoraMx));
        }

        // Si no hay nada que consultar en SendPulse, terminamos.
        if (porProviderId.isEmpty()) {
            return;
        }

        // Bulk call: 1 sola llamada para N providerIds.
        try {
            Map<String, SendPulseEmailInfoResponse> infoPorId =
                    sendPulseClient.getEmailInfoBulk(new ArrayList<>(porProviderId.keySet()));

            for (Map.Entry<String, List<EmailLogs>> entry : porProviderId.entrySet()) {
                String providerId = entry.getKey();
                SendPulseEmailInfoResponse info = infoPorId.get(providerId);

                for (EmailLogs log : entry.getValue()) {

                    // Re-chequeo de expiración (defensivo)
                    if (marcarNoLeidoSiExpira(log, ahoraMx)) {
                        continue;
                    }

                    registrarIntentoVerificacion(log, ahoraMx);

                    if (info == null) {
                        log.setErrorEnvioDetalle(recortarError("No se obtuvo info de SendPulse para providerMessageId=" + providerId));
                        log.setProximaVerificacion(calcularBackoffRegular(log, ahoraMx));
                        continue;
                    }

                    aplicarInfoDeSendPulse(log, info, ahoraMx);
                }
            }

        } catch (Exception ex) {
            // Si el bulk falla (timeout, 429, etc.), aplicamos backoff de error a todos los del grupo.
            for (List<EmailLogs> grupo : porProviderId.values()) {
                for (EmailLogs log : grupo) {
                    registrarIntentoVerificacion(log, ahoraMx);
                    aplicarBackoffError(log, ahoraMx, ex);
                }
            }
        }
    }

    // =====================================================================================
    // =============== Lógica principal para aplicar respuesta de SendPulse =================
    // =====================================================================================

    private void aplicarInfoDeSendPulse(EmailLogs log, SendPulseEmailInfoResponse info, LocalDateTime ahoraMx) {

        // Guardar diagnóstico SMTP
        log.setSmtpAnswerCode(info.smtpAnswerCode());
        log.setSmtpAnswerSubcode(info.smtpAnswerSubcode());
        log.setSmtpAnswerCodeExplain(info.smtpAnswerCodeExplain());
        log.setSmtpAnswerData(info.smtpAnswerData());
        log.setErrorEnvioDetalle(null);

        // 4xx/5xx => lo tratamos como NO_ENTREGADO (final)
        if (info.smtpAnswerCode() != null && info.smtpAnswerCode() >= 400) {
            log.setEstado(EstadoEnvioCorreo.NO_ENTREGADO);
            log.setProximaVerificacion(null);
            return;
        }

        // 2xx => marcar ENVIADO y set fechaEntrega si no existe
        if (info.smtpAnswerCode() != null && info.smtpAnswerCode() >= 200 && info.smtpAnswerCode() < 300) {
            if (log.getEstado() == EstadoEnvioCorreo.PENDIENTE_ENVIO) {
                log.setEstado(EstadoEnvioCorreo.ENVIADO);
            }
            if (log.getFechaEntrega() == null) {
                LocalDateTime entregadoMx = parsearSendDateUtcAMexico(info.sendDate());
                if (entregadoMx != null) {
                    log.setFechaEntrega(entregadoMx);
                }
            }
        }

        // Guardar tracking enriquecido (raw + mx)
        if (info.tracking() != null) {
            log.setTrackingLinkDetalle(construirTrackingEnriquecido(info.tracking()));
        }

        // Si hay open => LEIDO (pero fechaLectura solo si hay action_date parseable)
        if (hayEventoOpen(info)) {
            log.setEstado(EstadoEnvioCorreo.LEIDO);
            if (log.getFechaLectura() == null) {
                LocalDateTime primeraLecturaMx = extraerPrimeraFechaOpen(info);
                if (primeraLecturaMx != null) {
                    log.setFechaLectura(primeraLecturaMx);
                }
            }
        }

        // Click: guardar primera fecha click si existe
        LocalDateTime primerClickMx = extraerPrimeraFechaClick(info);
        if (primerClickMx != null && log.getFechaDescargaVinculo() == null) {
            log.setFechaDescargaVinculo(primerClickMx);
        }

        // Si ya es LEIDO con click => final
        if (estaLeidoConClick(log)) {
            log.setProximaVerificacion(null);
            return;
        }

        // Si expiró sin leer => NO_LEIDO
        if (marcarNoLeidoSiExpira(log, ahoraMx)) {
            return;
        }

        // Si sigue vivo, programar próxima verificación según backoff
        log.setProximaVerificacion(calcularBackoffRegular(log, ahoraMx));
    }

    // =====================================================================================
    // ========================= Reglas: open/click/finalización ===========================
    // =====================================================================================

    private boolean hayEventoOpen(SendPulseEmailInfoResponse info) {
        return info != null
                && info.tracking() != null
                && info.tracking().open() != null
                && info.tracking().open() > 0;
    }

    /**
     * Determina si un correo ya es "final" para nosotros:
     * Estado LEIDO + tracking JSON contiene al menos un link (click).
     */
    private boolean estaLeidoConClick(EmailLogs log) {
        if (log.getEstado() != EstadoEnvioCorreo.LEIDO) {
            return false;
        }
        if (log.getTrackingLinkDetalle() == null || log.getTrackingLinkDetalle().isNull()) {
            return false;
        }
        JsonNode linkNode = log.getTrackingLinkDetalle().path("link");
        return linkNode.isArray() && !linkNode.isEmpty();
    }

    /**
     * Regla de negocio:
     * Si no está LEIDO y ya pasaron 7 días desde fechaEnvio (o fechaEntrega),
     * se marca NO_LEIDO y se detiene el polling.
     */
    private boolean marcarNoLeidoSiExpira(EmailLogs log, LocalDateTime ahoraMx) {
        if (log.getEstado() == EstadoEnvioCorreo.LEIDO) {
            return false;
        }

        LocalDateTime referencia = obtenerFechaReferencia(log);
        if (referencia == null) {
            return false;
        }

        Duration transcurrido = Duration.between(referencia, ahoraMx);

        // Si transcurrido es negativo: referencia está en el futuro; no hacemos nada.
        if (transcurrido.isNegative()) {
            return false;
        }

        // compareTo: < 0 => transcurrido < VENTANA_NO_LEIDO (aún no expira)
        if (transcurrido.compareTo(VENTANA_NO_LEIDO) < 0) {
            return false;
        }

        log.setEstado(EstadoEnvioCorreo.NO_LEIDO);
        log.setUltimaVerificacion(ahoraMx);
        log.setProximaVerificacion(null);
        return true;
    }

    private void registrarIntentoVerificacion(EmailLogs log, LocalDateTime ahoraMx) {
        int intentos = (log.getIntentosVerificacion() == null) ? 0 : log.getIntentosVerificacion();
        log.setIntentosVerificacion(intentos + 1);
        log.setUltimaVerificacion(ahoraMx);
    }

    private void aplicarBackoffError(EmailLogs log, LocalDateTime ahoraMx, Exception ex) {
        log.setErrorEnvioDetalle(recortarError(ex.getMessage()));
        // En error aplicamos backoff conservador (6h) para proteger rate-limit
        log.setProximaVerificacion(ahoraMx.plus(BACKOFF_DIA_2_A_7));
    }

    /**
     * Backoff regular:
     * - 0 a 24h desde referencia => +1h
     * - 24h a 7 días => +6h
     * - si no hay referencia => +6h
     */
    private LocalDateTime calcularBackoffRegular(EmailLogs log, LocalDateTime ahoraMx) {
        LocalDateTime referencia = obtenerFechaReferencia(log);
        if (referencia == null) {
            return ahoraMx.plus(BACKOFF_DIA_2_A_7);
        }

        Duration transcurrido = Duration.between(referencia, ahoraMx);
        if (transcurrido.isNegative()) {
            return ahoraMx.plus(BACKOFF_PRIMER_DIA);
        }

        if (transcurrido.compareTo(VENTANA_PRIMER_DIA) < 0) {
            return ahoraMx.plus(BACKOFF_PRIMER_DIA);
        }

        return ahoraMx.plus(BACKOFF_DIA_2_A_7);
    }

    /**
     * Fecha base para calcular expiración/backoff:
     * Prioriza fechaEnvio; si no existe, usa fechaEntrega.
     */
    private LocalDateTime obtenerFechaReferencia(EmailLogs log) {
        return (log.getFechaEnvio() != null) ? log.getFechaEnvio() : log.getFechaEntrega();
    }

    // =====================================================================================
    // ============================ Parseo y normalización fechas ==========================
    // =====================================================================================

    /**
     * Convierte sendDate de SendPulse a hora México.
     *
     * <p>Se intenta en este orden:
     * <ol>
     *   <li>OffsetDateTime (ej: 2026-03-04T10:30:00Z o con -05:00)</li>
     *   <li>ZonedDateTime (ej: 2026-03-04T10:30:00Z[UTC])</li>
     *   <li>LocalDateTime con formato yyyy-MM-dd HH:mm:ss asumido como UTC</li>
     * </ol>
     */
    private LocalDateTime parsearSendDateUtcAMexico(String sendDate) {
        if (sendDate == null || sendDate.isBlank()) {
            return null;
        }

        try {
            return OffsetDateTime.parse(sendDate)
                    .atZoneSameInstant(ZONA_MEXICO)
                    .toLocalDateTime();
        } catch (Exception ignore) { /* ignore */ }

        try {
            return ZonedDateTime.parse(sendDate)
                    .withZoneSameInstant(ZONA_MEXICO)
                    .toLocalDateTime();
        } catch (Exception ignore) { /* ignore */ }

        try {
            LocalDateTime utc = LocalDateTime.parse(sendDate, FMT_SENDPULSE);
            return utc.atZone(ZONA_UTC)
                    .withZoneSameInstant(ZONA_MEXICO)
                    .toLocalDateTime();
        } catch (Exception ignore) {
            return null;
        }
    }

    private LocalDateTime extraerPrimeraFechaOpen(SendPulseEmailInfoResponse info) {
        if (info == null || info.tracking() == null) {
            return null;
        }
        List<SendPulseEmailInfoResponse.ActionInfo> acciones = info.tracking().clientInfo();
        if (acciones == null || acciones.isEmpty()) {
            return null;
        }

        return acciones.stream()
                .map(SendPulseEmailInfoResponse.ActionInfo::actionDate)
                .map(this::parsearActionDateAMexico)
                .filter(Objects::nonNull)
                .min(Comparator.naturalOrder())
                .orElse(null);
    }

    private LocalDateTime extraerPrimeraFechaClick(SendPulseEmailInfoResponse info) {
        if (info == null || info.tracking() == null) {
            return null;
        }
        List<SendPulseEmailInfoResponse.ActionInfo> links = info.tracking().link();
        if (links == null || links.isEmpty()) {
            return null;
        }

        return links.stream()
                .map(SendPulseEmailInfoResponse.ActionInfo::actionDate)
                .map(this::parsearActionDateAMexico)
                .filter(Objects::nonNull)
                .min(Comparator.naturalOrder())
                .orElse(null);
    }

    /**
     * Parseo de action_date a México con el mismo enfoque que sendDate.
     */
    private LocalDateTime parsearActionDateAMexico(String actionDate) {
        if (actionDate == null || actionDate.isBlank()) {
            return null;
        }

        try {
            return OffsetDateTime.parse(actionDate)
                    .atZoneSameInstant(ZONA_MEXICO)
                    .toLocalDateTime();
        } catch (Exception ignore) { /* ignore */ }

        try {
            return ZonedDateTime.parse(actionDate)
                    .withZoneSameInstant(ZONA_MEXICO)
                    .toLocalDateTime();
        } catch (Exception ignore) { /* ignore */ }

        try {
            LocalDateTime utc = LocalDateTime.parse(actionDate, FMT_SENDPULSE);
            return utc.atZone(ZONA_UTC)
                    .withZoneSameInstant(ZONA_MEXICO)
                    .toLocalDateTime();
        } catch (Exception ignore) {
            return null;
        }
    }

    // =====================================================================================
    // ============================ Tracking JSON enriquecido ==============================
    // =====================================================================================

    /**
     * Convierte Tracking a JSON y agrega dos campos por item:
     * - action_date_raw: valor original
     * - action_date_mx: valor convertido a México (formato yyyy-MM-dd HH:mm:ss)
     */
    private ObjectNode construirTrackingEnriquecido(SendPulseEmailInfoResponse.Tracking tracking) {
        ObjectNode trackingNode = objectMapper.valueToTree(tracking);
        enriquecerActionDates(trackingNode, "link");
        enriquecerActionDates(trackingNode, "client_info");
        return trackingNode;
    }

    private void enriquecerActionDates(ObjectNode trackingNode, String fieldName) {
        JsonNode node = trackingNode.path(fieldName);
        if (!node.isArray()) {
            return;
        }

        ArrayNode arrayNode = (ArrayNode) node;
        for (JsonNode item : arrayNode) {
            if (!(item instanceof ObjectNode itemObject)) {
                continue;
            }

            JsonNode actionDateNode = itemObject.get("action_date");
            if (actionDateNode == null || actionDateNode.isNull() || !actionDateNode.isTextual()) {
                continue;
            }

            String raw = actionDateNode.asText();
            itemObject.put("action_date_raw", raw);

            LocalDateTime mexicoDate = parsearActionDateAMexico(raw);
            if (mexicoDate != null) {
                itemObject.put("action_date_mx", mexicoDate.format(FMT_SENDPULSE));
            }
        }
    }

    private String recortarError(String error) {
        if (error == null) {
            return null;
        }
        return error.length() <= MAX_ERROR_DETALLE ? error : error.substring(0, MAX_ERROR_DETALLE);
    }
}