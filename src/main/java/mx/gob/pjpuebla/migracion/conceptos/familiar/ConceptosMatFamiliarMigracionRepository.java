package mx.gob.pjpuebla.migracion.conceptos.familiar;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ConceptosMatFamiliarMigracionRepository extends JpaRepository<ConceptosMatFamiliarMigracion, Integer> {
    
    Optional<ConceptosMatFamiliarMigracion> findByClave(String clave);
}
