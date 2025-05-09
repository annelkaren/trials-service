package mx.gob.pjpuebla.trials.workflow.documentos.bandejaEnvios.records;

import java.util.List;

import mx.gob.pjpuebla.trials.util.enums.EstadoEnvio;

public record BandejaEnviosCambioEstatus(
    List<Integer> oficiosIds,
    EstadoEnvio estadoEnvio,
    Integer mensajero
) {}
