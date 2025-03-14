package mx.gob.pjpuebla.trials.workflow.documentos.records;

import java.time.LocalDateTime;

import mx.gob.pjpuebla.trials.util.enums.Prioridad;

public record DocumentoBandejaDevueltos(
    Integer carpetaId,
    String folio,
    String expediente,
    String tipoEntrada,
    String origen,
    String concepto,
    String motivoDevolucion,
    LocalDateTime fechaHoraEnvio,
    Boolean isInterno,
    Prioridad prioridad,
    Integer horas
) {}
