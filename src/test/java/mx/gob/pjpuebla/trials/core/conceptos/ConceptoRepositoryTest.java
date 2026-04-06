package mx.gob.pjpuebla.trials.core.conceptos;

import mx.gob.pjpuebla.trials.core.utils.audit.AuditConfigTest;
import mx.gob.pjpuebla.trials.util.enums.Estado;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(properties = {
                "spring.jpa.properties.hibernate.hbm2ddl.auto: create-drop"
})
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@Sql(value = {
                "/scripts/INSERT_MATERIAS.sql",
                "/scripts/INSERT_TIPO_SISTEMAS.sql",
                "/scripts/INSERT_TIPO_JUICIOS.sql",
                "/scripts/INSERT_DISTRITOS.sql",
                "/scripts/INSERT_DOMICILIOS.sql",
                "/scripts/INSERT_SEDES.sql",
                "/scripts/INSERT_TIPO_OFICIALIAS.sql",
                "/scripts/INSERT_JUZGADOS.sql",
                "/scripts/INSERT_JUZGADO_TIPOJUICIO.sql",
                "/scripts/INSERT_OFICIALIAS.sql",
                "/scripts/INSERT_OFICIALIAS_JUZGADO.sql",
                "/scripts/INSERT_CONCEPTOS.sql"
}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
@Sql(value = {
                "/scripts/DELETE_OFICIALIAS_JUZGADOS.sql",
                "/scripts/DELETE_OFICIALIAS.sql",
                "/scripts/DELETE_JUZGADO_TIPOJUICIO.sql",
                "/scripts/DELETE_JUZGADOS.sql",
                "/scripts/DELETE_TIPO_OFICIALIAS.sql",
                "/scripts/DELETE_SEDES.sql",
                "/scripts/DELETE_CONCEPTOS.sql",
                "/scripts/DELETE_TIPO_JUICIOS.sql",
                "/scripts/DELETE_TIPO_SISTEMAS.sql",
                "/scripts/DELETE_MATERIAS.sql",
                "/scripts/DELETE_SEDES.sql",
                "/scripts/DELETE_DOMICILIOS.sql",
                "/scripts/DELETE_DISTRITOS.sql",
}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_CLASS)
class ConceptoRepositoryTest extends AuditConfigTest {
        @Autowired
        private ConceptoRepository conceptoRepository;

        @Test
        void findAllConceptos_shouldReturnResultsBasedOnCriteria() {
                List<Estado> estados = List.of(Estado.ACTIVE, Estado.INACTIVE);
                String key = "adjuntar";
                Pageable pageable = PageRequest.of(0, 10);
                Page<Concepto> result = conceptoRepository.findAllConceptos(key, estados, pageable, key, null, key);
                assertThat(result).isNotEmpty();
                assertThat(result.getContent()).allMatch(concepto -> concepto.getEstado().equals(Estado.ACTIVE)
                                || concepto.getEstado().equals(Estado.INACTIVE));
                assertThat(result.getContent()).allMatch(concepto -> concepto.getNombre().toLowerCase().contains(key)
                                || (concepto.getTipoJuicio() != null
                                                && concepto.getTipoJuicio().getNombre().toLowerCase().contains(key)));
        }
}