package mx.gob.pjpuebla.trials.core.documentoidentificacion;

import mx.gob.pjpuebla.trials.workflow.folios.SecuenciaRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DocumentoIdentificacionRepository extends JpaRepository<DocumentoIdentificacion, Integer>, SecuenciaRepositoryCustom {

}
