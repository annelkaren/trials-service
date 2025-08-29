package mx.gob.pjpuebla.migracion.expediente;

import java.util.List;

import mx.gob.pjpuebla.migracion.actores.ActoresMigracion;
import mx.gob.pjpuebla.migracion.acuerdos.AcuerdosMigracion;
import mx.gob.pjpuebla.migracion.amparos.AmparosMigracion;
import mx.gob.pjpuebla.migracion.detallesProm.DetallesProm;
import mx.gob.pjpuebla.migracion.exhortoCapital.ExhortosCapitalMigracion;
import mx.gob.pjpuebla.migracion.exhortoForaneo.ExhortoForaneoMigracion;
import mx.gob.pjpuebla.migracion.juicios.JuiciosMigracion;
import mx.gob.pjpuebla.migracion.juzgados.JuzgadosMigracion;
import mx.gob.pjpuebla.migracion.movimientos.MovimientosMigracionRecord;
import mx.gob.pjpuebla.migracion.oficios.OficiosMigracion;

public record EntradasMigracionRecord(
    EntradasMigracion entrada,
    JuzgadosMigracion juzgado,
    MovimientosMigracionRecord ubicaciones,
    JuiciosMigracion juicios,
    List<ActoresMigracion> actores
) {}