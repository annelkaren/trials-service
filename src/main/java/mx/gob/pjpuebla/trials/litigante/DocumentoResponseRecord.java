package mx.gob.pjpuebla.trials.litigante;

import java.time.LocalDate;
public record DocumentoResponseRecord(
    String id,
    LocalDate fechaResolucion,
    String rubros,
    String rutaArchivo
) {}
