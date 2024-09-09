package mx.gob.pjpuebla.trials.core.tipojuicio;

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

import java.util.Optional;

import static mx.gob.pjpuebla.trials.core.tipojuicio.TipoJuicioSetUp.createTipoJuicio;
import static org.assertj.core.api.Assertions.assertThat;


@DataJpaTest(properties = {"spring.jpa.properties.hibernate.hbm2ddl.auto: create-drop"})
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
class TipoJuicioRepositoryTest extends AuditConfigTest {
    @Autowired
    private TipoJuicioRepository tipoJuicioRepository;

    @Test
    void findByAllAndEstadoActive() {
        TipoJuicio validTipoJuicio = createTipoJuicio(null, null);
        tipoJuicioRepository.save(validTipoJuicio);
        ExampleMatcher exampleMatcher = ExampleMatcher.matching()
                .withMatcher("nombre", ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase())
                .withMatcher("estado", ExampleMatcher.GenericPropertyMatchers.ignoreCase());

        Page<TipoJuicio> page = tipoJuicioRepository.findAll(Example.of(new TipoJuicio().setNombre("Laboral").setEstado(Estado.ACTIVE), exampleMatcher), PageRequest.of(0, 20));
        assertThat(page.get()).hasSize(1);
    }

    @Test
    void findByIdAndEstadoActive() {
        TipoJuicio validTipoJuicio = tipoJuicioRepository.save(createTipoJuicio(null, null));
        Optional<TipoJuicio> tipoJuicio = tipoJuicioRepository.findByIdAndEstado(validTipoJuicio.getId(), Estado.ACTIVE);
        assertThat(tipoJuicio).isPresent();
        assertThat(tipoJuicio.get().getEstado()).isEqualTo(Estado.ACTIVE);
    }
}