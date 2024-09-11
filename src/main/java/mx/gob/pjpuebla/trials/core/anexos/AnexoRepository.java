package mx.gob.pjpuebla.trials.core.anexos;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnexoRepository extends JpaRepository<Anexo, Integer> {

    List<Anexo> findAllByDocumentoId(Integer documentoId);

}
