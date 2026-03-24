package mx.gob.pjpuebla.migracion.readers.amparos;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AmparoMigracionRepository extends JpaRepository<AmparosMigracion, Integer> {
    
    List<AmparosMigracion> findByCuAndEstatus(String cu, String estatus);
}