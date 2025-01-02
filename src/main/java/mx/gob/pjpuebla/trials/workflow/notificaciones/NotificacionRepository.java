package mx.gob.pjpuebla.trials.workflow.notificaciones;

import mx.gob.pjpuebla.trials.util.enums.EstadoNotificacion;
import mx.gob.pjpuebla.trials.util.enums.TipoNotificacion;
import mx.gob.pjpuebla.trials.workflow.notificaciones.records.DocumentoDetalleRecord;
import mx.gob.pjpuebla.trials.workflow.notificaciones.records.ListaExpedientesRutaDTO;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NotificacionRepository extends JpaRepository<Notificacion, Integer> {

    @Query("""
            SELECT c
            FROM Notificacion c
            WHERE c.tipoNotificacion = :tipoNotificacion
            AND (:estadoNotificacion IS NULL OR c.estadoNotificacion = :estadoNotificacion)
            """)
    Page<Notificacion> getNotificacionByTipo(
            @Param("tipoNotificacion") TipoNotificacion tipoNotificacion,
            @Param("estadoNotificacion") EstadoNotificacion estadoNotificacion,
            Pageable pageable
    );

    @Query("""
            SELECT new mx.gob.pjpuebla.trials.workflow.notificaciones.records.DocumentoDetalleRecord(
                dd.fechaResolucion,
                dd.fechaPublicacion
            )
            FROM Notificacion n
            JOIN n.documento d
            JOIN DocumentoDetalle dd ON dd.documento.id = d.id
            WHERE d.id = :documentoId
            """)
    Optional<DocumentoDetalleRecord> findDocumentoDetalleByDocumentoId(@Param("documentoId") Integer documentoId);

    @Query("SELECT COUNT(n) FROM Notificacion n WHERE n.listaEstrado.id = :listaEstradoId")
    long countNotificacionesByListaEstradoId(@Param("listaEstradoId") Integer listaEstradoId);

    @Query("SELECT n FROM Notificacion n WHERE n.listaEstrado.id = :listaEstradoId")
    List<Notificacion> getNotificacionByTipo(@Param("listaEstradoId") Integer listaEstradoId);

   @Query("""
        SELECT 
                c.expediente,
                d.data,
                n.notas
       FROM Notificacion n
       JOIN n.documento d
       JOIN d.carpeta c
       WHERE n.estadoNotificacion = EstadoNotificacion.EN_RUTA AND n.tipoNotificacion = TipoNotificacion.DOMICILIO""")
   List<Object[]> findAllNotificacionesByEstadoEnRutaAndTipoNotificacionDomicilio();

}
