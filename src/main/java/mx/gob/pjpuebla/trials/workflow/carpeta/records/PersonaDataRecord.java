package mx.gob.pjpuebla.trials.workflow.carpeta.records;

import mx.gob.pjpuebla.trials.util.enums.Rol;
import mx.gob.pjpuebla.trials.workflow.personasdocumentos.PersonaDocumentoDetalleNotificacionRecord;

import java.io.Serializable;

public record PersonaDataRecord(
        Integer id,
        String nombre,
        String apellidoPaterno,
        String apellidoMaterno,
        String tipoPartesNombre,
        Rol rol,
        String pseudonimo,
        PersonaDocumentoDetalleNotificacionRecord notificacionData
) implements Serializable {
}
