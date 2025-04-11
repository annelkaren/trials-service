package mx.gob.pjpuebla.trials.core.sedes.records;

import mx.gob.pjpuebla.trials.core.domicilios.DomicilioRecord;
import mx.gob.pjpuebla.trials.util.enums.Estado;

import java.io.Serializable;

public record SedeDomicilioRecordResponse(
        Integer id,
        String nombre,
        Estado estado,
        DomicilioRecord domicilio,
        String telefono
) implements Serializable {
}
