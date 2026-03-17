package mx.gob.pjpuebla.trials.workflow.notificacionesSalas;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import mx.gob.pjpuebla.trials.workflow.emailLogs.EmailLogs;
import mx.gob.pjpuebla.trials.workflow.notificacionesSalas.records.NotificacionSalaDestinatarioRecord;

@Repository
public interface NotificacionSalaDestinatarioRepository extends JpaRepository<NotificacionSalaDestinatario, Integer> {
    List<NotificacionSalaDestinatario> findByNotificacionSalaId(Integer notificacionSalaId);

    Optional<NotificacionSalaDestinatario> findFirstByNotificacionSalaIdOrderByIdAsc(Integer notificacionSalaId);

    Integer countByNotificacionSalaId(Integer notificacionSalaId);

    @Query("""
            SELECT new mx.gob.pjpuebla.trials.workflow.notificacionesSalas.records.NotificacionSalaDestinatarioRecord(
                notificacionDestinatario.id,
                notificacionDestinatario.nombreDestinatario,
                notificacionDestinatario.correoElectronico,
                notificacionDestinatario.tipoParte,
                notificacionDestinatario.estado,
                email.estado,
                email.fechaEntrega,
                email.fechaLectura,
                email.fechaDescargaVinculo
            )
            FROM NotificacionSalaDestinatario notificacionDestinatario
            JOIN notificacionDestinatario.emailLog email
            WHERE notificacionDestinatario.notificacionSala.id = :notificacionSalaId
            """)
    List<NotificacionSalaDestinatarioRecord> findSalaDestinatarioRecord(Integer notificacionSalaId);

    Optional<NotificacionSalaDestinatario> findByEmailLog(EmailLogs emailLog);

}
