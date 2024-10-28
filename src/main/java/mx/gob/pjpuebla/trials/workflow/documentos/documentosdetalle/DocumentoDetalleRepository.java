package mx.gob.pjpuebla.trials.workflow.documentos.documentosdetalle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.List;

@Repository
public interface DocumentoDetalleRepository extends JpaRepository<DocumentoDetalle, Integer> {
    List<DocumentoDetalle> findAllByDocumentoId(Integer documentoId);

    Optional<DocumentoDetalle> findByDocumentoId(Integer documentoId);
}
