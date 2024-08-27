package mx.gob.pjpuebla.trials.core.especialidadJuzgado;

import mx.gob.pjpuebla.trials.util.Estado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EspecialidadesRepository extends JpaRepository<Especialidades, Integer> {

    Optional<Especialidades> findByIdAndEstado(Integer integer, Estado estado);

}
