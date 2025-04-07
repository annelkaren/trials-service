package mx.gob.pjpuebla.trials.litigante;

import java.time.LocalDate;
import java.time.LocalTime;

public record DocumentoResponseRecord(
    String id,
    LocalDate fechaResolucion,
    String rubros,
    String rutaArchivo
) {}
