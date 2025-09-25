package mx.gob.pjpuebla.trials.workflow.notificaciones;

import mx.gob.pjpuebla.trials.core.personas.Persona;
import mx.gob.pjpuebla.trials.core.personas.PersonaService;
import mx.gob.pjpuebla.trials.core.tipopartes.TipoPartes;
import mx.gob.pjpuebla.trials.util.enums.EstadoNotificacion;
import mx.gob.pjpuebla.trials.util.enums.TipoDocumento;
import mx.gob.pjpuebla.trials.core.tipojuicio.TipoJuicioSetUp;
import mx.gob.pjpuebla.trials.util.enums.TipoNotificacion;
import mx.gob.pjpuebla.trials.workflow.carpeta.Carpeta;
import mx.gob.pjpuebla.trials.workflow.carpeta.CarpetaSetUp;
import mx.gob.pjpuebla.trials.workflow.listaestrados.ListaEstrado;
import mx.gob.pjpuebla.trials.workflow.listaestrados.ListaEstradoRepository;
import mx.gob.pjpuebla.trials.workflow.documentos.Documento;
import mx.gob.pjpuebla.trials.workflow.documentos.DocumentoRepository;
import mx.gob.pjpuebla.trials.workflow.documentos.DocumentoSetUp;
import mx.gob.pjpuebla.trials.workflow.documentos.documentosdetalle.DocumentoDetalle;
import mx.gob.pjpuebla.trials.workflow.documentos.documentosdetalle.DocumentoDetalleRepository;
import mx.gob.pjpuebla.trials.workflow.notificaciones.records.*;
import mx.gob.pjpuebla.trials.workflow.notificaciondetalle.NotificacionesDetalles;
import mx.gob.pjpuebla.trials.workflow.notificaciondetalle.NotificacionesDetallesRepository;
import mx.gob.pjpuebla.trials.workflow.personasdocumentos.PersonaDocumento;
import mx.gob.pjpuebla.trials.workflow.personasdocumentos.PersonaDocumentoRepository;
import mx.gob.pjpuebla.trials.workflow.personasdocumentos.PersonasDocumentosSetUp;

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

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificacionServiceTest {

    @Mock
    private NotificacionRepository notificacionRepository;

    @Mock
    private ListaEstradoRepository listaEstradoRepository;

    @Mock
    private PersonaService personaService;

    @Mock
    private NotificacionesDetallesRepository notificacionesDetallesRepository;

    @Mock
    private DocumentoRepository documentoRepository;

    @Mock
    private DocumentoDetalleRepository documentoDetalleRepository;

    @InjectMocks
    private NotificacionService notificacionService;

    private Carpeta carpeta;
    private Notificacion notificacion;

    @Mock
    private PersonaDocumentoRepository personaDocumentoRepository;

    @BeforeEach
    public void setUp() {
        carpeta = CarpetaSetUp.create();
        notificacion = NotificacionSetUp.createNotificacion();
    }

    @Test
    void getNotificacionPorTipoEstrado() {
        // Mock de Pageable
        Pageable pageable = PageRequest.of(0, 10);

        // Mock de Carpeta
        Carpeta carpeta = CarpetaSetUp.create();

        // Mock de Documento
        Documento documento = new Documento();
        documento.setCarpeta(carpeta);
        documento.setTipoDocumento(TipoDocumento.SENTENCIA);

        // Mock de Notificacion
        Notificacion notificacion = new Notificacion();
        notificacion.setId(1);
        notificacion.setDocumento(documento);
        notificacion.setNotas("Notas de prueba");
        notificacion.setTipoNotificacion(TipoNotificacion.ESTRADO);

        // Mock del repositorio
        Page<Notificacion> notificacionPage = new PageImpl<>(List.of(notificacion), pageable, 1);
        when(notificacionRepository.getNotificacionByTipo(
                TipoNotificacion.ESTRADO, EstadoNotificacion.PENDIENTE_DE_ASIGNAR, pageable))
                .thenReturn(notificacionPage);

        // Mock del DocumentoDetalleRepository
        DocumentoDetalle docDetalle = new DocumentoDetalle();
        docDetalle.setExtractoSentencia("Extracto de la sentencia de prueba.");
        when(documentoDetalleRepository.findByDocumentoId(notificacion.getDocumento().getId()))
                .thenReturn(Optional.of(docDetalle));

        PersonaDocumento persona = new PersonaDocumento();
        persona.setId(1)
                .setTipoNotificacion(TipoNotificacion.CORREO_ELECTRONICO)
                .setNombre("Juan")
                .setApellidoPaterno("Perez");

        NotificacionesDetalles notificacionesDetalles = new NotificacionesDetalles()
                .setId(1)
                .setNotificacion(notificacion)
                .setPersonaDocumento(persona)
                .setPersonaDocumento(PersonasDocumentosSetUp.createPersonasDocumentosTipoParte());
        when(notificacionesDetallesRepository.findByNotificacionId(anyInt()))
                .thenReturn(Optional.of(notificacionesDetalles));

        // Llamada al servicio
        Page<NotificacionRecord> result = notificacionService.getAllNotificaciones("ESTRADO", "PENDIENTE_DE_ASIGNAR",
                pageable);

        // Verificaciones
        assertEquals(1, result.getTotalElements());
        NotificacionRecord notificacionRecord = result.getContent().get(0);
        assertEquals(notificacion.getNotas(), notificacionRecord.notas());
        assertEquals(notificacion.getTipoNotificacion(), notificacionRecord.tipo());
        assertEquals("Extracto de la sentencia de prueba.".substring(0, 25), notificacionRecord.concepto().get(0));
    }

    @Mock
    private Pageable pageable;

    @Test
    void getInvalid() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<NotificacionRecord> result = notificacionService.getAllNotificaciones("invalido", "invalido", pageable);

        assertEquals(0, result.getTotalElements());
    }

    @Test
    void getAll() {
        // Configuración del Pageable
        Pageable pageable = PageRequest.of(0, 10);

        // Mock de Documento
        Documento documento = new Documento();
        documento.setId(1);
        documento.setTipoDocumento(TipoDocumento.SENTENCIA);
        documento.setCarpeta(carpeta);

        // Mock de DocumentoDetalle
        DocumentoDetalle documentoDetalle = new DocumentoDetalle();
        documentoDetalle.setExtractoSentencia("Este es el extracto de la sentencia");

        // Mock de Notificacion
        Notificacion notificacion = new Notificacion();
        notificacion.setId(1);
        notificacion.setDocumento(documento);
        notificacion.setNotas("Notas de prueba");
        notificacion.setTipoNotificacion(TipoNotificacion.ESTRADO);

        // Mock del repositorio de Notificaciones
        Page<Notificacion> notificacionesPage = new PageImpl<>(List.of(notificacion), pageable, 1);
        when(notificacionRepository.getNotificacionByTipo(
                TipoNotificacion.ESTRADO, EstadoNotificacion.PENDIENTE_DE_ASIGNAR, pageable))
                .thenReturn(notificacionesPage);

        // Mock del repositorio de DocumentoDetalle
        when(documentoDetalleRepository.findByDocumentoId(1)).thenReturn(Optional.of(documentoDetalle));

        PersonaDocumento persona = new PersonaDocumento();
        persona.setId(1)
                .setTipoNotificacion(TipoNotificacion.CORREO_ELECTRONICO)
                .setNombre("Juan")
                .setApellidoPaterno("Perez");

        NotificacionesDetalles notificacionesDetalles = new NotificacionesDetalles()
                .setId(1)
                .setNotificacion(notificacion)
                .setPersonaDocumento(persona)
                .setPersonaDocumento(PersonasDocumentosSetUp.createPersonasDocumentosTipoParte());

        when(notificacionesDetallesRepository.findByNotificacionId(any())).thenReturn(Optional.of(notificacionesDetalles));

        // Llamada al método que se está probando
        Page<NotificacionRecord> result = notificacionService.getAllNotificaciones(
                "ESTRADO", "PENDIENTE_DE_ASIGNAR", pageable);

        // Verificaciones
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());

        NotificacionRecord record = result.getContent().get(0);
        assertEquals(1, record.id());
        assertEquals("000001/2024", record.expediente());
        assertEquals("Este es el extracto de la sentencia".substring(0, 25), record.concepto().get(0));
        assertEquals("Notas de prueba", record.notas());
        assertEquals(TipoNotificacion.ESTRADO, record.tipo());
    }
    @Test
    void getAll_Notificaciones() {
        // Mock the Pageable
        pageable = PageRequest.of(0, 10);

        // Mock Documento
        Documento documento = new Documento();
        documento.setId(1);
        documento.setTipoDocumento(TipoDocumento.SENTENCIA);
        Carpeta carpeta = new Carpeta();
        carpeta.setExpediente("000001/2024");
        documento.setCarpeta(carpeta);

        // Mock DocumentoDetalle
        DocumentoDetalle documentoDetalle = new DocumentoDetalle();
        documentoDetalle.setExtractoSentencia("Este es el extracto de la sentencia");

        // Mock Notificacion
        Notificacion notificacion = new Notificacion();
        notificacion.setId(1);
        notificacion.setDocumento(documento);
        notificacion.setNotas("Notas de prueba");
        notificacion.setTipoNotificacion(TipoNotificacion.ESTRADO);

        // Mock the Page of Notificaciones
        Page<Notificacion> notificacionesPage = new PageImpl<>(List.of(notificacion), pageable, 1);
        when(notificacionRepository.getNotificacionByTipo(
                TipoNotificacion.ESTRADO, EstadoNotificacion.PENDIENTE_DE_ASIGNAR, pageable))
                .thenReturn(notificacionesPage);

        // Mock DocumentoDetalleRepository
        when(documentoDetalleRepository.findByDocumentoId(1)).thenReturn(Optional.of(documentoDetalle));

        // Mock NotificacionesDetalles
        PersonaDocumento persona = new PersonaDocumento();
        persona.setId(1)
                .setTipoNotificacion(TipoNotificacion.CORREO_ELECTRONICO)
                .setNombre("Juan")
                .setApellidoPaterno("Perez");

        NotificacionesDetalles notificacionesDetalles = new NotificacionesDetalles()
                .setId(1)
                .setNotificacion(notificacion)
                .setPersonaDocumento(PersonasDocumentosSetUp.createPersonasDocumentosTipoParte());

        when(notificacionesDetallesRepository.findByNotificacionId(any())).thenReturn(Optional.of(notificacionesDetalles));

        // Call the service method
        Page<NotificacionRecord> result = notificacionService.getAllNotificaciones(
                "ESTRADO", "PENDIENTE_DE_ASIGNAR", pageable);

        // Assertions
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());

        NotificacionRecord record = result.getContent().get(0);
        assertEquals(1, record.id());
        assertEquals("000001/2024", record.expediente());
        assertEquals("Este es el extracto de la sentencia".substring(0, 25), record.concepto().get(0));
        assertEquals("Notas de prueba", record.notas());
        assertEquals(TipoNotificacion.ESTRADO, record.tipo());
    }
    @Test
    void create_withValidData_createsNotification() throws Exception {
        NotificacionDto notificacionDto = new NotificacionDto();
        notificacionDto.setPersonId(1);
        notificacionDto.setMetodo(TipoNotificacion.CORREO_ELECTRONICO); // Notificación por correo
        notificacionDto.setUsarCorreoRegistrado(false);
        notificacionDto.setCorreo("nuevo@correo.com");

        PersonaDocumento persona = new PersonaDocumento();
        persona.setId(1);
        persona.setTipoNotificacion(TipoNotificacion.CORREO_ELECTRONICO);

        when(personaDocumentoRepository.findById(1)).thenReturn(Optional.of(persona));

        notificacionService.create(notificacionDto);

        assertEquals(TipoNotificacion.CORREO_ELECTRONICO, persona.getTipoNotificacion());
        assertEquals("nuevo@correo.com", persona.getCorreoNotificacion());
        assertNull(persona.getFnDomicilio());

        verify(personaDocumentoRepository).save(persona);
    }

    @Test
    void create_ShouldUpdatePersonaWithNotificationDetails() throws Exception {
        NotificacionDto notificacionDto = new NotificacionDto();
        notificacionDto.setPersonId(1);
        notificacionDto.setMetodo(TipoNotificacion.CORREO_ELECTRONICO); // Email
        notificacionDto.setUsarCorreoRegistrado(false);
        notificacionDto.setCorreo("nuevo_correo@example.com");

        PersonaDocumento persona = new PersonaDocumento();
        persona.setId(1);

        when(personaDocumentoRepository.findById(1)).thenReturn(Optional.of(persona));

        notificacionService.create(notificacionDto);

        assertEquals(TipoNotificacion.CORREO_ELECTRONICO, persona.getTipoNotificacion());
        assertEquals("nuevo_correo@example.com", persona.getCorreoNotificacion());
        assertNull(persona.getFnDomicilio());

        verify(personaDocumentoRepository).save(persona);
    }

    @Test
    void create_ShouldThrowException_WhenPersonaNotFound() {
        NotificacionDto notificacionDto = new NotificacionDto();
        notificacionDto.setPersonId(99);

        when(personaDocumentoRepository.findById(99)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> notificacionService.create(notificacionDto));

        assertEquals("PersonaDocumento no encontrado para el ID: 99", exception.getMessage());
    }

    @Test
    void createRegistroNotificacion() {
        NotificacionSaveRecord notificacion = NotificacionSetUp.createNotificacionSaveRecord();
        List<Integer> personaIds = List.of(1, 2, 3);

        List<PersonaDocumento> personasMock = List.of(
                PersonasDocumentosSetUp.createPersonasDocumentos(),
                PersonasDocumentosSetUp.createPersonasDocumentos(),
                PersonasDocumentosSetUp.createPersonasDocumentos());

        List<NotificacionesDetalles> detalles = List.of(
                NotificacionSetUp.createNotificacionDetalles(),
                NotificacionSetUp.createNotificacionDetalles(),
                NotificacionSetUp.createNotificacionDetalles());

        // Simular la búsqueda del documento
        given(documentoRepository.findById(anyInt()))
                .willReturn(Optional.of(DocumentoSetUp.create(TipoJuicioSetUp.createTipoJuicio())));

        // Simular que se guarda la notificación y se le asigna un ID
        Notificacion notificacionMock = NotificacionSetUp.createNotificacion().setId(1).setTipoNotificacion(TipoNotificacion.ESTRADO); // Asignar un ID mock
        given(notificacionRepository.save(any(Notificacion.class)))
                .willReturn(notificacionMock); // Retornar la notificación mock con ID

        // Simular la búsqueda de personas
        given(personaDocumentoRepository.findAllById(personaIds)).willReturn(personasMock);

        // Simular el guardado de los detalles
        lenient().when(notificacionesDetallesRepository.saveAll(anyList())).thenReturn(detalles);

        // Ejecutar el método
        NotificacionResponseRecord response = notificacionService.createRegistroNotificacion(notificacion);

        // Validar el resultado
        assertNotNull(response);
        assertEquals(200, response.estatus());
        assertTrue(response.mensaje().contains("Notificación creada con éxito"));
    }

    @Test
    void createNotaNotificacion_Success() {
        Integer id = 1;
        String nuevasNotas = "Estas son las nuevas notas";
        Notificacion notificacionExistente = new Notificacion();
        notificacionExistente.setId(id);
        notificacionExistente.setNotas("Notas originales");

        when(notificacionRepository.findById(id)).thenReturn(Optional.of(notificacionExistente));
        notificacionService.createNotaNotificacion(id, nuevasNotas);
        assertEquals(nuevasNotas, notificacionExistente.getNotas());
        verify(notificacionRepository, times(1)).save(notificacionExistente);
    }

    @Test
    void createListaEstrado_Success() {
        List<Integer> notificacionIds = List.of(1, 2, 3);
        LocalDate fechaVencimiento = LocalDate.now();

        Persona auditor = new Persona();
        auditor.setUsuario("auditorUsuario");
        when(personaService.getAuditor()).thenReturn(auditor);

        ListaEstrado listaEstradoMock = new ListaEstrado();
        listaEstradoMock.setId(1);
        listaEstradoMock.setPersona(auditor);
        listaEstradoMock.setFechaVencimiento(fechaVencimiento);
        when(listaEstradoRepository.save(any(ListaEstrado.class))).thenReturn(listaEstradoMock);

        List<Notificacion> notificaciones = notificacionIds.stream()
                .map(id -> {
                    Notificacion nuevaNotificacion = new Notificacion();
                    nuevaNotificacion.setId(id);
                    nuevaNotificacion.setEstadoNotificacion(EstadoNotificacion.PENDIENTE_DE_ASIGNAR);
                    return nuevaNotificacion;
                })
                .toList();

        when(notificacionRepository.findAllById(notificacionIds)).thenReturn(notificaciones);

        notificacionService.createListaEstrado(notificacionIds, fechaVencimiento);
        verify(listaEstradoRepository).save(any(ListaEstrado.class));
        verify(notificacionRepository).findAllById(notificacionIds);

        assertTrue(notificaciones.stream().allMatch(n -> n.getEstadoNotificacion() == EstadoNotificacion.ASIGNADO &&
                n.getListaEstrado().equals(listaEstradoMock)));

        verify(notificacionRepository).saveAll(notificaciones);
    }

    @Test
    void createListaEstrado_Error() {
        List<Integer> notificacionIds = Collections.emptyList();
        LocalDate fechaVencimiento = LocalDate.now();
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> notificacionService.createListaEstrado(notificacionIds, fechaVencimiento));

        assertEquals("Debe proporcionar al menos un ID de notificación.", exception.getMessage());
        verifyNoInteractions(personaService, listaEstradoRepository, notificacionRepository);
    }

    @Test
    void getNotificacionDetalle() {
        Documento documento = new Documento();
        documento.setId(1);
        documento.setTipoDocumento(TipoDocumento.SENTENCIA);
        documento.setCarpeta(carpeta);

        Notificacion notificacion = new Notificacion();
        notificacion.setId(1);
        notificacion.setDocumento(documento);
        notificacion.setNotas("Notas de prueba");
        notificacion.setTipoNotificacion(TipoNotificacion.ESTRADO);

        PersonaDocumento persona = new PersonaDocumento();
        persona.setId(1)
                .setTipoNotificacion(TipoNotificacion.CORREO_ELECTRONICO)
                .setNombre("Juan")
                .setApellidoPaterno("Perez")
                .setTipoPartes(new TipoPartes().setNombre("Actor"));

        NotificacionesDetalles notificacionesDetalles = new NotificacionesDetalles()
                .setId(1)
                .setNotificacion(notificacion)
                .setPersonaDocumento(persona);

        given(notificacionesDetallesRepository.findByNotificacionId(anyInt())).willReturn(Optional.of(notificacionesDetalles));

        NotificacionDetalleRecord response = notificacionService.getNotificacionDetalle(1);

        assertNotNull(response);
        assertEquals(response.parte(), "Actor");
        assertEquals(response.nombre(), "Juan Perez");
        assertEquals(response.domicilio(), "Sin Domicilio");
    }

    @Test
    void updateBatchNotificacionEnRuta() {
        List<Integer> integerList = Collections.singletonList(1);
        String estado = "EN_RUTA";

        Documento documento = new Documento();
        documento.setId(1);
        documento.setTipoDocumento(TipoDocumento.SENTENCIA);
        documento.setCarpeta(carpeta);

        Notificacion notificacion = new Notificacion();
        notificacion.setId(1);
        notificacion.setDocumento(documento);
        notificacion.setNotas("Notas de prueba");
        notificacion.setTipoNotificacion(TipoNotificacion.ESTRADO);

        PersonaDocumento persona = new PersonaDocumento();
        persona.setId(1)
                .setTipoNotificacion(TipoNotificacion.CORREO_ELECTRONICO)
                .setNombre("Juan")
                .setApellidoPaterno("Perez");

        given(notificacionRepository.findAllById(any())).willReturn(Collections.singletonList(notificacion));

        notificacionService.updateBatchNotificacionEnRuta(integerList, estado);

        verify(notificacionRepository, times(1)).findAllById(integerList);
        verify(notificacionRepository, times(1)).saveAll(Collections.singletonList(notificacion));
    }

    @Test
    void acuerdoNotificaciones_success() {

        NotificacionesDetalles notificacionesDetalles = new NotificacionesDetalles();
        notificacionesDetalles.setId(1);
        Notificacion notificacion = new Notificacion();
        notificacion.setId(1);
        notificacion.setTipoNotificacion(TipoNotificacion.ESTRADO);
        notificacion.setEstadoNotificacion(EstadoNotificacion.PENDIENTE_DE_ASIGNAR);
        notificacionesDetalles.setNotificacion(notificacion);

        PersonaDocumento personaDocumento = new PersonaDocumento();
        personaDocumento.setId(1);
        personaDocumento.setNombre("Juan");
        personaDocumento.setApellidoPaterno("Perez");
        personaDocumento.setApellidoMaterno("Lopez");
        notificacionesDetalles.setPersonaDocumento(personaDocumento);

        Page<NotificacionesDetalles> notificacionesDetallesPage = new PageImpl<>(List.of(notificacionesDetalles), PageRequest.of(0, 10), 1);

        given(notificacionesDetallesRepository.findByNotificacionDocumentoId(anyInt(), any(Pageable.class)))
                .willReturn(notificacionesDetallesPage);

        Page<AcuerdoNotificacionesRecord> result = notificacionService.acuerdoNotificaciones(1, PageRequest.of(0, 10));

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        AcuerdoNotificacionesRecord record = result.getContent().get(0);
        assertEquals(1, record.numAcuerdo());
        assertEquals("Juan Perez Lopez", record.nombreDestinatario());
        assertEquals(TipoNotificacion.ESTRADO, record.metodoNotificacion());
        assertEquals(EstadoNotificacion.PENDIENTE_DE_ASIGNAR, record.estatus());
        assertNull(record.comentarios());
    }

    @Test public void testNotificacionesTurnado() {
        List<Integer> carpetaIds = Arrays.asList(1, 2, 3);
        when(notificacionRepository.findNotificacionesTurnado(1)).thenReturn(Collections.emptyList());
        when(notificacionRepository.findNotificacionesTurnado(2)).thenReturn(Arrays.asList(new Notificacion()));
        when(notificacionRepository.findNotificacionesTurnado(3)).thenReturn(Collections.emptyList()); // Call the method to test
        List<Integer> result = notificacionService.notificacionesTurnado(carpetaIds); // Verify the result
        assertEquals(Arrays.asList(1, 3), result);
    }


}