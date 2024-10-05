package mx.gob.pjpuebla.trials.workflow.audiencias;

import mx.gob.pjpuebla.trials.core.bloques.Bloque;
import mx.gob.pjpuebla.trials.core.bloques.BloqueRepository;
import mx.gob.pjpuebla.trials.core.bloques.BloqueSetUp;
import mx.gob.pjpuebla.trials.core.salas.Sala;
import mx.gob.pjpuebla.trials.core.salas.SalaAudienciaRecord;
import mx.gob.pjpuebla.trials.core.salas.SalaRepository;
import mx.gob.pjpuebla.trials.core.salas.SalaSetUp;
import mx.gob.pjpuebla.trials.core.tipoaudiencia.TipoAudiencia;
import mx.gob.pjpuebla.trials.core.tipoaudiencia.TipoAudienciaRepository;
import mx.gob.pjpuebla.trials.error.NotFoundException;
import mx.gob.pjpuebla.trials.util.enums.EstatusAudiencia;
import mx.gob.pjpuebla.trials.workflow.carpeta.Carpeta;
import mx.gob.pjpuebla.trials.workflow.carpeta.CarpetaRepository;
import mx.gob.pjpuebla.trials.workflow.carpeta.CarpetaSetUp;
import mx.gob.pjpuebla.trials.util.enums.Estado;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AudienciaServiceTest {

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
        sala = SalaSetUp.createSala();
        bloque = BloqueSetUp.createBloque();
        tipoAudiencia = new TipoAudiencia().setNombre("Audiencia Inicial").setId(1);
        carpeta = CarpetaSetUp.create();
        salaAudiencia = SalaSetUp.salaAudienciaRecord();

        when(salaRepository.findById(sala.getId())).thenReturn(Optional.of(sala));
        when(bloqueRepository.findById(salaAudiencia.bloqueId())).thenReturn(Optional.of(bloque));

    }

    @Test
    void createAudiencia_SaveAudiencia() {
       

        Audiencia savedAudiencia = AudienciaSetUp.generarAudiencia(
                salaAudiencia.fechaAudiencia(),
                sala,
                bloque,
                tipoAudiencia,
                carpeta);

        when(audienciaRepository.save(any(Audiencia.class))).thenReturn(savedAudiencia);

        Audiencia result = audienciaService.create(salaAudiencia, tipoAudiencia, carpeta);

        assertThat(result).isNotNull();
        verify(audienciaRepository).save(any(Audiencia.class));

        assertThat(result.getFechaAudiencia()).isEqualTo(salaAudiencia.fechaAudiencia());
        assertThat(result.getSala()).isEqualTo(sala);
        assertThat(result.getBloque()).isEqualTo(bloque);
        assertThat(result.getTipoAudiencia()).isEqualTo(tipoAudiencia);
        assertThat(result.getCarpeta()).isEqualTo(carpeta);
        assertThat(result.getEstatusAudiencia()).isEqualTo(EstatusAudiencia.PROGRAMADA);
        assertThat(result.getEstado()).isEqualTo(Estado.ACTIVE);
    }
}
