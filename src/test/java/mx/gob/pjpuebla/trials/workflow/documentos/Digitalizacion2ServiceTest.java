package mx.gob.pjpuebla.trials.workflow.documentos;

import mx.gob.pjpuebla.trials.workflow.documentos.Digitalizacion2Service;
import mx.gob.pjpuebla.trials.core.juzgados.Juzgado;
import mx.gob.pjpuebla.trials.core.personas.Persona;
import mx.gob.pjpuebla.trials.core.personas.PersonaRepository;
import mx.gob.pjpuebla.trials.core.personas.PersonaService;
import mx.gob.pjpuebla.trials.core.personas.PersonaSetUp;
import mx.gob.pjpuebla.trials.core.tipojuicio.TipoJuicioSetUp;
import mx.gob.pjpuebla.trials.workflow.carpeta.Carpeta;
import mx.gob.pjpuebla.trials.workflow.documentos.Documento;
import mx.gob.pjpuebla.trials.workflow.documentos.DocumentoRepository;
import mx.gob.pjpuebla.trials.util.enums.TipoCarpeta;
import mx.gob.pjpuebla.trials.util.enums.TipoDocumento;
import mx.gob.pjpuebla.trials.workflow.documentos.records.DigitalizacionRecord;
import mx.gob.pjpuebla.trials.workflow.documentos.records.DocumentoData;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@Slf4j
@SpringBootTest
@ExtendWith(MockitoExtension.class)
class Digitalizacion2ServiceTest {

    @Mock
    private PersonaService personaService;

    @Mock
    private PersonaRepository personaRepository;

    @Mock
    private DocumentoRepository documentoRepository;

    @Mock
    private MultipartFile file;

    @InjectMocks
    private Digitalizacion2Service digitalizacionService;

    @Value("${app.root-folder}")
    private String rootFolder;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        digitalizacionService.init();
    }

    @Test
    void testCrearDirectorio_OficioAdministrativo() {
        DocumentoData docData = DocumentoSetUp.createDocumentoData("Administrativo");
        Persona persona = PersonaSetUp.createPersona();
        Documento documento = DocumentoSetUp.create(TipoJuicioSetUp.createTipoJuicio());
        documento.setCarpeta(null);
        documento.setTipoDocumento(TipoDocumento.OFICIO);
        documento.setData(docData);

        ReflectionTestUtils.setField(digitalizacionService, "rootFolder", rootFolder);

        given(personaRepository.findByUsuario(any())).willReturn(Optional.of(persona));
        given(personaService.getAuditor()).willReturn(persona);

        Path result = digitalizacionService.crearDirectorio(documento);
        System.out.println(result.toString());
        assertNotNull(result);
        assertTrue(result.toString().contains("oficiosAdministrativos"));
    }

    @Test
    void testCrearDirectorio_OficioAdministrativo2() {
        // Crear el objeto Juzgado simulado
        Juzgado juzgado = new Juzgado();
        juzgado.setNombre("Juzgado1");
    
        // Crear el objeto Persona simulado
        Persona persona = new Persona();
        persona.setJuzgado(juzgado);
    
        // Configurar el mock de personaService para devolver el objeto Persona
        when(personaService.getAuditor()).thenReturn(persona);
    
        // Crear el documento para la prueba
        Documento documento = new Documento();
        documento.setTipoDocumento(TipoDocumento.OFICIO);
        documento.setData(DocumentoSetUp.createDocumentoData("Administrativo"));
    
        // Ejecutar el método que se quiere probar
        Path result = digitalizacionService.crearDirectorio(documento);
    
        // Validar el resultado
        assertNotNull(result);
        assertTrue(result.toString().contains("oficiosAdministrativos"));
    }
    


    @Test
    void testGuardarArchivo_ValidFile() throws IOException {
        Documento documento = new Documento();
        documento.setId(1);
        documento.setCarpeta(new Carpeta(TipoCarpeta.DEMANDA));

        when(documentoRepository.findById(1)).thenReturn(Optional.of(documento));
        when(file.isEmpty()).thenReturn(false);
        when(file.getContentType()).thenReturn("application/pdf");
        when(file.getBytes()).thenReturn(new byte[10]);

        DigitalizacionRecord record = digitalizacionService.guardarArchivo(file, 1);

        assertNotNull(record);
        assertEquals("1", record.documentoId());
    }

    @Test
    void testGetDocumento_DocumentExists() throws IOException {
        Documento documento = new Documento();
        documento.setId(1);
        documento.setRuta("test.pdf");
        when(documentoRepository.findById(1)).thenReturn(Optional.of(documento));

        byte[] fileData = digitalizacionService.getDocumento(1);
        assertNotNull(fileData);
    }

    @Test
    void testValidarArchivo_ArchivoVacio() {
        when(file.isEmpty()).thenReturn(true);

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> digitalizacionService.guardarArchivo(file, 1));
        assertEquals("400 BAD_REQUEST \"El archivo no puede estar vacío.\"", exception.getMessage());
    }

    @Test
    void testValidarArchivo_ArchivoGrande() {
        when(file.isEmpty()).thenReturn(false);
        when(file.getSize()).thenReturn(51L * 1024 * 1024); // mayor que 50MB

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> digitalizacionService.guardarArchivo(file, 1));
        assertEquals("400 BAD_REQUEST \"El archivo no puede superar los 50 MB.\"", exception.getMessage());
    }

    @Test
    void testCrearDirectorio_OficioJurisdiccional() {
        Documento documento = new Documento();
        documento.setTipoDocumento(TipoDocumento.OFICIO);
        documento.setData(new DocumentoData("Jurisdiccional"));

        Carpeta carpeta = new Carpeta();
        carpeta.setExpediente("EXP123");
        documento.setCarpeta(carpeta);

        when(personaService.getAuditor().getJuzgado().getNombre()).thenReturn("Juzgado1");

        Path result = digitalizacionService.crearDirectorio(documento);
        assertNotNull(result);
        assertTrue(result.toString().contains("oficiosJurisdiccionales"));
    }

    @Test
    void testGenerarNombreArchivo() throws Exception {
        TipoCarpeta tipoCarpeta = TipoCarpeta.EXHORTO;
        String fileName = digitalizacionService.generarNombreArchivo(tipoCarpeta);

        assertTrue(fileName.contains("EXHORTO"));
        assertTrue(fileName.endsWith(".pdf"));
    }

    @Test
    void testConstruirRutaExpediente() {
        String year = "2024";
        String juzgado = "Juzgado1";
        String expediente = "EXP123";

        String rutaExpediente = digitalizacionService.construirRutaExpediente(year, juzgado, expediente);
        assertEquals("/opt/pjp/files/digitalizacion/2024/Juzgado1/EXP123", rutaExpediente);
    }

    // Más tests pueden añadirse aquí según las necesidades, siguiendo el mismo
    // patrón.
}
