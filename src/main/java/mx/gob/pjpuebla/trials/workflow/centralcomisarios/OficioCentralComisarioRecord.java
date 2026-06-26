package mx.gob.pjpuebla.trials.workflow.centralcomisarios;

import mx.gob.pjpuebla.trials.util.enums.EstadoCentralComisario;

public record OficioCentralComisarioRecord(
        Integer oficioId,
        EstadoCentralComisario estado,
        Integer personaId
) {
}
