package mx.gob.pjpuebla.migracion.readers.conceptos;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ConceptosMigracionRepository extends JpaRepository<ConceptosMigracion, Integer> {
    
    Optional<ConceptosMigracion> findByClave(String clave);
    
}
