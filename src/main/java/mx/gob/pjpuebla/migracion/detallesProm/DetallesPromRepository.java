package mx.gob.pjpuebla.migracion.detallesProm;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface DetallesPromRepository extends JpaRepository<DetallesProm, Integer> {
    List<DetallesProm> findByCu(String cu);
}