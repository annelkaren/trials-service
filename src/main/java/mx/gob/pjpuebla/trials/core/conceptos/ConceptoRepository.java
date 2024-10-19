package mx.gob.pjpuebla.trials.core.conceptos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ConceptoRepository extends JpaRepository<Concepto, Integer> {
    Optional<Concepto> findByNombre(String nombre);
}
