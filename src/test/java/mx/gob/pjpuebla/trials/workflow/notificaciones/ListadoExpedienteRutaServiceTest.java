package mx.gob.pjpuebla.trials.workflow.notificaciones;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;

import org.springframework.core.io.Resource;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;


import com.fasterxml.jackson.databind.ObjectMapper;

import mx.gob.pjpuebla.trials.core.personas.Persona;
import mx.gob.pjpuebla.trials.core.personas.PersonaService;
import mx.gob.pjpuebla.trials.core.personas.PersonaSetUp;

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

    private final ObjectMapper objectMapper = new ObjectMapper();


    @Test
    void exportToPdf()  throws Exception{
        String rubrosJson = "{\"rubros\": [\"rubro1\", \"rubro2\"]}";
        Persona persona = PersonaSetUp.createPersona();

        List<Object[]> mockData = List.of(
                new Object[]{"EXP001", rubrosJson, "Nota 1"},
                new Object[]{"EXP002", rubrosJson, "Nota 2"}
        );


        when(personaService.getAuditor()).thenReturn(persona);

        // Configuramos el mock del repositorio
        when(notificacionRepository.findAllNotificacionesByEstadoEnRutaAndTipoNotificacionDomicilio())
                .thenReturn(mockData);

        // Mockeamos el recurso Jasper
        InputStream jasperStream = new FileInputStream("src/main/resources/jasper/ListaExpedientesRuta.jasper");
        when(listaExpedientes.getInputStream()).thenReturn(jasperStream);

        // Ejecutamos el método
        byte[] pdfBytes = listadoExpedientesRutaService.exportToPdf();

        // Validaciones
        assertNotNull(pdfBytes, "El PDF generado no debe ser nulo");
        verify(notificacionRepository, times(1)).findAllNotificacionesByEstadoEnRutaAndTipoNotificacionDomicilio();
        verify(listaExpedientes, times(1)).getInputStream();
    }
}
