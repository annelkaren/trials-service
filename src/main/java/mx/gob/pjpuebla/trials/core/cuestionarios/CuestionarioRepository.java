package mx.gob.pjpuebla.trials.core.cuestionarios;

import mx.gob.pjpuebla.trials.util.enums.ListCuestionario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CuestionarioRepository extends JpaRepository<Cuestionario, Integer> {
    List<Cuestionario> findByLista(ListCuestionario lista);
}
