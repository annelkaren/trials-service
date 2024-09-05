package mx.gob.pjpuebla.trials.core.bloques;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import mx.gob.pjpuebla.trials.util.Estado;

@Repository
public interface BloqueRepository extends JpaRepository<Bloque, Integer> {
    
    Optional<Bloque> findByIdAndEstadoIn(Integer id, List <Estado> estados); 
}
