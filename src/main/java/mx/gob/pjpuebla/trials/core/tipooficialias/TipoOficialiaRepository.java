package mx.gob.pjpuebla.trials.core.tipooficialias;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TipoOficialiaRepository extends JpaRepository<TipoOficialia, Integer> {
    
}

