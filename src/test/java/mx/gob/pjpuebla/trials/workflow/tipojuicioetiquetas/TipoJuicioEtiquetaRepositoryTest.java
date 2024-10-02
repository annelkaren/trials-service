package mx.gob.pjpuebla.trials.workflow.tipojuicioetiquetas;

import mx.gob.pjpuebla.trials.core.utils.audit.AuditConfigTest;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(properties = {
        "spring.jpa.properties.hibernate.hbm2ddl.auto: create-drop"
})
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@Disabled
class TipoJuicioEtiquetaRepositoryTest extends AuditConfigTest {

    @Autowired
    private TipoJuicioEtiquetaRepository tipoJuicioEtiquetaRepository;

    @Test
    void getAllByTipoJuicioId() {
        List<TipoJuicioEtiqueta> list = tipoJuicioEtiquetaRepository.findByTipoJuicioId(100);

        assertThat(list)
                .isNotNull()
                .hasSize(1);
    }
}
