package mx.gob.pjpuebla.trials.core.acuerdorubros;

import mx.gob.pjpuebla.trials.core.materias.Materia;
import mx.gob.pjpuebla.trials.core.tiposistema.TipoSistema;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface AcuerdoRubrosRepository extends JpaRepository<AcuerdoRubros, Integer> {

    Page<AcuerdoRubros> findByMateria(Materia materia, Pageable pageable);

    Page<AcuerdoRubros> findByMateriaAndTipoSistema(Materia materia, TipoSistema tipoSistema, Pageable pageable);
}
