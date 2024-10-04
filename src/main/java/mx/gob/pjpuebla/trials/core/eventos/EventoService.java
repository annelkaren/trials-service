package mx.gob.pjpuebla.trials.core.eventos;

import lombok.AllArgsConstructor;

import mx.gob.pjpuebla.trials.util.DiaHabil;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.time.DateTimeException;

@Service
@AllArgsConstructor
public class EventoService {
    private EventoRepository eventoRepository;

    @Transactional(readOnly = true)
    public Page<EventoRecord> getAll(Evento example, Pageable pageable) {
        Page<Evento> page = eventoRepository.findAll(pageable);

        List<EventoRecord> list = page.getContent().stream()
            .map(evento -> new EventoRecord(evento.getId(), 
                evento.getDiaInicio(), 
                evento.getDiaFin(), 
                evento.getDescripcion(), 
                evento.getJuzgado().getNombre(), 
                evento.getOficialia().getNombre()))
            .toList();

        return new PageImpl<>(list, pageable, page.getTotalElements());
    }

    public Evento create(Evento evento){

        if (evento.getDiaInicio().isAfter(evento.getDiaFin()))
            throw new DateTimeException("La fecha de inicio no puede ser mayor a la fecha de finalización");

        return eventoRepository.save(evento);
    }

    public Evento save(Evento evento){
        return eventoRepository.save(evento);
    }

    public void delete (Evento evento){
        eventoRepository.delete(evento);
    }

    public Boolean esDiaHabil(LocalDate fecha) {

        return eventoRepository.existsFechaEntreDiaInicioAndDiaFin(fecha);
    }

    public LocalDate siguienteDiaHabil(LocalDate fecha){
        Optional<Evento> evento = eventoRepository.findFechaEntreDiaInicioAndDiaFin(fecha);

        LocalDate siguienteDia = evento.orElseThrow().getDiaFin();

        if (DiaHabil.esInhabil(siguienteDia)) {
            siguienteDia = DiaHabil.proximoDiaHabil(siguienteDia);
        }else{
            siguienteDia = siguienteDia.plusDays(1);
        }

        return siguienteDia;
    }
}
