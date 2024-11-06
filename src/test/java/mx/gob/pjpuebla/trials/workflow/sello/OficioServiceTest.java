package mx.gob.pjpuebla.trials.workflow.sello;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperPrint;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

class OficioServiceTest {

    @InjectMocks
    private OficioService oficioService;

    @Mock
    private Resource oficioOficio;

    @Mock
    private Resource oficioCarta;

    @Mock
    private Resource mockResource;

    private static final Integer OFICIO_ID = 1;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
     void testGetOficioWithFormatoTrue() throws JRException, IOException {
        InputStream mockInputStream = getClass().getClassLoader().getResourceAsStream("jasper/OficioOficio.jasper");
        assertNotNull(mockInputStream, "El archivo .jasper no se pudo encontrar");

        when(oficioOficio.getInputStream()).thenReturn(mockInputStream);

        byte[] result = oficioService.getOficio(OFICIO_ID);

        assertNotNull(result);
        verify(oficioOficio).getInputStream();
    }



    @Test
     void testGetOficioWithFormatoFalse() throws JRException, IOException {
        InputStream mockInputStream = getClass().getClassLoader().getResourceAsStream("jasper/OficioCarta.jasper");
        assertNotNull(mockInputStream, "El archivo .jasper no se pudo encontrar");

        when(oficioCarta.getInputStream()).thenReturn(mockInputStream);

        byte[] result = oficioService.getOficio(OFICIO_ID);

        assertNotNull(result);
        verify(oficioCarta).getInputStream();
    }

//    @Test
//    void testGetReport() throws JRException, IOException {
//        InputStream mockInputStream = getClass().getClassLoader().getResourceAsStream("jasper/OficioOficio.jasper");
//        assertNotNull(mockInputStream, "El archivo .jasper no se pudo encontrar");
//
//        when(mockResource.getInputStream()).thenReturn(mockInputStream);
//        Integer oficioId = 123;
//
//        JasperPrint result = oficioService.getReport(mockResource, oficioId);
//
//        assertNotNull(result);
//        verify(mockResource).getInputStream();
//    }


//    @Test
//    void testSetHeder() {
//        Integer code = 2134323;
//        Integer noOficio = 1;
//
//        List<String> result = oficioService.setHeder(code, noOficio);
//
//        assertNotNull(result);
//        assertEquals(2, result.size());
//        assertEquals("<b>" + code + "</b>", result.get(0));
//        assertEquals("<b>No. Oficio: " + noOficio + "</b>", result.get(1));
//    }

//    @Test
//    void testBodyText() {
//        String expectedBody = """
//                //TODO. Obtener cuerpo del oficio de base de datos
//                <h1>El agujero aplastante</h1>
//                <p style="line-height: 1.5;" >Por Chris Mills</p>
//                <h2>Capítulo 1: La oscura noche</h2>
//                <p>
//                  Era una noche oscura. En algún lugar, un búho ululó. La lluvia azotó el ...
//                </p>
//                <h2>Capítulo 2: El silencio eterno</h2>
//                <p>Nuestro protagonista ni susurrar pudo al ver esa sombría figura ...</p>
//                <h3>El espectro habla</h3>
//                <p>
//                  Habían pasado varias horas más, cuando de repente el espectro se incorporó y
//                  exclamó: "¡Por favor, ten piedad de mi alma!"
//                </p>
//                """;
//
//        String result = oficioService.bodyText();
//
//        assertNotNull(result);
//        assertEquals(expectedBody.trim(), result.trim());
//    }

}
