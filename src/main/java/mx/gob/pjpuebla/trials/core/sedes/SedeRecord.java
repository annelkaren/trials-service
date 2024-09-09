package mx.gob.pjpuebla.trials.core.sedes;

import mx.gob.pjpuebla.trials.core.distritos.DistritoRecord;
import mx.gob.pjpuebla.trials.core.domicilios.DomicilioRecord;
import mx.gob.pjpuebla.trials.util.enums.Estado;
import mx.gob.pjpuebla.trials.util.enums.Tipo;

public record SedeRecord(
        Integer id,
        Integer version,
        String nombre,
        Estado estado,
        Tipo tipo,
        String telefono,
        String extension,
        DistritoRecord distrito,
        DomicilioRecord domicilio) {
}
