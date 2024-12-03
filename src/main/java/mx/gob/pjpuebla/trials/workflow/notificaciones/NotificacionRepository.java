package mx.gob.pjpuebla.trials.workflow.notificaciones;

import mx.gob.pjpuebla.trials.util.enums.TipoNotificacion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificacionRepository extends JpaRepository<Notificacion, Integer> {

    @Query("""
            SELECT c
            FROM Notificacion c
            WHERE c.tipoNotificacion = :tipoNotificacion
            """)
    Page<Notificacion> getNotificacionByTipo(@Param("tipoNotificacion") TipoNotificacion tipoNotificacion, Pageable pageable);
}
