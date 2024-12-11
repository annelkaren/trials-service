package mx.gob.pjpuebla.trials.workflow.listaestrados;

import mx.gob.pjpuebla.trials.core.personas.Persona;

public record ListaEstradoRecord(
        Integer id,
        String fechaAlta,
        Integer noNotificaciones,
        String usuarioAlta
) {
}
