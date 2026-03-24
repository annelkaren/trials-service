package mx.gob.pjpuebla.trials.workflow.emailLogs;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mx.gob.pjpuebla.trials.error.NotFoundException;
import mx.gob.pjpuebla.trials.util.enums.EstadoEnvioCorreo;
import mx.gob.pjpuebla.trials.workflow.notificaciones.NotificacionService;
import mx.gob.pjpuebla.trials.workflow.notificacionesSalas.NotificacionSalaDestinatario;
import mx.gob.pjpuebla.trials.workflow.notificacionesSalas.NotificacionSalaDestinatarioRepository;
import mx.gob.pjpuebla.trials.workflow.notificacionesSalas.NotificacionesSalas;
import mx.gob.pjpuebla.trials.workflow.notificacionesSalas.NotificacionesSalasRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailStatusService {

    private final EmailLogsRepository emailLogsRepository;
    private final NotificacionesSalasRepository notificacionesSalasRepository;
    private final NotificacionSalaDestinatarioRepository notificacionSalaDestinatarioRepository;

    private static final ZoneId ZONA_MEXICO = ZoneId.of("America/Mexico_City");

    // Se considerara como NO leido a un correo el cual vensa su fecha de termino.

    public boolean marcarNoLeido(EmailLogs emailLog) {
        LocalDateTime ahoraMx = ZonedDateTime.now(ZONA_MEXICO).toLocalDateTime();

        if (emailLog.getEstado().equals(EstadoEnvioCorreo.NO_ENTREGADO)) {
            return false;
        }

        // Evaluamos tiempo de termino de la notificación:
        NotificacionSalaDestinatario nsd = notificacionSalaDestinatarioRepository.findByEmailLog(emailLog)
                .orElseThrow(() -> new NotFoundException("NotificacionSalaDestinatario no encontrada",
                        "emailLogId: " + emailLog.getId()));

        NotificacionesSalas notificacionSala = notificacionesSalasRepository.findById(nsd.getNotificacionSala().getId())
                .orElseThrow(() -> new NotFoundException("NotificacionesSalas no encontrada",
                        "notificacionSalaId: " + nsd.getNotificacionSala().getId()));

        LocalDateTime fechaTermino = notificacionSala.getFechaTermino();
        if (fechaTermino != null && fechaTermino.isBefore(ahoraMx)) {
            emailLog.setEstado(EstadoEnvioCorreo.NO_LEIDO);
            emailLog.setUltimaVerificacion(ahoraMx);
            emailLogsRepository.save(emailLog);
            return true;
        }

        return false;

    }

    public boolean estaLeidoFinalizado(EmailLogs emailLog) {
        if (emailLog.getEstado() != EstadoEnvioCorreo.LEIDO) {
            return false;
        }

        JsonNode tracking = emailLog.getTrackingLinkDetalle();

        if (tracking == null || tracking.isNull()) {
            return false;
        }

        return tieneLinkEnTracking(tracking);
    }

    private boolean tieneLinkEnTracking(JsonNode tracking) {
        JsonNode linkNode = tracking.path("link");

        if (!linkNode.isArray() || linkNode.isEmpty())
            return false;

        for (JsonNode linkItem : linkNode) {
            if (linkItem == null || linkItem.isNull() || !linkItem.isObject()) {
                continue;
            }

            if (tieneTextoNoVacio(linkItem, "action_date")
                    || tieneTextoNoVacio(linkItem, "url")
                    || tieneTextoNoVacio(linkItem, "ip")
                    || tieneTextoNoVacio(linkItem, "browser")
                    || tieneTextoNoVacio(linkItem, "os")
                    || tieneTextoNoVacio(linkItem, "screen_resolution")
                    || tieneTextoNoVacio(linkItem, "country")) {
                return true;
            }
        }

        return false;
    }

    private boolean tieneTextoNoVacio(JsonNode node, String fieldName) {
        JsonNode field = node.path(fieldName);
        if (!field.isTextual()) {
            return false;
        }
        String value = field.asText();
        if (value == null) {
            return false;
        }
        if (value.isBlank()) {
            return false;
        }
        if ("null".equalsIgnoreCase(value.trim())) {
            return false;
        }

        return true;
    }

}
