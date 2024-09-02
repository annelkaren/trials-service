package mx.gob.pjpuebla.trials.core.tipojuicio;

import mx.gob.pjpuebla.trials.core.materias.MateriaRecord;
import mx.gob.pjpuebla.trials.core.tiposistema.TipoSistemaRecord;

public record TipoJuicioRecord(Integer id, String nombre,
                               TipoSistemaRecord tipoSistemaRecord,
                               MateriaRecord materiaRecord) {
}
