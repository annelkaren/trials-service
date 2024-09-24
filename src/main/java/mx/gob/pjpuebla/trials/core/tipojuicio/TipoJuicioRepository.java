package mx.gob.pjpuebla.trials.core.tipojuicio;

import mx.gob.pjpuebla.trials.util.enums.Estado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TipoJuicioRepository extends JpaRepository<TipoJuicio, Integer> {
    Optional<TipoJuicio> findByIdAndEstado(Integer integer, Estado estado);
}
