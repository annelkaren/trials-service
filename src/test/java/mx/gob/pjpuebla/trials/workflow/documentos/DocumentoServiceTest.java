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
import mx.gob.pjpuebla.trials.util.enums.*;
import mx.gob.pjpuebla.trials.workflow.anexos.Anexo;
import mx.gob.pjpuebla.trials.workflow.anexos.AnexoRepository;
import mx.gob.pjpuebla.trials.workflow.anexos.AnexoSetUp;
import mx.gob.pjpuebla.trials.workflow.carpeta.CarpetaRepository;
import mx.gob.pjpuebla.trials.workflow.documentos.records.DocumentoGridRecord;
import mx.gob.pjpuebla.trials.workflow.documentos.records.DocumentoRecord;
import mx.gob.pjpuebla.trials.workflow.documentos.records.DocumentoResponseRecord;
import mx.gob.pjpuebla.trials.workflow.documentos.records.DocumentoSaveRecord;
import mx.gob.pjpuebla.trials.workflow.personasdocumentos.PersonaDocumentoRecord;
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

import java.util.*;

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
    @Mock
    private CarpetaRepository carpetaRepository;

    private TipoJuicio tipoJuicio;
    private Juzgado juzgado;
    private TipoPartes actor;
    private TipoPartes demandado;
    private DocumentoSaveRecord recordRequest;

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
        recordRequest = DocumentoSetUp.createDocumentoSaveRecord(tipoJuicio.getId());
    }

    @Test
    void create_demanda() {
        Documento demanda = DocumentoSetUp.create(tipoJuicio);
        demanda.getCarpeta().setFolio("1");
        demanda.getCarpeta().setTipoCarpeta(TipoCarpeta.DEMANDA);
        given(documentoRepository.getNextValFolio("SEQ_DEMANDA_FOLIO")).willReturn(2L);
        given(tipoJuicioRepository.findById(any())).willReturn(Optional.of(tipoJuicio));
        given(documentoRepository.save(any())).willReturn(demanda);
        given(tipoPartesRepository.findByNombreAndTipoJuicioId(eq("Actor"), any())).willReturn(Optional.of(actor));
        given(anexoRepository.save(any())).willReturn(AnexoSetUp.createAnexo());
        given(juzgadoService.getConexidadJuzgado(any(), any(), any())).willReturn(juzgado);
        given(juzgadoService.getNumeroExpediente(any())).willReturn(new NumeroExpedienteRecord("1"));
        given(carpetaRepository.save(any())).willReturn(demanda.getCarpeta());

        DocumentoRecord documentoRecord = new DocumentoRecord(demanda.getId(), demanda.getCarpeta().getFolio(), TipoCarpeta.DEMANDA);

        DocumentoRecord response = documentoService.createDemanda(recordRequest);
        assertThat(response).isOfAnyClassIn(DocumentoRecord.class)
                .hasFieldOrPropertyWithValue("id", documentoRecord.id())
                .hasFieldOrPropertyWithValue("folio", documentoRecord.folio())
                .hasFieldOrPropertyWithValue("tipoCarpeta", documentoRecord.tipoCarpeta());
    }

    @Test
    void update_status_success() {
        Documento demanda = DocumentoSetUp.create(tipoJuicio);
        demanda.getCarpeta().setFolio("1");
        demanda.getCarpeta().setTipoCarpeta(TipoCarpeta.DEMANDA);
        given(documentoRepository.findById(any())).willReturn(Optional.of(demanda));
        given(carpetaRepository.save(any())).willReturn(demanda.getCarpeta().setEstatus(EstadoCarpeta.SALIDA));

        DocumentoRecord documentoRecord = new DocumentoRecord(demanda.getId(), demanda.getCarpeta().getFolio(), demanda.getCarpeta().getTipoCarpeta());

        DocumentoRecord response = documentoService.updateStatus(demanda.getId(), 1);
        assertThat(response).isOfAnyClassIn(DocumentoRecord.class)
                .hasFieldOrPropertyWithValue("id", documentoRecord.id())
                .hasFieldOrPropertyWithValue("folio", documentoRecord.folio())
                .hasFieldOrPropertyWithValue("tipoCarpeta", documentoRecord.tipoCarpeta());

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
        Documento demanda = DocumentoSetUp.create(tipoJuicio);
        demanda.getCarpeta().setTipoCarpeta(TipoCarpeta.DEMANDA);
        demanda.getCarpeta().setFolio("1");
        demanda.getCarpeta().setJuzgado(juzgado);
        demanda.getCarpeta().getJuzgado().setMateria(MateriaSetUp.createMateria());

        List<Documento> listPage = Collections.singletonList(demanda);
        given(documentoRepository.findByEstatusCaptura(any(String.class), any(PageRequest.class)))
                .willReturn(new PageImpl<>(listPage, PageRequest.of(0, listPage.size()), listPage.size()));
        Page<DocumentoGridRecord> page = documentoService.getAll(demanda.getCarpeta().getFolio(), PageRequest.of(1, listPage.size()));
        assertThat(page.getContent())
                .hasSize(1)
                .first()
                .hasFieldOrPropertyWithValue("id", demanda.getId())
                .hasFieldOrPropertyWithValue("tipoEntrada", demanda.getCarpeta().getTipoCarpeta().toString())
                .hasFieldOrPropertyWithValue("expediente", demanda.getCarpeta().getExpediente());
    }

    @Test
    void asignaJuzgado() {
        int invocaciones = 2;
        Documento demanda = DocumentoSetUp.create(tipoJuicio);
        demanda.getCarpeta().setTipoCarpeta(TipoCarpeta.DEMANDA);
        demanda.getCarpeta().setFolio("1");
        given(juzgadoService.getJuzgado(any())).willReturn(juzgado);

        juzgado = juzgadoService.getJuzgado(demanda.getCarpeta().getTipoJuicio());
        assertThat(juzgado).isNotNull();

        for (int i = 0; i < invocaciones; i++) {
            juzgadoService.actualizarCarga(juzgado);
        }

        juzgadoService.revisarCargaJuzgados(tipoJuicio.getMateria());

        verify(juzgadoService, times(invocaciones)).actualizarCarga(juzgado);
        verify(juzgadoService, times(1)).revisarCargaJuzgados(demanda.getCarpeta().getTipoJuicio().getMateria());
    }

    @Test
    void edit_anexos() {
        Documento demanda = DocumentoSetUp.create(tipoJuicio);
        demanda.getCarpeta().setTipoCarpeta(TipoCarpeta.DEMANDA);
        demanda.getCarpeta().setFolio("1").setSelloEstatus(SelloEstatus.VALIDO);

        List<String> nuevosAnexos = Arrays.asList("INE", "Acta de nacimiento");
        String motivoEdita = "Corrección";

        List<Anexo> anexosActuales = Arrays.asList(new Anexo().setNombre("Acta de nacimiento"), new Anexo().setNombre("IFE"));

        given(documentoRepository.findById(demanda.getId())).willReturn(Optional.of(demanda));
        given(anexoRepository.findAllByDocumentoId(demanda.getId())).willReturn(anexosActuales);

        DocumentoRecord result = documentoService.editarAnexos(demanda.getId(), nuevosAnexos, motivoEdita);

        verify(documentoRepository).save(demanda);
        verify(anexoRepository).delete(anexosActuales.get(1));
        verify(anexoRepository, times(1)).save(any(Anexo.class));

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(demanda.getId());

        assertThat(demanda.getMotivoEdita()).isEqualTo(motivoEdita);
        assertThat(demanda.getCarpeta().getSelloEstatus()).isEqualTo(SelloEstatus.NO_VALIDO);
    }


    @Test
    void getDemandaById_notFoundException() {
        Integer documentoId = 1;
        List<String> nuevosAnexos = Arrays.asList("Anexo1", "Anexo2");
        String motivoEdita = "Corrección";

        given(documentoRepository.findById(documentoId)).willReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () ->
                documentoService.editarAnexos(documentoId, nuevosAnexos, motivoEdita));

        assertThat(exception.getMessage()).contains("Documento no encontrado");
    }


    @Test
    void getDemandaById_success() {
        Documento demanda = DocumentoSetUp.create(tipoJuicio);
        demanda.getCarpeta().setTipoCarpeta(TipoCarpeta.DEMANDA);
        demanda.getCarpeta().setFolio("1");

        PersonaDocumentoRecord actorRecord = new PersonaDocumentoRecord("Juan", "Perez", "", null, "fisica", "Actor", 1, demanda.getCarpeta().getId());
        PersonaDocumentoRecord demandadoRecord = new PersonaDocumentoRecord("María", "López", "Martínez", null, "fisica", "Demandado", 2, demanda.getCarpeta().getId());

        List<PersonaDocumentoRecord> personas = Arrays.asList(actorRecord, demandadoRecord);
        List<String> anexos = Arrays.asList("Acta de nacimiento", "INE");

        given(documentoRepository.findById(demanda.getId())).willReturn(Optional.of(demanda));
        given(personaDocumentoRepository.findPersonasByCarpetaId(demanda.getCarpeta().getId(), Rol.PRINCIPAL)).willReturn(personas);
        given(anexoRepository.findNombresAnexosByDocumentoId(demanda.getId())).willReturn(anexos);

        DocumentoResponseRecord response = documentoService.getDemandaById(demanda.getId());

        PersonaDocumentoRecord actorResult = response.actor();
        PersonaDocumentoRecord demandadoResult = response.demandado();
        List<String> anexosResult = response.anexos();

        assertThat(response).isNotNull();
        assertThat(actorResult).isNotNull();
        assertThat(actorResult.nombre()).isEqualTo(actorRecord.nombre());
        assertThat(actorResult.apellidoPaterno()).isEqualTo(actorRecord.apellidoPaterno());
        assertThat(actorResult.apellidoMaterno()).isEqualTo(actorRecord.apellidoMaterno());
        assertThat(actorResult.pseudonimo()).isEqualTo(actorRecord.pseudonimo());
        assertThat(actorResult.tipoPersona()).isEqualTo(actorRecord.tipoPersona());
        assertThat(actorResult.tipoParte()).isEqualTo(actorRecord.tipoParte());

        assertThat(demandadoResult).isNotNull();
        assertThat(demandadoResult.nombre()).isEqualTo(demandadoRecord.nombre());
        assertThat(demandadoResult.apellidoPaterno()).isEqualTo(demandadoRecord.apellidoPaterno());
        assertThat(demandadoResult.apellidoMaterno()).isEqualTo(demandadoRecord.apellidoMaterno());
        assertThat(demandadoResult.pseudonimo()).isEqualTo(demandadoRecord.pseudonimo());
        assertThat(demandadoResult.tipoPersona()).isEqualTo(demandadoRecord.tipoPersona());
        assertThat(demandadoResult.tipoParte()).isEqualTo(demandadoRecord.tipoParte());

        assertThat(anexosResult).isNotNull().containsExactly(anexos.get(0), anexos.get(1));
    }
}
