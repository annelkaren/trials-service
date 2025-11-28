package mx.gob.pjpuebla.migracion.readers.notificacionAcuerdo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificacionAcuerdoMigracionRepository extends JpaRepository<NotificacionAcuerdoMigracion, Integer> {
    

    public List<NotificacionAcuerdoMigracion> findByClaveAcuerdoAndStatus(Integer clave, String status);

}
