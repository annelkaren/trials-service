package mx.gob.pjpuebla.trials.core.materias;

import mx.gob.pjpuebla.trials.core.utils.audit.AuditConfigTest;
import mx.gob.pjpuebla.trials.util.enums.Estado;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.jdbc.Sql;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;


@DataJpaTest(properties = {
        "spring.jpa.properties.hibernate.hbm2ddl.auto: create-drop"
})
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@Sql(value = {
        "/scripts/INSERT_MATERIAS.sql"
}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
@Sql(value = {
        "/scripts/DELETE_MATERIAS.sql"
}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_CLASS)
class MateriaRepositoryTest extends AuditConfigTest {

    @Autowired
    private MateriaRepository materiaRepository;

    @Test
    void findByAllAndEstadoActive() {
        ExampleMatcher exampleMatcher = ExampleMatcher.matching()
                .withMatcher("nombre", ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase())
                .withMatcher("estado", ExampleMatcher.GenericPropertyMatchers.ignoreCase());

        Page<Materia> page = materiaRepository.findAll(Example.of(new Materia().setNombre("P").setEstado(Estado.ACTIVE), exampleMatcher), PageRequest.of(0, 20));
        assertThat(page.get()).hasSize(1);
    }

    @Test
    void findByIdAndEstadoActive() {
        Optional<Materia> materia = materiaRepository.findByIdAndEstado(100, Estado.ACTIVE);
        assertThat(materia).isPresent();
        assertThat(materia.get().getEstado()).isEqualTo(Estado.ACTIVE);
    }
}