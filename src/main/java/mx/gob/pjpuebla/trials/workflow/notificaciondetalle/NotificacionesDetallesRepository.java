package mx.gob.pjpuebla.trials.workflow.notificaciondetalle;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NotificacionesDetallesRepository extends JpaRepository<NotificacionesDetalles, Integer> {
    Optional<NotificacionesDetalles> findByNotificacionId(Integer id);
}
