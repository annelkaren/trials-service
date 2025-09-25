package mx.gob.pjpuebla.trials.core.configuraciones;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConfiguracionesRepository extends JpaRepository<Configuraciones, Integer> {
    
    Optional<Configuraciones> findByPropiedad(String propiedad);
}
