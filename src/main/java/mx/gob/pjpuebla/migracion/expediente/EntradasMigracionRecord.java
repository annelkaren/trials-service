package mx.gob.pjpuebla.migracion.expediente;

import java.util.List;

import mx.gob.pjpuebla.migracion.juicios.JuiciosMigracion;
import mx.gob.pjpuebla.migracion.juzgados.JuzgadosMigracion;
import mx.gob.pjpuebla.migracion.movimientos.MovimientosMigracionRecord;

public record EntradasMigracionRecord(
    EntradasMigracion entrada,
    JuzgadosMigracion juzgado,
    List<MovimientosMigracionRecord> ubicaciones,
    JuiciosMigracion juicios
) {}