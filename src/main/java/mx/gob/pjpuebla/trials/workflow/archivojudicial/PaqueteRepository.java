package mx.gob.pjpuebla.trials.workflow.archivojudicial;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PaqueteRepository extends JpaRepository<Paquete, Integer> {

    @Query("SELECT MAX(p.paqueteId) FROM Paquete p")
    Integer findMaxPaqueteId();
}
