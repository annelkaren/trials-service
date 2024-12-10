package mx.gob.pjpuebla.trials.workflow.notificaciones;

import mx.gob.pjpuebla.trials.core.tipojuicio.TipoJuicioSetUp;
import mx.gob.pjpuebla.trials.util.enums.TipoNotificacion;
import mx.gob.pjpuebla.trials.workflow.carpeta.Carpeta;
import mx.gob.pjpuebla.trials.workflow.carpeta.CarpetaSetUp;
import mx.gob.pjpuebla.trials.workflow.documentos.DocumentoRepository;
import mx.gob.pjpuebla.trials.workflow.documentos.DocumentoSetUp;
import mx.gob.pjpuebla.trials.workflow.notificaciones.DTO.NotificacionDto;
import mx.gob.pjpuebla.trials.workflow.notificaciones.records.NotificacionResponseRecord;
import mx.gob.pjpuebla.trials.workflow.notificaciones.records.NotificacionSaveRecord;
import mx.gob.pjpuebla.trials.workflow.notificacionesDetalles.NotificacionesDetalles;
import mx.gob.pjpuebla.trials.workflow.notificacionesDetalles.NotificacionesDetallesRepository;
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
import static org.mockito.BDDMockito.willReturn;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NotificacionServiceTest {

    @Mock
    private NotificacionRepository notificacionRepository;

    @Mock
    private NotificacionesDetallesRepository notificacionesDetallesRepository;

    @Mock
    private DocumentoRepository documentoRepository;

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

        Pageable pageable = PageRequest.of(0, 10);
        Page<Notificacion> notificacionPage = new PageImpl<>(List.of(notificacion), pageable, 1);
        when(notificacionRepository.getNotificacionByTipo(TipoNotificacion.ESTRADO, pageable))
                .thenReturn(notificacionPage);
        Page<NotificacionRecord> result = notificacionService.getAllNotificaciones("ESTRADO", pageable);

        assertEquals(1, result.getTotalElements());
        NotificacionRecord notificacionrecord = result.getContent().get(0);
        assertEquals(notificacion.getConcepto(), notificacionrecord.concepto());
        assertEquals(notificacion.getNotas(), notificacionrecord.notas());
        assertEquals(notificacion.getTipoNotificacion(), notificacionrecord.tipo());
    }

    @Test
    void getInvalid() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<NotificacionRecord> result = notificacionService.getAllNotificaciones("invalido", pageable);

        assertEquals(0, result.getTotalElements());
    }

    @Test
    void getAll() {

        Pageable pageable = PageRequest.of(0, 10);
        Page<Notificacion> notificacionPage = new PageImpl<>(List.of(notificacion), pageable, 1);
        when(notificacionRepository.getNotificacionByTipo(TipoNotificacion.ESTRADO, pageable))
                .thenReturn(notificacionPage);

        Page<NotificacionRecord> result = notificacionService.getAllNotificaciones(null, pageable);
        assertEquals(1, result.getTotalElements());
    }

    @Test
    void create_withValidData_createsNotification() throws Exception {
        NotificacionDto notificacionDto = new NotificacionDto();
        notificacionDto.setPersonId(1);
        notificacionDto.setMetodo(1); // Notificación por correo
        notificacionDto.setUsarCorreoRegistrado(false);
        notificacionDto.setCorreo("nuevo@correo.com");

        PersonaDocumento persona = new PersonaDocumento();
        persona.setId(1);
        persona.setTipoNotificacion(TipoNotificacion.CORREO_ELECTRONICO);

        when(personaDocumentoRepository.findById(1)).thenReturn(Optional.of(persona));

        notificacionService.create(notificacionDto);

        assertEquals(1, persona.getTipoNotificacion());
        assertEquals("nuevo@correo.com", persona.getCorreoNotificacion());
        assertNull(persona.getFnDomicilio());

        verify(personaDocumentoRepository).save(persona);
    }

    @Test
    void create_ShouldUpdatePersonaWithNotificationDetails() throws Exception {
        NotificacionDto notificacionDto = new NotificacionDto();
        notificacionDto.setPersonId(1);
        notificacionDto.setMetodo(1); // Email
        notificacionDto.setUsarCorreoRegistrado(false);
        notificacionDto.setCorreo("nuevo_correo@example.com");

        PersonaDocumento persona = new PersonaDocumento();
        persona.setId(1);

        when(personaDocumentoRepository.findById(1)).thenReturn(Optional.of(persona));

        notificacionService.create(notificacionDto);

        assertEquals(1, persona.getTipoNotificacion());
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
    assertEquals(1, response.idNotificacion());
    assertTrue(response.mensaje().contains("Notificación creada con éxito"));
}

}