package mx.gob.pjpuebla.trials.core.sedes.records;

import com.fasterxml.jackson.annotation.JsonInclude;
import mx.gob.pjpuebla.trials.core.distritos.DistritoRecord;
import mx.gob.pjpuebla.trials.core.domicilios.DomicilioRecord;
import mx.gob.pjpuebla.trials.util.enums.Estado;
import mx.gob.pjpuebla.trials.util.enums.Tipo;

import java.io.Serializable;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record SedeRecord(
        Integer id,
        Integer version,
        String nombre,
        Estado estado,
        Tipo tipo,
        String telefono,
        String extension,
        DistritoRecord distrito,
        DomicilioRecord domicilio
) implements Serializable {
}
