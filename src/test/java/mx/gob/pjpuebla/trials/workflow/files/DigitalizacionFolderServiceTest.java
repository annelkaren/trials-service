package mx.gob.pjpuebla.trials.workflow.files;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import lombok.extern.slf4j.Slf4j;
import mx.gob.pjpuebla.trials.core.distritos.Distrito;
import mx.gob.pjpuebla.trials.core.distritos.DistritoRepository;
import mx.gob.pjpuebla.trials.core.distritos.DistritoSetUp;
import mx.gob.pjpuebla.trials.core.documentos.Documento;
import mx.gob.pjpuebla.trials.core.domicilio.DomicilioSetUp;
import mx.gob.pjpuebla.trials.core.domicilios.Domicilio;
import mx.gob.pjpuebla.trials.core.domicilios.DomicilioRepository;
import mx.gob.pjpuebla.trials.core.juzgados.DocumentoTestSetUp;
import mx.gob.pjpuebla.trials.core.juzgados.Juzgado;
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

@Slf4j
@SpringBootTest
public class DigitalizacionFolderServiceTest {

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

    private Documento documento;
    private Juzgado juzgado;
    private Path testFolderPath;
 
    @Value("${root-folder}")
    private String ROOT_FOLDER;

    @InjectMocks
    private DigitalizacionFolderService digitalizacionFolderService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        // Configuración de los mocks
        Materia materia = MateriaSetUp.createMateria();
        Distrito distrito = DistritoSetUp.createDistrito();
        Domicilio domicilio = DomicilioSetUp.createDomicilio();
        Sede sede = SedeSetUp.createSede();
        TipoSistema tipoSistema = TipoSistemaSetUp.createTipoSistema();
        TipoJuicio tipoJuicio = TipoJuicioSetUp.createTipoJuicio(tipoSistema, materia);

        given(materiaRepository.save(materia)).willReturn(materia);
        given(distritoRepository.save(distrito)).willReturn(distrito);
        given(domicilioRepository.save(domicilio)).willReturn(domicilio);
        given(sedeRepository.save(sede)).willReturn(sede);
        given(tipoSistemaRepository.save(tipoSistema)).willReturn(tipoSistema);
        given(tipoJuicioRepository.save(tipoJuicio)).willReturn(tipoJuicio);

        sede.setDistrito(distrito);
        sede.setDomicilio(domicilio);

        juzgado = JuzgadoSetUp.createJuzgado(materia, sede);
        documento = DocumentoTestSetUp.create(TipoDocumento.DEMANDA, tipoJuicio, juzgado);
        System.out.println("EL VALOR INICIAL DE LA PRUEBA UNINTARIA ES: " + ROOT_FOLDER);
        testFolderPath = Paths.get(ROOT_FOLDER, "digitalizacion", "2024", "Juzgado", "000001");
    }

    @Test
    void createFolder() throws IOException {

        String rutaCarpeta = digitalizacionFolderService.createFolderDigitalizacion(documento);
        System.out.println("comprobare que se creo la carpeta: "+testFolderPath);
        assertThat(Files.exists(testFolderPath)).isTrue();
        assertThat(rutaCarpeta).isEqualTo(testFolderPath.toString());
    }

    @AfterEach
    public void tearDown() throws IOException {
        try {
            Path rootFolder = Paths.get(ROOT_FOLDER, "digitalizacion");
    
            if (Files.exists(rootFolder)) {
               
                Files.walkFileTree(rootFolder, new SimpleFileVisitor<Path>() {
                    @Override
                    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                        Files.delete(file);  
                        return FileVisitResult.CONTINUE;
                    }
    
                    @Override
                    public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                        Files.delete(dir); 
                        return FileVisitResult.CONTINUE;
                    }
                });
                log.info("Carpeta 'digitalizacion' y su contenido fueron eliminados exitosamente.");
            } else {
                log.warn("La carpeta raíz 'digitalizacion' no existe.");
            }
        } catch (IOException e) {
            log.error("Error al eliminar las carpetas de digitalización: {}", e.getMessage(), e);
            throw new RuntimeException("Error al eliminar las carpetas de digitalización", e);
        }
    }
    
    
    
}
