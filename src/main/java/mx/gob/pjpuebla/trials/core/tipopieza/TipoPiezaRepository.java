package mx.gob.pjpuebla.trials.core.tipopieza;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TipoPiezaRepository  extends JpaRepository<TipoPieza, Integer> {

}
