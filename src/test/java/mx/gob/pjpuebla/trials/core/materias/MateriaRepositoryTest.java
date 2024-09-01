package mx.gob.pjpuebla.trials.core.materias;

import lombok.extern.slf4j.Slf4j;
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

import java.util.Optional;

import static mx.gob.pjpuebla.trials.core.materias.MateriaSetUp.createMateria;
import static org.assertj.core.api.Assertions.assertThat;


@DataJpaTest(properties = {
        "spring.jpa.properties.hibernate.hbm2ddl.auto: create-drop"
})
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@Slf4j
class MateriaRepositoryTest extends AuditConfigTest {

    @Autowired
    private MateriaRepository materiaRepository;

    @Test
    void findByAllAndEstadoActive() {
        Materia validMateria = createMateria();
        materiaRepository.save(validMateria);
        ExampleMatcher exampleMatcher = ExampleMatcher.matching()
                .withMatcher("nombre", ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase())
                .withMatcher("estado", ExampleMatcher.GenericPropertyMatchers.ignoreCase());

        Page<Materia> page = materiaRepository.findAll(Example.of(new Materia().setNombre("P").setEstado(Estado.ACTIVE), exampleMatcher), PageRequest.of(0, 20));
        assertThat(page.get()).hasSize(1);
    }

    @Test
    void findByIdAndEstadoActive() {
        Materia save = materiaRepository.save(createMateria());

        Optional<Materia> materia = materiaRepository.findByIdAndEstado(save.getId(), Estado.ACTIVE);
        assertThat(materia).isPresent();
        assertThat(materia.get().getEstado()).isEqualTo(Estado.ACTIVE);
    }
}