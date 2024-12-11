package mx.gob.pjpuebla.trials.workflow.documentos.Acuerdos.records;

import mx.gob.pjpuebla.trials.util.enums.TipoNotificacion;

public record AcuerdoNotificadosRecord(
    Integer id,
    String nombre,
    String tipo,
    TipoNotificacion metodo
) {}
