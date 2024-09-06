package mx.gob.pjpuebla.trials.core.personas;

import mx.gob.pjpuebla.trials.core.domicilio.DomicilioSetUp;
import mx.gob.pjpuebla.trials.core.domicilios.Domicilio;
import mx.gob.pjpuebla.trials.core.domicilios.DomicilioRepository;
import mx.gob.pjpuebla.trials.core.utils.audit.AuditConfigTest;
import mx.gob.pjpuebla.trials.util.enums.Estado;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;


@DataJpaTest(properties = {
        "spring.jpa.properties.hibernate.hbm2ddl.auto: create-drop"
})
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
class PersonaRepositoryTest extends AuditConfigTest {

    @Autowired
    private PersonaRepository personaRepository;
    @Autowired
    private DomicilioRepository domicilioRepository;
    private Persona persona = PersonaSetUp.createPersona();

    @BeforeEach
    void setUp() {
        Domicilio domicilio = domicilioRepository.save(DomicilioSetUp.createDomicilio());
        persona.setDomicilio(domicilio);
    }

    @Test
    void findByIdAndEstadoActive() {
        persona = personaRepository.save(persona);
        List<Estado> estados = Arrays.asList(Estado.INACTIVE, Estado.ACTIVE);
        Optional<Persona> entity = personaRepository.findByIdAndEstadoIn(persona.getId(), estados);
        assertThat(entity).isPresent();
        assertThat(entity.get().getEstado()).isEqualTo(Estado.ACTIVE);
    }

    @Test
    void findByIdAndEstadoInactive() {
        persona.setEstado(Estado.INACTIVE);
        persona = personaRepository.save(persona);
        List<Estado> estados = Arrays.asList(Estado.INACTIVE, Estado.ACTIVE);
        Optional<Persona> entity = personaRepository.findByIdAndEstadoIn(persona.getId(), estados);
        assertThat(entity).isPresent();
        assertThat(entity.get().getEstado()).isEqualTo(Estado.INACTIVE);
    }

    @Test
    void findByCurp() {
        persona.setCurp("XXXX111111XXXXXX11");
        persona = personaRepository.save(persona);
        Optional<Persona> entity = personaRepository.findByCurp(persona.getCurp());
        assertThat(entity).isPresent();
    }
}