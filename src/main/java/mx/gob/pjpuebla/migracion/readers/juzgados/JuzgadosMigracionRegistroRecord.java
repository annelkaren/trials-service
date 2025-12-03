package mx.gob.pjpuebla.migracion.readers.juzgados;

public record JuzgadosMigracionRegistroRecord(
    Integer id,
    String tablaUbicacion,
    String materiaCodigo,
    String juzgadoCodigo,
    String nombreJuzgado

) {}
