package mx.gob.pjpuebla.trials.workflow.folios;

import mx.gob.pjpuebla.trials.core.juzgados.Juzgado;
import mx.gob.pjpuebla.trials.core.utils.audit.AuditConfigTest;
import mx.gob.pjpuebla.trials.util.enums.TipoCarpeta;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(properties = { "spring.jpa.properties.hibernate.hbm2ddl.auto: create-drop" })
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@Sql(value = {
                "/scripts/INSERT_DOMICILIOS.sql",
                "/scripts/INSERT_DISTRITOS.sql",
                "/scripts/INSERT_SEDES.sql",
                "/scripts/INSERT_MATERIAS.sql",
                "/scripts/INSERT_TIPO_SISTEMAS.sql",
                "/scripts/INSERT_TIPO_JUICIOS.sql",
                "/scripts/INSERT_JUZGADOS.sql",
                "/scripts/INSERT_JUZGADO_FOLIOS.sql",
}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
@Sql(value = {
                "/scripts/DELETE_JUZGADO_FOLIOS.sql",
                "/scripts/DELETE_JUZGADOS.sql",
                "/scripts/DELETE_TIPO_JUICIOS.sql",
                "/scripts/DELETE_TIPO_SISTEMAS.sql",
                "/scripts/DELETE_MATERIAS.sql",
                "/scripts/DELETE_SEDES.sql",
                "/scripts/DELETE_DISTRITOS.sql",
                "/scripts/DELETE_DOMICILIOS.sql",
}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_CLASS)
class JuzgadoFoliosRepositoryTest extends AuditConfigTest {

        @Autowired
        private JuzgadoFoliosRepository juzgadoFoliosRepository;

        @Test
        void findByAllAndEstadoActive() {
                ExampleMatcher exampleMatcher = ExampleMatcher.matching()
                                .withMatcher("tipoDocumento",
                                                ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase())
                                .withMatcher("value", ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase())
                                .withMatcher("year", ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase());

                List<JuzgadoFolios> juzgadoFoliosList = juzgadoFoliosRepository.findAll(Example.of(
                                new JuzgadoFolios().setTipoCarpeta(TipoCarpeta.DEMANDA).setValue(1).setYear(2024),
                                exampleMatcher));

                assertThat(juzgadoFoliosList).hasSize(1);
        }

        @Test
        void findByIdAndEstadoActive() {
                Optional<JuzgadoFolios> juzgadoFoliosResult = juzgadoFoliosRepository.findByJuzgadoAndTipoCarpeta(
                                new Juzgado().setId(51).setVersion(0), TipoCarpeta.DEMANDA);
                assertThat(juzgadoFoliosResult).isPresent();
                assertThat(juzgadoFoliosResult.get().getTipoCarpeta()).isEqualTo(TipoCarpeta.DEMANDA);
        }
}