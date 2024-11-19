package mx.gob.pjpuebla.trials.workflow.carpeta.carpetadetalle;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CarpetaDetalleRepository extends JpaRepository<CarpetaDetalle, Integer> {
    CarpetaDetalle findByCarpetaId(Integer id);
}
