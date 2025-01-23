package mx.gob.pjpuebla.trials.litigante;

import java.time.LocalDate;
import java.time.LocalTime;

public record DocumentoResponseRecord(
    String numeroAcuerdo,
    LocalDate fechaCompletado,
    LocalTime horaCompletado,
    String rutaArchivo
) {}
