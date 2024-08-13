package mx.gob.pjpuebla.trials.core.tipopartes;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TipoPartesRepository extends JpaRepository<TipoPartes, Integer> {
}