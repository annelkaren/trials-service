package mx.gob.pjpuebla.trials.workflow.audiencias;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import mx.gob.pjpuebla.trials.core.bloques.Bloque;
import mx.gob.pjpuebla.trials.core.bloques.BloqueRepository;
import mx.gob.pjpuebla.trials.core.bloques.BloqueSetUp;
import mx.gob.pjpuebla.trials.core.salas.Sala;
import mx.gob.pjpuebla.trials.core.salas.SalaAudienciaRecord;
import mx.gob.pjpuebla.trials.core.salas.SalaRepository;
import mx.gob.pjpuebla.trials.core.salas.SalaSetUp;
import mx.gob.pjpuebla.trials.core.tipoaudiencia.TipoAudiencia;
import mx.gob.pjpuebla.trials.core.tipoaudiencia.TipoAudienciaRepository;
import mx.gob.pjpuebla.trials.util.enums.Estado;
import mx.gob.pjpuebla.trials.workflow.audiencias.Audiencia;
import mx.gob.pjpuebla.trials.workflow.audiencias.AudienciaRepository;
import mx.gob.pjpuebla.trials.workflow.audiencias.AudienciaService;
import mx.gob.pjpuebla.trials.workflow.carpeta.Carpeta;
import mx.gob.pjpuebla.trials.workflow.carpeta.CarpetaRepository;
import mx.gob.pjpuebla.trials.workflow.carpeta.CarpetaSetUp;
import mx.gob.pjpuebla.trials.error.NotFoundException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AudienciaServiceTest {

    @Mock
    private AudienciaRepository audienciaRepository;

    @Mock
    private SalaRepository salaRepository;

    @Mock
    private BloqueRepository bloqueRepository;

    @Mock
    private TipoAudienciaRepository tipoAudienciaRepository;

    @Mock
    private CarpetaRepository carpetaRepository;

    @InjectMocks
    private AudienciaService audienciaService;

    private SalaAudienciaRecord salaAudiencia;
    private Sala sala;
    private Bloque bloque;
    private TipoAudiencia tipoAudiencia;
    private Carpeta carpeta;

    @BeforeEach
    public void setUp() {
        sala = SalaSetUp.createSala(); // Crear instancia de Sala
        bloque = BloqueSetUp.createBloque(); // Crear instancia de Bloque
        tipoAudiencia = new TipoAudiencia().setNombre("Audiencia Inicial"); // Crear instancia de TipoAudiencia
        carpeta = CarpetaSetUp.create(); // Crear instancia de Carpeta
        salaAudiencia = SalaSetUp.salaAudienciaRecord(); // Crear el SalaAudienciaRecord

        // Mockear los repositorios para que devuelvan las entidades creadas
        when(salaRepository.findById(sala.getId())).thenReturn(Optional.of(sala));
        when(bloqueRepository.findById(salaAudiencia.bloqueId())).thenReturn(Optional.of(bloque));
        when(tipoAudienciaRepository.save(any(TipoAudiencia.class))).thenReturn(tipoAudiencia);
        when(carpetaRepository.save(any(Carpeta.class))).thenReturn(carpeta);
    }

    @Test
    void createAudiencia_SaveAudiencia() {

        Audiencia savedAudiencia = new Audiencia(); 
        when(audienciaRepository.save(any(Audiencia.class))).thenReturn(savedAudiencia);

 
        Audiencia result = audienciaService.create(salaAudiencia, tipoAudiencia, carpeta);

        assertThat(result).isNotNull();
        verify(audienciaRepository).save(any(Audiencia.class));
    }
}
