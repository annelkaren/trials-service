package mx.gob.pjpuebla.trials.workflow.audiencias.record;

import java.io.Serializable;

public record ExtraAudienciaSelloRecord(
        String nombreJuez,
        String nombreSala,
        String nombreTipoJuicio,
        String fechaAudiencia,
        String domicilio
) implements Serializable {
}
