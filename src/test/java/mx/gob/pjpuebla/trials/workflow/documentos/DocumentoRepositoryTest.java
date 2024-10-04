package mx.gob.pjpuebla.trials.workflow.documentos;

import mx.gob.pjpuebla.trials.core.utils.audit.AuditConfigTest;
import mx.gob.pjpuebla.trials.workflow.documentos.records.DocumentoJuzgadoRecord;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;


@DataJpaTest(properties = {"spring.jpa.properties.hibernate.hbm2ddl.auto: create-drop"})
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
        "/scripts/INSERT_CARPETAS.sql",
        "/scripts/INSERT_DOCUMENTOS.sql",
        "/scripts/INSERT_TIPO_PARTES.sql",
        "/scripts/INSERT_PERSONAS_DOCUMENTOS.sql",
}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
@Sql(value = {
        "/scripts/DELETE_PERSONAS_DOCUMENTOS.sql",
        "/scripts/DELETE_TIPO_PARTES.sql",
        "/scripts/DELETE_DOCUMENTOS.sql",
        "/scripts/DELETE_CARPETAS.sql",
        "/scripts/DELETE_JUZGADO_TIPOJUICIO.sql",
        "/scripts/DELETE_JUZGADOS.sql",
        "/scripts/DELETE_TIPO_JUICIOS.sql",
        "/scripts/DELETE_TIPO_SISTEMAS.sql",
        "/scripts/DELETE_MATERIAS.sql",
        "/scripts/DELETE_SEDES.sql",
        "/scripts/DELETE_DISTRITOS.sql",
        "/scripts/DELETE_DOMICILIOS.sql",
}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_CLASS)
class DocumentoRepositoryTest extends AuditConfigTest {

    @Autowired
    private  DocumentoRepository documentoRepository;

    @Test
    void findDistritoJuzgadoByDocumentoId(){
        DocumentoJuzgadoRecord entity = documentoRepository.findDistritoJuzgadoByDocumentoId(1);
        assertThat(entity).isNotNull();
        assertThat(entity.nombreDistrito()).isEqualTo("ACATLÁN");
        assertThat(entity.nombreJuzgado()).isEqualTo("Juzgado Laboral");
    }

}