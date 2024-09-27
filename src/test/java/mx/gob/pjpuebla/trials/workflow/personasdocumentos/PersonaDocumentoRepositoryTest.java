package mx.gob.pjpuebla.trials.workflow.personasdocumentos;

import mx.gob.pjpuebla.trials.core.distritos.Distrito;
import mx.gob.pjpuebla.trials.core.distritos.DistritoRepository;
import mx.gob.pjpuebla.trials.core.distritos.DistritoSetUp;
import mx.gob.pjpuebla.trials.core.domicilio.DomicilioSetUp;
import mx.gob.pjpuebla.trials.core.domicilios.Domicilio;
import mx.gob.pjpuebla.trials.core.domicilios.DomicilioRepository;
import mx.gob.pjpuebla.trials.core.juzgados.Juzgado;
import mx.gob.pjpuebla.trials.core.juzgados.JuzgadoRepository;
import mx.gob.pjpuebla.trials.core.materias.Materia;
import mx.gob.pjpuebla.trials.core.materias.MateriaRepository;
import mx.gob.pjpuebla.trials.core.sedes.Sede;
import mx.gob.pjpuebla.trials.core.sedes.SedeRepository;
import mx.gob.pjpuebla.trials.core.tipojuicio.TipoJuicio;
import mx.gob.pjpuebla.trials.core.tipojuicio.TipoJuicioRepository;
import mx.gob.pjpuebla.trials.core.tipojuicio.TipoJuicioSetUp;
import mx.gob.pjpuebla.trials.core.tipopartes.TipoPartes;
import mx.gob.pjpuebla.trials.core.tipopartes.TipoPartesRepository;
import mx.gob.pjpuebla.trials.core.tipopartes.TipoPartesSetUp;
import mx.gob.pjpuebla.trials.core.tiposistema.TipoSistema;
import mx.gob.pjpuebla.trials.core.tiposistema.TipoSistemaRepository;
import mx.gob.pjpuebla.trials.core.utils.audit.AuditConfigTest;
import mx.gob.pjpuebla.trials.util.enums.Rol;
import mx.gob.pjpuebla.trials.util.enums.TipoDocumento;
import mx.gob.pjpuebla.trials.workflow.anexos.AnexoRepository;
import mx.gob.pjpuebla.trials.workflow.carpeta.Carpeta;
import mx.gob.pjpuebla.trials.workflow.carpeta.CarpetaRepository;
import mx.gob.pjpuebla.trials.workflow.documentos.Documento;
import mx.gob.pjpuebla.trials.workflow.documentos.DocumentoRepository;
import mx.gob.pjpuebla.trials.workflow.documentos.DocumentoSetUp;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;

import static mx.gob.pjpuebla.trials.core.juzgados.JuzgadoSetUp.createJuzgado;
import static mx.gob.pjpuebla.trials.core.materias.MateriaSetUp.createMateria;
import static mx.gob.pjpuebla.trials.core.tipojuicio.TipoJuicioSetUp.createTipoJuicio;
import static mx.gob.pjpuebla.trials.core.tiposistema.TipoSistemaSetUp.createTipoSistema;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(properties = {"spring.jpa.properties.hibernate.hbm2ddl.auto: create-drop"})
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@Disabled
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
    @Autowired
    private CarpetaRepository carpetaRepository;
    @Autowired
    private JuzgadoRepository juzgadoRepository;
    @Autowired
    private SedeRepository sedeRepository;
    @Autowired
    private DistritoRepository distritoRepository;
    @Autowired
    private DomicilioRepository domicilioRepository;

    private PersonaDocumento personaDocumento;
    private Documento documento;
    private TipoPartes tipoPartes;
    private Carpeta carpeta;

    @BeforeEach
    public void setUp() {
        Materia materia = materiaRepository.save(createMateria());
        TipoSistema tipoSistema = tipoSistemaRepository.save(createTipoSistema());
        TipoJuicio tipoJuicio = tipoJuicioRepository.save(createTipoJuicio().setTipoSistema(tipoSistema).setMateria(materia));
        Distrito distrito = distritoRepository.save(DistritoSetUp.createDistrito());
        Domicilio domicilio = domicilioRepository.save(DomicilioSetUp.createDomicilio());

        TipoJuicio tj1 = TipoJuicioSetUp.createTipoJuicio(tipoSistema, materia);
        TipoJuicio tj2 = TipoJuicioSetUp.createTipoJuicio(tipoSistema, materia).setId(2).setNombre("Laboral Dos");

        Juzgado juzgado = createJuzgado();
        Sede sede = juzgado.getSede().setDistrito(distrito).setDomicilio(domicilio);
        sede = sedeRepository.save(sede);
        juzgado.setSede(sede)
                .setMateria(materia)
                .setTipoJuicios(Arrays.asList(tj1, tj2));
        juzgado = juzgadoRepository.save(juzgado);

        documento = DocumentoSetUp.create(TipoDocumento.DEMANDA, tipoJuicio).setVersion(0);
        carpeta = documento.getCarpeta();
        carpeta.setJuzgado(juzgado);
        carpeta = carpetaRepository.save(carpeta);
        documento = documentoRepository.save(documento.setCarpeta(carpeta));
        tipoPartes = TipoPartesSetUp.createTipoPartes();
        tipoPartes.setTipoJuicio(tipoJuicio);
        tipoPartes = tipoPartesRepository.save(tipoPartes);
        personaDocumento = PersonasDocumentosSetUp.createPersonasDocumentos()
                .setCarpeta(documento.getCarpeta())
                .setTipoPartes(tipoPartes);
    }

    @Test
    void findPersonaAndTipoParteByCarpetaId() {
        personaDocumento = personaDocumentoRepository.save(personaDocumento);
        List<Rol> rol = Arrays.asList(Rol.PRINCIPAL);
        PersonaDocumentoRecord entity = personaDocumentoRepository
                .findPersonaAndTipoParteByCarpetaId(personaDocumento.getCarpeta().getId(), tipoPartes.getNombre(), rol);
        assertThat(entity).isNotNull();
        assertThat(entity.nombre()).isEqualTo(personaDocumento.getNombre());
        assertThat(entity.apellidoPaterno()).isEqualTo(personaDocumento.getApellidoPaterno());
        assertThat(entity.tipoParte()).isEqualTo(personaDocumento.getTipoPartes().getNombre());
        assertThat(entity.tipoPersona()).isEqualTo(personaDocumento.getTipoPersona());
    }

    @Test
    void findPersonasByCarpetaId() {
        personaDocumento = personaDocumentoRepository.save(personaDocumento);
        List<PersonaDocumentoRecord> entity = personaDocumentoRepository
                .findPersonasByCarpetaId(personaDocumento.getCarpeta().getId(), Rol.PRINCIPAL);
        assertThat(entity).isNotNull()
                .hasSize(1);
    }
}