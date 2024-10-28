package mx.gob.pjpuebla.trials.workflow.documentos.documentoscontenido;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;



import java.util.List;
import java.util.Optional;

@Repository
public interface DocumentoContenidoRepository extends JpaRepository<DocumentoContenido, Integer> {
    List<DocumentoContenido> findAllByDocumentoId(Integer documentoId);

    Optional<DocumentoContenido> findByDocumentoId(Integer documentoId);
}
