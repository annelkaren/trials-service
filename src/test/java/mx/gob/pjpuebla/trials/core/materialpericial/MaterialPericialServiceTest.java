package mx.gob.pjpuebla.trials.core.materialpericial;


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
class MaterialPericialServiceTest {

    @Mock
    private MaterialPericialRepository materialPericialRepository;

    @InjectMocks
    private MaterialPericialService materialPericialService;

    private MaterialPericial materialPericial;

    @BeforeEach
    public void SetUp() {
        materialPericial = MaterialPericialSetUp.createMaterialPericial();
    }

    @Test
    void testGetAllMaterialParicial_WithNameFilter() {
        String nombre = "Documento";
        when(materialPericialRepository.getAllMateriaPericial(nombre.toLowerCase()))
                .thenReturn(List.of(materialPericial));
        List<MaterialPericial> result = materialPericialService.getallMaterialParicial(nombre);

        assertThat(result).isNotNull().hasSize(1);
        assertThat(result.get(0).getNombre()).isEqualTo("Documentoscopía y Grafoscopía");
        verify(materialPericialRepository, times(1)).getAllMateriaPericial(nombre.toLowerCase());
    }

    @Test
    void testGetAllMaterialParicial_WithoutNameFilter() {
        String nombre = null;
        when(materialPericialRepository.getAllMateriaPericial("")).thenReturn(List.of(materialPericial));
        List<MaterialPericial> result = materialPericialService.getallMaterialParicial(nombre);

        assertThat(result).isNotNull().hasSize(1);
        assertThat(result.get(0).getNombre()).isEqualTo("Documentoscopía y Grafoscopía");
        verify(materialPericialRepository, times(1)).getAllMateriaPericial("");
    }
}