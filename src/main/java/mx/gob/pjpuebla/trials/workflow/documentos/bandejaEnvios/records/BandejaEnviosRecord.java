package mx.gob.pjpuebla.trials.workflow.documentos.bandejaEnvios.records;

import mx.gob.pjpuebla.trials.util.enums.EstadoCarpeta;

public record BandejaEnviosRecord(
    Integer documentoId,
    String salida,
    String recepcion,
    String origen, 
    String mensajero,
    EstadoCarpeta estatus, 
    String oficioFolio
) {}
