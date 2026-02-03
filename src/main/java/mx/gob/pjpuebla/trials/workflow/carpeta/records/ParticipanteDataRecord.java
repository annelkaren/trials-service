package mx.gob.pjpuebla.trials.workflow.carpeta.records;


import mx.gob.pjpuebla.trials.util.enums.Rol;
import mx.gob.pjpuebla.trials.workflow.personasdocumentos.PersonaDocumentoDetalleNotificacionRecord;

import java.io.Serializable;

public record ParticipanteDataRecord(
        Integer id,
        String nombre,
        Rol rol,
        PersonaDocumentoDetalleNotificacionRecord notificacionData
) implements Serializable {
}
