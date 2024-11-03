package mx.gob.pjpuebla.trials.workflow.documentos;

import mx.gob.pjpuebla.trials.core.juzgados.Juzgado;
import mx.gob.pjpuebla.trials.core.personas.Persona;
import mx.gob.pjpuebla.trials.core.personas.PersonaService;
import mx.gob.pjpuebla.trials.core.tipojuicio.TipoJuicioSetUp;
import mx.gob.pjpuebla.trials.util.enums.TipoDocumento;
import mx.gob.pjpuebla.trials.workflow.documentos.Digitalizacion2Service;
import mx.gob.pjpuebla.trials.workflow.documentos.Documento;
import mx.gob.pjpuebla.trials.workflow.documentos.DocumentoRepository;
import mx.gob.pjpuebla.trials.workflow.documentos.records.DocumentoData;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.util.ReflectionTestUtils;

import lombok.extern.slf4j.Slf4j;
import mx.gob.pjpuebla.trials.workflow.documentos.DocumentoSetUp;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;

class Digitalizacion2ServiceTest {

    @Mock
    private PersonaService personaService;

    @Mock
    private DocumentoRepository documentoRepository;

    @Mock
    private Persona persona;

    @Mock
    private Juzgado juzgado;

    private Path createdDirectory;

    @InjectMocks
    private Digitalizacion2Service digitalizacionService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        // Definir una ruta temporal para el test
        ReflectionTestUtils.setField(digitalizacionService, "rootFolder", "/opt/pjp/files");
    }

    @Test
    void testCrearDirectorio_OficioAdministrativo() {
        // Crear datos de documento
        DocumentoData docData = DocumentoSetUp.createDocumentoData("Administrativo");
        Documento documento = DocumentoSetUp.create(TipoJuicioSetUp.createTipoJuicio());
        documento.setCarpeta(null);
        documento.setTipoDocumento(TipoDocumento.OFICIO);
        documento.setData(docData);

        // Configurar mocks
        given(personaService.getAuditor()).willReturn(persona);
        given(persona.getJuzgado()).willReturn(juzgado);
        given(juzgado.getNombre()).willReturn("NombreDelJuzgadoTEST");

        // Llamar al método de prueba
        createdDirectory = digitalizacionService.crearDirectorio(documento);

        // Verificar los resultados
        assertNotNull(createdDirectory);
        assertTrue(createdDirectory.toString().contains("oficiosAdministrativos"));
      
    }

    @Test
    void testCrearDirectorio_OficioJurisdiccional() {
        // Crear datos de documento
        DocumentoData docData = DocumentoSetUp.createDocumentoData("Jurisdiccional");
        Documento documento = DocumentoSetUp.create(TipoJuicioSetUp.createTipoJuicio());
        documento.setTipoDocumento(TipoDocumento.OFICIO);
        documento.setData(docData);

        // Llamar al método de prueba
        Path result = digitalizacionService.crearDirectorio(documento);

        // Verificar los resultados
        assertNotNull(result);
        assertTrue(result.toString().contains("oficiosJurisdiccionales"));
    }

    @AfterEach
    void tearDown() throws IOException {
        // Verifica si la ruta creada existe antes de intentar eliminarla
        if (createdDirectory != null && Files.exists(createdDirectory)) {
            // Recorre y elimina todos los archivos y directorios
            Files.walk(createdDirectory.getParent()) // Empezar desde el directorio padre
                 .sorted((path1, path2) -> path2.compareTo(path1)) // Ordenar en reversa para eliminar subdirectorios primero
                 .forEach(path -> {
                     try {
                         Files.delete(path);
                     } catch (IOException e) {
                         e.printStackTrace(); // Manejo básico de excepciones
                     }
                 });
        }
    }
    
}