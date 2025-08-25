package mx.gob.pjpuebla.migracion.actores.complementoCampos;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;



public interface ActorGeneralMigracionRepository extends JpaRepository<ActorGeneralMigracion, Integer> {
    
    Optional<ActorGeneralMigracion> findBycuActor(String cuActor);

}
