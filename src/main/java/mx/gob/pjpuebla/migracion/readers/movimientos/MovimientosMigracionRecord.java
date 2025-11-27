package mx.gob.pjpuebla.migracion.readers.movimientos;

import java.time.LocalDate;

public record MovimientosMigracionRecord(
        Integer idUbicaciones,
        String cu,
        LocalDate fecha,
        String status,
        String estado,
        String entrego,
        String recibio,
        String puestoEntrego,
        String puestoRecibio,
        String puestoRecibioTBLPuesto) {
}
