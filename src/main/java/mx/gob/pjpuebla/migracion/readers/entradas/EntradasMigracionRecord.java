package mx.gob.pjpuebla.migracion.readers.entradas;

import java.util.List;

import mx.gob.pjpuebla.migracion.readers.actores.ActoresMigracion;
import mx.gob.pjpuebla.migracion.readers.detallesProm.DetallesProm;
import mx.gob.pjpuebla.migracion.readers.juicios.JuicioResponseRecord;
import mx.gob.pjpuebla.migracion.readers.juzgados.JuzgadosMigracionRegistroRecord;
import mx.gob.pjpuebla.migracion.readers.movimientos.MovimientosMigracionRecord;
import mx.gob.pjpuebla.trials.util.enums.EstadoMigracion;

public record EntradasMigracionRecord(
    EntradasMigracion entrada,
    JuzgadosMigracionRegistroRecord juzgado,
    MovimientosMigracionRecord ubicaciones,
    JuicioResponseRecord juicios,
    List<ActoresMigracion> actores,
    List<DetallesProm> promociones,
    EstadoMigracion migrado
) {}