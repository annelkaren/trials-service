package mx.gob.pjpuebla.migracion.actores.complementoCampos;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface DemandadoGeneralMigracionRepository extends JpaRepository<DemandadoGeneralMigracion, Integer> {
    
    Optional<DemandadoGeneralMigracion> findBycuDem(String cuDem);
}
