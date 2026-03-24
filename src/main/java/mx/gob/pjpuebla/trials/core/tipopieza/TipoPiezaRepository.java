package mx.gob.pjpuebla.trials.core.tipopieza;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface TipoPiezaRepository  extends JpaRepository<TipoPieza, Integer> {

    List<TipoPieza> findByIdOrClave(Integer id, String clave);

    Optional<TipoPieza> findByClave(String clave);

    boolean existsByClave(String clave);
}
