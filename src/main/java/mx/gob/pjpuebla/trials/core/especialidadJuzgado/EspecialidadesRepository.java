package mx.gob.pjpuebla.trials.core.especialidadJuzgado;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EspecialidadesRepository extends JpaRepository<Especialidades, Integer> {
}
