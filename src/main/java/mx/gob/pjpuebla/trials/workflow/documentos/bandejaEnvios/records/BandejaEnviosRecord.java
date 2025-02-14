package mx.gob.pjpuebla.trials.workflow.documentos.bandejaEnvios.records;

public record BandejaEnviosRecord(
    Integer documentoId,
    String folio,
    String destino,
    String origen, 
    String mensajero,
    String estatus
    
) {}
