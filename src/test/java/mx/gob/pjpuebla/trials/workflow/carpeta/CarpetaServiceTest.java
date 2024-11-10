package mx.gob.pjpuebla.trials.workflow.carpeta;

import mx.gob.pjpuebla.trials.core.distritos.Distrito;
import mx.gob.pjpuebla.trials.core.distritos.DistritoRepository;
import mx.gob.pjpuebla.trials.core.distritos.DistritoSetUp;
import mx.gob.pjpuebla.trials.core.domicilio.DomicilioSetUp;
import mx.gob.pjpuebla.trials.core.domicilios.Domicilio;
import mx.gob.pjpuebla.trials.core.domicilios.DomicilioRepository;
import mx.gob.pjpuebla.trials.core.juzgados.Juzgado;
import mx.gob.pjpuebla.trials.core.juzgados.JuzgadoSetUp;
import mx.gob.pjpuebla.trials.core.materias.Materia;
import mx.gob.pjpuebla.trials.core.materias.MateriaRepository;
import mx.gob.pjpuebla.trials.core.materias.MateriaSetUp;
import mx.gob.pjpuebla.trials.core.personas.Persona;
import mx.gob.pjpuebla.trials.core.personas.PersonaRepository;
import mx.gob.pjpuebla.trials.core.personas.PersonaService;
import mx.gob.pjpuebla.trials.core.personas.PersonaSetUp;
import mx.gob.pjpuebla.trials.core.procedimientos.Procedimiento;
import mx.gob.pjpuebla.trials.core.rubros.Rubro;
import mx.gob.pjpuebla.trials.core.sedes.Sede;
import mx.gob.pjpuebla.trials.core.sedes.SedeRepository;
import mx.gob.pjpuebla.trials.core.sedes.SedeSetUp;
import mx.gob.pjpuebla.trials.core.tipojuicio.TipoJuicio;
import mx.gob.pjpuebla.trials.core.tipojuicio.TipoJuicioSetUp;
import mx.gob.pjpuebla.trials.core.tipopartes.TipoPartes;
import mx.gob.pjpuebla.trials.core.tipopartes.TipoPartesRepository;
import mx.gob.pjpuebla.trials.core.tipopartes.TipoPartesSetUp;
import mx.gob.pjpuebla.trials.core.tiposistema.TipoSistema;
import mx.gob.pjpuebla.trials.core.tiposistema.TipoSistemaRepository;
import mx.gob.pjpuebla.trials.core.tiposistema.TipoSistemaSetUp;
import mx.gob.pjpuebla.trials.error.NotFoundException;
import mx.gob.pjpuebla.trials.util.enums.EstadoAnexo;
import mx.gob.pjpuebla.trials.util.enums.TipoDocumento;
import mx.gob.pjpuebla.trials.util.enums.carpeta.*;
import mx.gob.pjpuebla.trials.workflow.anexos.Anexo;
import mx.gob.pjpuebla.trials.workflow.anexos.AnexoBandejaRecepcionRecord;
import mx.gob.pjpuebla.trials.workflow.anexos.AnexoRepository;
import mx.gob.pjpuebla.trials.workflow.anexos.AnexoSetUp;
import mx.gob.pjpuebla.trials.workflow.audiencias.AudienciaService;
import mx.gob.pjpuebla.trials.workflow.audiencias.record.ExtraAudienciaSelloRecord;
import mx.gob.pjpuebla.trials.workflow.carpeta.records.*;
import mx.gob.pjpuebla.trials.workflow.documentos.Documento;
import mx.gob.pjpuebla.trials.workflow.documentos.DocumentoRepository;
import mx.gob.pjpuebla.trials.workflow.documentos.DocumentoSetUp;
import mx.gob.pjpuebla.trials.workflow.documentos.records.DocumentoRecepcionMovimientosRecord;
import mx.gob.pjpuebla.trials.workflow.documentos.records.DocumentoRecord;
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
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

import static mx.gob.pjpuebla.trials.workflow.folios.JuzgadoFoliosSetUp.createJuzgadoFolios;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
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
        carpetaService.setObservacionesAnexos(documento, anexos);

        verify(movimientoService, never()).createMovimento(any(), any(), any(), any());
    }

    @Test
    void observacionAnexos_createsMovimiento_nullCarpeta() {
        TipoJuicio tipoJuicio1 = TipoJuicioSetUp.createTipoJuicio();
        Documento documento = DocumentoSetUp.create(tipoJuicio1);
        List<String> anexos = List.of("Anexo 1", "Anexo 2");
        Persona persona = PersonaSetUp.createPersona();
        documento.setTipoDocumento(TipoDocumento.OFICIO);

        when(personaService.getAuditor()).thenReturn(persona);

        target.setObservacionesAnexos(documento, anexos);

        verify(movimientoService).createMovimento(
                eq(null),
                eq(documento),
                eq(persona),
                argThat(motivo -> motivo.equals("Hacen falta los siguientes anexos: Anexo 1, Anexo 2. Por favor validar."))
        );
    }

    @Test
    void observacionAnexos_createsMovimiento_nullDocumento() {
        TipoJuicio tipoJuicio1 = TipoJuicioSetUp.createTipoJuicio();
        Documento documento = DocumentoSetUp.create(tipoJuicio1);
        List<String> anexos = List.of("Anexo 1", "Anexo 2");
        Persona persona = PersonaSetUp.createPersona();


        when(personaService.getAuditor()).thenReturn(persona);

        target.setObservacionesAnexos(documento, anexos);

        verify(movimientoService).createMovimento(
                eq(documento.getCarpeta()),
                eq(null),
                eq(persona),
                argThat(motivo -> motivo.equals("Hacen falta los siguientes anexos: Anexo 1, Anexo 2. Por favor validar."))
        );
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
                .setNombre("Rubro1")
                .setProcedimiento(new Procedimiento().setNombre("Procedimiento1"));
        Rubro rubro2 = new Rubro()
                .setNombre("Rubro2")
                .setProcedimiento(new Procedimiento().setNombre("Procedimiento2"));
        validCarpeta.setRubros(Set.of(rubro1, rubro2));
        Documento documento = DocumentoSetUp.create(tipoJuicio)
                .setCarpeta(validCarpeta);

        PersonaDataRecord participante1 = new PersonaDataRecord(
                1,
                "Juan",
                "Pérez",
                "Gómez",
                "TipoParte1"
        );
        PersonaDataRecord participante2 = new PersonaDataRecord(
                2,
                "Maria",
                "López",
                "Sánchez",
                "TipoParte2"
        );
        ExtraAudienciaSelloRecord extraAudienciaSelloRecord = new ExtraAudienciaSelloRecord(
                "Juez Perez",
                "",
                "",
                "",
                ""
        );

        given(documentoRepository.findById(any())).willReturn(Optional.of(documento));
        given(personaDocumentoRepository.findPersonaDocumentoDataByCarpetaId(any()))
                .willReturn(List.of(participante1, participante2));
        given(audienciaService.getAudienciaAndSalaAndDomicilio(any()))
                .willReturn(extraAudienciaSelloRecord);

        InfoExpedienteRecord result = target.getInfoExpediente(1);

        assertThat(result).isNotNull();
        assertThat(result.expediente()).isEqualTo(documento.getCarpeta().getExpediente());
        assertThat(result.tipoJuicio()).isEqualTo(documento.getCarpeta().getTipoJuicio().getNombre());
        assertThat(result.juezAsignado()).isEqualTo("Juez Perez");
        assertThat(result.tipoProcedimiento()).isEqualTo("Procedimiento1, Procedimiento2");
        assertThat(result.rubros()).isEqualTo("Rubro1, Rubro2");
        assertThat(result.etapaProcesal()).isEqualTo("Primera Etapa");
        assertThat(result.participantes()).hasSize(2);
    }
}