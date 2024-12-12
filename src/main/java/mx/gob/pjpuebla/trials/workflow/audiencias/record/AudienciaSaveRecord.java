package mx.gob.pjpuebla.trials.workflow.audiencias.record;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;

public record AudienciaSaveRecord(
    Integer carpetaId,
    Integer tipoAudiencia,
    Integer salaId,
    LocalDate fecha,
    LocalTime hora,
    Integer duracion,
    String descripcion

) implements Serializable {}
