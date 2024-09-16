package mx.gob.pjpuebla.trials.core.digitalizacion;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import java.io.IOException;

import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import lombok.extern.slf4j.Slf4j;
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
import mx.gob.pjpuebla.trials.util.TipoDocumento;
import mx.gob.pjpuebla.trials.workflow.documentos.DigitalizacionService;
import mx.gob.pjpuebla.trials.workflow.documentos.Documento;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;


@Slf4j
@SpringBootTest
public class DigitalizacionServiceTest {
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

    @InjectMocks
    private DigitalizacionService digitalizacionService;

    @Test
    void uploadFileTest() throws IOException{
        given(materiaRepository.save(any(Materia.class))).willReturn(MateriaSetUp.createMateria());
        given(distritoRepository.save(any(Distrito.class))).willReturn(DistritoSetUp.createDistrito());
        given(domicilioRepository.save(any(Domicilio.class))).willReturn(DomicilioSetUp.createDomicilio());
        given(sedeRepository.save(any(Sede.class))).willReturn(SedeSetUp.createSede());
        given(tipoSistemaRepository.save(any(TipoSistema.class))).willReturn(TipoSistemaSetUp.createTipoSistema());
        given(tipoJuicioRepository.save(any(TipoJuicio.class))).willReturn(TipoJuicioSetUp.createTipoJuicio());
    
        Juzgado juzgado = JuzgadoSetUp.createJuzgado();
        Documento documento = DocumentoTestSetUp.create(TipoDocumento.DEMANDA, TipoJuicioSetUp.createTipoJuicio(), juzgado);
        MultipartFile fileMock = DigitalizacionSetUp.generarFile();

        digitalizacionService.procesarArchivo(fileMock, documento);
    }

}
