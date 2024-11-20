package mx.gob.pjpuebla.trials.core.procedimientos;


import mx.gob.pjpuebla.trials.util.enums.Estado;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProcedimientoRepository extends JpaRepository<Procedimiento, Integer> {
    List<Procedimiento> findByTipoJuicioIdAndEstado(Integer id, Estado estado);
}
