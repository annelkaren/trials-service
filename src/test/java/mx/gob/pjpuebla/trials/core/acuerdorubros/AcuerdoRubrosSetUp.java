package mx.gob.pjpuebla.trials.core.acuerdorubros;

import mx.gob.pjpuebla.trials.core.materias.MateriaSetUp;
import mx.gob.pjpuebla.trials.core.tiposistema.TipoSistemaSetUp;

public class AcuerdoRubrosSetUp {

    private AcuerdoRubrosSetUp() {

    }

    public static AcuerdoRubros createAcuerdoRubro() {
        return new AcuerdoRubros()
                .setId(1)
                .setNombre("Generico")
                .setMateria(MateriaSetUp.createMateria())
                .setTipoSistema(TipoSistemaSetUp.createTipoSistema());
    }

    public static AcuerdoRubrosRecord createAcuerdoRubrosRecord() {
        return new AcuerdoRubrosRecord(100, "ACEPTACION DE CARGO");
    }
}
