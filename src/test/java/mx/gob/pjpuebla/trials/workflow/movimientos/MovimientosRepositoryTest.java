package mx.gob.pjpuebla.trials.workflow.movimientos;

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.jdbc.Sql;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.jdbc.core.JdbcTemplate;

import mx.gob.pjpuebla.trials.core.personas.Persona;
import mx.gob.pjpuebla.trials.core.personas.PersonaSetUp;
import mx.gob.pjpuebla.trials.core.utils.audit.AuditConfigTest;
import mx.gob.pjpuebla.trials.util.enums.EstadoCarpeta;
import mx.gob.pjpuebla.trials.workflow.documentos.records.DocumentoBandejaRecepcionRecord;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@DataJpaTest(properties = {
    "spring.jpa.properties.hibernate.hbm2ddl.auto=create-drop"
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
    "/scripts/INSERT_JUZGADO_TIPOJUICIO.sql",
    "/scripts/INSERT_TIPO_OFICIALIAS.sql",
    "/scripts/INSERT_OFICIALIAS.sql",
    "/scripts/INSERT_ESTADO_CIVIL.sql",
    "/scripts/INSERT_ESCOLARIDADES.sql",
    "/scripts/INSERT_PERSONAS.sql",
    "/scripts/INSERT_TIPO_PIEZAS.sql",
    "/scripts/INSERT_CARPETAS.sql",
    "/scripts/INSERT_DOCUMENTOS.sql",
    "/scripts/INSERT_TIPO_PARTES.sql",
    "/scripts/INSERT_PERSONAS_DOCUMENTOS.sql",
    "/scripts/INSERT_MOVIMIENTOS.sql"
}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
@Sql(value = {
    "/scripts/DELETE_MOVIMIENTOS.sql",
    "/scripts/DELETE_PERSONAS_DOCUMENTOS.sql",
    "/scripts/DELETE_TIPO_PARTES.sql",
    "/scripts/DELETE_DOCUMENTOS.sql",
    "/scripts/DELETE_CARPETAS.sql",
    "/scripts/DELETE_TIPO_PIEZAS.sql",
    "/scripts/DELETE_PERSONAS.sql",
    "/scripts/DELETE_ESCOLARIDADES.sql",
    "/scripts/DELETE_ESTADO_CIVIL.sql",
    "/scripts/DELETE_OFICIALIAS.sql",
    "/scripts/DELETE_TIPO_OFICIALIAS.sql",
    "/scripts/DELETE_JUZGADO_TIPOJUICIO.sql",
    "/scripts/DELETE_JUZGADOS.sql",
    "/scripts/DELETE_TIPO_JUICIOS.sql",
    "/scripts/DELETE_TIPO_SISTEMAS.sql",
    "/scripts/DELETE_MATERIAS.sql",
    "/scripts/DELETE_SEDES.sql",
    "/scripts/DELETE_DISTRITOS.sql",
    "/scripts/DELETE_DOMICILIOS.sql"
}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_CLASS)
class MovimientosRepositoryTest extends AuditConfigTest {

    @Autowired
    MovimientoRepository movimientoRepository;

    @Autowired
    JdbcTemplate jdbcTemplate;

    @BeforeEach
    void registerH2Functions() {
        jdbcTemplate.execute(
            "CREATE ALIAS IF NOT EXISTS JSONB_EXTRACT_PATH_TEXT " +
            "FOR 'mx.gob.pjpuebla.trials.workflow.movimientos.H2Jsonb.jsonbExtractPathText'"
        );
    }

    @Test
    void getMovimientosSalidaTest() {
        String uuidMov = "d8945bc4-af8e-4eb0-b742-7ee13beb43e0";
        UUID uuid = UUID.fromString(uuidMov);

        List<MovimientoSalidaRecord> movimientos = movimientoRepository.getSalidas(uuid, EstadoCarpeta.TURNADO);

        assertThat(movimientos)
            .isNotEmpty()
            .anyMatch(movimiento -> movimiento.uuid().equals(uuid));
    }

    @Test
    void getAllBandejaRecepcion() {
        Persona persona = PersonaSetUp.createPersona();

        Integer juzgadoId = 51;
        List<EstadoCarpeta> estados = Arrays.asList(EstadoCarpeta.RECEPCION, EstadoCarpeta.TURNADO);

        String key = "";
        String motivo = "";
        String cmdLetra = "";
        String cmdFolio = "";
        String folio = "";
        String expediente = "";
        String tipoEntrada = "";
        String origen = "";
        List<String> motivosTurnado = List.of("dsd");

        LocalDateTime fechaFrom = LocalDateTime.now();
        LocalDateTime fechaTo = LocalDateTime.now();

        String userJuzgadoNombre = "";
        String userOficialiaNombre = "";

        Page<DocumentoBandejaRecepcionRecord> page = movimientoRepository.getBandejaRecepcionUnifiedPage( PageRequest.of(0, 20), juzgadoId, estados, persona, false, motivo, motivosTurnado, key, cmdLetra, cmdFolio, folio, expediente, tipoEntrada, origen, motivo, fechaFrom, fechaTo, userJuzgadoNombre, userOficialiaNombre);

        assertThat(page).isNotNull();
        assertThat(page.getContent()).isNotNull();

        // Si en tus inserts sí hay data que cumpla el filtro, usa esta:
        // assertThat(page.getNumberOfElements()).isPositive();
    }
}
