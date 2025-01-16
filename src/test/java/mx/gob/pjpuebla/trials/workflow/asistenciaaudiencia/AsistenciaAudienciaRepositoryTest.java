package mx.gob.pjpuebla.trials.workflow.asistenciaaudiencia;

import mx.gob.pjpuebla.trials.core.utils.audit.AuditConfigTest;
import mx.gob.pjpuebla.trials.litigante.LitiganteExpedienteAudienciaRecord;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


@DataJpaTest(properties = {"spring.jpa.properties.hibernate.hbm2ddl.auto: create-drop"})
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@Sql(value = {
        "/scripts/INSERT_TIPO_AUDIENCIA.sql",
        "/scripts/INSERT_DOMICILIOS.sql",
        "/scripts/INSERT_ESTADO_CIVIL.sql",
        "/scripts/INSERT_ESCOLARIDADES.sql",
        "/scripts/INSERT_MATERIAS.sql",
        "/scripts/INSERT_DISTRITOS.sql",
        "/scripts/INSERT_SEDES.sql",
        "/scripts/INSERT_JUZGADOS.sql",
        "/scripts/INSERT_PERSONAS.sql",
        "/scripts/INSERT_BLOQUES.sql",
        "/scripts/INSERT_SALAS.sql",
        "/scripts/INSERT_TIPO_SISTEMAS.sql",
        "/scripts/INSERT_TIPO_JUICIOS.sql",
        "/scripts/INSERT_TIPO_PIEZAS.sql",
        "/scripts/INSERT_CARPETAS.sql",
        "/scripts/INSERT_AUDIENCIAS.sql",
        "/scripts/INSERT_TIPO_PARTES.sql",
        "/scripts/INSERT_PERSONAS_DOCUMENTOS.sql",
        "/scripts/INSERT_DOCUMENTOS_IDENTIFICACION.sql",
        "/scripts/INSERT_ASISTENCIA_AUDIENCIA.sql",
}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
@Sql(value = {
        "/scripts/DELETE_ASISTENCIA_AUDIENCIA.sql",
        "/scripts/DELETE_AUDIENCIAS.sql",
        "/scripts/DELETE_DOCUMENTOS_IDENTIFICACION.sql",
        "/scripts/DELETE_PERSONAS_DOCUMENTOS.sql",
        "/scripts/DELETE_TIPO_PARTES.sql",
        "/scripts/DELETE_CARPETAS.sql",
        "/scripts/DELETE_TIPO_PIEZAS.sql",
        "/scripts/DELETE_TIPO_JUICIOS.sql",
        "/scripts/DELETE_TIPO_SISTEMAS.sql",
        "/scripts/DELETE_SALAS.sql",
        "/scripts/DELETE_BLOQUES.sql",
        "/scripts/DELETE_PERSONAS.sql",
        "/scripts/DELETE_JUZGADOS.sql",
        "/scripts/DELETE_SEDES.sql",
        "/scripts/DELETE_DISTRITOS.sql",
        "/scripts/DELETE_MATERIAS.sql",
        "/scripts/DELETE_ESCOLARIDADES.sql",
        "/scripts/DELETE_ESTADO_CIVIL.sql",
        "/scripts/DELETE_DOMICILIOS.sql",
        "/scripts/DELETE_TIPO_AUDIENCIA.sql",
}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_CLASS)
class AsistenciaAudienciaRepositoryTest extends AuditConfigTest {

    @Autowired
    private AsistenciaAudienciaRepository asistenciaAudienciaRepository;

    @Test
    void testFindByPersonaDocumentoIdAndAudienciaId() {
        AsistenciaAudiencia resultado = asistenciaAudienciaRepository.findByPersonaDocumentoIdAndAudienciaId(1, 1);
        assertThat(resultado).isNotNull();
    }

    @Test
    void  testgetAllAudicenciasByUser(){
        List<LitiganteExpedienteAudienciaRecord> litiganteExpedienteAudienciaRecord = asistenciaAudienciaRepository.getAllAudicenciasByUser("example@example.com", Pageable.ofSize(2));
        assertThat(litiganteExpedienteAudienciaRecord).isNotNull();
    }

}