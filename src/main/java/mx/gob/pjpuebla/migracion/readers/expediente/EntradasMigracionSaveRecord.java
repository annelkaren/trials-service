package mx.gob.pjpuebla.migracion.readers.expediente;

public record EntradasMigracionSaveRecord(
    String expediente,
    Integer year,
    String juzgado
) {}
