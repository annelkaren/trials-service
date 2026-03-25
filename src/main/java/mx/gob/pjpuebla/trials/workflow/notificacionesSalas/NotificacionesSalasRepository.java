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
      WHERE
          (
            COALESCE(:q, '') = '' OR
            LOWER(ns.toca) LIKE LOWER(CONCAT('%', COALESCE(:q,''), '%')) OR
            LOWER(ns.sala.nombre) LIKE LOWER(CONCAT('%', COALESCE(:q,''), '%')) OR
            EXISTS (
                SELECT 1 FROM NotificacionSalaDestinatario ndq
                WHERE ndq.notificacionSala.id = ns.id
                AND (LOWER(ndq.nombreDestinatario) LIKE LOWER(CONCAT('%', COALESCE(:q,''), '%'))
                     OR LOWER(ndq.correoElectronico) LIKE LOWER(CONCAT('%', COALESCE(:q,''), '%')))
            )
          )
          AND (
            COALESCE(:numeroExpediente, '') = '' OR
            LOWER(ns.toca) LIKE LOWER(CONCAT('%', COALESCE(:numeroExpediente,''), '%'))
          )
          AND (
            :salaId IS NULL OR ns.sala.id = :salaId
          )
          AND (
            COALESCE(:nombreDestinatario, '') = '' OR
            EXISTS (
                SELECT 1 FROM NotificacionSalaDestinatario ndn
                WHERE ndn.notificacionSala.id = ns.id
                AND LOWER(ndn.nombreDestinatario) LIKE LOWER(CONCAT('%', COALESCE(:nombreDestinatario,''), '%'))
            )
          )
          AND (
            COALESCE(:correoElectronico, '') = '' OR
            EXISTS (
                SELECT 1 FROM NotificacionSalaDestinatario ndc
                WHERE ndc.notificacionSala.id = ns.id
                AND LOWER(ndc.correoElectronico) LIKE LOWER(CONCAT('%', COALESCE(:correoElectronico,''), '%'))
            )
          )
          AND (cast(:fechaEnvioFrom as timestamp) IS NULL OR ns.fechaEnvio >= :fechaEnvioFrom)
          AND (cast(:fechaEnvioTo as timestamp) IS NULL OR ns.fechaEnvio <= :fechaEnvioTo)
          AND (cast(:fechaTerminoFrom as timestamp) IS NULL OR ns.fechaTermino >= :fechaTerminoFrom)
          AND (cast(:fechaTerminoTo as timestamp) IS NULL OR ns.fechaTermino <= :fechaTerminoTo)
          AND ns.audit.usuarioAlta = :usuario
      """)
  Page<NotificacionesSalasRecord> findPageNotificaciones(
      Pageable pageable,
      @Param("q") String q,
      @Param("numeroExpediente") String numeroExpediente,
      @Param("nombreDestinatario") String nombreDestinatario,
      @Param("correoElectronico") String correoElectronico,
      @Param("fechaEnvioFrom") java.time.LocalDateTime fechaEnvioFrom,
      @Param("fechaEnvioTo") java.time.LocalDateTime fechaEnvioTo,
      @Param("fechaTerminoFrom") java.time.LocalDateTime fechaTerminoFrom,
      @Param("fechaTerminoTo") java.time.LocalDateTime fechaTerminoTo,
      @Param("salaId") Integer salaId,
      @Param("usuario") String usuario);

  @Query("""
      SELECT ns
      FROM NotificacionesSalas ns
      WHERE ns.rutaArchivo = :nombreArchivo
      """)
  Optional<NotificacionesSalas> findByRutaArchivo(@Param("nombreArchivo") String nombreArchivo);
}
