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
        String puestoRecibioTBLPuesto,
        String tipoPieza) {

        public MovimientosMigracionRecord(Integer idUbicaciones, String cu, LocalDate fecha, String status, String estado, String entrego, String recibio, String puestoEntrego, String puestoRecibio, String puestoRecibioTBLPuesto) {
                this(idUbicaciones, cu, fecha, status, estado, entrego, recibio, puestoEntrego, puestoRecibio, puestoRecibioTBLPuesto, null);
        }
}
