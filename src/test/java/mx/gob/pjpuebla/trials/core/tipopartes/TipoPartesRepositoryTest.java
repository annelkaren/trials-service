package mx.gob.pjpuebla.trials.core.tipopartes;

import mx.gob.pjpuebla.trials.core.utils.audit.AuditConfigTest;
import mx.gob.pjpuebla.trials.util.enums.Estado;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
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
                "/scripts/INSERT_TIPO_SISTEMAS.sql",
                "/scripts/INSERT_MATERIAS.sql",
                "/scripts/INSERT_TIPO_JUICIOS.sql",
                "/scripts/INSERT_TIPO_PARTES.sql",
}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
@Sql(value = {
                "/scripts/DELETE_TIPO_PARTES.sql",
                "/scripts/DELETE_TIPO_JUICIOS.sql",
                "/scripts/DELETE_MATERIAS.sql",
                "/scripts/DELETE_TIPO_SISTEMAS.sql",
}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_CLASS)
class TipoPartesRepositoryTest extends AuditConfigTest {

        @Autowired
        private TipoPartesRepository tipoPartesRepository;

        @Test
        void findByAllAndEstadoActive() {
                ExampleMatcher exampleMatcher = ExampleMatcher.matching()
                                .withMatcher("nombre", ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase())
                                .withMatcher("estado", ExampleMatcher.GenericPropertyMatchers.ignoreCase());

                Page<TipoPartes> page = tipoPartesRepository.findAll(
                                Example.of(new TipoPartes().setNombre("A").setEstado(Estado.ACTIVE), exampleMatcher),
                                PageRequest.of(0, 20));
                assertThat(page).isNotNull();
                assertThat(page.getSize()).isPositive();

        }

        @Test
        void findByIdAndEstadoActive() {
                Optional<TipoPartes> tipoPartes = tipoPartesRepository.findByIdAndEstado(1, Estado.ACTIVE);
                assertThat(tipoPartes).isPresent();
                assertThat(tipoPartes.get().getEstado()).isEqualTo(Estado.ACTIVE);
        }

}
