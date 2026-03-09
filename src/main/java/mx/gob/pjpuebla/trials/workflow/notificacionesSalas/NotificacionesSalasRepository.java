package mx.gob.pjpuebla.trials.workflow.notificacionesSalas;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import mx.gob.pjpuebla.trials.workflow.notificacionesSalas.records.NotificacionesSalasRecord;

@Repository
public interface NotificacionesSalasRepository extends JpaRepository<NotificacionesSalas, Integer> {
    @Query("""
            SELECT new mx.gob.pjpuebla.trials.workflow.notificacionesSalas.records.NotificacionesSalasRecord(
                ns.id,
                ns.toca,
                ns.sala.id,
                ns.sala.nombre,
                ns.fechaEnvio,
                ns.fechaTermino,
                ns.rutaArchivo,
                (SELECT COUNT(ndc)
                 FROM NotificacionSalaDestinatario ndc
                 WHERE ndc.notificacionSala.id = ns.id),
                COALESCE(
                    (
                        SELECT ndp.nombreDestinatario
                        FROM NotificacionSalaDestinatario ndp
                        WHERE ndp.id = (
                            SELECT MIN(ndi.id)
                            FROM NotificacionSalaDestinatario ndi
                            WHERE ndi.notificacionSala.id = ns.id
                        )
                    ),
                    '-'
                )
            )
            FROM NotificacionesSalas ns
            """)
    Page<NotificacionesSalasRecord> findPageNotificaciones(Pageable pageable);

    @Query("""
            SELECT ns
            FROM NotificacionesSalas ns
            WHERE ns.rutaArchivo = :nombreArchivo
            """)
    Optional<NotificacionesSalas> findByRutaArchivo(@Param("nombreArchivo") String nombreArchivo);
}
