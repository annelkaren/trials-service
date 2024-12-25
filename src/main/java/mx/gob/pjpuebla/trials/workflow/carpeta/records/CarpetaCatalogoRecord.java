package mx.gob.pjpuebla.trials.workflow.carpeta.records;

import java.io.Serializable;

public record CarpetaCatalogoRecord(
        String clave,
        String etiqueta
) implements Serializable {
}
