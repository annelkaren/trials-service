package mx.gob.pjpuebla.trials.core.materias;

import mx.gob.pjpuebla.trials.core.utils.audit.AuditConfigTest;
import mx.gob.pjpuebla.trials.util.Estado;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static mx.gob.pjpuebla.trials.core.materias.MateriaSetUp.createMateria;
import static org.assertj.core.api.Assertions.assertThat;


@DataJpaTest(properties = {
        "spring.jpa.properties.hibernate.hbm2ddl.auto: create-drop"
})
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
class MateriaRepositoryTest extends AuditConfigTest {

    @Autowired
    private MateriaRepository materiaRepository;

    @Test
    void findByIdAndEstadoActive() {
        materiaRepository.save(createMateria());
        Optional<Materia> materia = materiaRepository.findByIdAndEstado(1, Estado.ACTIVE);
        assertThat(materia.isPresent()).isTrue();
        assertThat(materia.get().getEstado()).isEqualTo(Estado.ACTIVE);
    }
}