package mx.gob.pjpuebla.trials.workflow.notificaciones;


import lombok.RequiredArgsConstructor;
import mx.gob.pjpuebla.trials.util.enums.TipoNotificacion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


import java.util.List;

@RequiredArgsConstructor
@Service
public class NotificacionService {

    private final NotificacionRepository notificacionRepository;

    public Page<NotificacionRecord> getAllNotificaciones(String key, Pageable pageable) {
        key = (key != null) ? key.toLowerCase() : "";

        TipoNotificacion tipoNotificacion = TipoNotificacion.ESTRADO;
        if (!key.isEmpty()) {
            try {
                tipoNotificacion = TipoNotificacion.valueOf(key.toUpperCase());
            } catch (IllegalArgumentException e) {
                return Page.empty(pageable);
            }
        }
        Page<Notificacion> page = notificacionRepository.getNotificacionByTipo(tipoNotificacion, pageable);
        List<NotificacionRecord> list = page.getContent().stream()
                .map(item -> new NotificacionRecord(
                        item.getCarpeta().getExpediente(),
                        item.getConcepto(),
                        item.getNotas(),
                        item.getTipoNotificacion(),
                        item.getFechaPublicacion(),
                        item.getFechaResolucion()
                ))
                .toList();

        return new PageImpl<>(list, pageable, page.getTotalElements());
    }


}
