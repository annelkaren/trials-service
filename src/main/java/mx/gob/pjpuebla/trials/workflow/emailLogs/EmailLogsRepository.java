package mx.gob.pjpuebla.trials.workflow.emailLogs;

import mx.gob.pjpuebla.trials.util.enums.EstadoEnvioCorreo;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface EmailLogsRepository extends JpaRepository<EmailLogs, Integer> {

    Optional<EmailLogs> findByProviderMessageId(String providerMessageId);

    List<EmailLogs> findTop200ByEstadoInOrderByIdAsc(List<EstadoEnvioCorreo> estados);

    List<EmailLogs> findTop200ByEstadoInOrderByUltimaVerificacionAsc(List<EstadoEnvioCorreo> estados);

    @Query("""
                SELECT e
                FROM EmailLogs e
                WHERE e.estado IN :estados
                  AND (e.proximaVerificacion IS NULL OR e.proximaVerificacion <= :now)
                  AND (
                        e.estado <> mx.gob.pjpuebla.trials.util.enums.EstadoEnvioCorreo.LEIDO
                        OR e.trackingLinkDetalle IS NULL
                        OR FUNCTION('jsonb_path_exists', e.trackingLinkDetalle, '$.link[0]') = false
                  )
                ORDER BY COALESCE(e.ultimaVerificacion, e.fechaEnvio, :now) ASC, e.id ASC
            """)
    List<EmailLogs> findBatchToVerify(
            @Param("estados") List<EstadoEnvioCorreo> estados,
            @Param("now") LocalDateTime now,
            Pageable pageable);

}
