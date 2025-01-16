package mx.gob.pjpuebla.trials.workflow.notificaciondetalle;

import mx.gob.pjpuebla.trials.util.enums.TipoNotificacion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import mx.gob.pjpuebla.trials.workflow.documentos.Documento;
import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface NotificacionesDetallesRepository extends JpaRepository<NotificacionesDetalles, Integer> {
    Optional<NotificacionesDetalles> findByNotificacionId(Integer id);

    @Query("""
            SELECT nd
            FROM NotificacionesDetalles nd
            JOIN nd.notificacion n
            JOIN n.documento d
            WHERE d.id = :documentoId
            """)
    Page<NotificacionesDetalles> findByNotificacionDocumentoId(@Param("documentoId") Integer documentoId, Pageable pageable);

    @Query("""
            SELECT count(nd)
            FROM NotificacionesDetalles nd
            JOIN nd.notificacion n
            JOIN n.documento d
            JOIN d.carpeta c
            JOIN nd.personaDocumento pd
            WHERE c.id = :carpetaId AND (pd.correoNotificacion = :email OR pd.correoElectronico = :email)
            AND n.estadoNotificacion = mx.gob.pjpuebla.trials.util.enums.EstadoNotificacion.POR_LEER
            """)
    Long countNotificacionesPorLeer(Integer carpetaId, String email);

    @Query("""
            SELECT nd
            FROM NotificacionesDetalles nd
            JOIN nd.notificacion n
            JOIN nd.personaDocumento pd
            JOIN n.documento d
            WHERE (LOWER(pd.correoElectronico) = LOWER(:username)
                OR LOWER(pd.correoNotificacion) = LOWER(:username))
            AND pd.tipoNotificacion = :tipoNotificacion
            AND (d.tipoDocumento = mx.gob.pjpuebla.trials.util.enums.TipoDocumento.ACUERDO
                OR d.tipoDocumento = mx.gob.pjpuebla.trials.util.enums.TipoDocumento.SENTENCIA)
            """)
    List<NotificacionesDetalles> getAllByUsername(
            @Param("username") String username, @Param("tipoNotificacion") TipoNotificacion tipoNotificacion
    );

    @Query("""
        SELECT nd.fechaConsulta
        FROM NotificacionesDetalles nd
        JOIN nd.notificacion n
        WHERE n.documento = :documentoId
        """)
    LocalDateTime findFechaYHoraByDocumentoId(Documento documentoId);
}
