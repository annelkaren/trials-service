package mx.gob.pjpuebla.trials.core.personas;

import mx.gob.pjpuebla.trials.core.juzgados.Juzgado;
import mx.gob.pjpuebla.trials.core.roles.RoleService;
import mx.gob.pjpuebla.trials.core.utils.audit.AuditConfigTest;
import mx.gob.pjpuebla.trials.util.enums.Estado;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.jdbc.Sql;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(properties = {
        "spring.jpa.properties.hibernate.hbm2ddl.auto: create-drop"
})
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@Sql(value = {
        "/scripts/INSERT_DOMICILIOS.sql",
        "/scripts/INSERT_ESCOLARIDADES.sql",
        "/scripts/INSERT_ESTADO_CIVIL.sql",
        "/scripts/INSERT_DISTRITOS.sql",
        "/scripts/INSERT_SEDES.sql",
        "/scripts/INSERT_MATERIAS.sql",
        "/scripts/INSERT_TIPO_SISTEMAS.sql",
        "/scripts/INSERT_TIPO_JUICIOS.sql",
        "/scripts/INSERT_JUZGADOS.sql",
        "/scripts/INSERT_JUZGADO_TIPOJUICIO.sql",
        "/scripts/INSERT_PERSONAS.sql",
}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
@Sql(value = {
        "/scripts/DELETE_PERSONAS.sql",
        "/scripts/DELETE_JUZGADO_TIPOJUICIO.sql",
        "/scripts/DELETE_JUZGADOS.sql",
        "/scripts/DELETE_TIPO_JUICIOS.sql",
        "/scripts/DELETE_TIPO_SISTEMAS.sql",
        "/scripts/DELETE_MATERIAS.sql",
        "/scripts/DELETE_SEDES.sql",
        "/scripts/DELETE_DISTRITOS.sql",
        "/scripts/DELETE_ESTADO_CIVIL.sql",
        "/scripts/DELETE_ESCOLARIDADES.sql",
        "/scripts/DELETE_DOMICILIOS.sql",
}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_CLASS)
class PersonaRepositoryTest extends AuditConfigTest {

    @Autowired
    private PersonaRepository personaRepository;
    private Persona persona = PersonaSetUp.createPersona();

    @Mock
    RoleService roleService;

    @Test
    void findByIdAndEstadoActive() {
        List<Estado> estados = Arrays.asList(Estado.INACTIVE, Estado.ACTIVE);
        Optional<PersonaRecord> entity = personaRepository.findByIdAndEstadoIn(1L, estados);
        assertThat(entity).isPresent();
        assertThat(entity.get().estado()).isEqualTo(Estado.ACTIVE);
    }

    @Test
    void findByCurp() {
        Optional<PersonaRecord> entity = personaRepository.findByCurp("XXXX000000XXXXXX00");
        assertThat(entity).isPresent();
    }

    @Test
    void findByUsuario() {
        persona.setUsuario("6b13785f-d213-4585-a76b-437ffe57c9c7");
        Optional<Persona> entity = personaRepository.findByUsuario(persona.getUsuario());
        assertThat(entity).isPresent();
        assertThat(entity.get().getNombre()).isEqualTo(persona.getNombre());
    }

    @Test
    void findByUsuarioAndJuzgadoIdAndEstadoIn() {
        persona.setJuzgado(new Juzgado().setId(51));
        persona.setUsuario("6b13785f-d213-4585-a76b-437ffe57c9c7");
        Optional<Persona> entity = personaRepository
                .findByUsuarioAndJuzgadoIdAndEstadoIn(persona.getUsuario(), persona.getJuzgado().getId(),
                        List.of(Estado.ACTIVE));
        assertThat(entity).isPresent();
        assertThat(entity.get().getNombre()).isEqualTo(persona.getNombre());
        assertThat(entity.get().getUsuario()).isEqualTo(persona.getUsuario());
    }

    @Test
    void findByCentroTrabajoAndSearch() {
        persona.setJuzgado(new Juzgado().setId(51));
        persona.setUsuario("6b13785f-d213-4585-a76b-437ffe57c9c7");

        Page<Persona> page = personaRepository.findByCentroTrabajoAndSearch(
                null,
                null,
                null,
                null,
                Arrays.asList(Estado.ACTIVE),
                null,
                null,
                persona.getJuzgado().getId(),
                true,
                PageRequest.of(0, 20));

        assertThat(page).isNotEmpty();
    }

    @Test
    void findByJuzgadoId() {
        List<Persona> personas = personaRepository.findByJuzgadoId(51);
        assertThat(personas).isNotEmpty();
    }
}