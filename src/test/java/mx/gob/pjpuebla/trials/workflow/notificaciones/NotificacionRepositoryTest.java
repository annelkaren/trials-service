package mx.gob.pjpuebla.trials.workflow.notificaciones;

import mx.gob.pjpuebla.trials.core.utils.audit.AuditConfigTest;
import mx.gob.pjpuebla.trials.util.enums.TipoNotificacion;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.jdbc.Sql;

import static org.assertj.core.api.Assertions.assertThat;


@DataJpaTest(properties = {"spring.jpa.properties.hibernate.hbm2ddl.auto: create-drop"})
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@Sql(value = {
        "/scripts/INSERT_DOMICILIOS.sql",
        "/scripts/INSERT_DISTRITOS.sql",
        "/scripts/INSERT_SEDES.sql",
        "/scripts/INSERT_MATERIAS.sql",
        "/scripts/INSERT_TIPO_SISTEMAS.sql",
        "/scripts/INSERT_TIPO_JUICIOS.sql",
        "/scripts/INSERT_ESCOLARIDADES.sql",
        "/scripts/INSERT_ESTADO_CIVIL.sql",
        "/scripts/INSERT_JUZGADOS.sql",
        "/scripts/INSERT_PERSONAS.sql",
        "/scripts/INSERT_TIPO_PIEZAS.sql",
        "/scripts/INSERT_CARPETAS.sql",
        "/scripts/INSERT_NOTIFICACIONES.sql",
}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
@Sql(value = {
        "/scripts/DELETE_NOTIFICACIONES.sql",
        "/scripts/DELETE_CARPETAS.sql",
        "/scripts/DELETE_TIPO_PIEZAS.sql",
        "/scripts/DELETE_PERSONAS.sql",
        "/scripts/DELETE_ESTADO_CIVIL.sql",
        "/scripts/DELETE_ESCOLARIDADES.sql",
        "/scripts/DELETE_JUZGADOS.sql",
        "/scripts/DELETE_TIPO_JUICIOS.sql",
        "/scripts/DELETE_TIPO_SISTEMAS.sql",
        "/scripts/DELETE_MATERIAS.sql",
        "/scripts/DELETE_SEDES.sql",
        "/scripts/DELETE_DISTRITOS.sql",
        "/scripts/DELETE_DOMICILIOS.sql",

}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_CLASS)
class NotificacionRepositoryTest  extends AuditConfigTest {

    @Autowired
    private NotificacionRepository notificacionRepository;

    @Test
    void testGetNotificacionByTipo() {

        TipoNotificacion tipo = TipoNotificacion.ESTRADO;
        Page<Notificacion> result = notificacionRepository.getNotificacionByTipo(tipo, PageRequest.of(0,20));

        assertThat(result).isNotNull();
        assertThat(result.getContent()).isNotEmpty();
        assertThat(result.getContent()).allMatch(notificacion -> notificacion.getTipoNotificacion() == tipo);

    }


}