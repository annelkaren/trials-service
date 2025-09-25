package mx.gob.pjpuebla.migracion.readers.movimientos;

import java.time.LocalDate;

public record MovimientosMigracionRecord(
        Integer idUbicaciones,
        String cu,
        Integer idPuesto,
        LocalDate fecha,
        String hora,
        String status,
        String estado,
        String etapa,
        String entrego,
        String recibio,
        String puestoEntrego,
        String puestoRecibio,
        String libro,
        Integer numFoja,
        String obse,
        String sentido,
        String digitalizadoAcu,
        String puestoRecibioTBLPuesto) {
}
