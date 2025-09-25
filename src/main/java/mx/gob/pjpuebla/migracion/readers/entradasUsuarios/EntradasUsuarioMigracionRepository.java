package mx.gob.pjpuebla.migracion.readers.entradasUsuarios;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface EntradasUsuarioMigracionRepository extends JpaRepository<EntradasUsuarioMigracion, Integer> {
    
    Optional<EntradasUsuarioMigracion> findByClaveActorAndEstatus(String claveActor, String estatus);
}
