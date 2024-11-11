package mx.gob.pjpuebla.trials.core.juzgados;

import mx.gob.pjpuebla.trials.core.utils.audit.AuditConfigTest;
import mx.gob.pjpuebla.trials.util.enums.Estado;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
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
        "/scripts/INSERT_SEDES.sql",
        "/scripts/INSERT_MATERIAS.sql",
        "/scripts/INSERT_TIPO_SISTEMAS.sql",
        "/scripts/INSERT_TIPO_JUICIOS.sql",
        "/scripts/INSERT_JUZGADOS.sql",
        "/scripts/INSERT_JUZGADO_TIPOJUICIO.sql"
}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
@Sql(value = {
        "/scripts/DELETE_JUZGADO_TIPOJUICIO.sql",
        "/scripts/DELETE_JUZGADOS.sql",
        "/scripts/DELETE_TIPO_JUICIOS.sql",
        "/scripts/DELETE_TIPO_SISTEMAS.sql",
        "/scripts/DELETE_MATERIAS.sql",
        "/scripts/DELETE_SEDES.sql",
        "/scripts/DELETE_DISTRITOS.sql",
        "/scripts/DELETE_DOMICILIOS.sql",
}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_CLASS)
class JuzgadoRepositoryTest extends AuditConfigTest {

    @Autowired
    private JuzgadoRepository juzgadoRepository;

    @Test
    void findAll() {
        String key = "mer";
        List<Estado> estados = Arrays.asList(Estado.INACTIVE, Estado.ACTIVE);
        Page<Juzgado> page = juzgadoRepository.findAll(key, estados, PageRequest.of(0, 20));
        assertThat(page.get()).hasSize(1);
    }

    @Test
    void findByIdAndEstadoActive() {
        List<Estado> estados = Arrays.asList(Estado.INACTIVE, Estado.ACTIVE);
        Optional<Juzgado> entity = juzgadoRepository.findByIdAndEstadoIn(51, estados);
        assertThat(entity).isPresent().get().hasFieldOrPropertyWithValue("estado", Estado.ACTIVE);
        assertThat(entity.get().getTipoJuicios()).hasSize(3);
    }

    @Test
    void findTipoJuiciosByJuzgadoId(){
        List<JuzgadoTipoJuiciosRecord> list = juzgadoRepository.findTipoJuiciosByJuzgadoId(51);
        assertThat(list).isNotNull().isNotEmpty();
    }

    @Test
    void findAllByEstadoAutocomplete() {
        Estado estado = Estado.ACTIVE;
        String key = "juzgado";
        Page<JuzgadoRecordItem> page = juzgadoRepository.findAllByEstadoAutocomplete(estado, key, PageRequest.of(0, 10));
        assertThat(page).isNotNull();
        assertThat(page.getContent()).isNotEmpty();

        page.getContent().forEach(record -> {
            assertThat(record.estado()).isEqualTo(Estado.ACTIVE);
        });
    }

    @Test
    void findByNombre() {
        String nombreJuzgado = "Juzgado Mercantil";
        Optional<Juzgado> juzgado = juzgadoRepository.findByNombreIgnoreCase(nombreJuzgado);
        assertThat(juzgado).isPresent();
        assertThat(juzgado.get().getNombre()).isEqualTo(nombreJuzgado);
    }

}