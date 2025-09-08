package mx.gob.pjpuebla.migracion.readers.domicilio;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DomicilioMigracionRepository extends JpaRepository<DomicilioMigracion, Integer> {

    Optional<DomicilioMigracion> findByCuActorAndEstatus(String cu, String estatus);

}
