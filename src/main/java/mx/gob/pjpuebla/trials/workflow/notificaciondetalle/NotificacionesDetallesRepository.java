package mx.gob.pjpuebla.trials.workflow.notificaciondetalle;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificacionesDetallesRepository extends JpaRepository<NotificacionesDetalles, Integer> {
    
}
