package mx.gob.pjpuebla.migracion.readers.entradas;

public record EntradasMigracionSaveRecord(
    String expediente,
    Integer year,
    String juzgado
) {}
