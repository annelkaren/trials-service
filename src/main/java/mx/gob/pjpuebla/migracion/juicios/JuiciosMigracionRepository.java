package mx.gob.pjpuebla.migracion.juicios;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface JuiciosMigracionRepository extends JpaRepository<JuiciosMigracion, String> {
    
    Optional<JuiciosMigracion> findById(String idJuicio);
}
