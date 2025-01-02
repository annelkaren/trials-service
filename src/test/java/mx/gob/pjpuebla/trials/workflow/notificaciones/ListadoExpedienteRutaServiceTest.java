package mx.gob.pjpuebla.trials.workflow.notificaciones;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;
import java.lang.reflect.Field;

import org.springframework.core.io.Resource;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import mx.gob.pjpuebla.trials.core.personas.Persona;
import mx.gob.pjpuebla.trials.core.personas.PersonaService;
import mx.gob.pjpuebla.trials.core.personas.PersonaSetUp;
import mx.gob.pjpuebla.trials.workflow.documentos.records.DocumentoData;

import org.mockito.InjectMocks;
import org.mockito.Mock;

@ExtendWith(MockitoExtension.class)
public class ListadoExpedienteRutaServiceTest {

    @Mock
    private NotificacionRepository notificacionRepository;

    @Mock
    private PersonaService personaService;

    @InjectMocks
    private ListadoExpedientesRutaService listadoExpedientesRutaService;

    @Mock
    private Resource listaExpedientes;

    @Test
    void exportToPdf() throws Exception {
        DocumentoData rubrosJson = new DocumentoData();
        rubrosJson.setRubros(List.of("RUBRO1", "RUBRO2"));

        Persona persona = PersonaSetUp.createPersona();

        List<Object[]> mockData = List.of(
                new Object[] { "EXP001", rubrosJson, "Nota 1" },
                new Object[] { "EXP002", rubrosJson, "Nota 2" });

        when(personaService.getAuditor()).thenReturn(persona);

        // Configuramos el mock del repositorio
        when(notificacionRepository.findAllNotificacionesByEstadoEnRutaAndTipoNotificacionDomicilio())
                .thenReturn(mockData);

        // Mockeamos el recurso Jasper
        Resource mockResource = mock(Resource.class);
        InputStream jasperStream = new FileInputStream("src/main/resources/jasper/ListaExpedientesRuta.jasper");
        when(mockResource.getInputStream()).thenReturn(jasperStream);

        // Inyectamos el recurso mockeado en el servicio
        Field listaExpedientesField = ListadoExpedientesRutaService.class.getDeclaredField("listaExpedientes");
        listaExpedientesField.setAccessible(true);
        listaExpedientesField.set(listadoExpedientesRutaService, mockResource);

        // Ejecutamos el método
        byte[] pdfBytes = listadoExpedientesRutaService.exportToPdf();

        // Validaciones
        assertNotNull(pdfBytes, "El PDF generado no debe ser nulo");
        verify(notificacionRepository, times(1)).findAllNotificacionesByEstadoEnRutaAndTipoNotificacionDomicilio();
       
    }
}
