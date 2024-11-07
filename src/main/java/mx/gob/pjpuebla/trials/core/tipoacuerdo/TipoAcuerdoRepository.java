package mx.gob.pjpuebla.trials.core.tipoacuerdo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TipoAcuerdoRepository extends JpaRepository<TipoAcuerdo, Integer> {
}
