package mx.gob.pjpuebla.trials.workflow.transferencias;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface TransferenciaRepository extends JpaRepository<Transferencia, Integer> {
    Optional<Transferencia> findByUuuid(UUID uuid);
}
