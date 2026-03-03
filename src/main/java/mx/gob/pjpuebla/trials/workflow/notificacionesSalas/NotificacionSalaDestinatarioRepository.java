package mx.gob.pjpuebla.trials.workflow.notificacionesSalas;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificacionSalaDestinatarioRepository extends JpaRepository<NotificacionSalaDestinatario, Integer> {
    List<NotificacionSalaDestinatario> findByNotificacionSalaId(Integer notificacionSalaId);

    Optional<NotificacionSalaDestinatario> findFirstByNotificacionSalaIdOrderByIdAsc(Integer notificacionSalaId);

    Integer countByNotificacionSalaId(Integer notificacionSalaId);
}
