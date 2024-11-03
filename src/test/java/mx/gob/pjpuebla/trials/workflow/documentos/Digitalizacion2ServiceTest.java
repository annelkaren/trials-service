package mx.gob.pjpuebla.trials.workflow.documentos;

import mx.gob.pjpuebla.trials.core.juzgados.Juzgado;
import mx.gob.pjpuebla.trials.core.personas.Persona;
import mx.gob.pjpuebla.trials.core.personas.PersonaService;
import mx.gob.pjpuebla.trials.core.tipojuicio.TipoJuicioSetUp;
import mx.gob.pjpuebla.trials.util.enums.TipoCarpeta;
import mx.gob.pjpuebla.trials.util.enums.TipoDocumento;
import mx.gob.pjpuebla.trials.workflow.documentos.records.DigitalizacionRecord;
import mx.gob.pjpuebla.trials.workflow.documentos.records.DocumentoData;

import org.springframework.http.HttpStatus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import lombok.extern.slf4j.Slf4j;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@Slf4j
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

    // TEST DE CREACIÓN DE DIRECTORIOS:
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
        documento.getCarpeta().setTipoCarpeta(TipoCarpeta.OFICIO);
        documento.setTipoDocumento(TipoDocumento.OFICIO);
        documento.setData(docData);

        // Llamar al método de prueba
        createdDirectory = digitalizacionService.crearDirectorio(documento);

        // Verificar los resultados
        assertNotNull(createdDirectory);
        assertTrue(createdDirectory.toString().contains("oficiosJurisdiccionales"));
    }

    @Test
    void testCreateDirectorio_Demanda() {
        // Crear datos de documento
        Documento documento = DocumentoSetUp.create(TipoJuicioSetUp.createTipoJuicio());
        documento.getCarpeta().setTipoCarpeta(TipoCarpeta.DEMANDA);

        given(documentoRepository.findById(anyInt())).willReturn(Optional.of(documento));

        // Llamar al método de prueba
        createdDirectory = digitalizacionService.crearDirectorio(documento);

        // Verificar los resultados
        assertNotNull(createdDirectory);
        assertTrue(createdDirectory.toString().contains("/2024/JuzgadoTEST/000001"));
    }

    @Test
    void testCreateDirectorio_Exhorto() {
        // Crear datos de documento
        Documento documento = DocumentoSetUp.create(TipoJuicioSetUp.createTipoJuicio());
        documento.getCarpeta().setTipoCarpeta(TipoCarpeta.EXHORTO);
        documento.getCarpeta().setExpediente("E000006");

        given(documentoRepository.findById(anyInt())).willReturn(Optional.of(documento));

        // Llamar al método de prueba
        createdDirectory = digitalizacionService.crearDirectorio(documento);

        // Verificar los resultados
        assertNotNull(createdDirectory);
        assertTrue(createdDirectory.toString().contains("JuzgadoTEST/E000006"));
    }

    // TEST DE SUBIDA DE ARCHIVOS:
    @Test
    void cargarArchivoPdf() throws IOException {
        // Crear datos de documento
        Documento documento = DocumentoSetUp.create(TipoJuicioSetUp.createTipoJuicio());
        documento.getCarpeta().setTipoCarpeta(TipoCarpeta.DEMANDA);

        // Crear el archivo simulado
        MultipartFile fileMock = DigitalizacionSetUp.generarArchivo(50, "file", "application/pdf");
        long expectedFileSize = fileMock.getSize(); // Tamaño maximo esperado del archivo 50 MB

        // givens
        given(documentoRepository.findById(any())).willReturn(Optional.of(documento));

        // Llamar al método a probar
        DigitalizacionRecord result = digitalizacionService.guardarArchivo(fileMock, documento.getId());

        // Verificar las interacciones con los mocks
        verify(documentoRepository).findById(documento.getId());
        verify(documentoRepository).save(any(Documento.class));

        // Validar que el archivo fue creado correctamente en la ruta especificada

        createdDirectory = Paths.get(result.rutaArchivo());
        assert Files.exists(createdDirectory) : "El archivo no fue creado correctamente";

        // Validar el tamaño del archivo
        long actualFileSize = Files.size(createdDirectory);
        assert actualFileSize == expectedFileSize : "El tamaño del archivo no coincide";

        // Validar que el archivo es un PDF (si aplicable)
        assert result.rutaArchivo().endsWith(".pdf") : "El archivo creado no es un PDF";
    }

    @Test
    void cargarArchivoDiferenteAPdf() {
        // Crear datos de documento
        Documento documento = DocumentoSetUp.create(TipoJuicioSetUp.createTipoJuicio());
        documento.getCarpeta().setTipoCarpeta(TipoCarpeta.DEMANDA);

        // Crear el archivo simulado
        MultipartFile fileMock = DigitalizacionSetUp.generarArchivo(50, "file", "text/plain");

        // givens
        given(documentoRepository.findById(any())).willReturn(Optional.of(documento));

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            digitalizacionService.guardarArchivo(fileMock, documento.getId());
        });

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals("El archivo debe ser un PDF.", exception.getReason());
    }

    @Test
    void cargarArchivoConTamanioMayor() {
        // Crear datos de documento
        Documento documento = DocumentoSetUp.create(TipoJuicioSetUp.createTipoJuicio());
        documento.getCarpeta().setTipoCarpeta(TipoCarpeta.DEMANDA);

        // Crear el archivo simulado
        MultipartFile fileMock = DigitalizacionSetUp.generarArchivo(51, "file", "application/pdf");

        // givens
        given(documentoRepository.findById(any())).willReturn(Optional.of(documento));

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> digitalizacionService.guardarArchivo(fileMock, documento.getId()));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals("El archivo no puede superar los 50 MB.", exception.getReason());

    }

    // TEST DE DESCARGA DE ARCHIVOS:

    @Test
    void testGetDocumentoExistente() throws IOException {
        // Crear datos de documento
        Documento documento = DocumentoSetUp.create(TipoJuicioSetUp.createTipoJuicio());
        documento.setCarpeta(null);
        documento.setTipoDocumento(TipoDocumento.OFICIO);
        documento.setRuta("testfile.pdf");
        
        // Simular el comportamiento del repositorio
        given(documentoRepository.findById(documento.getId())).willReturn(Optional.of(documento));
    
        // Crear un archivo simulado
        Path rutaArchivo = Paths.get("/opt/pjp/files" + "/digitalizacion/" + documento.getRuta());
        Files.createDirectories(rutaArchivo.getParent());
        Files.write(rutaArchivo, "Contenido del archivo".getBytes());
    
        // Llamar al método a probar
        byte[] resultado = digitalizacionService.getDocumento(documento.getId());
    
        // Verificar que se obtiene el contenido correcto
        assertNotNull(resultado);
        assertEquals("Contenido del archivo", new String(resultado));
    }
    

    @AfterEach
    void tearDown() throws IOException {
        // Verifica si la ruta creada existe antes de intentar eliminarla
        if (createdDirectory != null && Files.exists(createdDirectory)) {
            // Empezamos eliminando todos los archivos y subdirectorios dentro de
            // `createdDirectory`
            Files.walk(createdDirectory)
                    .sorted((path1, path2) -> path2.compareTo(path1)) // Orden inverso para eliminar archivos y
                                                                      // subdirectorios primero
                    .forEach(path -> {
                        try {
                            log.info("Eliminando: " + path.toString() + " (Es directorio: "
                                    + Files.isDirectory(path) + ")");
                            Files.delete(path); // Elimina cada archivo o subdirectorio
                        } catch (IOException e) {
                            log.error("Error eliminando " + path.toString() + ": " + e.getMessage());
                        }
                    });

            // Subimos en la jerarquía y eliminamos directorios vacíos hasta `2024`
            Path parentDir = createdDirectory.getParent();
            while (parentDir != null && !parentDir.endsWith("2024")) {
                try {
                    if (Files.isDirectory(parentDir) && Files.list(parentDir).findAny().isEmpty()) {
                        log.info("Eliminando directorio vacío: " + parentDir);
                        Files.delete(parentDir);
                    }
                    parentDir = parentDir.getParent(); // Continuar hacia arriba
                } catch (IOException e) {
                    log.error("Error eliminando directorio vacío " + parentDir + ": " + e.getMessage());
                    break; // Detener si hay un error
                }
            }
        } else {
            log.info("El directorio no existe o es nulo: " + createdDirectory);
        }
    }

}