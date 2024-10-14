package mx.gob.pjpuebla.trials.workflow.files;

import mx.gob.pjpuebla.trials.core.juzgados.JuzgadoSetUp;
import mx.gob.pjpuebla.trials.core.personas.Persona;
import mx.gob.pjpuebla.trials.core.personas.PersonaService;
import mx.gob.pjpuebla.trials.core.personas.PersonaSetUp;
import mx.gob.pjpuebla.trials.util.enums.TipoCarpeta;
import mx.gob.pjpuebla.trials.util.enums.TipoDocumento;
import mx.gob.pjpuebla.trials.workflow.carpeta.Carpeta;
import mx.gob.pjpuebla.trials.workflow.documentos.Documento;
import mx.gob.pjpuebla.trials.workflow.documentos.records.DocumentoData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DigitalizacionFolderServiceTest {

    @InjectMocks
    private DigitalizacionFolderService digitalizacionFolderService;

    @Mock
    private PersonaService personaService;

    private final String rootFolder = "/opt/pjp/files/"; // Asumiendo una ruta de prueba

    @BeforeEach
    void setUp() {
        // Usamos ReflectionTestUtils para establecer la variable rootFolder
        ReflectionTestUtils.setField(digitalizacionFolderService, "rootFolder", rootFolder);
    }

    @Test
    void testCreateFolderDigitalizacion_ValidOficio() throws IOException {
        Documento doc = new Documento();
        doc.setTipoDocumento(TipoDocumento.OFICIO);

        DocumentoData dd = new  DocumentoData(); 
        dd.setTipoOficio("Administrativo");
        doc.setData(dd);

        Persona persona = PersonaSetUp.createPersona().setJuzgado(JuzgadoSetUp.createJuzgado());

        // Mock de la respuesta del servicio de auditoría
        when(personaService.getAuditor()).thenReturn(persona);

        // Verificar que se creen los directorios
        Path expectedPath = Paths.get(rootFolder, "digitalizacion", String.valueOf(LocalDate.now().getYear()), persona.getJuzgado().getNombre(), "oficios", (String.valueOf(doc.getId())));
        Files.createDirectories(expectedPath); // Simulando la creación del directorio

        String result = digitalizacionFolderService.createFolderDigitalizacion(doc);

        assertEquals(expectedPath.toString(), result);
        assertTrue(Files.exists(expectedPath));
    }

    @Test
    void testCreateFolderDigitalizacion_ValidDocumentoConCarpeta() throws IOException {
        Carpeta carpeta = new Carpeta();
        carpeta.setExpediente("000123/2024");
        carpeta.setTipoCarpeta(TipoCarpeta.DEMANDA);

        Documento doc = new Documento();
        doc.setCarpeta(carpeta);

        Persona persona = PersonaSetUp.createPersona().setJuzgado(JuzgadoSetUp.createJuzgado());
        when(personaService.getAuditor()).thenReturn(persona);

        Path expectedPath = Paths.get(rootFolder, "digitalizacion", "2024", persona.getJuzgado().getNombre(), "000123");

        String result = digitalizacionFolderService.createFolderDigitalizacion(doc);

        assertEquals(expectedPath.toString(), result);
        assertTrue(Files.exists(expectedPath));
    }

    @Test
    void testCreateFolderDigitalizacion_DocumentoInvalido() {
        Documento doc = new Documento();
        doc.setTipoDocumento(TipoDocumento.OFICIO);
        doc.setData(null); // Datos nulos

        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            digitalizacionFolderService.createFolderDigitalizacion(doc);
        });

        assertEquals("Documento o datos del documento no válidos", thrown.getMessage());
    }

    @Test
    void testValidacionDigitalizacion_NullDocumento() {
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            digitalizacionFolderService.validacionDigitalizacion(null);
        });

        assertEquals("Documento o datos del documento no válidos", thrown.getMessage());
    }

    @Test
    void testValidacionDigitalizacion_CarpetaInvalida() {
        Documento doc = new Documento();

        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            digitalizacionFolderService.validacionDigitalizacion(doc);
        });

        assertEquals("Documento o datos del documento no válidos", thrown.getMessage());
    }

    @Test
    void testObtenerDatosExpediente_FormatoIncorrecto() {
        String expediente = "123456"; // Formato incorrecto

        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            digitalizacionFolderService.obtenerDatosExpediente(expediente);
        });

        assertEquals("El expediente no tiene el formato esperado", thrown.getMessage());
    }
}
