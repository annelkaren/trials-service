package mx.gob.pjpuebla.trials.workflow.documentos;

import mx.gob.pjpuebla.trials.core.distritos.Distrito;
import mx.gob.pjpuebla.trials.core.distritos.DistritoRepository;
import mx.gob.pjpuebla.trials.core.distritos.DistritoSetUp;
import mx.gob.pjpuebla.trials.core.domicilio.DomicilioSetUp;
import mx.gob.pjpuebla.trials.core.domicilios.Domicilio;
import mx.gob.pjpuebla.trials.core.domicilios.DomicilioRepository;
import mx.gob.pjpuebla.trials.core.juzgados.Juzgado;
import mx.gob.pjpuebla.trials.core.juzgados.JuzgadoRepository;
import mx.gob.pjpuebla.trials.core.juzgados.JuzgadoService;
import mx.gob.pjpuebla.trials.core.juzgados.JuzgadoSetUp;
import mx.gob.pjpuebla.trials.core.materias.Materia;
import mx.gob.pjpuebla.trials.core.materias.MateriaRepository;
import mx.gob.pjpuebla.trials.core.materias.MateriaSetUp;
import mx.gob.pjpuebla.trials.core.oficialias.Oficialia;
import mx.gob.pjpuebla.trials.core.personas.Persona;
import mx.gob.pjpuebla.trials.core.personas.PersonaRepository;
import mx.gob.pjpuebla.trials.core.personas.PersonaService;
import mx.gob.pjpuebla.trials.core.roles.RoleService;
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
import mx.gob.pjpuebla.trials.workflow.carpeta.Carpeta;
import mx.gob.pjpuebla.trials.workflow.carpeta.CarpetaRepository;
import mx.gob.pjpuebla.trials.workflow.carpeta.CarpetaSetUp;
import mx.gob.pjpuebla.trials.workflow.documentos.records.*;
import mx.gob.pjpuebla.trials.workflow.carpeta.records.ApelacionRecord;
import mx.gob.pjpuebla.trials.workflow.folios.JuzgadoFolios;
import mx.gob.pjpuebla.trials.workflow.movimientos.Movimiento;
import mx.gob.pjpuebla.trials.workflow.movimientos.MovimientoService;
import mx.gob.pjpuebla.trials.workflow.personasdocumentos.PersonaDocumento;
import mx.gob.pjpuebla.trials.workflow.personasdocumentos.PersonaDocumentoRecord;
import mx.gob.pjpuebla.trials.workflow.personasdocumentos.PersonaDocumentoRepository;
import mx.gob.pjpuebla.trials.workflow.etiquetas.EtiquetaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static mx.gob.pjpuebla.trials.workflow.folios.JuzgadoFoliosSetUp.createJuzgadoFolios;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.lenient;

@ExtendWith(MockitoExtension.class)
class DocumentoServiceTest {

    @Mock
    private DocumentoRepository documentoRepository;
    @Mock
    private TipoJuicioRepository tipoJuicioRepository;
    @Mock
    private AnexoRepository anexoRepository;
    @InjectMocks
    private DocumentoService documentoService;
    @Mock
    private PersonaDocumentoRepository personaDocumentoRepository;
    @Mock
    private TipoPartesRepository tipoPartesRepository;
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
    @Mock
    private MovimientoService movimientoService;
    @Mock
    private AuditorAware<Jwt> auditorAware;
    @Mock
    private PersonaService personaService;
    @Mock
    private PersonaRepository personaRepository;
    @Mock
    private EtiquetaService etiquetaService;
    @Mock
    private RoleService roleService;

    private TipoJuicio tipoJuicio;
    private Juzgado juzgado;
    private TipoPartes actor;
    private TipoPartes demandado;
    private DocumentoSaveRecord recordRequest;
    private JuzgadoFolios juzgadoFolios;
    private Materia materia;


    @BeforeEach
    public void setUp() {
        materia = MateriaSetUp.createMateria();

        Distrito distrito = distritoRepository.save(DistritoSetUp.createDistrito());
        Domicilio domicilio = domicilioRepository.save(DomicilioSetUp.createDomicilio());
        TipoSistema tipoSistema = tipoSistemaRepository.save(TipoSistemaSetUp.createTipoSistema());
        
        tipoJuicio = TipoJuicioSetUp.createTipoJuicio(tipoSistema, materia);
       
        
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

        juzgadoFolios = createJuzgadoFolios();
        juzgadoFolios.setJuzgado(juzgado);

        juzgadoRepository.save(juzgado);
        recordRequest = DocumentoSetUp.createDocumentoSaveRecord(tipoJuicio.getId());

    }

    @Test
    void create_demanda() {

        Documento demanda = DocumentoSetUp.create(tipoJuicio);
        demanda.getCarpeta().setFolio("1");
        demanda.getCarpeta().setTipoCarpeta(TipoCarpeta.DEMANDA);
        
        given(tipoJuicioRepository.findById(1)).willReturn(Optional.of(tipoJuicio));
        given(juzgadoService.getConexidadJuzgado(any(), any(), any())).willReturn(juzgado);
        given(juzgadoService.getJuzgadoFolios(any(), any())).willReturn(juzgadoFolios);
        given(juzgadoService.checkYearJuzgadoFolios(any())).willReturn(juzgadoFolios);
        given(documentoRepository.save(any())).willReturn(demanda);
        given(tipoPartesRepository.findByNombreAndTipoJuicioId(eq("Actor"), any())).willReturn(Optional.of(actor));
        given(anexoRepository.save(any())).willReturn(AnexoSetUp.createAnexo());
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

        PersonaDocumentoRecord actorRecord = new PersonaDocumentoRecord("Juan", "Perez", "", null, "fisica", "", "", "", "", "Actor", 1, demanda.getCarpeta().getId());
        PersonaDocumentoRecord demandadoRecord = new PersonaDocumentoRecord("María", "López", "Martínez", null, "fisica", "", "", "", "", "Demandado", 2, demanda.getCarpeta().getId());

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

    @Test
    void generateNumExpediente_demanda() {
        given(juzgadoService.getJuzgadoFolios(any(), any())).willReturn(juzgadoFolios);
        given(juzgadoService.checkYearJuzgadoFolios(any())).willReturn(juzgadoFolios);
        String numExpediente = documentoService.generateNumExpediente(
                juzgado, TipoCarpeta.DEMANDA
        );
        assertThat(numExpediente).containsPattern("[0-9]{6}/2024");
    }

    @Test
    void generateNumExpediente_exhorto() {
        given(juzgadoService.getJuzgadoFolios(any(), any())).willReturn(juzgadoFolios);
        given(juzgadoService.checkYearJuzgadoFolios(any())).willReturn(juzgadoFolios);
        String numExpediente = documentoService.generateNumExpediente(
                juzgado, TipoCarpeta.EXHORTO
        );
        assertThat(numExpediente).containsPattern("E[0-9]{6}/2024");
    }

    @Test
    void generateNumExpediente_apelacion() {
        given(juzgadoService.getJuzgadoFolios(any(), any())).willReturn(juzgadoFolios);
        given(juzgadoService.checkYearJuzgadoFolios(any())).willReturn(juzgadoFolios);
        String numExpediente = documentoService.generateNumExpediente(
                juzgado, TipoCarpeta.APELACION
        );
        assertThat(numExpediente).containsPattern("[0-9]{6}/2024");
    }

    @Test
    void generateNumExpediente_despacho() {
        given(juzgadoService.getJuzgadoFolios(any(), any())).willReturn(juzgadoFolios);
        given(juzgadoService.checkYearJuzgadoFolios(any())).willReturn(juzgadoFolios);
        String numExpediente = documentoService.generateNumExpediente(
                juzgado, TipoCarpeta.DESPACHO
        );
        assertThat(numExpediente).containsPattern("D[0-9]{6}/2024");
    }

    @Test
    void generateNumExpediente_apelacion_municipal() {
        given(juzgadoService.getJuzgadoFolios(any(), any())).willReturn(juzgadoFolios);
        given(juzgadoService.checkYearJuzgadoFolios(any())).willReturn(juzgadoFolios);
        String numExpediente = documentoService.generateNumExpediente(
                juzgado, TipoCarpeta.APELACION_MUNICIPAL
        );
        assertThat(numExpediente).containsPattern("T[0-9]{6}/2024");
    }

    @Test
    void generateNumExpediente_amparo() {
        given(juzgadoService.getJuzgadoFolios(any(), any())).willReturn(juzgadoFolios);
        given(juzgadoService.checkYearJuzgadoFolios(any())).willReturn(juzgadoFolios);
        String numExpediente = documentoService.generateNumExpediente(
                juzgado, TipoCarpeta.AMPARO
        );
        assertThat(numExpediente).containsPattern("[0-9]{6}/2024");
    }

    @Test
    void getAllHistorial() {

        Documento documento1 = DocumentoSetUp.create(tipoJuicio);
        documento1.getCarpeta().setFolio("Folio1");
        documento1.getCarpeta().setExpediente("Expediente1");
        documento1.getCarpeta().setEstatus(EstadoCarpeta.CAPTURA);


        Juzgado juzgado1 = new Juzgado();
        documento1.getCarpeta().setJuzgado(juzgado1);
        documento1.getCarpeta().getJuzgado().setMateria(MateriaSetUp.createMateria());


        TipoCarpeta tipoCarpeta1 = TipoCarpeta.DEMANDA;
        documento1.getCarpeta().setTipoCarpeta(tipoCarpeta1);

        Documento documento2 = DocumentoSetUp.create(tipoJuicio);
        documento2.getCarpeta().setFolio("Folio2");
        documento2.getCarpeta().setExpediente("Expediente2");
        documento2.getCarpeta().setEstatus(EstadoCarpeta.TURNADO);


        Juzgado juzgado2 = new Juzgado();
        documento2.getCarpeta().setJuzgado(juzgado2);
        documento2.getCarpeta().getJuzgado().setMateria(MateriaSetUp.createMateria());


        TipoCarpeta tipoCarpeta2 = TipoCarpeta.EXHORTO;
        documento2.getCarpeta().setTipoCarpeta(tipoCarpeta2);

        List<Documento> listPage = Arrays.asList(documento1, documento2);
        Page<Documento> pageDocumentos = new PageImpl<>(listPage, PageRequest.of(0, listPage.size()), listPage.size());

        given(documentoRepository.findAll(any(), any(Pageable.class))).willReturn(pageDocumentos);


        Documento example = new Documento();
        example.setCarpeta(new Carpeta());

        Page<DocumentoGridRecord> result = documentoService.getAllHistorial(PageRequest.of(0, 10), example);


        assertThat(result.getContent())
                .hasSize(2)
                .first()
                .hasFieldOrPropertyWithValue("folio", "Folio1")
                .hasFieldOrPropertyWithValue("expediente", "Expediente1")
                .hasFieldOrPropertyWithValue("estatus", EstadoCarpeta.CAPTURA);


        assertThat(result.getContent().get(1))
                .hasFieldOrPropertyWithValue("folio", "Folio2")
                .hasFieldOrPropertyWithValue("expediente", "Expediente2")
                .hasFieldOrPropertyWithValue("estatus", EstadoCarpeta.TURNADO);


        assertThat(result.getTotalElements()).isEqualTo(2);
    }

    @Test
    void createPromocion() {
        Carpeta carpeta = CarpetaSetUp.create(tipoJuicio, juzgado);
        DocumentoData documentoData = new DocumentoData().setTipoPromocion(TipoPromocion.OFICIO);
        Documento promocion = DocumentoSetUp.create(tipoJuicio);
        promocion.setData(documentoData);
        promocion.setFolio("");
        promocion.setTipoDocumento(TipoDocumento.PROMOCION);

        given(carpetaRepository.findById(any())).willReturn(Optional.of((carpeta)));
        given(documentoRepository.save(any())).willReturn(promocion);
        given(documentoRepository.getNextValPromocion()).willReturn(1L);
        given(documentoRepository.getNextValPromocion()).willReturn(1L);

        List<String> anexos = List.of("Anexo1", "Anexo2");
        DocumentoPromocionRecord documentoPromocionRecord = new DocumentoPromocionRecord(1, TipoPromocion.OFICIO, anexos);
        DocumentoPromocionResponseRecord documentoResponse = documentoService.createPromocion(documentoPromocionRecord);

        assertThat(documentoResponse)
                .isNotNull()
                .hasFieldOrPropertyWithValue("id", promocion.getId())
                .hasFieldOrPropertyWithValue("folio", promocion.getFolio())
                .hasFieldOrPropertyWithValue("tipoDocumento", promocion.getTipoDocumento());
    }

    @Test
    void create_exhorto() {
        DocumentoExhortoRecord record = new DocumentoExhortoRecord("", "", Arrays.asList("1", "2"));
        Documento exhorto = DocumentoSetUp.create(tipoJuicio);
        exhorto.getCarpeta().setFolio("1");
        exhorto.getCarpeta().setTipoCarpeta(TipoCarpeta.EXHORTO);

        given(tipoJuicioRepository.findByNombreIgnoreCase(any())).willReturn(Optional.of(tipoJuicio));
        given(juzgadoService.getJuzgado(any())).willReturn(juzgado);
        given(juzgadoService.getJuzgadoFolios(any(), any())).willReturn(juzgadoFolios);
        given(juzgadoService.checkYearJuzgadoFolios(any())).willReturn(juzgadoFolios);
        given(documentoRepository.save(any())).willReturn(exhorto);
        given(anexoRepository.save(any())).willReturn(AnexoSetUp.createAnexo());
        given(carpetaRepository.save(any())).willReturn(exhorto.getCarpeta());

        DocumentoRecord documentoRecord = new DocumentoRecord(exhorto.getId(), exhorto.getCarpeta().getFolio(), TipoCarpeta.EXHORTO);

        DocumentoRecord response = documentoService.createExhorto(record);
        assertThat(response).isOfAnyClassIn(DocumentoRecord.class)
                .hasFieldOrPropertyWithValue("id", documentoRecord.id())
                .hasFieldOrPropertyWithValue("folio", documentoRecord.folio())
                .hasFieldOrPropertyWithValue("tipoCarpeta", documentoRecord.tipoCarpeta());
    }

    @Test
    void create_apelacion() {
        TipoJuicio tipoJuicio = TipoJuicioSetUp.createTipoJuicio();
        Documento demanda = DocumentoSetUp.create(tipoJuicio);
        Carpeta carpetaMock = CarpetaSetUp.create();
        demanda.setCarpeta(carpetaMock);
        demanda.getCarpeta().setTipoCarpeta(TipoCarpeta.APELACION);
        TipoPartes tipoPartesMock = new TipoPartes();

        given(juzgadoService.getJuzgadoFolios(any(), any())).willReturn(juzgadoFolios);
        given(juzgadoService.checkYearJuzgadoFolios(any())).willReturn(juzgadoFolios);
        lenient().when(carpetaRepository.findById(carpetaMock.getId())).thenReturn(Optional.of(carpetaMock));
        lenient().when(tipoJuicioRepository.findById(any())).thenReturn(Optional.of(tipoJuicio));
        lenient().when(juzgadoService.getConexidadJuzgado(any(), any(), any())).thenReturn(new Juzgado());
        lenient().when(carpetaRepository.findById(carpetaMock.getId())).thenReturn(Optional.of(carpetaMock));
        lenient().when(tipoJuicioRepository.findById(any())).thenReturn(Optional.of(tipoJuicio));
        given(tipoPartesRepository.findById(any())).willReturn(Optional.of(tipoPartesMock));
        given(anexoRepository.save(any())).willReturn(new Anexo());
        given(carpetaRepository.save(any())).willReturn(demanda.getCarpeta());
        given(documentoRepository.save(any(Documento.class))).willReturn(demanda);

        ApelacionRecord apelacionRecord = CarpetaSetUp.apelacionRecord();

        DocumentoRecord response = documentoService.createApelacion(apelacionRecord);
        assertThat(response)
                .isNotNull()
                .hasFieldOrPropertyWithValue("id", demanda.getId())
                .hasFieldOrPropertyWithValue("folio", demanda.getCarpeta().getFolio())
                .hasFieldOrPropertyWithValue("tipoCarpeta", TipoCarpeta.APELACION);
        verify(carpetaRepository).findById(apelacionRecord.carpetaId());
        verify(tipoJuicioRepository).findById(demanda.getCarpeta().getTipoJuicio().getId());
        verify(carpetaRepository).save(any(Carpeta.class));
        verify(documentoRepository).save(any(Documento.class));
        verify(anexoRepository).save(any(Anexo.class));
        verify(personaDocumentoRepository, times(apelacionRecord.apelacionPersonaRecords().size())).save(any(PersonaDocumento.class));
    }

    @Test
    void getAllBandejaRecepcion_return_page() {
        Documento demanda = DocumentoSetUp.create(tipoJuicio);
        demanda.getCarpeta().setFolio("1");
        demanda.getCarpeta().setJuzgado(juzgado);
        Movimiento movimiento = new Movimiento().setDocumento(demanda).setMotivo("RECEPCION");
        List<Movimiento> listPage = Collections.singletonList(movimiento);
        Persona persona = new Persona().setJuzgado(juzgado).setUsuario("d8945bc4-af8e-4eb0-b742-7ee13beb43e0");
        given(personaService.getAuditor()).willReturn(persona);
        given(roleService.hasRole(any(String.class), any(String.class))).willReturn(true);
        given(etiquetaService.renderEtiquetaRecepcion(any(String.class), any(Documento.class)))
                .willReturn("Expediente");

        List<EstadoCarpeta> list = Arrays.asList(EstadoCarpeta.TURNADO, EstadoCarpeta.RECEPCION);
        List<String> motivos = Arrays.asList(EstadoCarpeta.TURNADO.name(), EstadoCarpeta.RECEPCION.name());
        given(movimientoService.getAllBandejaRecepcion(
                PageRequest.of(0, listPage.size()),
                juzgado.getId(), list, "", motivos))
                .willReturn(new PageImpl<>(listPage, PageRequest.of(0, listPage.size()), listPage.size()));
        Page<DocumentoBandejaRecepcionRecord> page = documentoService.getAllBandejaRecepcion("", PageRequest.of(0, listPage.size()));
        assertThat(page.getContent())
                .hasSize(1)
                .first()
                .hasFieldOrPropertyWithValue("id", demanda.getId())
                .hasFieldOrPropertyWithValue("folio", demanda.getCarpeta().getFolio())
                .hasFieldOrPropertyWithValue("tipoEntrada", "Expediente")
                .hasFieldOrPropertyWithValue("expediente", demanda.getCarpeta().getExpediente());
    }

    @Test
    void getAllBandejaRecepcion_accessDenied() {
        Persona persona = new Persona().setUsuario("d8945bc4-af8e-4eb0-b742-7ee13beb43e0");
        given(personaService.getAuditor()).willReturn(persona);
        given(roleService.hasRole(any(String.class), any(String.class))).willReturn(false);

        Page<DocumentoBandejaRecepcionRecord> page = documentoService.getAllBandejaRecepcion("", PageRequest.of(1, 20));
        assertThat(page.getContent().size()).isEqualTo(0);
    }

    @Test
    void getOrigen_juzgado() {
        //Mismo juzgado
        Movimiento movimiento = new Movimiento().setJuzgado(juzgado);
        Persona persona = new Persona().setJuzgado(juzgado).setNombre("Juan");
        String origen = documentoService.getOrigen(movimiento, persona);
        assertThat(origen).contains(persona.getNombre());
        //diferente juzgado
        persona = new Persona().setJuzgado(new Juzgado()).setNombre("Juzgado 2").setId(100L);
        origen = documentoService.getOrigen(movimiento, persona);
        assertThat(origen).isEqualTo(persona.getJuzgado().getNombre());
    }

    @Test
    void getOrigen_oficialia() {
        //Misma oficialia
        Oficialia oficialia = new Oficialia().setNombre("Oficialia 1").setId(2);
        Movimiento movimiento = new Movimiento().setOficialia(oficialia);
        Persona persona = new Persona().setOficialia(oficialia).setNombre("Juan");
        String origen = documentoService.getOrigen(movimiento, persona);
        assertThat(origen).contains(persona.getNombre());
        //diferente oficialia
        persona = new Persona().setOficialia(new Oficialia().setNombre("Oficialia 2").setId(3));
        origen = documentoService.getOrigen(movimiento, persona);
        assertThat(origen).isEqualTo(persona.getOficialia().getNombre());
    }

    @Test
    void getOrigen_invalid() {
        Movimiento movimiento = new Movimiento();
        Persona persona = new Persona();
        String origen = documentoService.getOrigen(movimiento, persona);
        assertThat(origen).isEqualTo("");
    }
}
