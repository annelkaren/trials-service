package mx.gob.pjpuebla.trials.core.oficialias;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OficialiaRepository extends JpaRepository<Oficialia, Integer> {
}