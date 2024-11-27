package mx.gob.pjpuebla.trials.core.desahogoaudiencia;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class DesahogoAudienciaServiceTest {

    @InjectMocks
    DesahogoAudienciaService desahogoAudienciaService;

    @Mock
    DesahogoAudienciaRepository desahogoAudienciaRepository;

    @Test
    void testGetAll() {
        DesahogoAudiencia desAu1 = new DesahogoAudiencia();
        desAu1.setId(1);
        desAu1.setKey("COM_POR_CO");
        desAu1.setNombre("Conclusión por Convenio");

        DesahogoAudiencia desAu2 = new DesahogoAudiencia();
        desAu2.setId(2);
        desAu2.setKey("DES_DE_ACC");
        desAu2.setNombre("Desistimiento de la Acción");

        when(desahogoAudienciaRepository.findAll()).thenReturn(Arrays.asList(desAu1, desAu2));

        List<DesahogoAudienciaRecord> result = desahogoAudienciaService.getAll();

        assertEquals(2, result.size());

        assertEquals("COM_POR_CO", result.get(0).key());
        assertEquals("Conclusión por Convenio", result.get(0).desahogoAudiencia());
        assertNotNull(result.get(0).id());

        assertEquals("DES_DE_ACC", result.get(1).key());
        assertEquals("Desistimiento de la Acción", result.get(1).desahogoAudiencia());
        assertNotNull(result.get(1).id());
    }
}