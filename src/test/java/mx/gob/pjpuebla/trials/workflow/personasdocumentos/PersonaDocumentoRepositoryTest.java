package mx.gob.pjpuebla.trials.workflow.personasdocumentos;

import mx.gob.pjpuebla.trials.core.materias.Materia;
import mx.gob.pjpuebla.trials.core.materias.MateriaRepository;
import mx.gob.pjpuebla.trials.core.tipojuicio.TipoJuicio;
import mx.gob.pjpuebla.trials.core.tipojuicio.TipoJuicioRepository;
import mx.gob.pjpuebla.trials.core.tipopartes.TipoPartes;
import mx.gob.pjpuebla.trials.core.tipopartes.TipoPartesRepository;
import mx.gob.pjpuebla.trials.core.tipopartes.TipoPartesSetUp;
import mx.gob.pjpuebla.trials.core.tiposistema.TipoSistema;
import mx.gob.pjpuebla.trials.core.tiposistema.TipoSistemaRepository;
import mx.gob.pjpuebla.trials.core.tiposistema.TipoSistemaSetUp;
import mx.gob.pjpuebla.trials.core.utils.audit.AuditConfigTest;
import mx.gob.pjpuebla.trials.util.Audit;
import mx.gob.pjpuebla.trials.util.enums.*;
import mx.gob.pjpuebla.trials.workflow.anexos.Anexo;
import mx.gob.pjpuebla.trials.workflow.anexos.AnexoRepository;
import mx.gob.pjpuebla.trials.workflow.documentos.Documento;
import mx.gob.pjpuebla.trials.workflow.documentos.records.DocumentoAnexoRecord;
import mx.gob.pjpuebla.trials.workflow.documentos.DocumentoRepository;
import mx.gob.pjpuebla.trials.workflow.documentos.DocumentoSetUp;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(properties = {
        "spring.jpa.properties.hibernate.hbm2ddl.auto: create-drop"
})
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
class PersonaDocumentoRepositoryTest extends AuditConfigTest {


    @Autowired
    private TipoJuicioRepository tipoJuicioRepository;
    @Autowired
    private PersonaDocumentoRepository personaDocumentoRepository;
    @Autowired
    private DocumentoRepository documentoRepository;
    @Autowired
    private TipoPartesRepository tipoPartesRepository;
    @Autowired
    private MateriaRepository materiaRepository;
    @Autowired
    private TipoSistemaRepository tipoSistemaRepository;
    @Autowired
    private AnexoRepository anexoRepository;

    private PersonaDocumento personaDocumento;
    private Documento documento;
    private TipoPartes tipoPartes;
    private Documento doc;
    private  TipoJuicio tipoJuicio;

    Materia materia = createMateria();
    TipoSistema tipoSistema = TipoSistemaSetUp.createTipoSistema();

    @BeforeEach
    public void setUp() {
        materiaRepository.save(materia);
        tipoSistemaRepository.save(tipoSistema);

        tipoJuicio = tipoJuicioRepository.save(createTipoJuicio());

        documento = DocumentoSetUp.create(TipoDocumento.DEMANDA, tipoJuicio);
        documento.setFolio("1").setExpediente("000001/2024");
        doc = documentoRepository.save(documento);

        tipoPartes = TipoPartesSetUp.createTipoPartes();
        tipoPartes.setTipoJuicio(tipoJuicio);
        TipoPartes tipPar = tipoPartesRepository.save(tipoPartes);
        personaDocumento = PersonasDocumentosSetUp.createPersonasDocumentos(doc, tipPar);
    }

    @Test
    void findDocumentoPersonaTipoParteByDocumentoId() {
        personaDocumento = personaDocumentoRepository.save(personaDocumento);
        List<Rol> rol = Arrays.asList(Rol.PRINCIPAL);
        PersonaDocumentoRecord entity = personaDocumentoRepository
                .findDocumentoPersonaTipoParteByDocumentoId(personaDocumento.getDocumento().getId(), "Actor", rol);
        assertThat(entity).isNotNull();
    }



@Test
void findDocumentoAnexoByDocumentoId() {

    TipoPartes tipoPartesDocumentoAnexo =  TipoPartesSetUp.createTipoPartes();
    tipoPartesDocumentoAnexo.setTipoJuicio(tipoJuicio);
    tipoPartesDocumentoAnexo.setId(2);
    tipoPartesDocumentoAnexo.setNombre("Demandado");
    tipoPartesDocumentoAnexo = tipoPartesRepository.save(tipoPartes);

    PersonaDocumento personaDocumentoDemandado = PersonasDocumentosSetUp.createPersonasDocumentos(doc,tipoPartesDocumentoAnexo);
    personaDocumentoRepository.save(personaDocumentoDemandado);

    List<Anexo> anexos = createAnexos(documento);
    for (Anexo anexo : anexos) {
        anexo.setDocumento(documento);
        anexoRepository.save(anexo);
        System.out.println(anexo);
    }

    List<DocumentoAnexoRecord> documentoAnexoRecords = personaDocumentoRepository
            .findDocumentoAnexoByDocumentoId(documento.getId());
    assertThat(documentoAnexoRecords).isNotNull().isNotEmpty();
    assertThat(documentoAnexoRecords).hasSizeGreaterThan(0);
    assertThat(personaDocumento).isNotNull();
    assertThat(personaDocumento.getDocumento()).isNotNull();
    assertThat(personaDocumento.getDocumento().getId()).isNotNull();
    assertThat(personaDocumento.getDocumento()).isEqualTo(doc);
    assertThat(doc).isEqualTo(personaDocumento.getDocumento());

}

    @Test
    void findNombresAnexosByDocumentoId() {

        Documento savedDocumento = documentoRepository.save(documento);

        List<Anexo> anexos = createAnexos(savedDocumento);
        for (Anexo anexo : anexos) {
            anexoRepository.save(anexo);
        }
        List<String> nombresAnexos = personaDocumentoRepository.findNombresAnexosByDocumentoId(savedDocumento.getId());
        System.out.println("nombres de anexos" + nombresAnexos);
        assertThat(nombresAnexos)
                .isNotNull()
                .hasSize(2)
                .containsExactlyInAnyOrder("anexo test 1", "anexo test 2");

    }


    public static Materia createMateria() {
        Materia materia = new Materia()
                .setNombre("PENAL")
                .setEstado(Estado.ACTIVE)
                .setVersion(0);
        materia.setAudit(new Audit(LocalDateTime.now(), LocalDateTime.now(), "6b13785f-d213-4585-a76b-437ffe57c9c7", "6b13785f-d213-4585-a76b-437ffe57c9c7"));
        return materia;
    }

    public static TipoJuicio createTipoJuicio() {
        TipoJuicio tipoJuicio = new TipoJuicio()
                .setId(1)
                .setNombre("Laboral")
                .setEstado(Estado.ACTIVE)
                .setVersion(0);
        tipoJuicio.setAudit(
                new Audit(
                        LocalDateTime.now(),
                        LocalDateTime.now(),
                        "6b13785f-d213-4585-a76b-437ffe57c9c7",
                        "6b13785f-d213-4585-a76b-437ffe57c9c7")
        );
        return tipoJuicio;
    }

    public static List<Anexo> createAnexos(Documento documento) {
        Anexo anexo1 = new Anexo().setNombre("anexo test 1").setDocumento(documento);
        Anexo anexo2 = new Anexo().setNombre("anexo test 2").setDocumento(documento);
        return Arrays.asList(anexo1, anexo2);
    }




}