package mx.gob.pjpuebla.trials.core.tipojuicio;

import mx.gob.pjpuebla.trials.core.materias.Materia;
import mx.gob.pjpuebla.trials.core.materias.MateriaRecord;
import mx.gob.pjpuebla.trials.core.tiposistema.TipoSistema;
import mx.gob.pjpuebla.trials.core.materias.MateriaSetUp;
import mx.gob.pjpuebla.trials.core.tiposistema.TipoSistema;
import mx.gob.pjpuebla.trials.core.tiposistema.TipoSistemaRecord;
import mx.gob.pjpuebla.trials.core.tiposistema.TipoSistemaSetUp;
import mx.gob.pjpuebla.trials.util.Audit;
import mx.gob.pjpuebla.trials.util.enums.Estado;

import java.time.LocalDateTime;


public class TipoJuicioSetUp {

    private TipoJuicioSetUp() {
    }

    public static TipoJuicio createTipoJuicio() {
        TipoJuicio tipoJuicio = new TipoJuicio()
                .setId(1)
                .setNombre("Laboral")
                .setEstado(Estado.ACTIVE)
                .setVersion(0)
                .setTipoSistema(TipoSistemaSetUp.createTipoSistema())
                .setMateria(MateriaSetUp.createMateria());
        tipoJuicio.setAudit(
                new Audit(
                        LocalDateTime.now(),
                        LocalDateTime.now(),
                        "6b13785f-d213-4585-a76b-437ffe57c9c7",
                        "6b13785f-d213-4585-a76b-437ffe57c9c7")
        );
        return tipoJuicio;
    }

    public static TipoJuicio createTipoJuicio(TipoSistema tipoSistema, Materia materia) {
        TipoJuicio tipoJuicio = new TipoJuicio()
                .setId(1)
                .setNombre("Laboral")
                .setEstado(Estado.ACTIVE)
                .setVersion(0)
                .setTipoSistema(tipoSistema)
                .setMateria(materia);
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
        return new TipoJuicioRecord(1, "Laboral", createTipoSistemaRecord(), createMateriaRecord());
    }

    public static TipoSistemaRecord createTipoSistemaRecord() {
        return new TipoSistemaRecord(1, "Tradicional");
    }

    public static MateriaRecord createMateriaRecord() {
        return new MateriaRecord(1, "PENAL");
    }


}
