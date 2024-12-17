package mx.gob.pjpuebla.trials.workflow.transferencias;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import mx.gob.pjpuebla.trials.util.enums.EstadoTransferencia;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface TransferenciaRepository extends JpaRepository<Transferencia, Integer> {
    Optional<Transferencia> findByUuuid(UUID uuid);

    Optional<Transferencia> findByPersonaEntregaIdAndEstatus(Integer personaEntregaId, EstadoTransferencia estatus);
}
