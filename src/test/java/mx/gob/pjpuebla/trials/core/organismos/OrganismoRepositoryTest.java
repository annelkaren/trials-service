package mx.gob.pjpuebla.trials.core.organismos;

import mx.gob.pjpuebla.trials.core.utils.audit.AuditConfigTest;
import mx.gob.pjpuebla.trials.util.Estado;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import static mx.gob.pjpuebla.trials.core.organismos.OrganismoSetUp.createOrganismo;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(properties = {
        "spring.jpa.properties.hibernate.hbm2ddl.auto: create-drop"
})
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
class OrganismoRepositoryTest extends AuditConfigTest {

    @Autowired
    private OrganismoRepository organismosRepository;

    @Test
    void getAllEstadoActive() {
        Organismo organismo = createOrganismo();
        organismosRepository.save(organismo);
        ExampleMatcher exampleMatcher = ExampleMatcher.matching()
                .withMatcher("nombre", ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase())
                .withMatcher("estado", ExampleMatcher.GenericPropertyMatchers.ignoreCase());

        Page<Organismo> page = organismosRepository.findAll(Example.of(new Organismo().setNombre("CONSEJO DE LA JUDICATURA DEL PODER JUDICIAL DEL ESTADO DE PUEBLA").setEstado(Estado.ACTIVE), exampleMatcher), PageRequest.of(0, 20));
        assertThat(page.get()).hasSize(1);
    }
}
