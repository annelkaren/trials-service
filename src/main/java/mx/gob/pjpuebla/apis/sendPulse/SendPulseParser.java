package mx.gob.pjpuebla.apis.sendPulse;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import mx.gob.pjpuebla.apis.sendPulse.records.SendPulseEmailInfoResponse;

@Component
public class SendPulseParser {

    private static final ZoneId ZONA_MEXICO = ZoneId.of("America/Mexico_City");
    private static final ZoneId ZONA_UTC = ZoneId.of("UTC");
    private static final DateTimeFormatter FMT_SENDPULSE = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final ObjectMapper objectMapper;

    public SendPulseParser(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public LocalDateTime parsearSendDate(String sendDate) {
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

    public LocalDateTime parsearActionDate(String actionDate) {
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

    public LocalDateTime extraerPrimeraFechaOpen(SendPulseEmailInfoResponse info) {
        if (info == null || info.tracking() == null) {
            return null;
        }

        List<SendPulseEmailInfoResponse.ActionInfo> acciones = info.tracking().clientInfo();
        if (acciones == null || acciones.isEmpty()) {
            return null;
        }

        return acciones.stream()
                .map(SendPulseEmailInfoResponse.ActionInfo::actionDate)
                .map(this::parsearActionDate)
                .filter(Objects::nonNull)
                .min(Comparator.naturalOrder())
                .orElse(null);
    }

    public LocalDateTime extraerPrimeraFechaClick(SendPulseEmailInfoResponse info) {
        if (info == null || info.tracking() == null) {
            return null;
        }

        List<SendPulseEmailInfoResponse.ActionInfo> links = info.tracking().link();
        if (links == null || links.isEmpty()) {
            return null;
        }

        return links.stream()
                .map(SendPulseEmailInfoResponse.ActionInfo::actionDate)
                .map(this::parsearActionDate)
                .filter(Objects::nonNull)
                .min(Comparator.naturalOrder())
                .orElse(null);
    }

    public ObjectNode construirTrackingConLinkNormalizado(SendPulseEmailInfoResponse.Tracking tracking) {
        ObjectNode trackingNode = objectMapper.valueToTree(tracking);
        normalizarActionDates(trackingNode, "link");
        normalizarActionDates(trackingNode, "client_info");
        return trackingNode;
    }

    public boolean hayEventoOpen(SendPulseEmailInfoResponse info) {
        return info != null
                && info.tracking() != null
                && info.tracking().open() != null
                && info.tracking().open() > 0;
    }

    public boolean hayEventoClick(SendPulseEmailInfoResponse info) {
        return info != null
                && info.tracking() != null
                && info.tracking().click() != null
                && info.tracking().click() > 0;
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

            LocalDateTime mexicoDate = parsearActionDate(actionDateNode.asText());
            if (mexicoDate != null) {
                itemObject.put("action_date", mexicoDate.format(FMT_SENDPULSE));
            }
        }
    }

}
