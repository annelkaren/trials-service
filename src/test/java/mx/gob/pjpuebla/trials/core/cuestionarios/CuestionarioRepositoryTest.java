package mx.gob.pjpuebla.trials.core.cuestionarios;

import mx.gob.pjpuebla.trials.core.utils.audit.AuditConfigTest;
import mx.gob.pjpuebla.trials.util.enums.ListCuestionario;
import mx.gob.pjpuebla.trials.util.enums.TipoPregunta;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(properties = {
        "spring.jpa.properties.hibernate.hbm2ddl.auto: create-drop"
})
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@Sql(value = {
        "/scripts/INSERT_CUESTIONARIO.sql",
}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
@Sql(value = {
        "/scripts/DELETE_CUESTIONARIO.SQL",
}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_CLASS)
class CuestionarioRepositoryTest extends AuditConfigTest {

    @Autowired
    private CuestionarioRepository cuestionarioRepository;

    @Test
    void findByLista_Succes() {
        List<Cuestionario> cuestionarios = cuestionarioRepository.findByLista(ListCuestionario.LISTA_SALAS);
        assertThat(cuestionarios).hasSize(3)
                .isNotNull()
                .isNotEmpty();
    }
}