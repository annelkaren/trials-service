package mx.gob.pjpuebla.trials.core.eventos;

import lombok.AllArgsConstructor;
import mx.gob.pjpuebla.trials.core.eventos.records.EventoEditRecord;
import mx.gob.pjpuebla.trials.core.eventos.records.EventoPeriodosRecord;
import mx.gob.pjpuebla.trials.core.juzgados.Juzgado;
import mx.gob.pjpuebla.trials.core.oficialias.Oficialia;
import mx.gob.pjpuebla.trials.core.personas.Persona;
import mx.gob.pjpuebla.trials.core.personas.PersonaService;
import mx.gob.pjpuebla.trials.error.NotFoundException;
import mx.gob.pjpuebla.trials.util.FinSemana;
import mx.gob.pjpuebla.trials.util.enums.Estado;
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
    private final PersonaService personaService;

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

    public Boolean esDiaInHabil(LocalDate fecha, Juzgado juzgado, Oficialia oficialia) {

        return FinSemana.esInhabil(fecha) || eventoRepository.existsEventoEntreDiaInicioAndDiaFin(fecha, juzgado, oficialia);
    }

    public LocalDate siguienteDiaHabil(LocalDate fecha, Juzgado juzgado, Oficialia oficialia){
        Optional<Evento> evento = eventoRepository.findEntreDiaInicioAndDiaFin(fecha, juzgado, oficialia);

        LocalDate siguienteDia = fecha;

        if(evento.isPresent()){
            siguienteDia = evento.get().getDiaFin();
        }

        if (FinSemana.esInhabil(siguienteDia)==Boolean.TRUE) {
            siguienteDia = FinSemana.proximoDiaHabil(siguienteDia);
        }else{
            siguienteDia = siguienteDia.plusDays(1);
        }

        return siguienteDia;
    }

    public Evento createPeriodos(EventoPeriodosRecord record){

        if (record.diaInicio().isAfter(record.diaFin()))
            throw new DateTimeException("La fecha de inicio no puede ser mayor a la fecha de finalización");

        Persona persona = personaService.getAuditor();
        Evento evento = null;

        switch (record.calendarios()) {
            case "General" -> {
                evento = new Evento()
                        .setDescripcion(record.descripcion())
                        .setDiaInicio(record.diaInicio())
                        .setDiaFin(record.diaFin())
                        .setEstado(Estado.ACTIVE);
                eventoRepository.save(evento);
            }
            case "Oficialía Común" -> {
                evento = new Evento()
                        .setDescripcion(record.descripcion())
                        .setDiaInicio(record.diaInicio())
                        .setDiaFin(record.diaFin())
                        .setEstado(Estado.ACTIVE);
                if (persona.getOficialia() != null) evento.setOficialia(persona.getOficialia());
                if (persona.getJuzgado() != null) evento.setJuzgado(persona.getJuzgado());
                eventoRepository.save(evento);
            }
            case "General-Oficialía Común" -> {
                evento = new Evento()
                        .setDescripcion(record.descripcion())
                        .setDiaInicio(record.diaInicio())
                        .setDiaFin(record.diaFin())
                        .setEstado(Estado.ACTIVE);
                eventoRepository.save(evento);

                Evento evento2 = new Evento()
                        .setDescripcion(record.descripcion())
                        .setDiaInicio(record.diaInicio())
                        .setDiaFin(record.diaFin())
                        .setEstado(Estado.ACTIVE);
                if (persona.getOficialia() != null) evento2.setOficialia(persona.getOficialia());
                if (persona.getJuzgado() != null) evento2.setJuzgado(persona.getJuzgado());
                eventoRepository.save(evento2);
            }
        }

        assert evento != null;
        return eventoRepository.save(evento);
    }

    @Transactional(readOnly = true)
    public Page<EventoRecord> getEventosGenerales(Pageable pageable) {
        Page<Evento> eventosPage = eventoRepository.findEventosGenerales(pageable);

        List<EventoRecord> eventoRecords = eventosPage.getContent().stream()
                .map(evento -> new EventoRecord(
                        evento.getId(),
                        evento.getDiaInicio(),
                        evento.getDiaFin(),
                        evento.getDescripcion(),
                        evento.getJuzgado() != null ? evento.getJuzgado().getNombre() : null,
                        evento.getOficialia() != null ? evento.getOficialia().getNombre() : null
                ))
                .toList();

        return new PageImpl<>(eventoRecords, pageable, eventosPage.getTotalElements());
    }

    @Transactional(readOnly = true)
    public Page<EventoRecord> getEventosOficialiaComun(Pageable pageable) {
        Persona persona = personaService.getAuditor();
        Oficialia oficialia = persona.getOficialia();
        Juzgado juzgado = persona.getJuzgado();

        Page<Evento> eventosPage = eventoRepository.findByOficialiaOrJuzgado(oficialia, juzgado, pageable);

        List<EventoRecord> eventoRecords = eventosPage.getContent().stream()
                .map(evento -> new EventoRecord(
                        evento.getId(),
                        evento.getDiaInicio(),
                        evento.getDiaFin(),
                        evento.getDescripcion(),
                        evento.getJuzgado() != null ? evento.getJuzgado().getNombre() : null,
                        evento.getOficialia() != null ? evento.getOficialia().getNombre() : null
                ))
                .toList();

        return new PageImpl<>(eventoRecords, pageable, eventosPage.getTotalElements());
    }

    @Transactional
    public void deleteById(Integer id) {
        if (!eventoRepository.existsById(id)) {
            throw new IllegalArgumentException("El evento con id " + id + " no existe.");
        }
        eventoRepository.deleteById(id);
    }

    public EventoRecord editarEventoPeriodo(EventoEditRecord record) {
        Evento evento = eventoRepository.findById(record.id())
                .orElseThrow(() -> new NotFoundException("El evento no existe", "eventoId"));

        if (record.diaInicio().isAfter(record.diaFin())) {
            throw new DateTimeException("La fecha de inicio no puede ser mayor a la fecha de finalización");
        }

        evento
                .setDescripcion(record.descripcion())
                .setDiaInicio(record.diaInicio())
                .setDiaFin(record.diaFin());
        eventoRepository.save(evento);

        return new EventoRecord(
                evento.getId(),
                evento.getDiaInicio(),
                evento.getDiaFin(),
                evento.getDescripcion(),
                evento.getJuzgado() != null ? evento.getJuzgado().getNombre() : null,
                evento.getOficialia() != null ? evento.getOficialia().getNombre() : null
        );
    }
}
