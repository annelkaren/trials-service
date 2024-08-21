package mx.gob.pjpuebla.trials.core.tipoOficialias;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TipoOficialiasRepository extends JpaRepository<TipoOficialias, Integer> {
    
}

