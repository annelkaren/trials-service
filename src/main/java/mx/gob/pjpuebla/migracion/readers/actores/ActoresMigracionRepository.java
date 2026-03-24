package mx.gob.pjpuebla.migracion.readers.actores;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ActoresMigracionRepository extends JpaRepository<ActoresMigracion, Integer> {
    List<ActoresMigracion> findByClave(String clave);
}