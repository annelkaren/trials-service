package mx.gob.pjpuebla.trials.core.rubros;

import mx.gob.pjpuebla.trials.util.enums.Estado;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RubroRepository extends JpaRepository<Rubro, Integer > {
    List<Rubro> findByProcedimientoIdAndEstado(Integer id, Estado estado);
}
