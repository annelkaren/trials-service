package mx.gob.pjpuebla.trials.workflow.carpeta;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CarpetaRepository extends JpaRepository<Carpeta, Integer> {

}
