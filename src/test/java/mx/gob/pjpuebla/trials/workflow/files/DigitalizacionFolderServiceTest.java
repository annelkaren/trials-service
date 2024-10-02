package mx.gob.pjpuebla.trials.workflow.files;

import mx.gob.pjpuebla.trials.core.distritos.Distrito;
import mx.gob.pjpuebla.trials.core.distritos.DistritoRepository;
import mx.gob.pjpuebla.trials.core.distritos.DistritoSetUp;
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
import mx.gob.pjpuebla.trials.util.enums.TipoCarpeta;
import mx.gob.pjpuebla.trials.util.enums.TipoDocumento;
import mx.gob.pjpuebla.trials.workflow.documentos.Documento;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class DigitalizacionFolderServiceTest {

    public static final String FILE_PATH = "/opt/pjp/files/digitalizacion/2024/Juzgado/000001";
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
    private DigitalizacionFolderService digitalizacionFolderService;

    @Test
    @Disabled("La prueba es muy sencilla deberia ser incluida en el flujo general de crear carpeta")
    void createFolder() {
        given(materiaRepository.save(any(Materia.class))).willReturn(MateriaSetUp.createMateria());
        given(distritoRepository.save(any(Distrito.class))).willReturn(DistritoSetUp.createDistrito());
        given(domicilioRepository.save(any(Domicilio.class))).willReturn(DomicilioSetUp.createDomicilio());
        given(sedeRepository.save(any(Sede.class))).willReturn(SedeSetUp.createSede());
        given(tipoSistemaRepository.save(any(TipoSistema.class))).willReturn(TipoSistemaSetUp.createTipoSistema());
        given(tipoJuicioRepository.save(any(TipoJuicio.class))).willReturn(TipoJuicioSetUp.createTipoJuicio());

        ReflectionTestUtils.setField(digitalizacionFolderService, "rootFolder", rootFolder);
        Juzgado juzgado = JuzgadoSetUp.createJuzgado();

        Documento documento = DocumentoTestSetUp.create(TipoDocumento.PROMOCION, TipoJuicioSetUp.createTipoJuicio(), juzgado);
        documento.getCarpeta().setTipoCarpeta(TipoCarpeta.DEMANDA);

        try (MockedStatic<Files> files = Mockito.mockStatic(Files.class)) {
            files.when(() -> Files.createDirectories(any(Path.class)))
                    .thenReturn(Paths.get(FILE_PATH));

            String rutaCarpeta = digitalizacionFolderService.createFolderDigitalizacion(documento);
            String normalizedExpected = Paths.get(FILE_PATH).toString().replace(File.separator, "/");
            String normalizedActual = rutaCarpeta.replace(File.separator, "/");

            assertThat(normalizedActual).isEqualToIgnoringCase(normalizedExpected);

        }

        Documento documentoExhorto = DocumentoTestSetUp.create(TipoDocumento.PROMOCION, TipoJuicioSetUp.createTipoJuicio(), juzgado);
        documentoExhorto.getCarpeta().setTipoCarpeta(TipoCarpeta.EXHORTO);
        String expectedExhortoPath = digitalizacionFolderService.createFolderDigitalizacion(documentoExhorto);


        try (MockedStatic<Files> files = Mockito.mockStatic(Files.class)) {
            files.when(() -> Files.createDirectories(any(Path.class)))
                    .thenReturn(Paths.get(expectedExhortoPath));

            String rutaCarpetaExhorto = digitalizacionFolderService.createFolderDigitalizacion(documentoExhorto);
            String normalizedExpected = Paths.get(expectedExhortoPath).toString().replace(File.separator, "/");
            String normalizedActual = rutaCarpetaExhorto.replace(File.separator, "/");
            assertThat(normalizedActual).isEqualToIgnoringCase(normalizedExpected);
        }

    }

}
