package mx.gob.pjpuebla.trials.workflow.documentos.documentoContenido;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

import mx.gob.pjpuebla.trials.core.utils.audit.AuditConfigTest;
import mx.gob.pjpuebla.trials.workflow.documentos.documentoscontenido.DocumentoContenido;
import mx.gob.pjpuebla.trials.workflow.documentos.documentoscontenido.DocumentoContenidoRepository;

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
        "/scripts/INSERT_ESTADO_CIVIL.sql",
        "/scripts/INSERT_ESCOLARIDADES.sql",
        "/scripts/INSERT_PERSONAS.sql",
        "/scripts/INSERT_TIPO_PIEZAS.sql",
        "/scripts/INSERT_CARPETAS.sql",
        "/scripts/INSERT_DOCUMENTOS.sql",
        "/scripts/INSERT_TIPO_PARTES.sql",
        "/scripts/INSERT_PERSONAS_DOCUMENTOS.sql",
        "/scripts/INSERT_DOCUMENTO_CONTENIDO.sql"
}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
@Sql(value = {
        "/scripts/DELETE_DOCUMENTO_CONTENIDO.sql",
        "/scripts/DELETE_PERSONAS_DOCUMENTOS.sql",
        "/scripts/DELETE_TIPO_PARTES.sql",
        "/scripts/DELETE_DOCUMENTOS.sql",
        "/scripts/DELETE_CARPETAS.sql",
        "/scripts/DELETE_TIPO_PIEZAS.sql",
        "/scripts/DELETE_PERSONAS.sql",
        "/scripts/DELETE_ESCOLARIDADES.sql",
        "/scripts/DELETE_ESTADO_CIVIL.sql",
        "/scripts/DELETE_JUZGADO_TIPOJUICIO.sql",
        "/scripts/DELETE_JUZGADOS.sql",
        "/scripts/DELETE_TIPO_JUICIOS.sql",
        "/scripts/DELETE_TIPO_SISTEMAS.sql",
        "/scripts/DELETE_MATERIAS.sql",
        "/scripts/DELETE_SEDES.sql",
        "/scripts/DELETE_DISTRITOS.sql",
        "/scripts/DELETE_DOMICILIOS.sql",
}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_CLASS)
class DocumentoContenidoRepositoryTest  extends AuditConfigTest {
    
    @Autowired
    private DocumentoContenidoRepository documentoContenidoRepository;

    @Test
    void findByDocumento_Id(){
        Optional<DocumentoContenido> contenido = documentoContenidoRepository.findByDocumentoId(1);

        assertThat(contenido).isNotNull();
    }

}
