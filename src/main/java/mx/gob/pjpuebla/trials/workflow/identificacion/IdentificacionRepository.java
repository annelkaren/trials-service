package mx.gob.pjpuebla.trials.workflow.identificacion;

import mx.gob.pjpuebla.trials.workflow.folios.SecuenciaRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IdentificacionRepository extends JpaRepository<Identificacion, Integer>, SecuenciaRepositoryCustom {
}
