package mx.gob.pjpuebla.migracion.readers.entradas;

import java.util.List;

import mx.gob.pjpuebla.migracion.readers.actores.ActoresMigracion;
import mx.gob.pjpuebla.migracion.readers.detallesProm.DetallesProm;
import mx.gob.pjpuebla.migracion.readers.juicios.JuiciosMigracion;
import mx.gob.pjpuebla.migracion.readers.juzgados.JuzgadosMigracion;
import mx.gob.pjpuebla.migracion.readers.movimientos.MovimientosMigracionRecord;

public record EntradasMigracionRecord(
    EntradasMigracion entrada,
    JuzgadosMigracion juzgado,
    MovimientosMigracionRecord ubicaciones,
    JuiciosMigracion juicios,
    List<ActoresMigracion> actores,
    List<DetallesProm> promociones
) {}