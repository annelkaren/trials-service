package mx.gob.pjpuebla.trials.core.documentoidentificacion;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DocumentoIdentificacionRepository extends JpaRepository<DocumentoIdentificacion, Integer> {

}
