package mx.gob.pjpuebla.trials.workflow.documentos;

import mx.gob.pjpuebla.trials.core.distritos.Distrito;
import mx.gob.pjpuebla.trials.core.distritos.DistritoRepository;
import mx.gob.pjpuebla.trials.core.distritos.DistritoSetUp;
import mx.gob.pjpuebla.trials.core.domicilio.DomicilioSetUp;
import mx.gob.pjpuebla.trials.core.domicilios.Domicilio;
import mx.gob.pjpuebla.trials.core.domicilios.DomicilioRepository;
import mx.gob.pjpuebla.trials.core.juzgados.*;
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
import mx.gob.pjpuebla.trials.error.NotFoundException;
import mx.gob.pjpuebla.trials.util.enums.EstadoDocumento;
import mx.gob.pjpuebla.trials.util.enums.TipoDocumento;
import mx.gob.pjpuebla.trials.workflow.anexos.AnexoRepository;
import mx.gob.pjpuebla.trials.workflow.anexos.AnexoSetUp;
import mx.gob.pjpuebla.trials.workflow.documentos.records.DocumentoGridRecord;
import mx.gob.pjpuebla.trials.workflow.personasdocumentos.PersonaDocumentoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

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
    @Mock
    private JuzgadoService juzgadoService;

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
        juzgado.setContadorAsignaciones(0);
        juzgado.setMaxAsignacionesRonda(0);
        juzgadoRepository.save(juzgado);
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
        given(juzgadoService.getConexidadJuzgado(any(), any(), any())).willReturn(juzgado);
        given(juzgadoService.getNumeroExpediente(any())).willReturn(new NumeroExpedienteRecord("1"));

        DocumentoRecord documentoRecord = new DocumentoRecord(demanda.getId(), demanda.getFolio(), TipoDocumento.DEMANDA);

        DocumentoRecord response = documentoService.createDemanda(dto);
        assertThat(response).isOfAnyClassIn(DocumentoRecord.class)
                .hasFieldOrPropertyWithValue("id", documentoRecord.id())
                .hasFieldOrPropertyWithValue("folio", documentoRecord.folio())
                .hasFieldOrPropertyWithValue("tipoDocumento", documentoRecord.tipoDocumento());
    }

    @Test
    void update_status_success() {
        Documento demanda = DocumentoSetUp.create(TipoDocumento.DEMANDA, tipoJuicio).setFolio("1");
        given(documentoRepository.findById(any())).willReturn(Optional.of(demanda));
        given(documentoRepository.save(any())).willReturn(demanda.setEstatus(EstadoDocumento.SALIDA));

        DocumentoRecord documentoRecord = new DocumentoRecord(demanda.getId(), demanda.getFolio(), demanda.getTipoDocumento());

        DocumentoRecord response = documentoService.updateStatus(demanda.getId(), 1);
        assertThat(response).isOfAnyClassIn(DocumentoRecord.class)
                .hasFieldOrPropertyWithValue("id", documentoRecord.id())
                .hasFieldOrPropertyWithValue("folio", documentoRecord.folio())
                .hasFieldOrPropertyWithValue("tipoDocumento", documentoRecord.tipoDocumento());

    }

    @Test
    void update_status_not_found() {
        given(documentoRepository.findById(any())).willReturn(Optional.empty());

        NotFoundException assertThrows = assertThrows(
                NotFoundException.class,
                () -> {
                    documentoService.updateStatus(1, 1);
                }
        );

        assertThat(assertThrows.getMessage()).contains("Documento no encontrado");
    }

    @Test
    void getAll_return_page() {
        Documento demanda = DocumentoSetUp.create(TipoDocumento.DEMANDA, tipoJuicio).setFolio("1");
        demanda.setJuzgado(juzgado);
        demanda.getJuzgado().setMateria(MateriaSetUp.createMateria());

        List<Documento> listPage = Collections.singletonList(demanda);
        given(documentoRepository.findByEstatusCaptura(any(String.class), any(PageRequest.class)))
                .willReturn(new PageImpl<>(listPage, PageRequest.of(0, listPage.size()), listPage.size()));
        Page<DocumentoGridRecord> page = documentoService.getAll(demanda.getFolio(), PageRequest.of(1, listPage.size()));
        assertThat(page.getContent())
                .hasSize(1)
                .first()
                .hasFieldOrPropertyWithValue("id", demanda.getId())
                .hasFieldOrPropertyWithValue("tipoEntrada", demanda.getTipoDocumento().toString())
                .hasFieldOrPropertyWithValue("expediente", demanda.getExpediente());
    }

    @Test
    void asignaJuzgado() {
        int invocaciones = 2;
        Documento demanda = DocumentoSetUp.create(TipoDocumento.DEMANDA, tipoJuicio).setFolio("1");

        given(juzgadoService.getJuzgado(any())).willReturn(juzgado);

        juzgado = juzgadoService.getJuzgado(demanda.getTipoJuicio());
        assertThat(juzgado).isNotNull();

        for (int i = 0; i < invocaciones; i++) {
            juzgadoService.actualizarCarga(juzgado);
        }

        juzgadoService.revisarCargaJuzgados(tipoJuicio.getMateria());

        verify(juzgadoService, times(invocaciones)).actualizarCarga(juzgado);
        verify(juzgadoService, times(1)).revisarCargaJuzgados(demanda.getTipoJuicio().getMateria());
    }
}
