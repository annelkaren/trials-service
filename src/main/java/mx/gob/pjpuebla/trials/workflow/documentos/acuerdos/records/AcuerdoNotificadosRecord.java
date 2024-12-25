package mx.gob.pjpuebla.trials.workflow.documentos.acuerdos.records;

import mx.gob.pjpuebla.trials.util.enums.TipoNotificacion;

import java.io.Serializable;

public record AcuerdoNotificadosRecord(
    Integer id,
    String nombre,
    String tipo,
    TipoNotificacion metodo
) implements Serializable {}
