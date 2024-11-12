package mx.gob.pjpuebla.trials.core.tipoaudiencia;

import mx.gob.pjpuebla.trials.core.materias.MateriaSetUp;
import mx.gob.pjpuebla.trials.core.tiposistema.TipoSistemaSetUp;
import mx.gob.pjpuebla.trials.util.enums.Estado;

public class TipoAudienciaSetUp {

    private TipoAudienciaSetUp() {

    }

    public static TipoAudiencia createTipoAudencia() {
        return new TipoAudiencia()
                .setId(1)
                .setVersion(0)
                .setNombre("Aprobación de Convenio")
                .setEstado(Estado.ACTIVE)
                .setMateria(MateriaSetUp.createMateria())
                .setTipoSistema(TipoSistemaSetUp.createTipoSistema());
    }

    public static TipoAudienciaRecord createTipoAudienciaRecord() {
        return new TipoAudienciaRecord(49, "Conciliación");
    }
}
