package mx.gob.pjpuebla.trials.workflow.documentos;

import lombok.extern.slf4j.Slf4j;
import mx.gob.pjpuebla.trials.core.distritos.Distrito;
import mx.gob.pjpuebla.trials.core.distritos.DistritoRepository;
import mx.gob.pjpuebla.trials.core.distritos.DistritoSetUp;
import mx.gob.pjpuebla.trials.core.domicilio.DomicilioSetUp;
import mx.gob.pjpuebla.trials.core.domicilios.Domicilio;
import mx.gob.pjpuebla.trials.core.domicilios.DomicilioRepository;
import mx.gob.pjpuebla.trials.core.juzgados.JuzgadoSetUp;
import mx.gob.pjpuebla.trials.core.materias.Materia;
import mx.gob.pjpuebla.trials.core.materias.MateriaRepository;
import mx.gob.pjpuebla.trials.core.materias.MateriaSetUp;
import mx.gob.pjpuebla.trials.core.sedes.Sede;
import mx.gob.pjpuebla.trials.core.sedes.SedeRepository;
import mx.gob.pjpuebla.trials.core.sedes.SedeSetUp;
import mx.gob.pjpuebla.trials.core.tipojuicio.TipoJuicio;
import mx.gob.pjpuebla.trials.core.tipojuicio.TipoJuicioRepository;
import mx.gob.pjpuebla.trials.core.tipojuicio.TipoJuicioSetUp;
import mx.gob.pjpuebla.trials.core.tiposistema.TipoSistema;
import mx.gob.pjpuebla.trials.core.tiposistema.TipoSistemaRepository;
import mx.gob.pjpuebla.trials.core.tiposistema.TipoSistemaSetUp;
import mx.gob.pjpuebla.trials.util.enums.TipoCarpeta;
import mx.gob.pjpuebla.trials.workflow.documentos.records.DigitalizacionRecord;
import mx.gob.pjpuebla.trials.workflow.files.DigitalizacionFolderService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;

@Slf4j
@SpringBootTest
class DigitalizacionServiceTest {

    @Mock
    private DocumentoRepository documentoRepository;
    @Mock
    private DigitalizacionFolderService digitalizacionFolderService;
    @Mock
    private SedeRepository sedeRepository;
    @Mock
    private MateriaRepository materiaRepository;
    @Mock
    private DistritoRepository distritoRepository;
    @Mock
    private DomicilioRepository domicilioRepository;
    @Mock
    private TipoJuicioRepository tipoJuicioRepository;
    @Mock
    private TipoSistemaRepository tipoSistemaRepository;

    @Value("${app.root-folder}")
    private String rootFolder;

    @InjectMocks
    private DigitalizacionService digitalizacionService;

    private Documento documento;

    @BeforeEach
    void setUp() {

        // Configurar los mocks para los repositorios
        given(materiaRepository.save(any(Materia.class))).willReturn(MateriaSetUp.createMateria());
        given(distritoRepository.save(any(Distrito.class))).willReturn(DistritoSetUp.createDistrito());
        given(domicilioRepository.save(any(Domicilio.class))).willReturn(DomicilioSetUp.createDomicilio());
        given(sedeRepository.save(any(Sede.class))).willReturn(SedeSetUp.createSede());
        given(tipoSistemaRepository.save(any(TipoSistema.class))).willReturn(TipoSistemaSetUp.createTipoSistema());
        given(tipoJuicioRepository.save(any(TipoJuicio.class))).willReturn(TipoJuicioSetUp.createTipoJuicio());

        // Configurar el mock para DocumentoRepository y DigitalizacionFolderService
        documento = DocumentoSetUp.create(TipoJuicioSetUp.createTipoJuicio());
        documento.getCarpeta().setTipoCarpeta(TipoCarpeta.DEMANDA);
        documento.getCarpeta().setJuzgado(JuzgadoSetUp.createJuzgado());
        given(documentoRepository.findById(documento.getId())).willReturn(java.util.Optional.of(documento));
        given(documentoRepository.save(any(Documento.class))).willReturn(documento);
        given(digitalizacionFolderService.createFolderDigitalizacion(any(Documento.class)))
                .willReturn(rootFolder + "/digitalizacion/2024/Juzgado/000001");
    }

    @AfterEach
    void tearDown() throws IOException {

        Path folderPath1 = Paths.get(rootFolder + "/digitalizacion/2024/Juzgado");
        Path folderPath2 = Paths.get(rootFolder + "/digitalizacion/2024/juzgadoPrueba");

        List<Path> pathsToDelete = List.of(folderPath1, folderPath2);


        for (Path folderPath : pathsToDelete) {
            if (Files.exists(folderPath)) {
                Files.walk(folderPath)
                        .sorted(Comparator.reverseOrder())
                        .forEach(path -> {
                            try {
                                Files.delete(path);
                            } catch (IOException e) {
                                System.err.println("No se pudo eliminar: " + path + " - " + e.getMessage());
                            }
                        });
            }
        }
    }


    @Test
    void cargarArchivoPdf() throws IOException {

        // Crear el archivo simulado
        MultipartFile fileMock = DigitalizacionSetUp.generarArchivo(50, "file", "application/pdf");
        long expectedFileSize = fileMock.getSize(); // Tamaño maximo esperado del archivo 50 MB

        // Definir una ruta temporal para el test
        ReflectionTestUtils.setField(digitalizacionService, "rootFolder", rootFolder);
        ReflectionTestUtils.setField(digitalizacionFolderService, "rootFolder", rootFolder);

        // Llamar al método a probar
        DigitalizacionRecord result = digitalizacionService.procesarArchivo(fileMock, documento.getId());

        // Verificar las interacciones con los mocks
        verify(documentoRepository).findById(documento.getId());
        verify(documentoRepository).save(any(Documento.class));
        verify(digitalizacionFolderService).createFolderDigitalizacion(any(Documento.class));

        // Validar que el archivo fue creado correctamente en la ruta especificada

        Path pathArchivo = Paths.get(result.rutaArchivo());

        assert Files.exists(pathArchivo) : "El archivo no fue creado correctamente";

        // Validar el tamaño del archivo
        long actualFileSize = Files.size(pathArchivo);
        assert actualFileSize == expectedFileSize : "El tamaño del archivo no coincide";

        // Validar que el archivo es un PDF (si aplicable)
        assert result.rutaArchivo().endsWith(".pdf") : "El archivo creado no es un PDF";

        // Limpiar: borrar el archivo después de la prueba (opcional)
        Files.deleteIfExists(pathArchivo);
    }

    @Test
    void cargarArchivoDiferenteAPdf() {

        MultipartFile archivoNoPDF = DigitalizacionSetUp.generarArchivo(50, "archivoTexto", "text/plain");

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            digitalizacionService.procesarArchivo(archivoNoPDF, documento.getId()); // Reemplaza con el documentoId
            // adecuado
        });

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals("El archivo debe ser un PDF.", exception.getReason());
    }

    @Test
    void cargarArchivoConTamanioMayor() {

        MultipartFile archivoGrande = DigitalizacionSetUp.generarArchivo(51, "archivoGrande", "application/pdf");

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> digitalizacionService.procesarArchivo(archivoGrande, documento.getId()));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals("El archivo no puede superar los 50 MB.", exception.getReason());
    }

    @Test
    void descargarArchivoDocumento() throws IOException {

        // Crear el archivo simulado como MultipartFile
        MultipartFile fileMock = DigitalizacionSetUp.generarArchivo(50, "file", "application/pdf");

        // Convertir el archivo a un arreglo de bytes para la simulación de lectura
        byte[] fileContent = fileMock.getBytes();

        // Definir la ruta temporal para el test usando ReflectionTestUtils para simular
        // las rutas en el servicio
        ReflectionTestUtils.setField(digitalizacionService, "rootFolder", rootFolder);
        ReflectionTestUtils.setField(digitalizacionFolderService, "rootFolder", rootFolder);

        // Procesar el archivo para obtener el DigitalizacionRecord
        DigitalizacionRecord result = digitalizacionService.procesarArchivo(fileMock, documento.getId());

        // Definir las dos rutas completas del archivo
        Path pathArchivoNormal = Paths.get(result.rutaArchivo());
        Path pathArchivoExhorto = Paths.get(rootFolder, "digitalizacion", "2024", "juzgadoPrueba", "entrada", "E000006");

        // Simular la búsqueda del documento en la base de datos
        given(documentoRepository.findById(1)).willReturn(Optional.of(documento));

        // Simular métodos estáticos usando `mockStatic`
        try (MockedStatic<Files> mockedFiles = mockStatic(Files.class)) {
            // Simular que ambos archivos existen
            mockedFiles.when(() -> Files.exists(pathArchivoNormal)).thenReturn(true);
            mockedFiles.when(() -> Files.exists(pathArchivoExhorto)).thenReturn(true);

            // Simular la lectura de ambos archivos como un arreglo de bytes
            mockedFiles.when(() -> Files.readAllBytes(pathArchivoNormal)).thenReturn(fileContent);
            mockedFiles.when(() -> Files.readAllBytes(pathArchivoExhorto)).thenReturn(fileContent);

            // Llamar al método que descarga el archivo
            byte[] fileBytesNormal = digitalizacionService.getDocumento(1);
            byte[] fileBytesExhorto = digitalizacionService.getDocumento(1); // Simulación del segundo archivo

            // Verificar que el contenido del archivo descargado es correcto
            assertArrayEquals(fileContent, fileBytesNormal);
            assertArrayEquals(fileContent, fileBytesExhorto);
        }

        // Limpiar los archivos después de la prueba para evitar residuos (opcional)
        Files.deleteIfExists(pathArchivoNormal);
        Files.deleteIfExists(pathArchivoExhorto);
    }

    @Test
    void cargarArchivoPdfExhortos() throws IOException {

        MultipartFile fileMock = DigitalizacionSetUp.generarArchivo(50, "file", "application/pdf");
        long expectedFileSize = fileMock.getSize();

        documento.getCarpeta().setTipoCarpeta(TipoCarpeta.EXHORTO);

        ReflectionTestUtils.setField(digitalizacionService, "rootFolder", rootFolder);
        ReflectionTestUtils.setField(digitalizacionFolderService, "rootFolder", rootFolder);


        String expectedFolderPath = rootFolder + "/digitalizacion/2024/juzgadoPrueba/entrada/E000006";

        given(digitalizacionFolderService.createFolderDigitalizacion(any(Documento.class)))
                .willReturn(expectedFolderPath);

        DigitalizacionRecord result = digitalizacionService.procesarArchivo(fileMock, documento.getId());

        verify(documentoRepository).findById(documento.getId());
        verify(documentoRepository).save(any(Documento.class));
        verify(digitalizacionFolderService).createFolderDigitalizacion(any(Documento.class));
        Path expectedFolderPathNormalized = Paths.get(expectedFolderPath).normalize();
        Path actualFolderPathNormalized = Paths.get(result.rutaArchivo()).getParent().normalize();
        Path pathArchivo = Paths.get(result.rutaArchivo());
        long actualFileSize = Files.size(pathArchivo);

        assertEquals(expectedFolderPathNormalized, actualFolderPathNormalized, "La carpeta de exhorto no fue creada correctamente");
        assert Files.exists(pathArchivo) : "El archivo no fue creado correctamente";
        assert actualFileSize == expectedFileSize : "El tamaño del archivo no coincide";
        assert result.rutaArchivo().endsWith(".pdf") : "El archivo creado no es un PDF";
        Files.deleteIfExists(pathArchivo);
    }



}
