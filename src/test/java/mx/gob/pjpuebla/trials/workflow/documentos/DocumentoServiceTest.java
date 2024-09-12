package mx.gob.pjpuebla.trials.workflow.documentos;

import mx.gob.pjpuebla.trials.core.distritos.Distrito;
import mx.gob.pjpuebla.trials.core.distritos.DistritoRepository;
import mx.gob.pjpuebla.trials.core.distritos.DistritoSetUp;
import mx.gob.pjpuebla.trials.core.domicilio.DomicilioSetUp;
import mx.gob.pjpuebla.trials.core.domicilios.Domicilio;
import mx.gob.pjpuebla.trials.core.domicilios.DomicilioRepository;
import mx.gob.pjpuebla.trials.core.juzgados.Juzgado;
import mx.gob.pjpuebla.trials.core.juzgados.JuzgadoRepository;
import mx.gob.pjpuebla.trials.core.juzgados.JuzgadoSetUp;
import mx.gob.pjpuebla.trials.core.materias.Materia;
import mx.gob.pjpuebla.trials.core.materias.MateriaRepository;
import mx.gob.pjpuebla.trials.core.materias.MateriaSetUp;
import mx.gob.pjpuebla.trials.core.sedes.Sede;
import mx.gob.pjpuebla.trials.core.sedes.SedeRepository;
import mx.gob.pjpuebla.trials.core.sedes.SedeSetUp;
import mx.gob.pjpuebla.trials.core.tipojuicio.TipoJuicio;
import mx.gob.pjpuebla.trials.core.tipojuicio.TipoJuicioRepository;
import mx.gob.pjpuebla.trials.core.tipojuicio.TipoJuicioSetUp;
import mx.gob.pjpuebla.trials.core.tipopartes.TipoPartes;
import mx.gob.pjpuebla.trials.core.tipopartes.TipoPartesRepository;
import mx.gob.pjpuebla.trials.core.tipopartes.TipoPartesSetUp;
import mx.gob.pjpuebla.trials.core.tiposistema.TipoSistema;
import mx.gob.pjpuebla.trials.core.tiposistema.TipoSistemaRepository;
import mx.gob.pjpuebla.trials.core.tiposistema.TipoSistemaSetUp;
import mx.gob.pjpuebla.trials.util.TipoDocumento;
import mx.gob.pjpuebla.trials.workflow.anexos.AnexoRepository;
import mx.gob.pjpuebla.trials.workflow.anexos.AnexoSetUp;
import mx.gob.pjpuebla.trials.workflow.personasdocumentos.PersonaDocumentoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class DocumentoServiceTest {

    @Mock
    DocumentoRepository documentoRepository;
    @Mock
    TipoJuicioRepository tipoJuicioRepository;
    @Mock
    AnexoRepository anexoRepository;
    @InjectMocks
    DocumentoService documentoService;
    @Mock
    PersonaDocumentoRepository personaDocumentoRepository;
    @Mock
    TipoPartesRepository tipoPartesRepository;
    @Mock
    private JuzgadoRepository juzgadoRepository;
    @Mock
    private SedeRepository sedeRepository;
    @Mock
    private MateriaRepository materiaRepository;
    @Mock
    private DistritoRepository distritoRepository;
    @Mock
    private DomicilioRepository domicilioRepository;
    @Mock
    private TipoSistemaRepository tipoSistemaRepository;

    private TipoJuicio tipoJuicio;
    private Juzgado juzgado;
    private TipoPartes actor;
    private TipoPartes demandado;
    private DocumentoDTO dto = DocumentoSetUp.createDTO();

    @BeforeEach
    public void setUp() {
        Materia materia = materiaRepository.save(MateriaSetUp.createMateria());
        Distrito distrito = distritoRepository.save(DistritoSetUp.createDistrito());
        Domicilio domicilio = domicilioRepository.save(DomicilioSetUp.createDomicilio());
        TipoSistema tipoSistema = tipoSistemaRepository.save(TipoSistemaSetUp.createTipoSistema());
        tipoJuicio = TipoJuicioSetUp.createTipoJuicio(tipoSistema, materia);
        tipoJuicioRepository.save(tipoJuicio);
        actor = TipoPartesSetUp.createTipoPartes().setTipoJuicio(tipoJuicio);
        tipoPartesRepository.save(actor);
        demandado = TipoPartesSetUp.createTipoPartes().setTipoJuicio(tipoJuicio).setNombre("Demandado");
        tipoPartesRepository.save(demandado);

        Sede sede = SedeSetUp.createSede();
        sede.setDistrito(distrito);
        sede.setDomicilio(domicilio);
        sede = sedeRepository.save(sede);

        juzgado = JuzgadoSetUp.createJuzgado(materia, sede);
        juzgado = juzgadoRepository.save(juzgado);
        dto.setTipoJuicioId(tipoJuicio.getId());
    }

    @Test
    void create_demanda() {
        Documento demanda = DocumentoSetUp.create(TipoDocumento.DEMANDA, tipoJuicio).setFolio("1");
        given(documentoRepository.getNextValDemanda()).willReturn(2L);
        given(tipoJuicioRepository.findById(any())).willReturn(Optional.of(tipoJuicio));
        given(documentoRepository.save(any())).willReturn(demanda);
        given(tipoPartesRepository.findByNombreAndTipoJuicioId(eq("Actor"), any())).willReturn(Optional.of(actor));
        given(tipoPartesRepository.findByNombreAndTipoJuicioId(eq("Demandado"), any())).willReturn(Optional.of(demandado));
        given(anexoRepository.save(any())).willReturn(AnexoSetUp.createAnexo());

        DocumentoRecord documentoRecord = new DocumentoRecord(demanda.getId(), demanda.getFolio(), TipoDocumento.DEMANDA);

        DocumentoRecord response = documentoService.createDemanda(dto);
        assertThat(response).isOfAnyClassIn(DocumentoRecord.class)
                .hasFieldOrPropertyWithValue("id", documentoRecord.id())
                .hasFieldOrPropertyWithValue("folio", documentoRecord.folio())
                .hasFieldOrPropertyWithValue("tipoDocumento", documentoRecord.tipoDocumento());
    }
}
