package mx.gob.pjpuebla.trials.core.materiapericial;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.Mockito.verify;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MateriaPericialServiceTest {

    @Mock
    private MateriaPericialRepository materiaPericialRepository;

    @InjectMocks
    private MateriaPericialService materiaPericialService;

    private MateriaPericial materiaPericial;

    @BeforeEach
    public void SetUp() {
        materiaPericial = MateriaPericialSetUp.createMateriaPericial();
    }

    @Test
    void testGetAllMateriaParicial() {
        String nombre = "Documento";
        when(materiaPericialRepository.getAllMateriaPericial(nombre.toLowerCase()))
                .thenReturn(List.of(materiaPericial));
        List<MateriaPericial> result = materiaPericialService.getallMateriaParicial(nombre);

        assertThat(result).isNotNull().hasSize(1);
        assertThat(result.get(0).getNombre()).isEqualTo("Documentoscopía y Grafoscopía");
        verify(materiaPericialRepository, times(1)).getAllMateriaPericial(nombre.toLowerCase());
    }

    @Test
    void testGetAllMateriaParicial_null() {
        String nombre = null;
        when(materiaPericialRepository.getAllMateriaPericial("")).thenReturn(List.of(materiaPericial));
        List<MateriaPericial> result = materiaPericialService.getallMateriaParicial(nombre);

        assertThat(result).isNotNull().hasSize(1);
        assertThat(result.get(0).getNombre()).isEqualTo("Documentoscopía y Grafoscopía");
        verify(materiaPericialRepository, times(1)).getAllMateriaPericial("");
    }
}