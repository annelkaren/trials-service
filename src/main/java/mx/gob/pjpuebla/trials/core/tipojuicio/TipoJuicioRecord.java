package mx.gob.pjpuebla.trials.core.tipojuicio;

import com.fasterxml.jackson.annotation.JsonInclude;
import mx.gob.pjpuebla.trials.core.materias.MateriaRecord;
import mx.gob.pjpuebla.trials.core.tiposistema.TipoSistemaRecord;

import java.io.Serializable;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record TipoJuicioRecord(
        Integer id,
        String nombre,
        TipoSistemaRecord tipoSistemaRecord,
        MateriaRecord materiaRecord
) implements Serializable {
}
