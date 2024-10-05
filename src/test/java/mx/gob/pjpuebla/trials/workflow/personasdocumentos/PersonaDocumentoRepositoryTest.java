package mx.gob.pjpuebla.trials.workflow.personasdocumentos;

import mx.gob.pjpuebla.trials.core.utils.audit.AuditConfigTest;
import mx.gob.pjpuebla.trials.util.enums.Rol;
import mx.gob.pjpuebla.trials.workflow.carpeta.Carpeta;
import mx.gob.pjpuebla.trials.workflow.carpeta.CarpetaSetUp;
import mx.gob.pjpuebla.trials.workflow.carpeta.records.ApelacionRecordResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;

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
class PersonaDocumentoRepositoryTest extends AuditConfigTest {
    @Autowired
    private PersonaDocumentoRepository personaDocumentoRepository;

    @Test
    void findPersonaAndTipoParteByCarpetaId() {
        List<Rol> rol = List.of(Rol.PRINCIPAL);
        PersonaDocumentoRecord entity = personaDocumentoRepository
                .findPersonaAndTipoParteByCarpetaId(1, "Actor", rol);
        assertThat(entity).isNotNull();
        assertThat(entity.tipoParte()).isEqualTo("Actor");
        assertThat(entity.tipoPersona()).isEqualToIgnoringCase("Fisica");
    }

    @Test
    void findPersonasByCarpetaId() {
        List<PersonaDocumentoRecord> list = personaDocumentoRepository
                .findPersonasByCarpetaId(1, Rol.PRINCIPAL);
        assertThat(list).isNotNull()
                .isNotEmpty();
    }

    @Test
    void findPersonaDocumentoByCarpetaId() {
        List<ApelacionRecordResponse> entity = personaDocumentoRepository.findPersonaDocumentoByCarpetaId(1);
        assertThat(entity).isNotEmpty();
    }
}