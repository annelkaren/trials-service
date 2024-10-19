package mx.gob.pjpuebla.trials.core.tipopartes;

import mx.gob.pjpuebla.trials.util.enums.Estado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TipoPartesRepository extends JpaRepository<TipoPartes, Integer> {

    Optional<TipoPartes> findByIdAndEstado(Integer id, Estado estado);

    List<TipoPartes> findByTipoJuicioId(Integer id);

    Optional<TipoPartes> findByNombreAndTipoJuicioId(String name, Integer tipoJuicioId);
}