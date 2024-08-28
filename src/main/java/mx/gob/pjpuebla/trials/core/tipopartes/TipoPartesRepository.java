package mx.gob.pjpuebla.trials.core.tipopartes;

import mx.gob.pjpuebla.trials.util.Estado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TipoPartesRepository extends JpaRepository<TipoPartes, Integer> {

    Optional<TipoPartes> findByMateriaId(Integer integer);

}