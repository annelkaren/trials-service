package mx.gob.pjpuebla.trials.core.eventos;

import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;

import java.util.Optional;

public interface EventoRepository extends JpaRepository<Evento, Integer> {
    Boolean existsByDiaInicioAfterAndDiaFinBefore(LocalDate fecha);
    Optional<Evento> findByDiaInicioAfterAndDiaFinBefore(LocalDate fecha);
}
