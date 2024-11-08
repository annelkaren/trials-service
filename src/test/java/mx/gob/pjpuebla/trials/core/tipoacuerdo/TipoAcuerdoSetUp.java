package mx.gob.pjpuebla.trials.core.tipoacuerdo;

import mx.gob.pjpuebla.trials.core.materias.MateriaSetUp;
import mx.gob.pjpuebla.trials.core.tiposistema.TipoSistemaSetUp;

public class TipoAcuerdoSetUp {

    private TipoAcuerdoSetUp() {
    }

    public static TipoAcuerdo createTipoAcuerdo() {
        return new TipoAcuerdo()
                .setId(1)
                .setNombre("ACUERDO")
                .setMateria(MateriaSetUp.createMateria())
                .setTipoSistema(TipoSistemaSetUp.createTipoSistema());

    }

    public static TipoAcuerdoRecord createTipoAcuerdoRecord() {
        return new TipoAcuerdoRecord(1, "ACLARACION DE SENTENCIA");
    }
}
