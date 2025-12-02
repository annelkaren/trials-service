package mx.gob.pjpuebla.trials.migracion;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import mx.gob.pjpuebla.migracion.acl.mapper.NotificacionMapper;
import mx.gob.pjpuebla.migracion.readers.notificacionAcuerdo.NotificacionAcuerdoMigracion;
import mx.gob.pjpuebla.migracion.readers.notificacionAcuerdo.NotificacionAcuerdoMigracionRepository;
import mx.gob.pjpuebla.trials.util.enums.TipoNotificacion;
import mx.gob.pjpuebla.trials.workflow.documentos.Documento;
import mx.gob.pjpuebla.trials.workflow.listaestrados.ListaEstrado;
import mx.gob.pjpuebla.trials.workflow.notificaciones.Notificacion;

@Service
@RequiredArgsConstructor
public class NotificacionMigracionService {

    private final NotificacionAcuerdoMigracionRepository notificacionAcuerdoMigracionRepository;
    private final NotificacionMapper notificacionMapper;

    public void crearNotificacionLegacy(Documento documento, Integer claveAcuerdo) {

        List<NotificacionAcuerdoMigracion> notificacionesPhp = notificacionAcuerdoMigracionRepository
                .findByClaveAcuerdoAndStatus(claveAcuerdo, "A");

        for (NotificacionAcuerdoMigracion n : notificacionesPhp) {

            TipoNotificacion tipo = notificacionMapper.mapTipoNotificacion(n.getTipoNotificacion());
            ListaEstrado listaEstrado = null;
            if (tipo.equals(TipoNotificacion.ESTRADO)) {
                // Obtiene la lista de estrados de legacy (php) y la crea en java.
                listaEstrado = createListaEstrados();
            }

            Notificacion notificacionJava = new Notificacion()
                    .setNotas(n.getNotas())
                    .setTipoNotificacion(tipo)
                    .setEstadoNotificacion(null)
                    .setFechaSalida(null)
                    .setFechaNotificado(null)
                    .setDocumento(documento)
                    .setListaEstrado(listaEstrado);
        }
    }

    public ListaEstrado createListaEstrados() {
        return null;
    }

}
