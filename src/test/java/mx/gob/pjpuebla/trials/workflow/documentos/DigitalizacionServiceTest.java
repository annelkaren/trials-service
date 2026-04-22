package mx.gob.pjpuebla.trials.workflow.documentos;

import mx.gob.pjpuebla.trials.core.juzgados.Juzgado;
import mx.gob.pjpuebla.trials.core.oficialias.OficialiaSetUp;
import mx.gob.pjpuebla.trials.core.personas.Persona;
import mx.gob.pjpuebla.trials.core.personas.PersonaService;
import mx.gob.pjpuebla.trials.core.personas.PersonaSetUp;
import mx.gob.pjpuebla.trials.core.sedes.SedeSetUp;
import mx.gob.pjpuebla.trials.core.tipojuicio.TipoJuicioSetUp;
import mx.gob.pjpuebla.trials.core.tipooficialias.TipoOficialiaSetUp;
import mx.gob.pjpuebla.trials.util.enums.Migrado;
import mx.gob.pjpuebla.trials.util.enums.TipoCarpeta;
import mx.gob.pjpuebla.trials.util.enums.TipoDocumento;
import mx.gob.pjpuebla.trials.workflow.documentos.documentosdetalle.DocumentoDetalleRepository;
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
import org.junit.jupiter.api.io.TempDir;

@Slf4j
class DigitalizacionServiceTest {

    @Mock
    private PersonaService personaService;

    @Mock
    private DocumentoRepository documentoRepository;

    @Mock
    private DocumentoDetalleRepository documentoDetalleRepository;

    @Mock
    private Persona persona;

    @Mock
    private Juzgado juzgado;

    private Path createdDirectory;

    @TempDir
    Path tempDir;

    @InjectMocks
    private DigitalizacionService digitalizacionService;

    /**
     * Configura el contexto de prueba inicializando los mocks y estableciendo la
     * carpeta raíz para el servicio de digitalización.
     */
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(digitalizacionService, "rootFolder", tempDir.toString());
    }

    /**
     * Prueba la creación de directorio para documentos de tipo "Oficio
     * Administrativo".
     */
    @Test
    void testCrearDirectorio_OficioAdministrativo() {
        DocumentoData docData = DocumentoSetUp.createDocumentoData("Administrativo");
        Documento documento = DocumentoSetUp.create(TipoJuicioSetUp.createTipoJuicio());
        MultipartFile fileMock = DigitalizacionSetUp.generarArchivo(1, "file", "application/pdf");

        documento.setCarpeta(null);
        documento.setTipoDocumento(TipoDocumento.OFICIO);
        documento.setData(docData);

        given(documentoRepository.findById(anyInt())).willReturn(Optional.of(documento));
        given(personaService.getAuditor()).willReturn(persona);
        given(persona.getOficialia()).willReturn(
                OficialiaSetUp.createOficialia(TipoOficialiaSetUp.createtipoOficialia(), SedeSetUp.createSede()));

        DigitalizacionRecord result = digitalizacionService.guardarDocumento(fileMock, documento.getId());
        createdDirectory = Paths.get(result.rutaArchivo()).getParent();

        assertNotNull(createdDirectory);
        assertTrue(createdDirectory.toString().contains("oficiosAdministrativos"));
    }

    /**
     * Prueba la creación de directorio para documentos de tipo "Oficio
     * Jurisdiccional".
     */
    @Test
    void testCrearDirectorio_OficioJurisdiccional() {
        DocumentoData docData = DocumentoSetUp.createDocumentoData("Jurisdiccional");
        Documento documento = DocumentoSetUp.create(TipoJuicioSetUp.createTipoJuicio());
        MultipartFile fileMock = DigitalizacionSetUp.generarArchivo(1, "file", "application/pdf");
        documento.getCarpeta().setTipoCarpeta(TipoCarpeta.OFICIO);
        documento.setTipoDocumento(TipoDocumento.OFICIO);
        documento.setData(docData);

        given(documentoRepository.findById(anyInt())).willReturn(Optional.of(documento));
        given(personaService.getAuditor()).willReturn(persona);
        given(persona.getOficialia()).willReturn(
                OficialiaSetUp.createOficialia(TipoOficialiaSetUp.createtipoOficialia(), SedeSetUp.createSede()));

        DigitalizacionRecord result = digitalizacionService.guardarDocumento(fileMock, documento.getId());
        createdDirectory = Paths.get(result.rutaArchivo()).getParent();

        assertNotNull(createdDirectory);
        assertTrue(createdDirectory.toString().contains("oficiosJurisdiccionales"));
    }

    /**
     * Prueba la creación de directorio para documentos de tipo "Demanda".
     */
    @Test
    void testCreateDirectorio_Demanda() {
        Documento documento = DocumentoSetUp.create(TipoJuicioSetUp.createTipoJuicio());
        documento.getCarpeta().setTipoCarpeta(TipoCarpeta.DEMANDA);
        documento.setTipoDocumento(TipoDocumento.PROMOCION);
        MultipartFile fileMock = DigitalizacionSetUp.generarArchivo(1, "file", "application/pdf");
        Persona persona = PersonaSetUp.createPersona();

        given(documentoRepository.findById(anyInt())).willReturn(Optional.of(documento));
        given(personaService.getAuditor()).willReturn(persona);

        DigitalizacionRecord result = digitalizacionService.guardarDocumento(fileMock, documento.getId());
        createdDirectory = Paths.get(result.rutaArchivo()).getParent();

        assertNotNull(createdDirectory);
        String normalizedPath = createdDirectory.toString().replace("\\", "/");
        assertTrue(normalizedPath.contains("2024/JuzgadoTEST/000001"));
    }

    /**
     * Prueba la creación de directorio para documentos de tipo "Exhorto".
     */
    @Test
    void testCreateDirectorio_Exhorto() {
        Documento documento = DocumentoSetUp.create(TipoJuicioSetUp.createTipoJuicio());
        documento.setTipoDocumento(TipoDocumento.PROMOCION);
        MultipartFile fileMock = DigitalizacionSetUp.generarArchivo(1, "file", "application/pdf");
        Persona persona = PersonaSetUp.createPersona();

        documento.getCarpeta().setTipoCarpeta(TipoCarpeta.EXHORTO);
        documento.getCarpeta().setExpediente("E000006/2024");

        given(documentoRepository.findById(anyInt())).willReturn(Optional.of(documento));
        given(personaService.getAuditor()).willReturn(persona);

        DigitalizacionRecord result = digitalizacionService.guardarDocumento(fileMock, documento.getId());
        createdDirectory = Paths.get(result.rutaArchivo()).getParent();

        assertNotNull(createdDirectory);
        String normalizedPath = createdDirectory.toString().replace("\\", "/");
        String currentYear = String.valueOf(java.time.LocalDate.now().getYear());
        assertTrue(normalizedPath.contains(currentYear + "/JuzgadoTEST/E000006/2024"));
    }

    /**
     * Prueba que el método lanzar una excepción cuando el documento es nulo.
     */
    @Test
    void testCrearDirectorio_DocumentoNulo() {
        MultipartFile fileMock = DigitalizacionSetUp.generarArchivo(1, "file", "application/pdf");
        given(documentoRepository.findById(any())).willReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> digitalizacionService.guardarDocumento(fileMock, 999));
    }

    /**
     * Prueba que el método lanzar una excepción cuando la carpeta del documento es
     * nula.
     */
    @Test
    void testCrearDirectorio_CarpetaNula() {
        Documento documento = DocumentoSetUp.create(TipoJuicioSetUp.createTipoJuicio());
        MultipartFile fileMock = DigitalizacionSetUp.generarArchivo(1, "file", "application/pdf");
        documento.setCarpeta(null);
        documento.setTipoDocumento(null);
        given(documentoRepository.findById(any())).willReturn(Optional.of(documento));

        assertThrows(IllegalArgumentException.class,
                () -> digitalizacionService.guardarDocumento(fileMock, documento.getId()));
    }

    /**
     * Prueba que el método lanzar una excepción cuando el tipo de documento es
     * nulo.
     */
    @Test
    void testCrearDirectorio_TipoDocumentoNulo() {
        Documento documento = DocumentoSetUp.create(TipoJuicioSetUp.createTipoJuicio());
        MultipartFile fileMock = DigitalizacionSetUp.generarArchivo(1, "file", "application/pdf");
        documento.setTipoDocumento(null);
        documento.setCarpeta(null);
        given(documentoRepository.findById(any())).willReturn(Optional.of(documento));

        assertThrows(IllegalArgumentException.class,
                () -> digitalizacionService.guardarDocumento(fileMock, documento.getId()));
    }

    /**
     * Prueba la carga y almacenamiento de un archivo PDF válido.
     */
    @Test
    void cargarArchivoPdf() throws IOException {
        Persona persona = PersonaSetUp.createPersona();

        Documento documento = DocumentoSetUp.create(TipoJuicioSetUp.createTipoJuicio());
        documento.getCarpeta().setTipoCarpeta(TipoCarpeta.DEMANDA);
        documento.setTipoDocumento(TipoDocumento.PROMOCION);
        documento.setData(DocumentoSetUp.createDocumentoData("Jurisdiccional"));
        MultipartFile fileMock = DigitalizacionSetUp.generarArchivo(50, "file", "application/pdf");
        long expectedFileSize = fileMock.getSize();

        given(documentoRepository.findById(any())).willReturn(Optional.of(documento));
        given(personaService.getAuditor()).willReturn(persona);

        DigitalizacionRecord result = digitalizacionService.guardarDocumento(fileMock, documento.getId());

        verify(documentoRepository).findById(documento.getId());
        verify(documentoRepository).save(any(Documento.class));

        createdDirectory = Paths.get(result.rutaArchivo());
        assert Files.exists(createdDirectory) : "El archivo no fue creado correctamente";

        long actualFileSize = Files.size(createdDirectory);
        assert actualFileSize == expectedFileSize : "El tamaño del archivo no coincide";

        assert result.rutaArchivo().endsWith(".pdf") : "El archivo creado no es un PDF";
    }

    /**
     * Prueba que el método lanza una excepción cuando el archivo cargado no es un
     * PDF.
     */
    @Test
    void cargarArchivoDiferenteAPdf() {
        Documento documento = DocumentoSetUp.create(TipoJuicioSetUp.createTipoJuicio());
        documento.getCarpeta().setTipoCarpeta(TipoCarpeta.DEMANDA);

        MultipartFile fileMock = DigitalizacionSetUp.generarArchivo(50, "file", "text/plain");

        given(documentoRepository.findById(any())).willReturn(Optional.of(documento));

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            digitalizacionService.guardarDocumento(fileMock, documento.getId());
        });

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals("El archivo debe ser un PDF.", exception.getReason());
    }

    /**
     * Prueba que el método lanza una excepción cuando el tamaño del archivo supera
     * los 50 MB.
     */
    @Test
    void cargarArchivoConTamanioMayor() {
        Documento documento = DocumentoSetUp.create(TipoJuicioSetUp.createTipoJuicio());
        documento.getCarpeta().setTipoCarpeta(TipoCarpeta.DEMANDA);

        MultipartFile fileMock = DigitalizacionSetUp.generarArchivo(51, "file", "application/pdf");

        given(documentoRepository.findById(any())).willReturn(Optional.of(documento));

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> digitalizacionService.guardarDocumento(fileMock, documento.getId()));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals("El archivo no puede superar los 50 MB.", exception.getReason());
    }

    /**
     * Prueba la descarga de un documento existente.
     */
    @Test
    void testGetDocumentoExistente() throws IOException {
        Persona persona = PersonaSetUp.createPersona();

        Documento documento = DocumentoSetUp.create(TipoJuicioSetUp.createTipoJuicio());
        documento.setMigrado(Migrado.NO);
        documento.getCarpeta().setTipoCarpeta(TipoCarpeta.DEMANDA);
        documento.setTipoDocumento(TipoDocumento.PROMOCION);
        documento.setData(DocumentoSetUp.createDocumentoData("Jurisdiccional"));

        MultipartFile fileMock = DigitalizacionSetUp.generarArchivo(50, "file", "application/pdf");

        given(documentoRepository.findById(any())).willReturn(Optional.of(documento));
        given(personaService.getAuditor()).willReturn(persona);

        DigitalizacionRecord result = digitalizacionService.guardarDocumento(fileMock, documento.getId());

        createdDirectory = Paths.get(result.rutaArchivo());

        byte[] resultado = digitalizacionService.getDocumento(documento.getId());

        assertNotNull(resultado);
    }

    /**
     * Prueba la descarga de un documento que NO existente.
     */
    @Test
    void testGetDocumentoNoExistente() {

        Documento documento = DocumentoSetUp.create(TipoJuicioSetUp.createTipoJuicio());
        Persona persona = PersonaSetUp.createPersona();

        documento.getCarpeta().setTipoCarpeta(TipoCarpeta.DEMANDA);
        documento.setTipoDocumento(TipoDocumento.PROMOCION);
        documento.setMigrado(Migrado.NO);
        documento.setData(DocumentoSetUp.createDocumentoData("Jurisdiccional"));
        MultipartFile fileMock = DigitalizacionSetUp.generarArchivo(50, "file", "application/pdf");

        given(documentoRepository.findById(any())).willReturn(Optional.of(documento));
        given(personaService.getAuditor()).willReturn(persona);

        DigitalizacionRecord result = digitalizacionService.guardarDocumento(fileMock, documento.getId());

        createdDirectory = Paths.get(result.rutaArchivo());

        documento.setRuta("rutaCambiada");

        IOException exception = assertThrows(IOException.class, () -> {
            digitalizacionService.getDocumento(documento.getId());
        });

        assertEquals("El archivo rutaCambiada no existe en el directorio", exception.getMessage());

    }

    /**
     * Elimina archivos de prueba creados durante la ejecución de pruebas.
     */
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

                        Files.delete(parentDir);
                    }
                    parentDir = parentDir.getParent(); // Continuar hacia arriba
                } catch (IOException e) {
                    log.error("Error eliminando directorio vacío " + parentDir + ": " + e.getMessage());
                    break; // Detener si hay un error
                }
            }
        }
    }

    /**
     * Prueba la creación de directorio para documentos de tipo
     * "DOCUMENTO_IDENTIFICACION".
     */
    @Test
    void testCreateDirectorio_Documento_Identificacion() {
        Documento documento = DocumentoSetUp.create(TipoJuicioSetUp.createTipoJuicio());
        documento.getCarpeta().setTipoCarpeta(TipoCarpeta.DEMANDA);
        Persona persona = PersonaSetUp.createPersona();

        documento.setTipoDocumento(TipoDocumento.DOCUMENTO_IDENTIFICACION);

        given(documentoRepository.findById(anyInt())).willReturn(Optional.of(documento));
        given(personaService.getAuditor()).willReturn(persona);

        MultipartFile fileMock = DigitalizacionSetUp.generarArchivo(10, "file", "application/pdf");
        DigitalizacionRecord result = digitalizacionService.guardarDocumentoAsistencia(fileMock, documento.getId(), 1);
        createdDirectory = Paths.get(result.rutaArchivo()).getParent();

        assertNotNull(createdDirectory);
        String normalizedPath = createdDirectory.toString().replace("\\", "/");
        assertTrue(normalizedPath.contains("2024/JuzgadoTEST/000001/Audiencias/1/Asistencia"));
    }
}
