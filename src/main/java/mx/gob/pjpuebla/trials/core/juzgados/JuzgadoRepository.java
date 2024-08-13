package mx.gob.pjpuebla.trials.core.juzgados;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JuzgadoRepository extends JpaRepository<Juzgado, Integer> {

}
