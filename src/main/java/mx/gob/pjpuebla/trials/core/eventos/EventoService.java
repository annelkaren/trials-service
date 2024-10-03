package mx.gob.pjpuebla.trials.core.eventos;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;

@Service
@AllArgsConstructor
public class EventoService {
    private EventoRepository eventoRepository;

    public Boolean esDiaHabil(LocalDate fecha){
        return eventoRepository.existsByDiaInicioAfterAndDiaFinBefore(fecha);
    }

    public LocalDate siguienteDiaHabil(LocalDate fecha){
        Optional<Evento> evento = eventoRepository.findByDiaInicioAfterAndDiaFinBefore(fecha);

        return evento.orElseThrow().getDiaFin().plusDays(1);
    }
}
