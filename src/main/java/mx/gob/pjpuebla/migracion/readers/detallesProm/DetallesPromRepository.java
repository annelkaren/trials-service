package mx.gob.pjpuebla.migracion.readers.detallesProm;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface DetallesPromRepository extends JpaRepository<DetallesProm, Integer> {
    List<DetallesProm> findByCuAndStatus(String cu, String status);
}