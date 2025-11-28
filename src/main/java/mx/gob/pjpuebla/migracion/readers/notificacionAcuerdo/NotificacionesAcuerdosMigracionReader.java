package mx.gob.pjpuebla.migracion.readers.notificacionAcuerdo;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import mx.gob.pjpuebla.migracion.acl.mapper.NotificacionMapper;

@RequiredArgsConstructor
@Service
public class NotificacionesAcuerdosMigracionReader {
    
    private final NotificacionAcuerdoMigracionRepository notificacionAcuerdoMigracionRepository;
    private final  NotificacionMapper notificacionMapper;

    public List<NotificacionAcuerdoMigracionRecord> findByClaveAcuerdoAndStatus(Integer clave, String status) {
        List<NotificacionAcuerdoMigracion> list = notificacionAcuerdoMigracionRepository.findByClaveAcuerdoAndStatus(clave, status);
        return list.stream()
                .map(m -> new NotificacionAcuerdoMigracionRecord(
                       m.getId(),
                       m.getClaveAcuerdo(),
                       notificacionMapper.mapTipoNotificacion(m.getTipoNotificacion()),
                       m.getNotas()
                ))
                .toList();
    }

}
