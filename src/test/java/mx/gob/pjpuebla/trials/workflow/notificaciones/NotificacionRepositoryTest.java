package mx.gob.pjpuebla.trials.workflow.notificaciones;

import mx.gob.pjpuebla.trials.core.utils.audit.AuditConfigTest;
import mx.gob.pjpuebla.trials.util.enums.EstadoNotificacion;
import mx.gob.pjpuebla.trials.util.enums.TipoNotificacion;
import mx.gob.pjpuebla.trials.workflow.notificaciondetalle.NotificacionesDetalles;
import mx.gob.pjpuebla.trials.workflow.notificaciondetalle.NotificacionesDetallesRepository;
import mx.gob.pjpuebla.trials.workflow.notificaciones.records.DocumentoDetalleRecord;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;
import java.util.Optional;

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
        "/scripts/INSERT_DOCUMENTOS.sql",

        "/scripts/INSERT_TIPO_PARTES.sql",
        "/scripts/INSERT_PERSONAS_DOCUMENTOS.sql",
        "/scripts/INSERT_DOCUMENTO_CONTENIDO.sql",
        "/scripts/INSERT_DOCUMENTO_DETALLE.sql",

        "/scripts/INSERT_NOTIFICACIONES.sql",
        "/scripts/INSERT_NOTIFICACIONES_DETALLES.sql",
}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
@Sql(value = {
        "/scripts/DELETE_NOTIFICACIONES_DETALLES.sql",
        "/scripts/DELETE_NOTIFICACIONES.sql",
        "/scripts/DELETE_DOCUMENTO_DETALLE.sql",
        "/scripts/DELETE_DOCUMENTO_CONTENIDO.sql",
        "/scripts/DELETE_PERSONAS_DOCUMENTOS.sql",
        "/scripts/DELETE_TIPO_PARTES.sql",
        "/scripts/DELETE_DOCUMENTOS.sql",
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
class NotificacionRepositoryTest extends AuditConfigTest {

    @Autowired
    private NotificacionRepository notificacionRepository;

    @Autowired
    private NotificacionesDetallesRepository notificacionesDetallesRepository;

    @Test
    void testGetNotificacionByTipo() {

        TipoNotificacion tipo = TipoNotificacion.ESTRADO;
        EstadoNotificacion estado = EstadoNotificacion.PENDIENTE_DE_ASIGNAR;
        Page<Notificacion> result = notificacionRepository.getNotificacionByTipo(tipo, estado, PageRequest.of(0, 20));

        assertThat(result).isNotNull();
        assertThat(result.getContent()).isNotEmpty();
        assertThat(result.getContent()).allMatch(notificacion -> notificacion.getTipoNotificacion() == tipo &&
                notificacion.getEstadoNotificacion() == estado);

    }

    @Test
    void testFindDocumentoDetalleByDocumentoId() {
        Integer documentoId = 6;
        List<DocumentoDetalleRecord> result = notificacionRepository.findDocumentoDetalleByDocumentoId(documentoId);
        assertThat(result).isNotEmpty();

        DocumentoDetalleRecord documentoDetalleRecord = result.get(0);

        assertThat(documentoDetalleRecord.fechaResolucion()).isNotNull();
        assertThat(documentoDetalleRecord.fechaPublicacion()).isNotNull();
        assertThat(documentoDetalleRecord.fechaResolucion()).isEqualTo("1990-10-10");
        assertThat(documentoDetalleRecord.fechaPublicacion()).isEqualTo("1990-10-10");
    }
    @Test
    void testCountNotificacionesByListaEstradoId() {
        Integer listaEstradoId = 1;
        long count = notificacionRepository.countNotificacionesByListaEstradoId(listaEstradoId);

        assertThat(count).isNotNegative();
    }

    @Test
    void testFindByNotificacionDocumentoId() {
        Pageable pageable = PageRequest.of(0, 20);
        Page<NotificacionesDetalles> result = notificacionesDetallesRepository.findByNotificacionDocumentoId(6, pageable);
        assertThat(result).isNotNull();
        assertThat(result.getContent()).isNotEmpty();
    }

    @Test void testFindNotificacionesTurnado() {
        Integer carpetaId = 1;
        List<Notificacion> notificaciones = notificacionRepository.findNotificacionesTurnado(carpetaId);
        assertThat(notificaciones)
                .isNotNull()
                .isNotEmpty()
                .allMatch(notificacion ->
                (notificacion.getTipoNotificacion() == TipoNotificacion.ESTRADO && notificacion.getEstadoNotificacion() != EstadoNotificacion.ASIGNADO)
                        || (notificacion.getTipoNotificacion() == TipoNotificacion.DOMICILIO && notificacion.getEstadoNotificacion() != EstadoNotificacion.NOTIFICADOS));
    }
}