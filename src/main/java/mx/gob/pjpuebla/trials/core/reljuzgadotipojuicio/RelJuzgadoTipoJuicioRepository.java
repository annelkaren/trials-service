package mx.gob.pjpuebla.trials.core.reljuzgadotipojuicio;

import mx.gob.pjpuebla.trials.core.juzgados.Juzgado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RelJuzgadoTipoJuicioRepository extends JpaRepository<RelJuzgadoTipoJuicio, Integer> {
    List<RelJuzgadoTipoJuicio> findAllByjuzgado(Juzgado juzgado);
}
