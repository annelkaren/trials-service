package mx.gob.pjpuebla.trials.workflow.emailLogs;

import mx.gob.pjpuebla.trials.util.enums.EstadoEnvioCorreo;
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
                ORDER BY COALESCE(e.proximaVerificacion, e.ultimaVerificacion, e.fechaEnvio) ASC, e.id ASC
            """)
    List<EmailLogs> findBatchToVerify(
            @Param("estados") List<EstadoEnvioCorreo> estados,
            @Param("now") LocalDateTime now);

}