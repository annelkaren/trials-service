package mx.gob.pjpuebla.trials.workflow.solicitudesProrrogas;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SolicitudesProrrogasRepository extends JpaRepository<SolicitudesProrrogas, Integer> {
    
    Optional<SolicitudesProrrogas> findFirstByMovimientoIdOrderByIdDesc(Integer movimientoId);

}
