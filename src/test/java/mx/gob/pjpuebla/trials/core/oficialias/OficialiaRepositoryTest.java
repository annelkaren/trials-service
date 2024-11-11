package mx.gob.pjpuebla.trials.core.oficialias;

import mx.gob.pjpuebla.trials.core.utils.audit.AuditConfigTest;
import mx.gob.pjpuebla.trials.util.enums.Estado;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
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
        "/scripts/INSERT_SEDES.sql",
        "/scripts/INSERT_TIPO_OFICIALIAS.sql",
        "/scripts/INSERT_OFICIALIAS.sql",
}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
@Sql(value = {
        "/scripts/DELETE_OFICIALIAS.sql",
        "/scripts/DELETE_TIPO_OFICIALIAS.sql",
        "/scripts/DELETE_SEDES.sql",
        "/scripts/DELETE_DISTRITOS.sql",
        "/scripts/DELETE_DOMICILIOS.sql",
}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_CLASS)
class OficialiaRepositoryTest extends AuditConfigTest {

    @Autowired
    private OficialiaRepository oficialiaRepository;

    @Test
    void findByIdAndEstadoActive() {
        List<Estado> estados = Arrays.asList(Estado.INACTIVE, Estado.ACTIVE);
        Optional<OficialiaRecord> entity = oficialiaRepository.findByIdAndEstadoIn(51, estados);
        assertThat(entity).isPresent();
        assertThat(entity.get().estado()).isEqualTo(Estado.ACTIVE);
    }

    @Test
    void findOficialiasComunes() {
        List<Oficialia> oficialiasComunes = oficialiaRepository.findOficialiaComun();

        assertThat(oficialiasComunes).isNotEmpty().anyMatch(ofi -> ofi.getTipoOficialia().getNombre().contains("Común"));
    }

    @Test
    void findByNombre() {
        String nombreOficialia = "OFICIALIA";
        Optional<Oficialia> oficialia = oficialiaRepository.findByNombreIgnoreCase(nombreOficialia);
        assertThat(oficialia).isPresent();
        assertThat(oficialia.get().getNombre()).isEqualTo(nombreOficialia);
    }
}
