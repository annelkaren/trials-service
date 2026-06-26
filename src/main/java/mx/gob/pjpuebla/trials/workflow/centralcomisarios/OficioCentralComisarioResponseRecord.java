package mx.gob.pjpuebla.trials.workflow.centralcomisarios;

import mx.gob.pjpuebla.trials.util.enums.EstadoCentralComisario;

import java.time.LocalDateTime;

public record OficioCentralComisarioResponseRecord(
        Integer oficioId,
        EstadoCentralComisario estado,
        String personaEnvia,
        String personaRecibe,
        String comisario,
        String personaDevuelve,
        LocalDateTime fechaEnvio,
        LocalDateTime fechaRegistro,
        LocalDateTime fechaNotificacion,
        LocalDateTime fechaDevolucion
) {
}
