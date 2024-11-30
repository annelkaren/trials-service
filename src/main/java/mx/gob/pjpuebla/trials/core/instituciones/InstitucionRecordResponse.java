package mx.gob.pjpuebla.trials.core.instituciones;

import com.fasterxml.jackson.annotation.JsonInclude;
import mx.gob.pjpuebla.trials.core.distritos.DistritoRecord;
import mx.gob.pjpuebla.trials.core.domicilios.DomicilioRecord;
import mx.gob.pjpuebla.trials.util.enums.Estado;

import java.io.Serializable;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record InstitucionRecordResponse(
        Integer id,
        Integer version,
        String nombre,
        Estado estado,
        String telefono,
        String extension,
        String tipoInstitucion,
        DomicilioRecord domicilio
) implements Serializable {
}
