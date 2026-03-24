package mx.gob.pjpuebla.trials.workflow.documentos.promocionesSinExpediente.records;

public record PromocionSinExpedienteFiltrosRecord(
    String folio, 
    String expediente,
    String juzgado, 
    String tipoJuicio,
    String estatus, 
    String key
) {}
