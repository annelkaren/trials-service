package mx.gob.pjpuebla.trials.workflow.notificaciones.records;

import java.util.List;

import mx.gob.pjpuebla.trials.util.enums.EstadoNotificacion;

public record NotificacionSaveRecord(
    String notas,
    EstadoNotificacion estado,
    Integer documentoId,
    List<Integer> personasDocumentosId
) {}
