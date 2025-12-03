package mx.gob.pjpuebla.migracion.readers.ocomun;

public record OcomunResponseRecord(
    Integer id,
    Integer folio,
    String rutaDigitalizacion,
    String anexos

) {}
