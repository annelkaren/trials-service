package mx.gob.pjpuebla.migracion.expediente;

public record EntradasMigracionSaveRecord(
    String expediente,
    Integer year,
    String juzgado
) {}
