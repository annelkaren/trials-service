package mx.gob.pjpuebla.trials.core.tipoprueba;

public record TipoPruebasRecord(
    Integer id,
    Integer tipoJuicioId,
    String nombre,
    String dato,
    Integer pruebaPadreId
) {}

