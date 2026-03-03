package mx.gob.pjpuebla.trials.workflow.notificacionesSalas.records;

import java.time.LocalDateTime;

import mx.gob.pjpuebla.trials.util.enums.Estado;
import mx.gob.pjpuebla.trials.util.enums.EstadoEnvioCorreo;

public record NotificacionSalaDestinatarioRecord(
        Integer id,
        String nombreDestinatario,
        String correoElectronico,
        String tipoParte,
        Estado estado,
        EstadoEnvioCorreo estadoEnvioCorreo,
        LocalDateTime fechaEntrega,
        LocalDateTime fechaLectura) {
}
