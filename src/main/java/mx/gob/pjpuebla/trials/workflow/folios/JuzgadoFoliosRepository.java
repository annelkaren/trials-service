package mx.gob.pjpuebla.trials.workflow.folios;

import mx.gob.pjpuebla.trials.core.juzgados.Juzgado;
import mx.gob.pjpuebla.trials.util.enums.TipoCarpeta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface JuzgadoFoliosRepository extends JpaRepository<JuzgadoFolios, Integer> {
    Optional<JuzgadoFolios> findByJuzgadoAndTipoCarpeta(Juzgado juzgado, TipoCarpeta tipoCarpeta);

}
