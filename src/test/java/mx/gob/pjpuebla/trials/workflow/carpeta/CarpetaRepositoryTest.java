package mx.gob.pjpuebla.trials.workflow.carpeta;

import mx.gob.pjpuebla.trials.core.utils.audit.AuditConfigTest;
import mx.gob.pjpuebla.trials.util.enums.EstadoCarpeta;
import mx.gob.pjpuebla.trials.workflow.anexos.AnexoBandejaRecepcionRecord;
import mx.gob.pjpuebla.trials.workflow.carpeta.records.BandejaRecepcionRecord;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
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
        "/scripts/INSERT_JUZGADOS.sql",
        "/scripts/INSERT_JUZGADO_TIPOJUICIO.sql",
        "/scripts/INSERT_ESCOLARIDADES.sql",
        "/scripts/INSERT_ESTADO_CIVIL.sql",
        "/scripts/INSERT_PERSONAS.sql",
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
        "/scripts/DELETE_PERSONAS.sql",
        "/scripts/DELETE_ESTADO_CIVIL.sql",
        "/scripts/DELETE_ESCOLARIDADES.sql",
        "/scripts/DELETE_JUZGADO_TIPOJUICIO.sql",
        "/scripts/DELETE_JUZGADOS.sql",
        "/scripts/DELETE_TIPO_JUICIOS.sql",
        "/scripts/DELETE_TIPO_SISTEMAS.sql",
        "/scripts/DELETE_MATERIAS.sql",
        "/scripts/DELETE_SEDES.sql",
        "/scripts/DELETE_DISTRITOS.sql",
        "/scripts/DELETE_DOMICILIOS.sql",
}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_CLASS)
class CarpetaRepositoryTest extends AuditConfigTest {

    @Autowired
    private CarpetaRepository carpetaRepository;

    @Test
    void findByExpedienteAndJuzgadoId() {
        Optional<Carpeta> entity = carpetaRepository.findByExpedienteAndJuzgadoId("000001/2024", 51);
        assertThat(entity).isPresent();
        assertThat(entity.get().getId()).isEqualTo(1);
    }

    @Test
    void findByBandejaRecepcionByDocumentoIdSuccess() {
        Integer documentoId =  2;

        BandejaRecepcionRecord result = carpetaRepository.findByDocumentoId(documentoId);

        assertThat(result).isNotNull(); 
        assertThat(result.documentoId()).isEqualTo(documentoId); 
    }

    @Test
    void findByBandejaRecepcionByDocumentoIdFail() {
        Integer documentoId = 17;
        BandejaRecepcionRecord result = carpetaRepository.findByDocumentoId(documentoId);

        assertThat(result).isNull(); 
    }

    @Test
    void findAnexoByDocumentoIdSuccess(){
        Integer documentoId = 1;
        List<AnexoBandejaRecepcionRecord> result = carpetaRepository.findAnexosByDocumentoId(documentoId);
        assertThat(result).isNotNull();
    }

    @Test
    void findAnexoByDocumentoIdFail(){
        Integer documentoId = 2;
        List<AnexoBandejaRecepcionRecord> result = carpetaRepository.findAnexosByDocumentoId(documentoId);
        assertThat(result).isNullOrEmpty();
    }

    @Test
    void actualizarEstatus() {
        Integer carpetaId = 1;
        carpetaRepository.actualizarEstatus(carpetaId, EstadoCarpeta.DEVUELTO);
        Carpeta carpeta = carpetaRepository.findById(carpetaId).orElse(null);
        assertThat(carpeta).isNotNull();
        assertThat(carpeta.getEstatus()).isEqualTo(EstadoCarpeta.DEVUELTO);
    }
}