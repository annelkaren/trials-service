package mx.gob.pjpuebla.trials.core.bloques;

import java.time.LocalTime;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Repository
public interface BloqueRepository extends JpaRepository<Bloque, Integer> {

    Page<Bloque> findByHoraInicial(LocalTime horaInicial, Pageable pageable);

}
