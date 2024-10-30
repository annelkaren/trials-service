package mx.gob.pjpuebla.trials.core.tipojuicio;

import mx.gob.pjpuebla.trials.core.utils.audit.AuditConfigTest;
import mx.gob.pjpuebla.trials.util.enums.Estado;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;
import java.util.Optional;

import static mx.gob.pjpuebla.trials.core.tipojuicio.TipoJuicioSetUp.createTipoJuicio;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(properties = {"spring.jpa.properties.hibernate.hbm2ddl.auto: create-drop"})
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
        "/scripts/INSERT_OFICIALIAS_JUZGADO.sql"
}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
@Sql(value = {
        "/scripts/DELETE_OFICIALIAS_JUZGADOS.sql",
        "/scripts/DELETE_OFICIALIAS.sql",
        "/scripts/DELETE_JUZGADO_TIPOJUICIO.sql",
        "/scripts/DELETE_JUZGADOS.sql",
        "/scripts/DELETE_TIPO_OFICIALIAS.sql",
        "/scripts/DELETE_SEDES.sql",
        "/scripts/DELETE_TIPO_JUICIOS.sql",
        "/scripts/DELETE_TIPO_SISTEMAS.sql",
        "/scripts/DELETE_MATERIAS.sql",
        "/scripts/DELETE_SEDES.sql",
        "/scripts/DELETE_DOMICILIOS.sql",
        "/scripts/DELETE_DISTRITOS.sql"
}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_CLASS)
class TipoJuicioRepositoryTest extends AuditConfigTest {

    @Autowired
    private TipoJuicioRepository tipoJuicioRepository;

    @Test
    void findByAllAndEstadoActive() {
        TipoJuicio validTipoJuicio = createTipoJuicio(null, null);
        tipoJuicioRepository.save(validTipoJuicio);
        ExampleMatcher exampleMatcher = ExampleMatcher.matching()
                .withMatcher("nombre", ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase())
                .withMatcher("estado", ExampleMatcher.GenericPropertyMatchers.ignoreCase());

        Page<TipoJuicio> page = tipoJuicioRepository.findAll(Example.of(new TipoJuicio().setNombre("Laboral").setEstado(Estado.ACTIVE), exampleMatcher), PageRequest.of(0, 20));
        assertThat(page.getSize()).isPositive();
    }

    @Test
    void findByIdAndEstado() {
        Optional<TipoJuicio> result = tipoJuicioRepository.findByIdAndEstado(100, Estado.ACTIVE);
        assertThat(result).isPresent();
    }

    @Test
    void findByNombreIgnoreCase() {
        Optional<TipoJuicio> result = tipoJuicioRepository.findByNombreIgnoreCase("Laboral (Tradicional)");
        assertThat(result).isPresent();
    }

    @Test
    void findByCentroTrabajo(){
        Integer oficialiaId = 51;

        Page<TipoJuicio> page = tipoJuicioRepository.findByCentroTrabajo(oficialiaId, null, PageRequest.of(0, 20));

        assertThat(page).isNotEmpty()
                .anyMatch(tj->tj.getNombre().equals("Laboral (Tradicional)"))
                .allMatch(tj->tj.getTipoJuicioPadreOral()==null && tj.getTipoJuicioPadreTrad()==null);
    }

    @Test
    void findByMateriaId_shouldReturnTipoJuiciosWithNullPadres() {
    Integer materiaId = 150; 
    List<TipoJuicio> tipoJuicios = tipoJuicioRepository.findByMateriaId(materiaId);

    assertThat(tipoJuicios)
            .isNotEmpty()
            .allMatch(tj -> tj.getMateria().getId().equals(materiaId))
            .allMatch(tj -> tj.getTipoJuicioPadreOral() == null && tj.getTipoJuicioPadreTrad() == null);
    }
}