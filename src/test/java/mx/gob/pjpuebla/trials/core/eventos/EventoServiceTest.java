package mx.gob.pjpuebla.trials.core.eventos;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import mx.gob.pjpuebla.trials.core.juzgados.Juzgado;
import mx.gob.pjpuebla.trials.core.juzgados.JuzgadoRepository;
import mx.gob.pjpuebla.trials.core.juzgados.JuzgadoSetUp;

import java.time.LocalDate;
import java.util.Optional;

import static org.mockito.BDDMockito.given;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class EventoServiceTest {
    @InjectMocks
    private EventoService eventoService;

    @Mock
    private EventoRepository eventoRepository;

    @Mock
    private JuzgadoRepository juzgadoRepository;

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

}
