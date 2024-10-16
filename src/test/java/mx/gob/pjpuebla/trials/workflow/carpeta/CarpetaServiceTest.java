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
import mx.gob.pjpuebla.trials.util.Audit;
import mx.gob.pjpuebla.trials.util.enums.EstadoAnexo;
import mx.gob.pjpuebla.trials.workflow.anexos.Anexo;
import mx.gob.pjpuebla.trials.workflow.anexos.AnexoBandejaRecepcionRecord;
import mx.gob.pjpuebla.trials.workflow.anexos.AnexoRepository;
import mx.gob.pjpuebla.trials.workflow.carpeta.records.ApelacionRecordResponse;
import mx.gob.pjpuebla.trials.workflow.carpeta.records.BandejaRecepcionRecord;
import mx.gob.pjpuebla.trials.workflow.carpeta.records.CarpetaResponseRecord;
import mx.gob.pjpuebla.trials.workflow.documentos.Documento;
import mx.gob.pjpuebla.trials.workflow.documentos.DocumentoRepository;
import mx.gob.pjpuebla.trials.workflow.documentos.DocumentoSetUp;
import mx.gob.pjpuebla.trials.workflow.documentos.records.DocumentoRecord;
import mx.gob.pjpuebla.trials.workflow.folios.JuzgadoFolios;
import mx.gob.pjpuebla.trials.workflow.movimientos.MovimientoService;
import mx.gob.pjpuebla.trials.workflow.personasdocumentos.PersonaDocumentoRecord;
import mx.gob.pjpuebla.trials.workflow.personasdocumentos.PersonaDocumentoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static mx.gob.pjpuebla.trials.workflow.folios.JuzgadoFoliosSetUp.createJuzgadoFolios;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
    private PersonaRepository personaRepository;

    @Mock
    private DocumentoRepository documentoRepository;

    @Mock
    private AnexoRepository anexoRepository;

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

        CarpetaResponseRecord carpetaResponseRecord = target.getCarpetaResponseByNumExpYearJuzgado(any(), any());

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
                () -> {
                    target.getCarpetaResponseByNumExpYearJuzgado("1", 1);
                });
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

        assertThatThrownBy(() -> target.getBandejaRecepcionByDocumentoId( documentoId))
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
    void actualizarInformacionAnexos_Success() {
        Integer documentoId = 123;
        List<AnexoBandejaRecepcionRecord> anexos = List.of(new AnexoBandejaRecepcionRecord(1, "INE", EstadoAnexo.ASIGNADO));

        Documento documento = DocumentoSetUp.create(TipoJuicioSetUp.createTipoJuicio());
        Anexo anexo = new Anexo();
        anexo.setId(1);
        anexo.setNombre("INE");
        anexo.setEstado(EstadoAnexo.RECIBIDO);
        anexo.setAudit(new Audit(LocalDateTime.now(), LocalDateTime.now(),
                "6b13785f-d213-4585-a76b-437ffe57c9c7",
                "6b13785f-d213-4585-a76b-437ffe57c9c7"));

        Persona persona = PersonaSetUp.createPersona();
        Juzgado juzgado1 = JuzgadoSetUp.createJuzgado();
        persona.setJuzgado(juzgado1);
        documento.getCarpeta().setJuzgado(juzgado1);
        List<Anexo> anexoList = List.of(anexo);

        given(personaService.getAuditor()).willReturn(persona);
        given(anexoRepository.findAllByDocumentoId(documentoId)).willReturn(anexoList);
        given(anexoRepository.findById(1)).willReturn(Optional.of(anexo));
        given(documentoRepository.findById(documentoId)).willReturn(Optional.of(documento));

        DocumentoRecord response = target.actualizarInformacionAnexos(anexos, documentoId);

        assertThat(response).isNotNull();
        verify(anexoRepository, times(1)).save(anexo); // Asegúrate de que el anexo sea el correcto
        verify(documentoRepository, times(1)).save(documento);
    }

    @Test
    void actualizarInformacionAnexos_AnexoNoEncontrado() {
        Integer documentoId = 123;

        List<AnexoBandejaRecepcionRecord> anexos = List.of(new AnexoBandejaRecepcionRecord(1, "INE", EstadoAnexo.ASIGNADO));

        Documento documento = DocumentoSetUp.create(TipoJuicioSetUp.createTipoJuicio());
        Persona persona = PersonaSetUp.createPersona();
        Juzgado juzgado1 = JuzgadoSetUp.createJuzgado();
        persona.setJuzgado(juzgado1);
        documento.getCarpeta().setJuzgado(juzgado1);

        Anexo anexo = new Anexo();
        anexo.setId(1);
        anexo.setNombre("INE");
        anexo.setEstado(EstadoAnexo.RECIBIDO);
        anexo.setAudit(new Audit(LocalDateTime.now(), LocalDateTime.now(),
                "6b13785f-d213-4585-a76b-437ffe57c9c7",
                "6b13785f-d213-4585-a76b-437ffe57c9c7"));

        given(personaService.getAuditor()).willReturn(persona);
        given(anexoRepository.findAllByDocumentoId(documentoId)).willReturn(List.of(anexo));
        given(documentoRepository.findById(documentoId)).willReturn(Optional.of(documento));

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            target.actualizarInformacionAnexos(anexos, documentoId);
        });

        assertThat(exception.getMessage()).isEqualTo("404 NOT_FOUND \"No se encontró el anexo con id: " + 1 + "\"");
    }



    @Test
    void actualizarInformacionAnexos_DocumentoNoEncontrado() {
        Integer documentoId = 123;
        List<AnexoBandejaRecepcionRecord> anexos = List
                .of(new AnexoBandejaRecepcionRecord(1, "INE", EstadoAnexo.ASIGNADO));

      
        Persona persona = PersonaSetUp.createPersona(); 
        Juzgado juzgado1 = JuzgadoSetUp.createJuzgado();
        persona.setJuzgado(juzgado1);

     
        given(personaService.getAuditor())
        .willReturn(persona);
        given(documentoRepository.findById(documentoId)).willReturn(Optional.empty()); 
                                                                                      

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            target.actualizarInformacionAnexos(anexos, documentoId);
        });

    
        assertThat(exception.getMessage()).isEqualTo("404 NOT_FOUND \"No se encontró el documento asociado al documentoId: " + documentoId + "\"");
    }

    @Test
    void observacionAnexos_whenAnexosIsNull_doesNotCreateMovimiento() {
        Documento documento = new Documento();
        documento.setCarpeta(new Carpeta());

        List<String> anexos = null;
        carpetaService.observacionAnexos(documento, anexos);

        verify(movimientoService, never()).createMovimento(any(), any(), any(), any());
    }

    @Test
    void observacionAnexos_createsMovimiento() {
        TipoJuicio tipoJuicio1 = TipoJuicioSetUp.createTipoJuicio();
        Documento documento = DocumentoSetUp.create(tipoJuicio1);
        List<String> anexos = List.of("Anexo 1", "Anexo 2");
        Persona persona = PersonaSetUp.createPersona();

        when(personaService.getAuditor()).thenReturn(persona);

        target.observacionAnexos(documento, anexos);

        verify(movimientoService).createMovimento(
                eq(documento.getCarpeta()),
                eq(documento),
                eq(persona),
                argThat(motivo -> motivo.equals("Hacen falta los siguientes anexos: Anexo 1, Anexo 2. Por favor validar."))
        );
    }


}