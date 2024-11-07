package mx.gob.pjpuebla.trials.core.tipoacuerdo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TipoAcuerdoRepository extends JpaRepository<TipoAcuerdo, Integer> {

    List<TipoAcuerdo> findByMateriaId(Integer materiaId);

    List<TipoAcuerdo> findByMateriaIdAndTipoSistemaId(Integer materiaId, Integer tipoSistemaId);
}
