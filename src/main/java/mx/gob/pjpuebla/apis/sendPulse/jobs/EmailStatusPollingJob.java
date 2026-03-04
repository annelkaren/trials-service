package mx.gob.pjpuebla.apis.sendPulse.jobs;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
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

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;

@Component
public class EmailStatusPollingJob {

    private static final int MAX_INTENTOS = 10;
    private static final ZoneId MEXICO_ZONE = ZoneId.of("America/Mexico_City");
    private static final ZoneId UTC_ZONE = ZoneId.of("UTC");
    private static final DateTimeFormatter SENDPULSE_DATETIME_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final EmailLogsRepository emailLogRepository;
    private final SendPulseClient sendPulseClient;
    private final ObjectMapper objectMapper;

    public EmailStatusPollingJob(
            EmailLogsRepository emailLogRepository,
            SendPulseClient sendPulseClient,
            ObjectMapper objectMapper
    ) {
        this.emailLogRepository = emailLogRepository;
        this.sendPulseClient = sendPulseClient;
        this.objectMapper = objectMapper;
    }

    @Scheduled(cron = "0 * * * * *")
    @Transactional
    public void poll() {

        LocalDateTime now = LocalDateTime.now();

        List<EmailLogs> batch = emailLogRepository.findBatchToVerify(
                List.of(
                        EstadoEnvioCorreo.PENDIENTE_ENVIO,
                        EstadoEnvioCorreo.ENVIADO,
                        EstadoEnvioCorreo.LEIDO
                ),
                now,
                PageRequest.of(0, 200)
        );

        for (EmailLogs log : batch) {

            if (log.getEstado() == EstadoEnvioCorreo.LEIDO && hasTrackingLink(log)) {
                log.setUltimaVerificacion(now);
                log.setProximaVerificacion(null);
                continue;
            }

            int intentos = log.getIntentosVerificacion() == null ? 0 : log.getIntentosVerificacion();

            if (log.getEstado() != EstadoEnvioCorreo.LEIDO && intentos >= MAX_INTENTOS) {
                marcarFinalPorMaxIntentos(log, now);
                continue;
            }

            if (log.getProviderMessageId() == null || log.getProviderMessageId().isBlank()) {
                if (log.getEstado() == EstadoEnvioCorreo.LEIDO) {
                    log.setUltimaVerificacion(now);
                    log.setProximaVerificacion(null);
                    continue;
                }

                intentos++;
                log.setIntentosVerificacion(intentos);
                log.setUltimaVerificacion(now);

                if (intentos >= MAX_INTENTOS) {
                    marcarFinalPorMaxIntentos(log, now);
                } else {
                    log.setProximaVerificacion(null);
                }
                continue;
            }

            try {
                SendPulseEmailInfoResponse info = sendPulseClient.getEmailInfo(log.getProviderMessageId());

                log.setSmtpAnswerCode(info.smtpAnswerCode());
                log.setSmtpAnswerSubcode(info.smtpAnswerSubcode());
                log.setSmtpAnswerCodeExplain(info.smtpAnswerCodeExplain());
                log.setSmtpAnswerData(info.smtpAnswerData());
                log.setUltimaVerificacion(now);
                log.setErrorEnvioDetalle(null);

                if (info.smtpAnswerCode() != null && info.smtpAnswerCode() >= 400) {
                    log.setEstado(EstadoEnvioCorreo.NO_ENTREGADO);
                    log.setProximaVerificacion(null);
                    continue;
                }

                if (info.smtpAnswerCode() != null && info.smtpAnswerCode() >= 200 && info.smtpAnswerCode() < 300) {
                    if (log.getEstado() == EstadoEnvioCorreo.PENDIENTE_ENVIO) {
                        log.setEstado(EstadoEnvioCorreo.ENVIADO);
                    }
                    if (log.getFechaEntrega() == null) {
                        LocalDateTime delivered = parseSendPulseDateOrNull(info.sendDate());
                        log.setFechaEntrega(delivered != null ? delivered : now);
                    }
                }

                LocalDateTime openedAt = extractFirstOpenDate(info);
                if (openedAt != null) {
                    log.setEstado(EstadoEnvioCorreo.LEIDO);
                    if (log.getFechaLectura() == null) {
                        log.setFechaLectura(openedAt);
                    }
                }

                LocalDateTime clickedAt = extractFirstClickDate(info);
                if (clickedAt != null && log.getFechaDescargaVinculo() == null) {
                    log.setFechaDescargaVinculo(clickedAt);
                }

                if (info.tracking() != null) {
                    log.setTrackingLinkDetalle(buildTrackingDetalleWithMexicoTime(info.tracking()));
                }

                if (log.getEstado() == EstadoEnvioCorreo.LEIDO && hasTrackingLink(log)) {
                    log.setProximaVerificacion(null);
                    continue;
                }

                intentos++;
                log.setIntentosVerificacion(intentos);
                log.setProximaVerificacion(null);

            } catch (Exception ex) {
                intentos++;
                log.setIntentosVerificacion(intentos);
                log.setUltimaVerificacion(now);
                log.setErrorEnvioDetalle(truncateError(ex.getMessage()));

                if (log.getEstado() != EstadoEnvioCorreo.LEIDO && intentos >= MAX_INTENTOS) {
                    marcarFinalPorMaxIntentos(log, now);
                } else {
                    log.setProximaVerificacion(null);
                }
            }
        }
    }

    private boolean hasTrackingLink(EmailLogs log) {
        if (log.getTrackingLinkDetalle() == null || log.getTrackingLinkDetalle().isNull()) {
            return false;
        }
        JsonNode linkNode = log.getTrackingLinkDetalle().path("link");
        return linkNode.isArray() && !linkNode.isEmpty();
    }

    private void marcarFinalPorMaxIntentos(EmailLogs log, LocalDateTime now) {
        log.setUltimaVerificacion(now);
        log.setProximaVerificacion(null);
        if (log.getProviderMessageId() == null || log.getProviderMessageId().isBlank()) {
            log.setEstado(EstadoEnvioCorreo.NO_ENVIADO);
        } else {
            log.setEstado(EstadoEnvioCorreo.NO_ENTREGADO);
        }
    }

    private LocalDateTime parseSendPulseDateOrNull(String sendDate) {
        if (sendDate == null || sendDate.isBlank()) {
            return null;
        }

        try {
            return LocalDateTime.parse(sendDate, SENDPULSE_DATETIME_FMT);
        } catch (Exception ignore) {
            return null;
        }
    }

    private LocalDateTime extractFirstOpenDate(SendPulseEmailInfoResponse info) {

        if (info == null || info.tracking() == null) {
            return null;
        }

        Integer openCount = info.tracking().open();
        if (openCount == null || openCount <= 0) {
            return null;
        }

        List<SendPulseEmailInfoResponse.ActionInfo> actions = info.tracking().clientInfo();
        if (actions == null || actions.isEmpty()) {
            return LocalDateTime.now();
        }

        return actions.stream()
                .map(SendPulseEmailInfoResponse.ActionInfo::actionDate)
                .map(this::parseActionDateOrNull)
                .filter(d -> d != null)
                .min(Comparator.naturalOrder())
                .orElse(LocalDateTime.now());
    }

    private LocalDateTime extractFirstClickDate(SendPulseEmailInfoResponse info) {

        if (info == null || info.tracking() == null) {
            return null;
        }

        List<SendPulseEmailInfoResponse.ActionInfo> links = info.tracking().link();
        if (links == null || links.isEmpty()) {
            return null;
        }

        return links.stream()
                .map(SendPulseEmailInfoResponse.ActionInfo::actionDate)
                .map(this::parseActionDateOrNull)
                .filter(d -> d != null)
                .min(Comparator.naturalOrder())
                .orElse(null);
    }

    private LocalDateTime parseActionDateOrNull(String actionDate) {
        if (actionDate == null || actionDate.isBlank()) {
            return null;
        }

        try {
            return OffsetDateTime.parse(actionDate)
                    .atZoneSameInstant(MEXICO_ZONE)
                    .toLocalDateTime();
        } catch (Exception ignore) {
            // ignore
        }

        try {
            return ZonedDateTime.parse(actionDate)
                    .withZoneSameInstant(MEXICO_ZONE)
                    .toLocalDateTime();
        } catch (Exception ignore) {
            // ignore
        }

        try {
            LocalDateTime utcDateTime = LocalDateTime.parse(actionDate, SENDPULSE_DATETIME_FMT);
            return utcDateTime.atZone(UTC_ZONE)
                    .withZoneSameInstant(MEXICO_ZONE)
                    .toLocalDateTime();
        } catch (Exception ignore) {
            // ignore
        }

        return null;
    }

    private ObjectNode buildTrackingDetalleWithMexicoTime(SendPulseEmailInfoResponse.Tracking tracking) {
        ObjectNode trackingNode = objectMapper.valueToTree(tracking);
        normalizeActionDatesToMexicoTime(trackingNode, "link");
        normalizeActionDatesToMexicoTime(trackingNode, "client_info");
        return trackingNode;
    }

    private void normalizeActionDatesToMexicoTime(ObjectNode trackingNode, String fieldName) {
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
            LocalDateTime mexicoDate = parseActionDateOrNull(actionDateNode.asText());
            if (mexicoDate != null) {
                itemObject.put("action_date", mexicoDate.format(SENDPULSE_DATETIME_FMT));
            }
        }
    }

    private String truncateError(String error) {
        if (error == null) {
            return null;
        }
        return error.length() <= 1000 ? error : error.substring(0, 1000);
    }
}
