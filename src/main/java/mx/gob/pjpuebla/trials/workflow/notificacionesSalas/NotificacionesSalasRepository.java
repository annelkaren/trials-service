package mx.gob.pjpuebla.trials.workflow.notificacionesSalas;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import mx.gob.pjpuebla.trials.workflow.notificacionesSalas.records.NotificacionesSalasRecord;

import org.springframework.data.jpa.repository.Query;

@Repository
public interface NotificacionesSalasRepository extends JpaRepository<NotificacionesSalas, Integer> {

    //TODO: obtener fecha lectura y fecha entrega de log de correo.
    @Query("""
            SELECT new mx.gob.pjpuebla.trials.workflow.notificacionesSalas.records.NotificacionesSalasRecord(
                ns.id,
                ns.expediente,
                ns.tipoSala,
                ns.nombreDestinatario,
                ns.correoDestinatario,
                ns.fechaTermino,
                ns.rutaArchivo,
                ns.rutaArchivo,
                ns.fechaEnvio,
                null,
                null
            )
            FROM NotificacionesSalas ns
            """)
    Page<NotificacionesSalasRecord> getPageNotificaciones(Pageable pageable);

}
