package mx.gob.pjpuebla.trials.workflow.personasdocumentos;

import mx.gob.pjpuebla.trials.core.materias.Materia;
import mx.gob.pjpuebla.trials.core.materias.MateriaRepository;
import mx.gob.pjpuebla.trials.core.materias.MateriaSetUp;
import mx.gob.pjpuebla.trials.core.tipojuicio.TipoJuicio;
import mx.gob.pjpuebla.trials.core.tipojuicio.TipoJuicioRepository;
import mx.gob.pjpuebla.trials.core.tipojuicio.TipoJuicioSetUp;
import mx.gob.pjpuebla.trials.core.tipopartes.TipoPartes;
import mx.gob.pjpuebla.trials.core.tipopartes.TipoPartesRepository;
import mx.gob.pjpuebla.trials.core.tipopartes.TipoPartesSetUp;
import mx.gob.pjpuebla.trials.core.tiposistema.TipoSistema;
import mx.gob.pjpuebla.trials.core.tiposistema.TipoSistemaRepository;
import mx.gob.pjpuebla.trials.core.tiposistema.TipoSistemaSetUp;
import mx.gob.pjpuebla.trials.core.utils.audit.AuditConfigTest;
import mx.gob.pjpuebla.trials.util.TipoDocumento;
import mx.gob.pjpuebla.trials.util.enums.Rol;
import mx.gob.pjpuebla.trials.workflow.documentos.Documento;
import mx.gob.pjpuebla.trials.workflow.documentos.DocumentoRepository;
import mx.gob.pjpuebla.trials.workflow.documentos.DocumentoSetUp;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

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

    private PersonaDocumento personaDocumento;

    @BeforeEach
    public void setUp() {

        Materia materia = MateriaSetUp.createMateria();
        materiaRepository.save(materia);

        TipoSistema tipoSistema = TipoSistemaSetUp.createTipoSistema();
        tipoSistemaRepository.save(tipoSistema);

        TipoJuicio tipoJuicio = tipoJuicioRepository.save(TipoJuicioSetUp.createTipoJuicio());
        Documento documento = DocumentoSetUp.create(TipoDocumento.DEMANDA, tipoJuicio);
        documento.setFolio("1").setExpediente("000001/2024");
        Documento doc = documentoRepository.save(documento);

        TipoPartes tipoPartes = TipoPartesSetUp.createTipoPartes();
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

}