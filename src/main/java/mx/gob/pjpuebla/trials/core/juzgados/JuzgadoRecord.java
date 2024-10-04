package mx.gob.pjpuebla.trials.core.juzgados;

import com.fasterxml.jackson.annotation.JsonInclude;
import mx.gob.pjpuebla.trials.core.tipojuicio.TipoJuicioRecord;
import mx.gob.pjpuebla.trials.util.enums.Estado;

import java.io.Serializable;
import java.util.List;

/**
 * Objeto usado para mostrar una vista amplia del Juzgado
 *
 * @param id
 * @param version
 * @param nombre
 * @param estado
 * @param materiaId
 * @param sedeId
 * @param maxAsignacionesRonda
 * @param contadorAsignaciones
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record JuzgadoRecord(
        Integer id,
        Integer version,
        String nombre,
        Estado estado,
        Integer materiaId,
        Integer sedeId,
        Integer maxAsignacionesRonda,
        Integer contadorAsignaciones,
        Integer instanciaJuzgado,
        List<TipoJuicioRecord> tipoJuicios
) implements Serializable {
}
