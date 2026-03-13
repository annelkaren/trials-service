package mx.gob.pjpuebla.trials.workflow.emailLogs;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mx.gob.pjpuebla.trials.util.enums.EstadoEnvioCorreo;
import mx.gob.pjpuebla.trials.workflow.notificaciones.NotificacionService;
import mx.gob.pjpuebla.trials.workflow.notificacionesSalas.NotificacionesSalas;
import mx.gob.pjpuebla.trials.workflow.notificacionesSalas.NotificacionesSalasRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailStatusService {

    private final EmailLogsRepository emailLogsRepository;
    private final NotificacionService notificacionService;
    private final NotificacionesSalasRepository notificacionesSalasRepository;

    private static final ZoneId ZONA_MEXICO = ZoneId.of("America/Mexico_City");
    private static final Duration VENTANA_NO_LEIDO = Duration.ofDays(2);

    public void marcarNoLeido(EmailLogs emailLog) {
        LocalDateTime ahoraMx = ZonedDateTime.now(ZONA_MEXICO).toLocalDateTime();
        LocalDateTime referencia = obtenerFechaReferencia(emailLog);

        if (referencia != null) {
            // Continuamos flujo:
            Duration transcurrido = Duration.between(referencia, ahoraMx);
            Boolean transcurridoIsNegative = transcurrido.isNegative();
            Boolean transcurridoComparete = transcurrido.compareTo(VENTANA_NO_LEIDO) < 0;

            if (transcurridoIsNegative || transcurridoComparete) {
                emailLog.setEstado(EstadoEnvioCorreo.NO_LEIDO);
                emailLog.setUltimaVerificacion(ahoraMx);
                emailLogsRepository.save(emailLog);
            }
        }

        //Evaluamos tiempo de termino de la notificación: 
        NotificacionesSalas ns = notificacionesSalasRepository.findBy 

    }

    private LocalDateTime obtenerFechaReferencia(EmailLogs log) {
        return (log.getFechaEnvio() != null) ? log.getFechaEnvio() : log.getFechaEntrega();
    }

}
