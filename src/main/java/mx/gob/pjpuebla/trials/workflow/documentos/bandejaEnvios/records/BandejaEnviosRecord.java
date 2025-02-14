package mx.gob.pjpuebla.trials.workflow.documentos.bandejaEnvios.records;

import mx.gob.pjpuebla.trials.util.enums.EstadoCarpeta;

public record BandejaEnviosRecord(
    Integer documentoId,
    String folio,
    String destino,
    String origen, 
    String mensajero,
    EstadoCarpeta estatus
    
) {}
