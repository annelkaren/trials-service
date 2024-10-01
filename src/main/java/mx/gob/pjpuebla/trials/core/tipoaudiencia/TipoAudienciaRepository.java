package mx.gob.pjpuebla.trials.core.tipoaudiencia;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TipoAudienciaRepository extends JpaRepository<TipoAudiencia, Integer>{
    TipoAudiencia findByNombre(String nombre);
}
