package mx.gob.pjpuebla.trials.core.religiones;

import mx.gob.pjpuebla.trials.core.utils.audit.AuditConfigTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(properties = {
                "spring.jpa.properties.hibernate.hbm2ddl.auto: create-drop"
})
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@Sql(value = {
                "/scripts/INSERT_RELIGIONES.sql",
}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
@Sql(value = {
                "/scripts/DELETE_RELIGIONES.sql",
}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_CLASS)
class ReligionesRepositoryTest extends AuditConfigTest {

        @Autowired
        private ReligionesRepository religionesRepository;

        @Test
        void findAllByidReligion() {
                List<ReligionesRecord> entity = religionesRepository.findAllByidReligion("Católico");
                assertThat(entity).isNotEmpty();
                assertThat(entity.get(0).nombreReligion()).isEqualTo("Católico Apostólico Romano");
        }
}
