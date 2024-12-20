package mx.gob.pjpuebla.trials.workflow.notificaciones;

import mx.gob.pjpuebla.trials.core.personas.Persona;
import mx.gob.pjpuebla.trials.core.personas.PersonaService;
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
import mx.gob.pjpuebla.trials.workflow.notificaciones.records.NotificacionDto;
import mx.gob.pjpuebla.trials.workflow.notificaciones.records.NotificacionRecord;
import mx.gob.pjpuebla.trials.workflow.notificaciones.records.NotificacionResponseRecord;
import mx.gob.pjpuebla.trials.workflow.notificaciones.records.NotificacionSaveRecord;
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

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;

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
        Carpeta carpeta = new Carpeta();
        carpeta.setExpediente("EXP-123");
    
        // Mock de Documento
        Documento documento = new Documento();
        documento.setCarpeta(carpeta); // Aseguramos que no sea null
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
    
        // Llamada al servicio
        Page<NotificacionRecord> result = notificacionService.getAllNotificaciones("ESTRADO", "PENDIENTE_DE_ASIGNAR", pageable);
    
        // Verificaciones
        assertEquals(1, result.getTotalElements());
        NotificacionRecord notificacionRecord = result.getContent().get(0);
        assertEquals(notificacion.getNotas(), notificacionRecord.notas());
        assertEquals(notificacion.getTipoNotificacion(), notificacionRecord.tipo());
        assertEquals("Extracto de la sentencia de prueba.".substring(0, 25), notificacionRecord.concepto().get(0));
    }
    


    @Test
    void getInvalid() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<NotificacionRecord> result = notificacionService.getAllNotificaciones("invalido", "invalido", pageable);

        assertEquals(0, result.getTotalElements());
    }

    @Test
    void getAll() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Notificacion> notificacionPage = new PageImpl<>(List.of(notificacion), pageable, 1);
        List<String> rubros = List.of("Primer rubro", "Segundo rubro", "Tercer rubro");
        when(notificacionRepository.getNotificacionByTipo(TipoNotificacion.ESTRADO, EstadoNotificacion.PENDIENTE_DE_ASIGNAR, pageable))
                .thenReturn(notificacionPage);

        Page<NotificacionRecord> result = notificacionService.getAllNotificaciones(null, null, pageable);
        assertEquals(1, result.getTotalElements());
        List<String> formattedRubros = notificacionService.formatConcepto(rubros);

        assertEquals(2, formattedRubros.size());
        assertEquals("Primer rubro", formattedRubros.get(0));
        assertEquals(" y 2 más", formattedRubros.get(1));
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
    Notificacion notificacionMock = NotificacionSetUp.createNotificacion().setId(1); // Asignar un ID mock
    given(notificacionRepository.save(any(Notificacion.class)))
            .willReturn(notificacionMock);  // Retornar la notificación mock con ID

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
        Date fechaVencimiento = new Date();

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

        assertTrue(notificaciones.stream().allMatch(n ->
                n.getEstadoNotificacion() == EstadoNotificacion.ASIGNADO &&
                        n.getListaEstrado().equals(listaEstradoMock)
        ));


        verify(notificacionRepository).saveAll(notificaciones);
    }

    @Test
    void createListaEstrado_Error() {
        List<Integer> notificacionIds = Collections.emptyList();
        Date fechaVencimiento = new Date();
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> notificacionService.createListaEstrado(notificacionIds, fechaVencimiento));

        assertEquals("Debe proporcionar al menos un ID de notificación.", exception.getMessage());
        verifyNoInteractions(personaService, listaEstradoRepository, notificacionRepository);
    }


}