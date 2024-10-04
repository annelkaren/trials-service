package mx.gob.pjpuebla.trials.workflow.audiencias;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import mx.gob.pjpuebla.trials.core.bloques.BloqueRepository;
import mx.gob.pjpuebla.trials.core.salas.SalaRepository;

@ExtendWith(MockitoExtension.class)
public class AudienciaServiceTest {
    
    @Mock
    private AudienciaRepository audienciaRepository;

    @Mock
    private SalaRepository salaRepository;

    @Mock
    private BloqueRepository bloqueRepository;

    @BeforeEach
    public void setUp() {
        
    }

    
}
