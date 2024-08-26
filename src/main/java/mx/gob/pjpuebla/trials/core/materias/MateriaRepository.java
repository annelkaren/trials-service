package mx.gob.pjpuebla.trials.core.materias;

import mx.gob.pjpuebla.trials.util.Estado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MateriaRepository extends JpaRepository<Materia, Integer> {

    Optional<Materia> findByIdAndEstado(Integer integer, Estado estado);
}
