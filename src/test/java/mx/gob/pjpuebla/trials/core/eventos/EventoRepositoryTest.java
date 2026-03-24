package mx.gob.pjpuebla.trials.core.eventos;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

import mx.gob.pjpuebla.trials.core.juzgados.Juzgado;
import mx.gob.pjpuebla.trials.core.juzgados.JuzgadoRepository;
import mx.gob.pjpuebla.trials.core.utils.audit.AuditConfigTest;

@DataJpaTest(properties = {
        "spring.jpa.properties.hibernate.hbm2ddl.auto: create-drop"
})
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@Sql(value = {
        "/scripts/INSERT_DOMICILIOS.sql",
        "/scripts/INSERT_DISTRITOS.sql",
        "/scripts/INSERT_MATERIAS.sql",
        "/scripts/INSERT_SEDES.sql",
        "/scripts/INSERT_JUZGADOS.sql",
        "/scripts/INSERT_EVENTOS.sql"
}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
@Sql(value = {
        "/scripts/DELETE_EVENTOS.sql",
        "/scripts/DELETE_JUZGADOS.sql",
        "/scripts/DELETE_SEDES.sql",
        "/scripts/DELETE_MATERIAS.sql",
        "/scripts/DELETE_DISTRITOS.sql",
        "/scripts/DELETE_DOMICILIOS.sql"
}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_CLASS)
class EventoRepositoryTest extends AuditConfigTest {
    @Autowired
    EventoRepository eventoRepository;

    @Autowired
    JuzgadoRepository juzgadoRepository;

    @Test
    void findById(){
        Integer id = 51;
        Optional<Evento> evento = eventoRepository.findById(id);

        assertThat(evento).isPresent().get().hasFieldOrPropertyWithValue("descripcion", "DIA INHABIL");
    }

    @Test
    void checkDiaInhabilTest(){
        LocalDate diaFeriado = LocalDate.of(2024,10,1);

        Boolean diaInhabil = eventoRepository.existsEventoEntreDiaInicioAndDiaFin(diaFeriado, null, null);

        assertThat(diaInhabil).isTrue();
    }

    @Test
    void checkDiaInhabilJuzgadoTest(){
        LocalDate diaFeriado = LocalDate.of(2024,12,12);
        Juzgado juzgado = juzgadoRepository.findAll().stream().findFirst().orElseThrow();

        Boolean diaInhabil = eventoRepository.existsEventoEntreDiaInicioAndDiaFin(diaFeriado, juzgado, null);

        assertThat(diaInhabil).isTrue();
    }

    @Test
    void eventoDiaInhabilTest(){
        LocalDate diaFeriado = LocalDate.of(2024,10,1);

        Optional<Evento> eventoInhabil = eventoRepository.findEntreDiaInicioAndDiaFin(diaFeriado, null, null);

        assertThat(eventoInhabil).isPresent().get().hasFieldOrPropertyWithValue("descripcion", "DIA INHABIL");
    }

    @Test
    void findEventosGeneralesTest() {
        List<Evento> eventos = eventoRepository.findEventosGenerales();
        assertThat(eventos).isNotNull();
        assertThat(eventos.size()).isGreaterThan(0);
    }

    @Test
    void findByOficialiaOrJuzgado() {
        Juzgado juzgado = juzgadoRepository.findAll().stream().findFirst().orElseThrow();

        List<Evento> eventos = eventoRepository.findByOficialiaOrJuzgado(null, juzgado);

        assertThat(eventos).isNotNull();
        assertThat(eventos.size()).isGreaterThan(0);
        assertThat(eventos.get(0).getJuzgado()).isEqualTo(juzgado);
    }
}
