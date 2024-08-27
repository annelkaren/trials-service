package mx.gob.pjpuebla.trials.core.especialidadJuzgado;

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

import static mx.gob.pjpuebla.trials.core.especialidadJuzgado.EspecialidadesSetUp.createEspecialidades;
import static org.assertj.core.api.Assertions.assertThat;


@DataJpaTest(properties = {
        "spring.jpa.properties.hibernate.hbm2ddl.auto: create-drop"
})
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
class EspecialidadesRepositoryTest extends AuditConfigTest {

    @Autowired
    private EspecialidadesRepository especialidadesRepository;

    @Test
    void findByAllAndEstadoActive() {
        Especialidades validEspecialidades = createEspecialidades();
        especialidadesRepository.save(validEspecialidades);
        ExampleMatcher exampleMatcher = ExampleMatcher.matching()
                .withMatcher("nombre", ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase())
                .withMatcher("estado", ExampleMatcher.GenericPropertyMatchers.ignoreCase());

        Page<Especialidades> page = especialidadesRepository.findAll(Example.of(new Especialidades().setNombre("Juzgado Especializado en Juicios").setEstado(Estado.ACTIVE), exampleMatcher), PageRequest.of(0, 20));
        assertThat(page.get()).hasSize(1);
    }

    @Test
    void findByIdAndEstadoActive() {
        especialidadesRepository.save(createEspecialidades());
        Optional<Especialidades> especialidades = especialidadesRepository.findByIdAndEstado(1, Estado.ACTIVE);
        assertThat(especialidades).isPresent();
        assertThat(especialidades.get().getEstado()).isEqualTo(Estado.ACTIVE);
    }

}