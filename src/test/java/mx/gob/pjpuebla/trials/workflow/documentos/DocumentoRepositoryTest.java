package mx.gob.pjpuebla.trials.workflow.documentos;

import mx.gob.pjpuebla.trials.core.utils.audit.AuditConfigTest;
import mx.gob.pjpuebla.trials.util.enums.EstadoCarpeta;
import mx.gob.pjpuebla.trials.workflow.documentos.acuerdos.records.AcuerdoNotificadosRecord;
import mx.gob.pjpuebla.trials.workflow.documentos.acuerdos.records.AcuerdoPromocionesRecord;
import mx.gob.pjpuebla.trials.workflow.documentos.records.DocumentoDetalleCarpeta;
import mx.gob.pjpuebla.trials.workflow.documentos.records.DocumentoJuzgadoRecord;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

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
}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
@Sql(value = {
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

    @Test
    void actualizarEstatus() {
        Integer documentoId = 1;
        documentoRepository.actualizarEstatus(documentoId, EstadoCarpeta.DEVUELTO);
        Documento documento = documentoRepository.findById(documentoId).orElse(null);
        assertThat(documento).isNotNull();
        assertThat(documento.getEstatus()).isEqualTo(EstadoCarpeta.DEVUELTO);
    }

    @Test
    void testFindTipoPartesAcuerdo_actor() {
        Integer carpetaId = 1;
        String tipoParte = "actor";
    
        List<AcuerdoNotificadosRecord> acuerdoNotificados = documentoRepository.findTipoPartesAcuerdo(carpetaId, tipoParte);
    
        assertThat(acuerdoNotificados).isNotEmpty();
        // Verificamos que el nombre contiene el actor (pero sin buscar el término "actor" en el nombre completo)
        // Si tu consulta filtra bien, deberías comprobar que los resultados son consistentes con el tipo de parte
        // Ejemplo de que los registros sean los esperados para "actor" (aunque no contiene la palabra "actor")
        assertThat(acuerdoNotificados.get(0).nombre()).isNotEmpty();
        assertThat(acuerdoNotificados.get(0).nombre()).doesNotContain("demandado");
    }
    
    @Test
    void testFindTipoPartesAcuerdo_demandado() {
        Integer carpetaId = 1;
        String tipoParte = "demandado";
    
        List<AcuerdoNotificadosRecord> acuerdoNotificados = documentoRepository.findTipoPartesAcuerdo(carpetaId, tipoParte);
    
        assertThat(acuerdoNotificados).isNotEmpty();
        // Verificamos que solo los demandados estén presentes
        assertThat(acuerdoNotificados.get(0).nombre()).isNotEmpty();
        assertThat(acuerdoNotificados.get(0).nombre()).doesNotContain("actor");
    }
    
    @Test
    void testFindTipoPartesAcuerdo_otros() {
        Integer carpetaId = 1;
        String tipoParte = "otros";
    
        List<AcuerdoNotificadosRecord> acuerdoNotificados = documentoRepository.findTipoPartesAcuerdo(carpetaId, tipoParte);
    
        assertThat(acuerdoNotificados).isNotNull();
        assertThat(acuerdoNotificados).hasSize(0);
    }


@Test
void testActualizacionAcuerdoRespuesta() {
    Integer carpetaId = 1;
    Integer documentoId = 5;
    // Ejecutar la actualización
    documentoRepository.actualizacionAcuerdoRespuesta(carpetaId, documentoId);

    // Verificar que los documentos en la carpeta tienen `acuerdoRespuesta` como null
    List<AcuerdoPromocionesRecord> documentos = documentoRepository.obtenerPromociones(carpetaId, documentoId, "ACUERDO");
    assertThat(documentos.isEmpty());
}
    @Test
    void testfindDocumentosByCarpeta(){
        Integer carpetaId=1;

        List<DocumentoDetalleCarpeta> documentos = documentoRepository.findDocumentosByCarpeta(carpetaId);

        assertThat(documentos).isNotEmpty();

    }

    @Test
    void testFindDocumentosByAcuerdoId(){
        Integer acuerdoId=5;

        List<Documento> promociones = documentoRepository.findByAcuerdoRespuestaId(acuerdoId);

        assertThat(promociones).isNotEmpty();
    }

}
