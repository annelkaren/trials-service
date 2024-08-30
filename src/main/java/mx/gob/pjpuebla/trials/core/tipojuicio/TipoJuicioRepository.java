package mx.gob.pjpuebla.trials.core.tipojuicio;

import mx.gob.pjpuebla.trials.util.Estado;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TipoJuicioRepository extends JpaRepository<TipoJuicio, Integer> {
    Optional<TipoJuicio> findByIdAndEstado(Integer integer, Estado estado);
}
