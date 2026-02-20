package mx.gob.pjpuebla.trials.workflow.documentos.records;

public record ExpedienteSinAntecedentesRecord(
    String expediente, 
    Integer year,
    Integer tipoJuicioId
) {}
