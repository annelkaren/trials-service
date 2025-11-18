package mx.gob.pjpuebla.migracion.readers.entradasUsuarios;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface EntradasUsuarioMigracionRepository extends JpaRepository<EntradasUsuarioMigracion, Integer> {
    
    List<EntradasUsuarioMigracion> findByClaveActorAndEstatus(String claveActor, String estatus);
}
