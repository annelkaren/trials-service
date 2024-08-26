package mx.gob.pjpuebla.trials.core.escolaridades;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EscolaridadRepository extends JpaRepository<Escolaridad, Integer> {
}
