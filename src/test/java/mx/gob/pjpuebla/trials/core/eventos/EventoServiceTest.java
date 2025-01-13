package mx.gob.pjpuebla.trials.core.eventos;

import mx.gob.pjpuebla.trials.core.eventos.records.EventoEditRecord;
import mx.gob.pjpuebla.trials.core.eventos.records.EventoPeriodosRecord;
import mx.gob.pjpuebla.trials.core.oficialias.Oficialia;
import mx.gob.pjpuebla.trials.core.personas.Persona;
import mx.gob.pjpuebla.trials.core.personas.PersonaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import mx.gob.pjpuebla.trials.core.juzgados.Juzgado;
import mx.gob.pjpuebla.trials.core.juzgados.JuzgadoRepository;
import mx.gob.pjpuebla.trials.core.juzgados.JuzgadoSetUp;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class EventoServiceTest {
    @InjectMocks
    private EventoService eventoService;

    @Mock
    private EventoRepository eventoRepository;

    @Mock
    private JuzgadoRepository juzgadoRepository;

    @Mock
    PersonaService personaService;

    private Evento evento;
    private Juzgado juzgado;

    @BeforeEach
    void setUp(){
        evento = EventoSetUp.createEvento();
        juzgado = JuzgadoSetUp.createJuzgado();
    }

    @Test
    void createTest(){
        given(eventoRepository.save(evento)).willReturn(evento);

        Evento entity = eventoService.create(evento);

        assertThat(entity).isNotNull()
                .hasFieldOrPropertyWithValue("id", evento.getId())
                .hasFieldOrPropertyWithValue("descripcion", evento.getDescripcion())
                .hasFieldOrPropertyWithValue("diaInicio", evento.getDiaInicio())
                .hasFieldOrPropertyWithValue("diaFin", evento.getDiaFin());

    }

    @Test
    void saveTest(){
        evento.setDiaInicio(LocalDate.parse("2024-12-18"));
        evento.setDiaFin(LocalDate.parse("2025-01-05"));
        evento.setDescripcion("VACACIONES");

        given(eventoRepository.save(evento)).willReturn(evento);

        Evento entity = eventoService.save(evento);

        assertThat(entity).isNotNull()
                .hasFieldOrPropertyWithValue("descripcion", evento.getDescripcion())
                .hasFieldOrPropertyWithValue("diaInicio", evento.getDiaInicio())
                .hasFieldOrPropertyWithValue("diaFin", evento.getDiaFin());
    }

    @Test
    void checkDiaInhabilTest(){
        LocalDate diaInhabil = LocalDate.parse("2024-11-01");

        given(eventoRepository.existsEventoEntreDiaInicioAndDiaFin(diaInhabil, null, null)).willReturn(Boolean.TRUE);

        Boolean esDiaInhabil = eventoService.esDiaInHabil(diaInhabil, null, null);

        assertThat(esDiaInhabil).isTrue();
    }

    @Test
    void checkDiaInhabilJuzgadoTest(){
        LocalDate diaInhabil = LocalDate.parse("2024-11-01");

        given(eventoRepository.existsEventoEntreDiaInicioAndDiaFin(diaInhabil, juzgado, null)).willReturn(Boolean.TRUE);

        Boolean esDiaInhabil = eventoService.esDiaInHabil(diaInhabil, juzgado, null);

        assertThat(esDiaInhabil).isTrue();
    }

    @Test
    void checkDiaInhabilFalseTest(){
        LocalDate diaHabil = LocalDate.parse("2024-10-31");
        given(eventoRepository.existsEventoEntreDiaInicioAndDiaFin(diaHabil, null, null)).willReturn(Boolean.FALSE);

        Boolean esDiaHabil = eventoService.esDiaInHabil(diaHabil, null, null);

        assertThat(esDiaHabil).isFalse();
    }

    @Test
    void siguienteDiaHabilTest(){
        LocalDate diaInhabil = LocalDate.parse("2024-11-01");
        given(eventoRepository.findEntreDiaInicioAndDiaFin(diaInhabil, null, null)).willReturn(Optional.of(evento));

        LocalDate siguienteDia = eventoService.siguienteDiaHabil(diaInhabil, null, null);

        assertThat(siguienteDia).isAfter(diaInhabil);

    }

    @Test
    void createPeriodosTest() {
        EventoPeriodosRecord record = EventoSetUp.eventoPeriodosRecord();

        Persona personaMock = new Persona();
        given(personaService.getAuditor()).willReturn(personaMock);

        given(eventoRepository.save(any(Evento.class))).willReturn(EventoSetUp.createEvento());

        Evento eventoCreado = eventoService.createPeriodos(record);

        assertThat(eventoCreado).isNotNull();
        assertThat(eventoCreado.getDescripcion()).isEqualTo(record.descripcion());
        assertThat(eventoCreado.getDiaInicio()).isEqualTo(record.diaInicio());
        assertThat(eventoCreado.getDiaFin()).isEqualTo(record.diaFin());
    }

    @Test
    void getEventosGeneralesTest() {
        Evento eventoMock = EventoSetUp.createEvento();
        Page<Evento> pageMock = new PageImpl<>(List.of(eventoMock), PageRequest.of(0, 10), 1);
        given(eventoRepository.findEventosGenerales(PageRequest.of(0, 10))).willReturn(pageMock);

        Page<EventoRecord> eventoRecords = eventoService.getEventosGenerales(PageRequest.of(0, 10));

        assertThat(eventoRecords).isNotNull();
        assertThat(eventoRecords.getContent()).hasSize(1);
    }

    @Test
    void getEventosOficialiaComunTest() {
        Oficialia oficialiaMock = new Oficialia();
        Juzgado juzgadoMock = JuzgadoSetUp.createJuzgado();
        Persona personaMock = new Persona();
        personaMock.setOficialia(oficialiaMock);
        personaMock.setJuzgado(juzgadoMock);
        given(personaService.getAuditor()).willReturn(personaMock);

        Evento eventoMock = EventoSetUp.createEvento();
        Page<Evento> pageMock = new PageImpl<>(List.of(eventoMock), PageRequest.of(0, 10), 1);
        given(eventoRepository.findByOficialiaOrJuzgado(oficialiaMock, juzgadoMock, PageRequest.of(0, 10))).willReturn(pageMock);

        Page<EventoRecord> eventoRecords = eventoService.getEventosOficialiaComun(PageRequest.of(0, 10));

        assertThat(eventoRecords).isNotNull();
        assertThat(eventoRecords.getContent()).hasSize(1);
    }

    @Test
    void deleteByIdTest() {
        Integer eventoId = 1;
        given(eventoRepository.existsById(eventoId)).willReturn(true);
        eventoService.deleteById(eventoId);

        verify(eventoRepository).existsById(eventoId);
        verify(eventoRepository).deleteById(eventoId);
    }

    @Test
    void editarEventoPeriodoTest() {
        EventoEditRecord editRecord = EventoSetUp.eventoEditRecord();
        Evento eventoExistente = EventoSetUp.createEvento();

        given(eventoRepository.findById(editRecord.id())).willReturn(Optional.of(eventoExistente));
        given(eventoRepository.save(any(Evento.class))).willReturn(eventoExistente);

        EventoRecord resultado = eventoService.editarEventoPeriodo(editRecord);

        assertThat(resultado).isNotNull();
        assertThat(resultado.descripcion()).isEqualTo(editRecord.descripcion());
        assertThat(resultado.diaInicio()).isEqualTo(editRecord.diaInicio());
        assertThat(resultado.diaFin()).isEqualTo(editRecord.diaFin());

        verify(eventoRepository).save(any(Evento.class));
    }
}
