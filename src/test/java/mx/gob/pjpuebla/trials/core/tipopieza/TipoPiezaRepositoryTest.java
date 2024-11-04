package mx.gob.pjpuebla.trials.core.tipopieza;

import mx.gob.pjpuebla.trials.core.utils.audit.AuditConfigTest;
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
        "/scripts/INSERT_TIPO_PIEZA.sql",
}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
@Sql(value = {
        "/scripts/DELETE_TIPO_PIEZA.sql",
}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_CLASS)
class TipoPiezaRepositoryTest extends AuditConfigTest {

    @Autowired
    private TipoPiezaRepository tipoPiezaRepository;

    @Test
    void findAll_Succes() {
        List<TipoPieza> page = tipoPiezaRepository.findAll();
        assertThat(page).hasSize(4)
                .isNotNull()
                .isNotEmpty();
    }
}