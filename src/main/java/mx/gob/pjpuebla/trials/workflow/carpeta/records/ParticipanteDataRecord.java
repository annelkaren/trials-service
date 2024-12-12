package mx.gob.pjpuebla.trials.workflow.carpeta.records;


import mx.gob.pjpuebla.trials.util.enums.Rol;

import java.io.Serializable;

public record ParticipanteDataRecord(
        Integer id,
        String nombre,
        Rol rol
) implements Serializable {
}
