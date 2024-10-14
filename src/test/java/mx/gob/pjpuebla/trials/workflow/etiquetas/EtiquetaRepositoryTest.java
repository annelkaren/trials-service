package mx.gob.pjpuebla.trials.workflow.etiquetas;

import mx.gob.pjpuebla.trials.core.utils.audit.AuditConfigTest;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(properties = {"spring.jpa.properties.hibernate.hbm2ddl.auto: create-drop"})
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@Sql(value = {
        "/scripts/INSERT_MATERIAS.sql",
        "/scripts/INSERT_TIPO_SISTEMAS.sql",
        "/scripts/INSERT_TIPO_JUICIOS.sql",
        "/scripts/INSERT_TIPOJUICIO_ETIQUETAS.sql",
}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
@Sql(value = {
        "/scripts/DELETE_TIPOJUICIO_ETIQUETAS.sql",
        "/scripts/DELETE_TIPO_JUICIOS.sql",
        "/scripts/DELETE_TIPO_SISTEMAS.sql",
        "/scripts/DELETE_MATERIAS.sql"
}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_CLASS)
class EtiquetaRepositoryTest extends AuditConfigTest {

    @Autowired
    private EtiquetaRepository etiquetaRepository;

    @Test
    void getAllByTipoJuicioId() {
        List<Etiqueta> list = etiquetaRepository.findByTipoJuicioId(100);

        assertThat(list)
                .isNotNull()
                .hasSize(2);
    }
    @Test
    void getEtiquetaByNombreAndTipoJuicio(){
        String etiqueta = tipoJuicioEtiquetaRepository.getEtiquetaByNombreAndTipoJuicio(100, "documento");
        assertThat(etiqueta).isEqualTo("Demanda");
    }

}
