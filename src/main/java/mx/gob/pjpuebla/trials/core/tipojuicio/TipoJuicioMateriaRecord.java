package mx.gob.pjpuebla.trials.core.tipojuicio;

import java.io.Serializable;

public record TipoJuicioMateriaRecord (
        Integer id,
        String nombre,
        Integer materiaId,
        Integer tipoSistemaId)
        implements Serializable {
}
