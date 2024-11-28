package mx.gob.pjpuebla.trials.core.desahogoaudiencia;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DesahogoAudienciaRepository extends JpaRepository<DesahogoAudiencia, Integer> {
}
