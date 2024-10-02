package mx.gob.pjpuebla.trials.workflow.tipojuicioetiquetas;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TipoJuicioEtiquetaRepository extends JpaRepository<TipoJuicioEtiqueta, Integer> {

    List<TipoJuicioEtiqueta> findByTipoJuicioId(Integer id);
}
