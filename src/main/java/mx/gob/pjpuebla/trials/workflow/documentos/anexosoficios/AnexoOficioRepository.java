package mx.gob.pjpuebla.trials.workflow.documentos.anexosoficios;

import mx.gob.pjpuebla.trials.util.enums.Estado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AnexoOficioRepository extends JpaRepository<AnexoOficio, Integer> {
    List<AnexoOficio> findAllByDocumentoIdAndEstadoOrderByIdAsc(Integer documentoId, Estado estado);
    Optional<AnexoOficio> findByIdAndEstado(Integer id, Estado estado);
}
