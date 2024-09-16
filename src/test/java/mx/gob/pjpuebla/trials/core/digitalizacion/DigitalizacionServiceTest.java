package mx.gob.pjpuebla.trials.core.digitalizacion;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import lombok.extern.slf4j.Slf4j;
import mx.gob.pjpuebla.trials.core.distritos.Distrito;
import mx.gob.pjpuebla.trials.core.distritos.DistritoRepository;
import mx.gob.pjpuebla.trials.core.distritos.DistritoSetUp;
import mx.gob.pjpuebla.trials.core.domicilios.Domicilio;
import mx.gob.pjpuebla.trials.core.domicilios.DomicilioRepository;
import mx.gob.pjpuebla.trials.core.domicilio.DomicilioSetUp;
import mx.gob.pjpuebla.trials.core.juzgados.DocumentoTestSetUp;
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
import mx.gob.pjpuebla.trials.util.TipoDocumento;
import mx.gob.pjpuebla.trials.workflow.documentos.DigitalizacionRecord;
import mx.gob.pjpuebla.trials.workflow.documentos.DigitalizacionService;
import mx.gob.pjpuebla.trials.workflow.documentos.Documento;
import mx.gob.pjpuebla.trials.workflow.documentos.DocumentoRepository;
import mx.gob.pjpuebla.trials.workflow.files.DigitalizacionFolderService;
import org.springframework.http.HttpStatus;

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
        documento = DocumentoTestSetUp.create(TipoDocumento.DEMANDA, TipoJuicioSetUp.createTipoJuicio(),
                JuzgadoSetUp.createJuzgado());
        given(documentoRepository.findById(documento.getId())).willReturn(java.util.Optional.of(documento));
        given(documentoRepository.save(any(Documento.class))).willReturn(documento);
        given(digitalizacionFolderService.createFolderDigitalizacion(any(Documento.class)))
                .willReturn("digitalizacion/2024/");
    }

    @Test
    void uploadFileCorrectly() throws IOException {

        // Crear el archivo simulado
        MultipartFile fileMock = DigitalizacionSetUp.generarArchivoPDF();
        long expectedFileSize = fileMock.getSize(); // Tamaño maximo esperado del archivo 50 MB

        // Definir una ruta temporal para el test
        ReflectionTestUtils.setField(digitalizacionService, "rootFolder", rootFolder);

        // Llamar al método a probar
        DigitalizacionRecord result = digitalizacionService.procesarArchivo(fileMock, documento.getId());

        // Verificar las interacciones con los mocks
        verify(documentoRepository).findById(documento.getId());
        verify(documentoRepository).save(any(Documento.class));
        verify(digitalizacionFolderService).createFolderDigitalizacion(any(Documento.class));

        // Validar que el archivo fue creado correctamente en la ruta especificada
        String rutaArchivo = result.pathFile();
        log.info("Ruta donde se guarda el archivo: " + rutaArchivo);

        Path pathArchivo = Paths.get(rutaArchivo);
        assert Files.exists(pathArchivo) : "El archivo no fue creado correctamente";

        // Validar el tamaño del archivo
        long actualFileSize = Files.size(pathArchivo);
        assert actualFileSize == expectedFileSize : "El tamaño del archivo no coincide";

        // Validar que el archivo es un PDF (si aplicable)
        assert rutaArchivo.endsWith(".pdf") : "El archivo creado no es un PDF";

        // Limpiar: borrar el archivo después de la prueba (opcional)
        Files.deleteIfExists(pathArchivo);
    }

    @Test
    void uploadFileCorrectlySmallFile() throws IOException {

        // Crear el archivo simulado
        MultipartFile fileMock = DigitalizacionSetUp.generarArchivoPDFMenorMaximo();
        long expectedFileSize = fileMock.getSize(); // Tamaño maximo esperado del archivo 50 MB

        // Definir una ruta temporal para el test
        ReflectionTestUtils.setField(digitalizacionService, "rootFolder", rootFolder);

        // Llamar al método a probar
        DigitalizacionRecord result = digitalizacionService.procesarArchivo(fileMock, documento.getId());

        // Verificar las interacciones con los mocks
        verify(documentoRepository).findById(documento.getId());
        verify(documentoRepository).save(any(Documento.class));
        verify(digitalizacionFolderService).createFolderDigitalizacion(any(Documento.class));

        // Validar que el archivo fue creado correctamente en la ruta especificada
        String rutaArchivo = result.pathFile();
        log.info("Ruta donde se guarda el archivo: " + rutaArchivo);

        Path pathArchivo = Paths.get(rutaArchivo);
        assert Files.exists(pathArchivo) : "El archivo no fue creado correctamente";

        // Validar el tamaño del archivo
        long actualFileSize = Files.size(pathArchivo);
        assert actualFileSize == expectedFileSize : "El tamaño del archivo no coincide";

        // Validar que el archivo es un PDF (si aplicable)
        assert rutaArchivo.endsWith(".pdf") : "El archivo creado no es un PDF";

        // Limpiar: borrar el archivo después de la prueba (opcional)
        Files.deleteIfExists(pathArchivo);
    }


    @Test
    void uploadFileRejectsNonPDFFile() {
      
        MultipartFile archivoNoPDF = DigitalizacionSetUp.generarArchivoNoPDF();

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            digitalizacionService.procesarArchivo(archivoNoPDF, documento.getId()); // Reemplaza con el documentoId
                                                                                    // adecuado
        });

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals("El archivo debe ser un PDF.", exception.getReason());
    }

    @Test
    void uploadFileRejectsLargeFile() {
       
        MultipartFile archivoGrande = DigitalizacionSetUp.generarArchivoGrande();

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            digitalizacionService.procesarArchivo(archivoGrande, documento.getId());
        });

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals("El archivo no puede superar los 50 MB.", exception.getReason());
    }

}
