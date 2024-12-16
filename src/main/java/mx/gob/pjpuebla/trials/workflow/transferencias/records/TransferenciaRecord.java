package mx.gob.pjpuebla.trials.workflow.transferencias.records;

public record TransferenciaRecord(
        Integer personaEntregaId,
        Integer personaRecibeId,
        Integer juzgadoId,
        String observaciones
) {
}
