package mx.gob.pjpuebla.trials.workflow.documentos;

import mx.gob.pjpuebla.trials.workflow.folios.SecuenciaRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DocumentoRepository extends JpaRepository<Documento, Integer>, SecuenciaRepositoryCustom {
}
