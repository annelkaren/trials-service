package mx.gob.pjpuebla.trials.workflow.tipojuicioetiquetas;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EtiquetaRepository extends JpaRepository<Etiqueta, Integer> {

    List<Etiqueta> findByTipoJuicioId(Integer id);

    Etiqueta findByTipoJuicioIdAndNombre(Integer tipoJuicioId, String nombre);
}
