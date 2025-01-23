package mx.gob.pjpuebla.trials.core.tipoaudiencia;

import mx.gob.pjpuebla.trials.core.materias.Materia;
import mx.gob.pjpuebla.trials.core.tiposistema.TipoSistema;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TipoAudienciaRepository extends JpaRepository<TipoAudiencia, Integer>{
    TipoAudiencia findByNombre(String nombre);

    Page<TipoAudiencia> findByMateriaAndNombreContainingIgnoreCase(Materia materia, String nombre, Pageable pageable);

    Page<TipoAudiencia> findByMateriaAndTipoSistemaAndNombreContainingIgnoreCase(Materia materia, TipoSistema tipoSistema, String nombre, Pageable pageable);

    List<TipoAudienciaRecord> findByMateria_id(Integer id);
}
