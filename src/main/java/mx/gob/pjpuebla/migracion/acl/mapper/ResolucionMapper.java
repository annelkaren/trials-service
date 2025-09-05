package mx.gob.pjpuebla.migracion.acl.mapper;

import org.springframework.stereotype.Component;
import mx.gob.pjpuebla.trials.error.NotFoundException;
import mx.gob.pjpuebla.trials.util.enums.TipoResolucion;

/** Mapea código legacy → {@link TipoResolucion}. */
@Component
public class ResolucionMapper {
    public TipoResolucion mapTipoResolucionSentencia(String tipoResolucion) {
        return switch (tipoResolucion) {
            case "A" -> TipoResolucion.ABSOLUTORIA;
            case "C" -> TipoResolucion.CONDENATORIA;
            case "D" -> TipoResolucion.DECLARATIVA;
            case "I" -> TipoResolucion.IMPROCEDENTE;
            default -> throw new NotFoundException(
                "No se puede determinar el tipo de resolución de una sentencia",
                String.valueOf(tipoResolucion)
            );
        };
    }
}