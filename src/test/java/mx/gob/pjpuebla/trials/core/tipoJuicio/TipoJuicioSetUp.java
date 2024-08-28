package mx.gob.pjpuebla.trials.core.tipoJuicio;

import mx.gob.pjpuebla.trials.util.Audit;
import mx.gob.pjpuebla.trials.util.Estado;

import java.time.LocalDateTime;

public class TipoJuicioSetUp {

    private TipoJuicioSetUp() {
    }

    public static TipoJuicio createMateria() {
        TipoJuicio tipoJuicio = new TipoJuicio()
                .setId(1)
                .setNombre("Laboral")
                .setEstado(Estado.ACTIVE)
                .setVersion(0);
        tipoJuicio.setAudit(
                new Audit(
                        LocalDateTime.now(),
                        LocalDateTime.now(),
                        "6b13785f-d213-4585-a76b-437ffe57c9c7",
                        "6b13785f-d213-4585-a76b-437ffe57c9c7")
        );
        return tipoJuicio;
    }

    public static TipoJuicioRecord createTipoJuicioRecord() {
        return new TipoJuicioRecord(1, "Laboral");
    }

}
