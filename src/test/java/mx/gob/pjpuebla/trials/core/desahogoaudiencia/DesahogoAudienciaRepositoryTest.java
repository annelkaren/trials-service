package mx.gob.pjpuebla.trials.core.desahogoaudiencia;

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
                "/scripts/INSERT_DESAHOGO_AUDIENCIA.sql"
}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
@Sql(value = {
                "/scripts/DELETE_DESAHOGO_AUDIENCIA.sql"
}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_CLASS)
class DesahogoAudienciaRepositoryTest extends AuditConfigTest {

        @Autowired
        private DesahogoAudienciaRepository desahogoAudienciaRepository;

        @Test
        void findAll() {
                List<DesahogoAudiencia> entity = desahogoAudienciaRepository.findAll();
                assertThat(entity).isNotEmpty();
                assertThat(entity.get(0).getId()).isEqualTo(1);
                assertThat(entity.get(0).getNombre()).isEqualTo("Conclusión por Convenio");
                assertThat(entity.get(0).getKey()).isEqualTo("CON_POR_CO");
                assertThat(entity.get(1).getId()).isEqualTo(2);
                assertThat(entity.get(1).getNombre()).isEqualTo("Desistimiento de la Acción");
                assertThat(entity.get(1).getKey()).isEqualTo("DES_DE_ACC");
        }
}