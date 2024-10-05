package mx.gob.pjpuebla.trials.workflow.carpeta;

import mx.gob.pjpuebla.trials.core.juzgados.Juzgado;
import mx.gob.pjpuebla.trials.core.tipojuicio.TipoJuicio;
import mx.gob.pjpuebla.trials.core.tipojuicio.TipoJuicioSetUp;
import mx.gob.pjpuebla.trials.util.enums.EstadoCarpeta;
import mx.gob.pjpuebla.trials.util.enums.SelloEstatus;
import mx.gob.pjpuebla.trials.workflow.carpeta.records.CarpetaResponseRecord;

public class CarpetaSetUp {
    private CarpetaSetUp() {
    }

    public static Carpeta create() {
        return new Carpeta()
                .setId(1)
                .setVersion(1)
                .setFolio("1")
                .setExpediente("000001/2024")
                .setEstatus(EstadoCarpeta.CAPTURA)
                .setTipoJuicio(TipoJuicioSetUp.createTipoJuicio())
                .setSelloEstatus(SelloEstatus.VALIDO);
    }

    public static Carpeta create(TipoJuicio tipoJuicio, Juzgado juzgado) {
        return new Carpeta()
                .setId(1)
                .setVersion(1)
                .setFolio("1")
                .setExpediente("000001/2024")
                .setEstatus(EstadoCarpeta.CAPTURA)
                .setTipoJuicio(tipoJuicio)
                .setJuzgado(juzgado)
                .setSelloEstatus(SelloEstatus.VALIDO);
    }

    public static CarpetaResponseRecord createCarpetaResponseRecord(){
        return new CarpetaResponseRecord(1, "Persona1 Apellido1 Apellido1", "Persona2 Apellido2 Apellido2");
    }
}
