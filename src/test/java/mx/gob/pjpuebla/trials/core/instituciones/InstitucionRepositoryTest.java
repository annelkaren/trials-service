package mx.gob.pjpuebla.trials.core.instituciones;

import mx.gob.pjpuebla.trials.core.instituciones.records.InstitucionRecord;
import mx.gob.pjpuebla.trials.core.instituciones.records.InstitucionRecordResponse;
import mx.gob.pjpuebla.trials.core.utils.audit.AuditConfigTest;
import mx.gob.pjpuebla.trials.util.enums.Estado;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.jdbc.Sql;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(properties = {
        "spring.jpa.properties.hibernate.hbm2ddl.auto: create-drop"
})
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@Sql(value = {
        "/scripts/INSERT_DOMICILIOS.sql",
        "/scripts/INSERT_DISTRITOS.sql",
        "/scripts/INSERT_INSTITUCIONES.sql"
}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
@Sql(value = {
        "/scripts/DELETE_INSTITUCIONES.sql",
        "/scripts/DELETE_DOMICILIOS.sql",
        "/scripts/DELETE_DISTRITOS.sql",
}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_CLASS)
class InstitucionRepositoryTest extends AuditConfigTest {

    @Autowired
    private InstitucionRepository institucionRepository;

    @Test
    void findByIdAndEstadoActive() {
        List<Estado> estados = Arrays.asList(Estado.INACTIVE, Estado.ACTIVE);
        Optional<InstitucionRecordResponse> entity = institucionRepository.findByIdAndEstadoIn(1,
                estados);
        assertThat(entity).isPresent();
        assertThat(entity.get().estado()).isEqualTo(Estado.ACTIVE);
    }

    @Test
    void findAllEstadoIn_return_page() {

        List<Estado> estados = Arrays.asList(Estado.INACTIVE, Estado.ACTIVE);

        Page<InstitucionRecord> result = institucionRepository.findAllEstadoIn(estados, PageRequest.of(0, 1));

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).id()).isEqualTo(1);
        assertThat(result.getContent().get(0).nombre()).isEqualTo("INSTITUCION 1");
        assertThat(result.getContent().get(0).telefono()).isEqualTo("2221234567");
    }

    @Test
    void findByNombre() {
        String nombreInstitucion = "INSTITUCION 1";
        Optional<Institucion> institucion = institucionRepository.findByNombre(nombreInstitucion);
        assertThat(institucion).isPresent();
        assertThat(institucion.get().getNombre()).isEqualTo(nombreInstitucion);
    }

}
