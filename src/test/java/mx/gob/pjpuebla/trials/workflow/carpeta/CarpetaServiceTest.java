package mx.gob.pjpuebla.trials.workflow.carpeta;

import mx.gob.pjpuebla.trials.core.conceptos.Concepto;
import mx.gob.pjpuebla.trials.core.distritos.Distrito;
import mx.gob.pjpuebla.trials.core.distritos.DistritoRepository;
import mx.gob.pjpuebla.trials.core.distritos.DistritoSetUp;
import mx.gob.pjpuebla.trials.core.domicilio.DomicilioSetUp;
import mx.gob.pjpuebla.trials.core.domicilios.Domicilio;
import mx.gob.pjpuebla.trials.core.domicilios.DomicilioRepository;
import mx.gob.pjpuebla.trials.core.etapaprocesal.EtapaProcesal;
import mx.gob.pjpuebla.trials.core.etapaprocesal.EtapaProcesalRepository;
import mx.gob.pjpuebla.trials.core.juzgados.Juzgado;
import mx.gob.pjpuebla.trials.core.juzgados.JuzgadoSetUp;
import mx.gob.pjpuebla.trials.core.materias.Materia;
import mx.gob.pjpuebla.trials.core.materias.MateriaRepository;
import mx.gob.pjpuebla.trials.core.materias.MateriaSetUp;
import mx.gob.pjpuebla.trials.core.personas.*;
import mx.gob.pjpuebla.trials.core.procedimientos.Procedimiento;
import mx.gob.pjpuebla.trials.core.roles.RoleRecord;
import mx.gob.pjpuebla.trials.core.rubros.Rubro;
import mx.gob.pjpuebla.trials.core.rubros.RubroRecord;
import mx.gob.pjpuebla.trials.core.rubros.RubroRepository;
import mx.gob.pjpuebla.trials.core.sedes.Sede;
import mx.gob.pjpuebla.trials.core.sedes.SedeRepository;
import mx.gob.pjpuebla.trials.core.sedes.SedeSetUp;
import mx.gob.pjpuebla.trials.core.tipojuicio.TipoJuicio;
import mx.gob.pjpuebla.trials.core.tipojuicio.TipoJuicioRepository;
import mx.gob.pjpuebla.trials.core.tipojuicio.TipoJuicioSetUp;
import mx.gob.pjpuebla.trials.core.tipopartes.TipoPartes;
import mx.gob.pjpuebla.trials.core.tipopartes.TipoPartesRepository;
import mx.gob.pjpuebla.trials.core.tipopartes.TipoPartesSetUp;
import mx.gob.pjpuebla.trials.core.tipopieza.TipoPieza;
import mx.gob.pjpuebla.trials.core.tipopieza.TipoPiezaRepository;
import mx.gob.pjpuebla.trials.core.tiposistema.TipoSistema;
import mx.gob.pjpuebla.trials.core.tiposistema.TipoSistemaRepository;
import mx.gob.pjpuebla.trials.core.tiposistema.TipoSistemaSetUp;
import mx.gob.pjpuebla.trials.error.NotFoundException;
import mx.gob.pjpuebla.trials.util.Audit;
import mx.gob.pjpuebla.trials.util.enums.*;
import mx.gob.pjpuebla.trials.util.enums.EstadoAnexo;
import mx.gob.pjpuebla.trials.util.enums.PresentacionImputado;
import mx.gob.pjpuebla.trials.util.enums.SolicitudAudiencia;
import mx.gob.pjpuebla.trials.util.enums.EstadoCarpeta;
import mx.gob.pjpuebla.trials.util.enums.TipoDocumento;
import mx.gob.pjpuebla.trials.util.enums.carpeta.*;
import mx.gob.pjpuebla.trials.workflow.anexos.Anexo;
import mx.gob.pjpuebla.trials.workflow.anexos.AnexoBandejaRecepcionRecord;
import mx.gob.pjpuebla.trials.workflow.anexos.AnexoRepository;
import mx.gob.pjpuebla.trials.workflow.anexos.AnexoSetUp;
import mx.gob.pjpuebla.trials.workflow.audiencias.AudienciaService;
import mx.gob.pjpuebla.trials.workflow.carpeta.carpetadetalle.CarpetaDetalle;
import mx.gob.pjpuebla.trials.workflow.carpeta.carpetadetalle.CarpetaDetalleRepository;
import mx.gob.pjpuebla.trials.workflow.carpeta.carpetaetapas.CarpetaEtapas;
import mx.gob.pjpuebla.trials.workflow.carpeta.carpetaetapas.CarpetaEtapasRepository;
import mx.gob.pjpuebla.trials.workflow.carpeta.records.*;
import mx.gob.pjpuebla.trials.workflow.documentos.*;
import mx.gob.pjpuebla.trials.workflow.documentos.documentosdetalle.DocumentoDetalle;
import mx.gob.pjpuebla.trials.workflow.documentos.documentosdetalle.DocumentoDetalleRepository;
import mx.gob.pjpuebla.trials.workflow.documentos.records.*;
import mx.gob.pjpuebla.trials.workflow.folios.JuzgadoFolios;
import mx.gob.pjpuebla.trials.workflow.movimientos.MovimientoRepository;
import mx.gob.pjpuebla.trials.workflow.movimientos.MovimientoService;
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
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Stream;

import static mx.gob.pjpuebla.trials.workflow.folios.JuzgadoFoliosSetUp.createJuzgadoFolios;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CarpetaServiceTest {

    @Mock
    CarpetaRepository carpetaRepository;
    @Mock
    PersonaDocumentoRepository personaDocumentoRepository;
    @InjectMocks
    CarpetaService target;
    @Mock
    PersonaService personaService;
    @Mock
    TipoPartesRepository tipoPartesRepository;
    @Mock
    private TipoSistemaRepository tipoSistemaRepository;
    @Mock
    private MateriaRepository materiaRepository;
    @Mock
    private DistritoRepository distritoRepository;
    @Mock
    private DomicilioRepository domicilioRepository;
    @Mock
    private SedeRepository sedeRepository;
    @Mock
    private CarpetaService carpetaService;
    @Mock
    private MovimientoService movimientoService;
    @Mock
    private MovimientoRepository movimientoRepository;
    @Mock
    private PersonaRepository personaRepository;
    @Mock
    private DocumentoRepository documentoRepository;
    @Mock
    private AnexoRepository anexoRepository;
    @Mock
    private AudienciaService audienciaService;
    @Mock
    private TipoPiezaRepository tipoPiezaRepository;
    @Mock
    private CarpetaDetalleRepository carpetaDetalleRepository;
    @Mock
    private TipoJuicioRepository tipoJuicioRepository;
    @Mock
    private RubroRepository rubroRepository;
    @Mock
    private EtapaProcesalRepository etapaProcesalRepository;
    @Mock
    private CarpetaEtapasRepository carpetaEtapasRepository;
    @Mock
    private DocumentoDetalleRepository documentoDetalleRepository;

    private Carpeta validCarpeta;
    private PersonaDocumentoRecord actor;
    private PersonaDocumentoRecord demandado;
    private ApelacionRecordResponse apelacionRecordResponse;
    private TipoJuicio tipoJuicio;
    private Juzgado juzgado;
    private JuzgadoFolios juzgadoFolios;
    private TipoPartes actorApelacion;
    private TipoPartes demandadoApelacion;


    @BeforeEach
    public void setUp() {
        validCarpeta = CarpetaSetUp.create(TipoJuicioSetUp.createTipoJuicio(), JuzgadoSetUp.createJuzgado());
        actor = new PersonaDocumentoRecord("Juan", "Perez", "", null, "fisica", "Actor", "", "2461740005", "a@a.com",
                "Actor", 1, validCarpeta.getId());
        demandado = new PersonaDocumentoRecord("Nauj", "Zerep", "", null, "fisica", "Demandado", "", "2461740005",
                "a@d.com", "Demandado", 2, validCarpeta.getId());
        apelacionRecordResponse = CarpetaSetUp.apelacionRecordResponse();
        actorApelacion = TipoPartesSetUp.createTipoPartes().setTipoJuicio(tipoJuicio);
        tipoPartesRepository.save(actorApelacion);
        demandadoApelacion = TipoPartesSetUp.createTipoPartes().setTipoJuicio(tipoJuicio).setNombre("Demandado");
        tipoPartesRepository.save(demandadoApelacion);

        Materia materia = materiaRepository.save(MateriaSetUp.createMateria());
        Distrito distrito = distritoRepository.save(DistritoSetUp.createDistrito());
        Domicilio domicilio = domicilioRepository.save(DomicilioSetUp.createDomicilio());

        Sede sede = SedeSetUp.createSede();
        sede.setDistrito(distrito);
        sede.setDomicilio(domicilio);
        sede = sedeRepository.save(sede);

        juzgado = JuzgadoSetUp.createJuzgado(materia, sede);
        juzgado.setContadorAsignaciones(0);
        juzgado.setMaxAsignacionesRonda(0);

        juzgadoFolios = createJuzgadoFolios();
        juzgadoFolios.setJuzgado(juzgado);
        TipoSistema tipoSistema = tipoSistemaRepository.save(TipoSistemaSetUp.createTipoSistema());
        tipoJuicio = TipoJuicioSetUp.createTipoJuicio(tipoSistema, materia);
    }

    @Test
    void getCarpetaResponseByNumExpYearJuzgado_return_CarpetaResponseRecord() {
        given(carpetaRepository.findByExpedienteAndJuzgadoId(any(), any()))
                .willReturn(Optional.ofNullable(validCarpeta));
        given(personaDocumentoRepository.findPersonaAndTipoParteByCarpetaId(any(), eq("Actor"), any()))
                .willReturn(actor);
        given(personaDocumentoRepository.findPersonaAndTipoParteByCarpetaId(any(), eq("Demandado"), any()))
                .willReturn(demandado);

        CarpetaResponseRecord carpetaResponseRecord = target.getCarpetaResponseByNumExpYearJuzgado("000001/2024", 1);

        assertThat(carpetaResponseRecord)
                .isOfAnyClassIn(CarpetaResponseRecord.class)
                .hasFieldOrPropertyWithValue("idCarpeta", validCarpeta.getId())
                .hasFieldOrPropertyWithValue("actor", actor.nombre() + " " + actor.apellidoPaterno())
                .hasFieldOrPropertyWithValue("demandado", demandado.nombre() + " " + demandado.apellidoPaterno());
    }

    @Test
    void getCarpetaResponseByNumExpYearJuzgado_return_not_found() {
        NotFoundException assertThrows = assertThrows(
                NotFoundException.class,
                () -> target.getCarpetaResponseByNumExpYearJuzgado("1", 1));
        assertThat(assertThrows.getMessage()).contains("Carpeta no encontrada");
    }

    @Test
    void getPersonaDocumentoById_return_carpetaId() {
        List<ApelacionRecordResponse> expectedResponses = Collections.singletonList(apelacionRecordResponse);

        given(personaDocumentoRepository.findPersonaDocumentoByCarpetaId(validCarpeta.getId()))
                .willReturn(expectedResponses);

        List<ApelacionRecordResponse> result = target.getPersonasDocumentoByCarpetaId(validCarpeta.getId());
        assertThat(result).isNotEmpty();
        ApelacionRecordResponse actualResponse = result.get(0);
        assertThat(actualResponse)
                .usingRecursiveComparison()
                .isEqualTo(apelacionRecordResponse);
    }

    @Test
    void getBandejaRecepcionByDocumentoId_ReturnsBandejaRecepcion() {
        Integer documentoId = 1;

        // Setup de datos de prueba
        BandejaRecepcionRecord bandejaRecepcion = DocumentoSetUp.createBandejaRecepcion();
        List<AnexoBandejaRecepcionRecord> anexosRecepcion = DocumentoSetUp.createAnexosDocumento();
        Documento documento = DocumentoSetUp.create(tipoJuicio);
        Persona persona = PersonaSetUp.createPersona();
        Juzgado juzgado1 = JuzgadoSetUp.createJuzgado();

        // Relacionando juzgado de persona y documento
        persona.setJuzgado(juzgado1);
        documento.getCarpeta().setJuzgado(juzgado1);

        given(personaService.getAuditor())
                .willReturn(persona);
        given(documentoRepository.findById(documentoId))
                .willReturn(Optional.of(documento));
        given(carpetaRepository.findByDocumentoId(documento.getId()))
                .willReturn(bandejaRecepcion);
        given(carpetaRepository.findAnexosByDocumentoId(bandejaRecepcion.documentoId()))
                .willReturn(anexosRecepcion);

        BandejaRecepcionRecord result = target.getBandejaRecepcionByDocumentoId(documentoId);

        assertThat(result).isNotNull();
        assertThat(result.documentoId()).isEqualTo(bandejaRecepcion.documentoId());
        assertThat(result.folio()).isEqualTo(bandejaRecepcion.folio());
        assertThat(result.expediente()).isEqualTo(bandejaRecepcion.expediente());
        assertThat(result.tipo()).isEqualTo(bandejaRecepcion.tipo());
        assertThat(result.rutaDigitalizacion()).isEqualTo(bandejaRecepcion.rutaDigitalizacion());
        assertThat(result.anexos()).isEqualTo(anexosRecepcion);
    }

    @Test
    void getBandejaRecepcionByDocumentoId_DocumentoNotFound_ThrowsNotFoundException() {
        Integer documentoId = 1;

        Persona persona = PersonaSetUp.createPersona();
        given(personaService.getAuditor())
                .willReturn(persona);
        given(documentoRepository.findById(documentoId)).willReturn(Optional.empty());

        assertThatThrownBy(() -> target.getBandejaRecepcionByDocumentoId(documentoId))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("No se encontró el documento asociado al documentoId");
    }

    @Test
    void getBandejaRecepcionByDocumentoId_UnauthorizedAccess_ThrowsResponseStatusException() {
        Integer documentoId = 1;

        Persona persona = PersonaSetUp.createPersona();
        Documento documento = DocumentoSetUp.create(tipoJuicio);

        persona.setJuzgado(JuzgadoSetUp.createJuzgado());
        documento.getCarpeta().setJuzgado(JuzgadoSetUp.createJuzgado().setId(13));

        given(personaService.getAuditor())
                .willReturn(persona);
        given(documentoRepository.findById(documentoId)).willReturn(Optional.of(documento));

        assertThatThrownBy(() -> target.getBandejaRecepcionByDocumentoId(documentoId))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining(HttpStatus.UNAUTHORIZED.toString())
                .hasMessageContaining("No tiene permiso para visualizar esta información");
    }

    @Test
    void getBandejaRecepcionByDocumentoId_BandejaNotFound_ThrowsNotFoundException() {
        Integer documentoId = 1;

        Persona persona = PersonaSetUp.createPersona();
        Documento documento = DocumentoSetUp.create(tipoJuicio);
        Juzgado juzgado1 = JuzgadoSetUp.createJuzgado();

        persona.setJuzgado(juzgado1);
        documento.getCarpeta().setJuzgado(juzgado1);

        given(personaService.getAuditor())
                .willReturn(persona);
        given(documentoRepository.findById(documentoId)).willReturn(Optional.of(documento));
        given(carpetaRepository.findByDocumentoId(documento.getId())).willReturn(null);

        assertThatThrownBy(() -> target.getBandejaRecepcionByDocumentoId(documentoId))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("No se encontró la carpeta con el documentoId");
    }

    @Test
    void actualizarInformacionAnexos_promocion_Success() {
        Concepto concepto = new Concepto().setNombre("Adjuntar").setDias(1);
        Integer documentoId = 123;
        List<AnexoBandejaRecepcionRecord> anexos = List
                .of(new AnexoBandejaRecepcionRecord(1, "INE", EstadoAnexo.ASIGNADO));
        DocumentoRecepcionMovimientosRecord docRecepcionMovimientosRecord = new DocumentoRecepcionMovimientosRecord(
                anexos,
                "Observacion 1",
                "recomendacion 1"
        );
        Documento documento = DocumentoSetUp.create(TipoJuicioSetUp.createTipoJuicio());
        documento.setTipoDocumento(TipoDocumento.PROMOCION);
        Anexo anexo = AnexoSetUp.createAnexo().setEstado(EstadoAnexo.RECIBIDO);
        Persona persona = PersonaSetUp.createPersona();
        Juzgado juzgado2 = JuzgadoSetUp.createJuzgado();
        persona.setJuzgado(juzgado2);
        documento.getCarpeta().setJuzgado(juzgado2);
        documento.getCarpeta().setConcepto(concepto);

        given(personaService.getAuditor())
                .willReturn(persona);
        given(anexoRepository.findById(1)).willReturn(Optional.of(anexo));
        given(documentoRepository.findById(documentoId)).willReturn(Optional.of(documento));
        given(documentoRepository.save(any(Documento.class))).willReturn(documento);

        DocumentoRecord response = target.actualizarInformacionAnexos(docRecepcionMovimientosRecord, documentoId);

        assertThat(response).isNotNull();

        verify(anexoRepository, times(1)).save(anexo);
        verify(documentoRepository, times(1)).save(documento);
    }

    @Test
    void actualizarInformacionAnexos_Success() {
        Concepto concepto = new Concepto().setNombre("Adjuntar").setDias(1);
        Integer documentoId = 123;
        List<AnexoBandejaRecepcionRecord> anexos = List
                .of(new AnexoBandejaRecepcionRecord(1, "INE", EstadoAnexo.ASIGNADO));
        DocumentoRecepcionMovimientosRecord docRecepcionMovimientosRecord = new DocumentoRecepcionMovimientosRecord(
                anexos,
                "Observacion 1",
                "recomendacion 1"
        );
        Documento documento = DocumentoSetUp.create(TipoJuicioSetUp.createTipoJuicio());
        Anexo anexo = AnexoSetUp.createAnexo().setEstado(EstadoAnexo.RECIBIDO);
        Persona persona = PersonaSetUp.createPersona();
        Juzgado juzgado2 = JuzgadoSetUp.createJuzgado();
        persona.setJuzgado(juzgado2);
        documento.getCarpeta().setJuzgado(juzgado2);
        documento.getCarpeta().setConcepto(concepto);

        given(personaService.getAuditor())
                .willReturn(persona);
        given(anexoRepository.findById(1)).willReturn(Optional.of(anexo));
        given(documentoRepository.findById(documentoId)).willReturn(Optional.of(documento));
        given(documentoRepository.save(any(Documento.class))).willReturn(documento);

        DocumentoRecord response = target.actualizarInformacionAnexos(docRecepcionMovimientosRecord, documentoId);

        assertThat(response).isNotNull();

        verify(anexoRepository, times(1)).save(anexo);
        verify(documentoRepository, times(1)).save(documento);
    }

    @Test
    void actualizarInformacionAnexos_AnexoNoEncontrado() {
        Integer documentoId = 123;
        List<AnexoBandejaRecepcionRecord> anexos = List
                .of(new AnexoBandejaRecepcionRecord(1, "INE", EstadoAnexo.ASIGNADO));
        DocumentoRecepcionMovimientosRecord docRecepcionMovimientosRecord = new DocumentoRecepcionMovimientosRecord(
                anexos,
                "Observacion 1",
                "recomendacion 1"
        );
        Documento documento = DocumentoSetUp.create(TipoJuicioSetUp.createTipoJuicio());
        Persona persona = PersonaSetUp.createPersona();
        Juzgado juzgado2 = JuzgadoSetUp.createJuzgado();
        persona.setJuzgado(juzgado2);
        documento.getCarpeta().setJuzgado(juzgado2);

        given(personaService.getAuditor())
                .willReturn(persona);
        given(anexoRepository.findById(1)).willReturn(Optional.empty());
        given(documentoRepository.findById(documentoId)).willReturn(Optional.of(documento));

        NotFoundException exception = assertThrows(NotFoundException.class, () -> target.actualizarInformacionAnexos(docRecepcionMovimientosRecord, documentoId));

        assertThat(exception.getMessage()).isEqualTo("404 NOT_FOUND \"No se encontró el anexo con id: " + 1 + "\"");
    }

    @Test
    void actualizarInformacionAnexos_DocumentoNoEncontrado() {
        Integer documentoId = 123;
        List<AnexoBandejaRecepcionRecord> anexos = List
                .of(new AnexoBandejaRecepcionRecord(1, "INE", EstadoAnexo.ASIGNADO));
        DocumentoRecepcionMovimientosRecord docRecepcionMovimientosRecord = new DocumentoRecepcionMovimientosRecord(
                anexos,
                "Observacion 1",
                "recomendacion 1"
        );
        Persona persona = PersonaSetUp.createPersona();
        Juzgado juzgado1 = JuzgadoSetUp.createJuzgado();
        persona.setJuzgado(juzgado1);

        given(personaService.getAuditor())
                .willReturn(persona);
        given(documentoRepository.findById(documentoId)).willReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> target.actualizarInformacionAnexos(docRecepcionMovimientosRecord, documentoId));


        assertThat(exception.getMessage()).isEqualTo("404 NOT_FOUND \"No se encontró el documento asociado al documentoId: " + documentoId + "\"");
    }

    @Test
    void observacionAnexos_whenAnexosIsNull_doesNotCreateMovimiento() {
        Documento documento = new Documento();
        documento.setCarpeta(new Carpeta());

        List<String> anexos = null;
        carpetaService.setObservacionesAnexos(anexos);

        verify(movimientoService, never()).createMovimento(any(), any(), any(), any(), any());
    }

    @Test
    void observacionAnexos_createsMovimiento_nullCarpeta() {
        TipoJuicio tipoJuicio1 = TipoJuicioSetUp.createTipoJuicio();
        Documento documento = DocumentoSetUp.create(tipoJuicio1);
        List<String> anexos = List.of("Anexo 1", "Anexo 2");
        documento.setTipoDocumento(TipoDocumento.PROMOCION);

        String motivo = target.setObservacionesAnexos(anexos);

        assertThat(motivo).isEqualTo("Hacen falta los siguientes anexos: Anexo 1, Anexo 2. Por favor validar.");
    }

    @Test
    void testGetCatalogoList_ValidCatalogo() {
        List<CarpetaCatalogoRecord> result = target.getCatalogoList("catalogoDiscapacidades");
        List<CarpetaCatalogoRecord> items = Arrays.stream(CatalogoDiscapacidades.values())
                .map(e -> new CarpetaCatalogoRecord(e.name(), e.getEtiqueta()))
                .toList();
        assertNotNull(result);
        assertEquals(CatalogoDiscapacidades.values().length, result.size());
        assertThat(result).isEqualTo(items);
    }

    @Test
    void testGetCatalogoList_CaseCatalogoCondicionMigratoria() {
        List<CarpetaCatalogoRecord> result = target.getCatalogoList("catalogoCondicionMigratoria");
        List<CarpetaCatalogoRecord> items = Arrays.stream(CatalogoCondicionMigratoria.values())
                .map(data -> new CarpetaCatalogoRecord(data.name(), data.getEtiqueta()))
                .toList();
        assertNotNull(result);
        assertEquals(CatalogoCondicionMigratoria.values().length, result.size());
        assertThat(result).isEqualTo(items);
    }

    @Test
    void testGetCatalogoList_CaseCatalogoDeterminacionJurisdiccional() {
        List<CarpetaCatalogoRecord> result = target.getCatalogoList("catalogoDeterminacionJurisdiccional");
        List<CarpetaCatalogoRecord> items = Arrays.stream(CatalogoDeterminacionJurisdiccional.values())
                .map(data -> new CarpetaCatalogoRecord(data.name(), data.getEtiqueta()))
                .toList();
        assertNotNull(result);
        assertEquals(CatalogoDeterminacionJurisdiccional.values().length, result.size());
        assertThat(result).isEqualTo(items);
    }

    @Test
    void testGetCatalogoList_CaseCatalogoTiposDomicilio() {
        List<CarpetaCatalogoRecord> result = target.getCatalogoList("catalogoTiposDomicilio");
        List<CarpetaCatalogoRecord> items = Arrays.stream(CatalogoTiposDomicilio.values())
                .map(data -> new CarpetaCatalogoRecord(data.name(), data.getEtiqueta()))
                .toList();
        assertNotNull(result);
        assertEquals(CatalogoTiposDomicilio.values().length, result.size());
        assertThat(result).isEqualTo(items);
    }

    @Test
    void testGetCatalogoList_CaseCatalogoGrupoVulnerable() {
        List<CarpetaCatalogoRecord> result = target.getCatalogoList("catalogoGrupoVulnerable");
        List<CarpetaCatalogoRecord> items = Arrays.stream(CatalogoGrupoVulnerable.values())
                .map(data -> new CarpetaCatalogoRecord(data.name(), data.getEtiqueta()))
                .toList();
        assertNotNull(result);
        assertEquals(CatalogoGrupoVulnerable.values().length, result.size());
        assertThat(result).isEqualTo(items);
    }

    @Test
    void testGetCatalogoList_CaseCatalogoFrecuenciaIngreso() {
        List<CarpetaCatalogoRecord> result = target.getCatalogoList("catalogoFrecuenciaIngreso");
        List<CarpetaCatalogoRecord> items = Arrays.stream(CatalogoFrecuenciaIngreso.values())
                .map(data -> new CarpetaCatalogoRecord(data.name(), data.getEtiqueta()))
                .toList();
        assertNotNull(result);
        assertEquals(CatalogoFrecuenciaIngreso.values().length, result.size());
        assertThat(result).isEqualTo(items);
    }

    @Test
    void testGetCatalogoList_CaseCatalogoTipoDefensor() {
        List<CarpetaCatalogoRecord> result = target.getCatalogoList("catalogoTipoDefensor");
        List<CarpetaCatalogoRecord> items = Arrays.stream(CatalogoTipoDefensor.values())
                .map(data -> new CarpetaCatalogoRecord(data.name(), data.getEtiqueta()))
                .toList();
        assertNotNull(result);
        assertEquals(CatalogoTipoDefensor.values().length, result.size());
        assertThat(result).isEqualTo(items);
    }

    @Test
    void testGetCatalogoList_CaseCatalogoIngresoMensualNeto() {
        List<CarpetaCatalogoRecord> result = target.getCatalogoList("catalogoIngresoMensualNeto");
        List<CarpetaCatalogoRecord> items = Arrays.stream(CatalogoIngresoMensualNeto.values())
                .map(data -> new CarpetaCatalogoRecord(data.name(), data.getEtiqueta()))
                .toList();
        assertNotNull(result);
        assertEquals(CatalogoIngresoMensualNeto.values().length, result.size());
        assertThat(result).isEqualTo(items);
    }

    @Test
    void testGetCatalogoList_CaseCatalogoProfesionOficio() {
        List<CarpetaCatalogoRecord> result = target.getCatalogoList("catalogoProfesionOficio");
        List<CarpetaCatalogoRecord> items = Arrays.stream(CatalogoProfesionOficio.values())
                .map(data -> new CarpetaCatalogoRecord(data.name(), data.getEtiqueta()))
                .toList();
        assertNotNull(result);
        assertEquals(CatalogoProfesionOficio.values().length, result.size());
        assertThat(result).isEqualTo(items);
    }

    @Test
    void testGetCatalogoList_InvalidCatalogo() {
        List<CarpetaCatalogoRecord> result = target.getCatalogoList("catalogoInvalido");
        assertThat(result).isEmpty();
    }

    @Test
    void getInfoExpediente() {
        Rubro rubro1 = new Rubro()
                .setId(1)
                .setNombre("Rubro1")
                .setProcedimiento(new Procedimiento().setId(1).setNombre("Procedimiento1"));
        Rubro rubro2 = new Rubro()
                .setId(2)
                .setNombre("Rubro2")
                .setProcedimiento(new Procedimiento().setId(2).setNombre("Procedimiento2"));
        validCarpeta.setRubros(Set.of(rubro1, rubro2));
        Documento documento = DocumentoSetUp.create(tipoJuicio)
                .setCarpeta(validCarpeta);

        PersonaDataRecord participante1 = new PersonaDataRecord(
                1,
                "Juan",
                "Pérez",
                "Gómez",
                "TipoParte1",
                Rol.PRINCIPAL
        );
        PersonaDataRecord participante2 = new PersonaDataRecord(
                2,
                "Maria",
                "López",
                "Sánchez",
                "TipoParte2",
                Rol.PRINCIPAL
        );
       

        given(carpetaRepository.findById(any())).willReturn(Optional.of(documento.getCarpeta()));
        given(personaDocumentoRepository.findPersonaDocumentoDataByCarpetaId(any()))
                .willReturn(List.of(participante1, participante2));
        //given(audienciaService.getAudienciaAndSalaAndDomicilio(any()))
        //        .willReturn(extraAudienciaSelloRecord);

        InfoExpedienteRecord result = target.getInfoExpediente(1);

        assertThat(result).isNotNull();
        assertThat(result.expediente()).isEqualTo(documento.getCarpeta().getExpediente());
        assertThat(result.tipoJuicio()).isEqualTo(documento.getCarpeta().getTipoJuicio().getNombre());
        //assertThat(result.juezAsignado()).isEqualTo("Juez Perez");
        assertThat(result.tipoProcedimiento()).isEqualTo("Procedimiento1, Procedimiento2");
        assertThat(result.etapaProcesal()).isEqualTo(null);
        assertThat(result.participantes()).hasSize(2);
    }

    @Test
    void testGetCatalogoList_CaseCatalogoSentidoAmparo() {
        List<CarpetaCatalogoRecord> result = target.getCatalogoList("catalogoSentidoAmparo");
        List<CarpetaCatalogoRecord> items = Arrays.stream(CatalogoSentidoAmparo.values())
                .map(data -> new CarpetaCatalogoRecord(data.name(), data.getEtiqueta()))
                .toList();
        assertNotNull(result);
        assertEquals(CatalogoSentidoAmparo.values().length, result.size());
        assertThat(result).isEqualTo(items);
    }

    @Test
    void testGetCatalogoList_CaseCatalogoImpugnacionAmparo() {
        List<CarpetaCatalogoRecord> result = target.getCatalogoList("catalogoImpugnacionAmparo");
        List<CarpetaCatalogoRecord> items = Arrays.stream(CatalogoImpugnacionAmparo.values())
                .map(data -> new CarpetaCatalogoRecord(data.name(), data.getEtiqueta()))
                .toList();
        assertNotNull(result);
        assertEquals(CatalogoImpugnacionAmparo.values().length, result.size());
        assertThat(result).isEqualTo(items);
    }

    @Test
    void getInfoExpedienteDetalle() {
        Rubro rubro1 = new Rubro()
                .setId(1)
                .setNombre("Rubro1")
                .setProcedimiento(new Procedimiento().setNombre("Procedimiento1"));
        Rubro rubro2 = new Rubro()
                .setId(2)
                .setNombre("Rubro2")
                .setProcedimiento(new Procedimiento().setNombre("Procedimiento2"));
        Persona persona = new Persona();
        persona.setNombre("Nombre1")
                .setApellidoPaterno("apellido")
                .setApellidoMaterno("apellido2")
                .setOcupacion("Secretario")
                .setJuzgado(validCarpeta.getJuzgado());

        validCarpeta.setRubros(Set.of(rubro1, rubro2))
                .setDeterminacionJurisdiccional(CatalogoDeterminacionJurisdiccional.PRESENTACION)
                .setPersona(persona);

        Documento documento = DocumentoSetUp.create(tipoJuicio)
                .setCarpeta(validCarpeta);

        CarpetaDetalle carpetaDetalle = new CarpetaDetalle()
                .setId(1)
                .setCarpeta(validCarpeta)
                .setTipoJuicio(tipoJuicio);

        given(carpetaRepository.findById(any())).willReturn(Optional.of(documento.getCarpeta()));
        given(carpetaDetalleRepository.findByCarpetaId(any())).willReturn(carpetaDetalle);

        InfoExpedienteDetalleRecord result = target.getInfoExpedienteDetalle(1);

        assertThat(result).isNotNull();
        assertThat(result.determinacion()).isEqualTo("PRESENTACION");
        assertThat(result.ubicacion()).isEqualTo("Nombre1 apellido apellido2, Secretario, JuzgadoTEST");
    }

    @Test
    void saveExpedienteDetalle() {
        Rubro rubro1 = new Rubro()
                .setNombre("Rubro1")
                .setProcedimiento(new Procedimiento().setNombre("Procedimiento1"));
        Rubro rubro2 = new Rubro()
                .setNombre("Rubro2")
                .setProcedimiento(new Procedimiento().setNombre("Procedimiento2"));
        Persona persona = new Persona();
        persona.setNombre("Nombre1")
                .setApellidoPaterno("apellido")
                .setApellidoMaterno("apellido2")
                .setOcupacion("Secretario")
                .setJuzgado(validCarpeta.getJuzgado());

        validCarpeta.setRubros(Set.of(rubro1, rubro2))
                .setDeterminacionJurisdiccional(CatalogoDeterminacionJurisdiccional.PRESENTACION)
                .setPersona(persona);

        Documento documento = DocumentoSetUp.create(tipoJuicio)
                .setCarpeta(validCarpeta);

        CarpetaDetalle carpetaDetalle = new CarpetaDetalle()
                .setId(1)
                .setCarpeta(validCarpeta)
                .setTipoJuicio(tipoJuicio);

        EtapaProcesal etapaProcesal = new EtapaProcesal().setNombre("Primera Etapa").setId(1);
        EtapaProcesal etapaProcesal2 = new EtapaProcesal().setNombre("Segunda Etapa").setId(2);
        CarpetaEtapas carpetaEtapas = new CarpetaEtapas().setId(1).setEtapaProcesal(etapaProcesal2);

        SaveExpedienteDetalleRecord detalle = new SaveExpedienteDetalleRecord(
                CatalogoDeterminacionJurisdiccional.PRESENTACION,
                null,
                null,
                "Juzgado Central",
                "",
                "Fase inicial",
                "",
                "",
                "",
                "",
                "",
                "",
                null,
                100000,
                "MXN",
                0,
                0,
                "",
                "",
                "",
                "",
                "",
                null,
                null,
                null,
                "",
                PresentacionImputado.PRESENTACION_VOLUNTARIA,
                SolicitudAudiencia.SOLICITUD_AUDIENCIA_PRIVADA,
                null,
                "",
                "",
                "",
                "",
                "",
                1,
                new EtapaProcesalRecord(1, "Etapa Inicial"),
                List.of(
                        new RubroRecord(1, "Rubro1"),
                        new RubroRecord(2, "Rubro2")
                ),
                ""
        );

        given(carpetaRepository.findById(anyInt())).willReturn(Optional.of(documento.getCarpeta()));
        given(carpetaDetalleRepository.findByCarpetaId(anyInt())).willReturn(carpetaDetalle);
        given(tipoJuicioRepository.findById(anyInt())).willReturn(Optional.ofNullable(tipoJuicio));
        given(rubroRepository.findById(anyInt())).willReturn(Optional.of(rubro1));
        given(etapaProcesalRepository.findById(anyInt())).willReturn(Optional.ofNullable(etapaProcesal));
        given(carpetaEtapasRepository.findByCarpetaId(anyInt())).willReturn(Optional.ofNullable(carpetaEtapas));

        target.saveExpedienteDetalle(detalle, 1);

        verify(carpetaRepository).findById(anyInt());
        verify(carpetaDetalleRepository).findByCarpetaId(anyInt());
        verify(tipoJuicioRepository).findById(anyInt());
        verify(rubroRepository, times(2)).findById(anyInt());
        verify(etapaProcesalRepository).findById(anyInt());
        verify(carpetaEtapasRepository).save(any(CarpetaEtapas.class));
        verify(carpetaDetalleRepository).save(any(CarpetaDetalle.class));
        verify(carpetaRepository).save(any(Carpeta.class));
    }
    @Test
    void createPiezaTest(){
        Integer carpetaPadreId = 1;

        TipoPieza tipoPieza = new TipoPieza()
                .setId(1)
                .setClave("AD")
                .setTipo("Amparo Directo");
        Persona persona = PersonaSetUp.createPersona();
        String consecutivo = "AD01";
        String expediente = validCarpeta.getExpediente()+"/"+consecutivo;
        Documento documento = DocumentoSetUp.create(tipoJuicio).setData(new DocumentoData().setPieza(""));
        PiezaRecord piezaRecord = new PiezaRecord(null, tipoPieza.getClave(), Collections.singletonList(1));

        Carpeta piezaTmp = new Carpeta()
                .setId(5)
                .setExpediente(expediente)
                .setCarpetaPadre(validCarpeta);

        given(carpetaRepository.findById(any())).willReturn(Optional.of(validCarpeta));
        given(tipoPiezaRepository.findByIdOrClave(any(), eq("AD"))).willReturn(Collections.singletonList(tipoPieza));
        given(personaService.getAuditor()).willReturn(persona);
        given(carpetaRepository.existsById(carpetaPadreId)).willReturn(true);
        given(tipoPiezaRepository.existsByClave(any())).willReturn(true);
        given(carpetaRepository.save(any())).willReturn(piezaTmp);
        given(documentoRepository.findById(any())).willReturn(Optional.of(documento));

        piezaTmp = target.createPieza(carpetaPadreId, piezaRecord);
        assertThat(piezaTmp).isNotNull()
                .hasFieldOrPropertyWithValue("expediente", "000001/2024/AD01");

    }

    @Test
    void testGetAllDocumentosPiezas(){
        TipoPieza tipoPieza = new TipoPieza()
                .setId(1)
                .setClave("AD")
                .setTipo("Amparo Directo");
        PersonaRecord persona = PersonaSetUp.createPersonaRecord(Collections.singletonList(new RoleRecord("1","OFICIAL")));


        DocumentoDetalleCarpeta documento = new DocumentoDetalleCarpeta(1,"1", TipoDocumento.PROMOCION,
                null, LocalDateTime.now(), "DEMANDA_174FD31D-3E83-4F81-AE3D-0C4EA91D772E.PDF",
                1L, TipoCarpeta.DEMANDA, EstadoCarpeta.ASIGNADO);
        DocumentoDetalleCarpeta pieza = new DocumentoDetalleCarpeta(3, "000001/2024/AD01", null,
                tipoPieza, LocalDateTime.now(), null, 1L, TipoCarpeta.PIEZA, EstadoCarpeta.ASIGNADO);

        List<DocumentoDetalleCarpeta> documentos = Collections.singletonList(documento);
        List<DocumentoDetalleCarpeta> piezas = Collections.singletonList(pieza);
        Page<DocumentoDetalleCarpetaResponse> lista = new PageImpl<>(
                Stream.concat( documentos.stream(), piezas.stream())
                        .map(e->new DocumentoDetalleCarpetaResponse(
                            e.id(),
                            "",
                            e.folio(),
                            e.fechaRegistro(),
                            e.ruta(),
                            "",
                            e.tipoCarpeta().name(),
                            Boolean.FALSE,
                            EstadoCarpeta.ASIGNADO.name(), "")).toList());

        given(personaService.findById(any())).willReturn(persona);
        given(personaService.getAuditor()).willReturn(PersonaSetUp.createPersona());
        given(documentoRepository.findDocumentosByCarpeta(any())).willReturn(documentos);
        given(carpetaRepository.findPiezasByCarpetaPadreId(any(), any())).willReturn(piezas);

        lista = target.getAllDocumentosPiezas(null, validCarpeta.getId(), Pageable.ofSize(lista.getSize()));

        assertThat(lista).isNotEmpty();

    }

    @Test
    void acoplarPiezaExpediente(){

        TipoPieza tipoPieza = new TipoPieza()
                .setId(1)
                .setClave("AD")
                .setTipo("Amparo Directo");
        Persona persona = PersonaSetUp.createPersona();
        String consecutivo = "AD01";
        String expediente = validCarpeta.getExpediente()+"/"+consecutivo;
        Documento documento = DocumentoSetUp.create(tipoJuicio).setData(new DocumentoData().setPieza(""));
        List<Documento> documentos = Collections.singletonList(documento);

        Audit audit = new Audit(LocalDateTime.now(), LocalDateTime.now(), "1", "1");

        Carpeta pieza = new Carpeta()
                .setId(5)
                .setExpediente(expediente)
                .setCarpetaPadre(validCarpeta)
                .setTipoPieza(tipoPieza)
                .setTipoCarpeta(TipoCarpeta.PIEZA);

        pieza.setAudit(audit);
                
        PiezaRecordResponse response;

        given(carpetaRepository.findById(any())).willReturn(Optional.of(pieza));
        given(personaService.getAuditor()).willReturn(persona);
        given(documentoRepository.findByCarpetaId(any())).willReturn(documentos);
        given(carpetaRepository.save(any())).willReturn(pieza);

        response = target.acoplarPieza(pieza.getId(), EstadoCarpeta.CANCELADO.name());

        assertThat(response).isNotNull().hasFieldOrPropertyWithValue("estatus", EstadoCarpeta.CANCELADO);

    }

    @Test
    void libroDeGobierno() {

        Persona persona = PersonaSetUp.createPersona();
        Juzgado juzgado = JuzgadoSetUp.createJuzgado();
        persona.setJuzgado(juzgado);

        List<Carpeta> carpetas = List.of(CarpetaSetUp.create().setPersona(persona));
        Page<Carpeta> carpetaPage = new PageImpl<>(carpetas, PageRequest.of(0, 10), carpetas.size());

        CarpetaDetalle carpetaDetalle = new CarpetaDetalle()
                .setId(1)
                .setCujus("Saul Perez")
                .setTipoJuicio(tipoJuicio);

        given(personaService.getAuditor()).willReturn(persona);
        given(carpetaRepository.findByJuzgado(eq(Collections.singletonList(juzgado)), eq("000001/2024"), eq(PageRequest.of(0, 10))))
                .willReturn(carpetaPage);

        given(personaDocumentoRepository.findPersonaAndTipoParteByCarpetaId(any(), eq("Actor"), any()))
                .willReturn(actor);
        given(personaDocumentoRepository.findPersonaAndTipoParteByCarpetaId(any(), eq("Demandado"), any()))
                .willReturn(demandado);

        given(carpetaDetalleRepository.findByCarpetaId(any())).willReturn(carpetaDetalle);

        Page<LibroGobiernoRecord> result = target.libroDeGobierno("000001/2024", PageRequest.of(0, 10));

        assertThat(result).isNotNull();

        verify(personaService).getAuditor();
        verify(carpetaRepository).findByJuzgado(eq(Collections.singletonList(juzgado)), eq("000001/2024"), eq(PageRequest.of(0, 10)));
        verify(personaDocumentoRepository, times(1)).findPersonaAndTipoParteByCarpetaId(any(), eq("Actor"), any());
        verify(personaDocumentoRepository, times(1)).findPersonaAndTipoParteByCarpetaId(any(), eq("Demandado"), any());
        verify(carpetaDetalleRepository, times(1)).findByCarpetaId(any());
    }

    @Test
    void getCarpetaByExpedienteAndSentencia() {
        juzgado.setMateria(MateriaSetUp.createMateria());
        Persona auditor = PersonaSetUp.createPersona();
        auditor.setJuzgado(juzgado);
        Carpeta carpeta = CarpetaSetUp.create();
        carpeta.setJuzgado(juzgado);
        Documento documento = DocumentoSetUp.create(tipoJuicio);
        documento.setCarpeta(carpeta);
        DocumentoDetalle documentoDetalle = new DocumentoDetalle()
                .setTipoSentencia(TipoSentencia.SENTENCIA_DEFINITIVA)
                .setTipoResolucion(TipoResolucion.CONDENATORIA)
                .setFechaResolucion(LocalDate.now());

        given(personaService.getAuditor()).willReturn(auditor);
        given(documentoRepository.findByExpedienteAndTipoDocumento(carpeta.getExpediente(), TipoDocumento.SENTENCIA, auditor.getJuzgado().getId()))
                .willReturn(Optional.of(documento));
        given(documentoDetalleRepository.findByDocumentoId(documento.getId()))
                .willReturn(Optional.of(documentoDetalle));
        given(personaDocumentoRepository.findPersonaAndTipoParteByCarpetaId(any(), eq("Actor"), any()))
                .willReturn(actor);
        given(personaDocumentoRepository.findPersonaAndTipoParteByCarpetaId(any(), eq("Demandado"), any()))
                .willReturn(demandado);

        SentenciaPublicaResponseRecord result = target.getCarpetaByExpedienteAndSentencia(carpeta.getExpediente());

        assertThat(result)
                .isNotNull()
                .hasFieldOrPropertyWithValue("idCarpeta", carpeta.getId())
                .hasFieldOrPropertyWithValue("actor", "Juan Perez")
                .hasFieldOrPropertyWithValue("demandado", "Nauj Zerep")
                .hasFieldOrPropertyWithValue("materia", carpeta.getJuzgado().getMateria().getNombre())
                .hasFieldOrPropertyWithValue("juzgado", carpeta.getJuzgado().getNombre())
                .hasFieldOrPropertyWithValue("sentencia", documentoDetalle.getTipoSentencia().name())
                .hasFieldOrPropertyWithValue("resolucion", documentoDetalle.getTipoResolucion().name())
                .hasFieldOrPropertyWithValue("fechaResolucion", documentoDetalle.getFechaResolucion());
    }

    @Test
    void validaCancelacionPieza(){

        Integer piezaId = 1;
        LocalDateTime fecha = LocalDateTime.now();
        given(movimientoRepository.countByCarpetaId(anyInt())).willReturn(1);
        given(documentoRepository.countByCarpetaIdAndTipoDocumentoAndAuditFechaAltaAfter(piezaId, TipoDocumento.ACUERDO, fecha)).willReturn(0);

        Boolean result = target.validaCancelacionPieza(piezaId, fecha);

        assertThat(result).isTrue();
    }
}