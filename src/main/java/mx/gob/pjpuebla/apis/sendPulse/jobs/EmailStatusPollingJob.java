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
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Component
public class EmailStatusPollingJob {

    private static final int TAMANIO_LOTE = 200;
    private static final int MAX_ERROR_DETALLE = 1000;
    private static final Duration VENTANA_NO_LEIDO = Duration.ofDays(2);

    private static final ZoneId ZONA_MEXICO = ZoneId.of("America/Mexico_City");
    private static final ZoneId ZONA_UTC = ZoneId.of("UTC");
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

    @Scheduled(cron = "0 * * * * *")
    @Transactional
    public void ejecutarSondeo() {
        LocalDateTime ahoraMx = ZonedDateTime.now(ZONA_MEXICO).toLocalDateTime();

        List<EmailLogs> lote = emailLogsRepo.findBatchToVerify(
                List.of(EstadoEnvioCorreo.PENDIENTE_ENVIO, EstadoEnvioCorreo.ENVIADO, EstadoEnvioCorreo.LEIDO),
                ahoraMx,
                PageRequest.of(0, TAMANIO_LOTE)
        );

        if (lote.isEmpty()) {
            return;
        }

        Map<String, List<EmailLogs>> porProviderId = new HashMap<>();

        for (EmailLogs log : lote) {
            if (marcarNoLeidoSiExpira(log, ahoraMx)) {
                continue;
            }

            if (estaLeidoFinalizado(log)) {
                continue;
            }

            String providerId = log.getProviderMessageId();
            if (providerId == null || providerId.isBlank()) {
                registrarIntentoVerificacion(log, ahoraMx);
                log.setErrorEnvioDetalle(recortarError("No se puede consultar SendPulse: providerMessageId vacio."));
            } else {
                porProviderId.computeIfAbsent(providerId, __ -> new ArrayList<>()).add(log);
            }
        }

        if (porProviderId.isEmpty()) {
            return;
        }

        try {
            Map<String, SendPulseEmailInfoResponse> infoPorId =
                    sendPulseClient.getEmailInfoBulk(new ArrayList<>(porProviderId.keySet()));

            for (Map.Entry<String, List<EmailLogs>> entry : porProviderId.entrySet()) {
                String providerId = entry.getKey();
                SendPulseEmailInfoResponse info = infoPorId.get(providerId);

                for (EmailLogs log : entry.getValue()) {
                    if (marcarNoLeidoSiExpira(log, ahoraMx)) {
                        continue;
                    }

                    registrarIntentoVerificacion(log, ahoraMx);

                    if (info == null) {
                        log.setErrorEnvioDetalle(recortarError("No se obtuvo info de SendPulse para providerMessageId=" + providerId));
                        continue;
                    }

                    aplicarInfoDeSendPulse(log, info, ahoraMx);
                }
            }
        } catch (Exception ex) {
            for (List<EmailLogs> grupo : porProviderId.values()) {
                for (EmailLogs log : grupo) {
                    registrarIntentoVerificacion(log, ahoraMx);
                    log.setErrorEnvioDetalle(recortarError(ex.getMessage()));
                }
            }
        }
    }

    private void aplicarInfoDeSendPulse(EmailLogs log, SendPulseEmailInfoResponse info, LocalDateTime ahoraMx) {
        log.setSmtpAnswerCode(info.smtpAnswerCode());
        log.setSmtpAnswerSubcode(info.smtpAnswerSubcode());
        log.setSmtpAnswerCodeExplain(info.smtpAnswerCodeExplain());
        log.setSmtpAnswerData(info.smtpAnswerData());
        log.setErrorEnvioDetalle(null);

        if (info.smtpAnswerCode() != null && info.smtpAnswerCode() >= 400) {
            log.setEstado(EstadoEnvioCorreo.NO_ENTREGADO);
            return;
        }

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

        if (info.tracking() != null) {
            log.setTrackingLinkDetalle(construirTrackingConLinkNormalizado(info.tracking()));
        }

        if (hayEventoOpen(info)) {
            log.setEstado(EstadoEnvioCorreo.LEIDO);
            if (log.getFechaLectura() == null) {
                LocalDateTime primeraLecturaMx = extraerPrimeraFechaOpen(info);
                if (primeraLecturaMx != null) {
                    log.setFechaLectura(primeraLecturaMx);
                }
            }
        }

        LocalDateTime primerClickMx = extraerPrimeraFechaClick(info);
        if (primerClickMx != null && log.getFechaDescargaVinculo() == null) {
            log.setFechaDescargaVinculo(primerClickMx);
        } else if (hayEventoClick(info) && log.getFechaDescargaVinculo() == null) {
            log.setFechaDescargaVinculo(ahoraMx);
        }

        if (marcarNoLeidoSiExpira(log, ahoraMx)) {
            return;
        }
    }

    private boolean hayEventoOpen(SendPulseEmailInfoResponse info) {
        return info != null
                && info.tracking() != null
                && info.tracking().open() != null
                && info.tracking().open() > 0;
    }

    private boolean hayEventoClick(SendPulseEmailInfoResponse info) {
        return info != null
                && info.tracking() != null
                && info.tracking().click() != null
                && info.tracking().click() > 0;
    }

    private boolean estaLeidoFinalizado(EmailLogs log) {
        if (log.getEstado() != EstadoEnvioCorreo.LEIDO) {
            return false;
        }

        JsonNode tracking = log.getTrackingLinkDetalle();
        if (tracking == null || tracking.isNull()) {
            return false;
        }

        return tieneLinkEnTracking(tracking) || tieneClickEnTracking(tracking);
    }

    private boolean marcarNoLeidoSiExpira(EmailLogs log, LocalDateTime ahoraMx) {
        if (log.getEstado() == EstadoEnvioCorreo.LEIDO) {
            return false;
        }

        LocalDateTime referencia = obtenerFechaReferencia(log);
        if (referencia == null) {
            return false;
        }

        Duration transcurrido = Duration.between(referencia, ahoraMx);
        if (transcurrido.isNegative()) {
            return false;
        }
        if (transcurrido.compareTo(VENTANA_NO_LEIDO) < 0) {
            return false;
        }

        log.setEstado(EstadoEnvioCorreo.NO_LEIDO);
        log.setUltimaVerificacion(ahoraMx);
        return true;
    }

    private void registrarIntentoVerificacion(EmailLogs log, LocalDateTime ahoraMx) {
        int intentos = (log.getIntentosVerificacion() == null) ? 0 : log.getIntentosVerificacion();
        log.setIntentosVerificacion(intentos + 1);
        log.setUltimaVerificacion(ahoraMx);
    }

    private LocalDateTime obtenerFechaReferencia(EmailLogs log) {
        return (log.getFechaEnvio() != null) ? log.getFechaEnvio() : log.getFechaEntrega();
    }

    private LocalDateTime parsearSendDateUtcAMexico(String sendDate) {
        if (sendDate == null || sendDate.isBlank()) {
            return null;
        }

        try {
            return OffsetDateTime.parse(sendDate)
                    .atZoneSameInstant(ZONA_MEXICO)
                    .toLocalDateTime();
        } catch (Exception ignore) {
        }

        try {
            return ZonedDateTime.parse(sendDate)
                    .withZoneSameInstant(ZONA_MEXICO)
                    .toLocalDateTime();
        } catch (Exception ignore) {
        }

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

    private LocalDateTime parsearActionDateAMexico(String actionDate) {
        if (actionDate == null || actionDate.isBlank()) {
            return null;
        }

        try {
            return OffsetDateTime.parse(actionDate)
                    .atZoneSameInstant(ZONA_MEXICO)
                    .toLocalDateTime();
        } catch (Exception ignore) {
        }

        try {
            return ZonedDateTime.parse(actionDate)
                    .withZoneSameInstant(ZONA_MEXICO)
                    .toLocalDateTime();
        } catch (Exception ignore) {
        }

        try {
            LocalDateTime utc = LocalDateTime.parse(actionDate, FMT_SENDPULSE);
            return utc.atZone(ZONA_UTC)
                    .withZoneSameInstant(ZONA_MEXICO)
                    .toLocalDateTime();
        } catch (Exception ignore) {
            return null;
        }
    }

    private ObjectNode construirTrackingConLinkNormalizado(SendPulseEmailInfoResponse.Tracking tracking) {
        ObjectNode trackingNode = objectMapper.valueToTree(tracking);
        normalizarActionDates(trackingNode, "link");
        normalizarActionDates(trackingNode, "client_info");
        return trackingNode;
    }

    private void normalizarActionDates(ObjectNode trackingNode, String fieldName) {
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

            LocalDateTime mexicoDate = parsearActionDateAMexico(actionDateNode.asText());
            if (mexicoDate != null) {
                itemObject.put("action_date", mexicoDate.format(FMT_SENDPULSE));
            }
        }
    }

    private boolean tieneLinkEnTracking(JsonNode tracking) {
        JsonNode linkNode = tracking.path("link");
        return linkNode.isArray() && !linkNode.isEmpty();
    }

    private boolean tieneClickEnTracking(JsonNode tracking) {
        JsonNode clickNode = tracking.path("click");
        if (clickNode.isNumber()) {
            return clickNode.asInt() > 0;
        }
        if (clickNode.isTextual()) {
            try {
                return Integer.parseInt(clickNode.asText()) > 0;
            } catch (NumberFormatException ignored) {
                return false;
            }
        }
        return false;
    }

    private String recortarError(String error) {
        if (error == null) {
            return null;
        }
        return error.length() <= MAX_ERROR_DETALLE ? error : error.substring(0, MAX_ERROR_DETALLE);
    }
}
