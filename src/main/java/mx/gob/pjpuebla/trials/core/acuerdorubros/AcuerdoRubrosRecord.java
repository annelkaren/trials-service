package mx.gob.pjpuebla.trials.core.acuerdorubros;

import mx.gob.pjpuebla.trials.core.materias.MateriaRecord;
import mx.gob.pjpuebla.trials.core.tiposistema.TipoSistemaRecord;

public record AcuerdoRubrosRecord(
        Integer id,
        String nombre,
        MateriaRecord materia,
        TipoSistemaRecord tipoSistema
) {
}
