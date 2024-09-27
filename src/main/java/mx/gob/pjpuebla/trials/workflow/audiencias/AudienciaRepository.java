package mx.gob.pjpuebla.trials.workflow.audiencias;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import mx.gob.pjpuebla.trials.workflow.carpeta.Carpeta;

public interface AudienciaRepository extends JpaRepository<Audiencia, Integer> {

    Optional<Audiencia> findByCarpeta(Carpeta carpeta);

}
