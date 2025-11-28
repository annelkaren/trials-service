package mx.gob.pjpuebla.trials.migracion;

import java.util.ArrayList;
import java.util.List;

import mx.gob.pjpuebla.trials.util.enums.TipoNotificacion;
import mx.gob.pjpuebla.trials.workflow.documentos.Documento;
import mx.gob.pjpuebla.trials.workflow.notificaciondetalle.NotificacionesDetalles;
import mx.gob.pjpuebla.trials.workflow.notificaciones.Notificacion;
import mx.gob.pjpuebla.trials.workflow.personasdocumentos.PersonaDocumento;

public class NotificacionesMigracionService {
    

    public Notificacion registrarNotificacion(Documento documento, List<PersonaDocumento> personasDocumentos){

        List<NotificacionesDetalles> detalles = new ArrayList<>();

        personasDocumentos
        .stream()
        .forEach(p -> {
            TipoNotificacion notificacionSeleccionada = p.getTipoNotificacion();
            
        });
        
        return null;
    }


}
