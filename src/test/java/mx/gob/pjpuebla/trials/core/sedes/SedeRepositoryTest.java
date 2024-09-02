package mx.gob.pjpuebla.trials.core.sedes;

import mx.gob.pjpuebla.trials.core.utils.audit.AuditConfigTest;
import mx.gob.pjpuebla.trials.util.Estado;
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
class SedeRepositoryTest extends AuditConfigTest {

    @Autowired
    private SedeRepository sedeRepository;

    @Test
    void findByIdAndEstadoActive() {
        Sede entity = sedeRepository.save(SedeSetUp.createSede(Estado.ACTIVE));
        List<Estado> estados = Arrays.asList(Estado.INACTIVE, Estado.ACTIVE);
        Optional<SedeRecord> sede = sedeRepository.findByIdAndEstadoIn(entity.getId(), estados);
        assertThat(sede).isPresent();
        assertThat(sede.get().estado()).isEqualTo(Estado.ACTIVE);
    }

    @Test
    void findByIdAndEstadoInactive() {
        Sede entity = sedeRepository.save(SedeSetUp.createSede(Estado.INACTIVE));
        List<Estado> estados = Arrays.asList(Estado.INACTIVE, Estado.ACTIVE);
        Optional<SedeRecord> sede = sedeRepository.findByIdAndEstadoIn(entity.getId(), estados);
        assertThat(sede).isPresent();
        assertThat(sede.get().estado()).isEqualTo(Estado.INACTIVE);
    }
}
