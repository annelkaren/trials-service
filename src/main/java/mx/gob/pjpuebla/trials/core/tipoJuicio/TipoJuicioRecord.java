package mx.gob.pjpuebla.trials.core.tipoJuicio;

import mx.gob.pjpuebla.trials.core.materias.Materia;
import mx.gob.pjpuebla.trials.core.tiposistema.TipoSistema;

public record TipoJuicioRecord(Integer id, String nombre,
                               TipoSistema tipoSistema,
                               Materia materia) {
}
