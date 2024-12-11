package mx.gob.pjpuebla.trials.workflow.listaestrados;


public record ListaEstradoRecord(
        Integer id,
        String fechaAlta,
        Integer noNotificaciones,
        String usuarioAlta
) {
}
