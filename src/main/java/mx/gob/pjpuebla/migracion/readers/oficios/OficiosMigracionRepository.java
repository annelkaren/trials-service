package mx.gob.pjpuebla.migracion.readers.oficios;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface OficiosMigracionRepository extends JpaRepository<OficiosMigracion, Integer> {
    List<OficiosMigracion> findByCuAndEstatusOfiIn(String cu, List<String> estatus);
}