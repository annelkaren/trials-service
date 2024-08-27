package mx.gob.pjpuebla.trials.core.personas;

import mx.gob.pjpuebla.trials.core.utils.audit.AuditConfigTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static mx.gob.pjpuebla.trials.core.personas.PersonaSetUp.createPersona;
import static org.assertj.core.api.Assertions.assertThat;


@DataJpaTest(properties = {
        "spring.jpa.properties.hibernate.hbm2ddl.auto: create-drop"
})
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
class PersonaRepositoryTest extends AuditConfigTest {

    @Autowired
    private PersonaRepository personaRepository;

    @Test
    void findById() {
        personaRepository.save(createPersona());
        Optional<Persona> persona = personaRepository.findById(1L);
        assertThat(persona).isPresent();
    }
}