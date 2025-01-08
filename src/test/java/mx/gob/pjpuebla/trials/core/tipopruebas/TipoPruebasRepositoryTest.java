package mx.gob.pjpuebla.trials.core.tipopruebas;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import mx.gob.pjpuebla.trials.core.tipoprueba.TipoPruebas;
import mx.gob.pjpuebla.trials.core.tipoprueba.TipoPruebasRepository;
import mx.gob.pjpuebla.trials.core.tipoprueba.TipoPruebasService;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import java.util.Optional;

@SpringBootTest
public class TipoPruebasRepositoryTest {

    @Mock
    private TipoPruebasRepository tipoPruebasRepository;

    @InjectMocks
    private TipoPruebasService tipoPruebasService;

    @Test
    public void testFindByNombre() {
        String nombreTipoPrueba = "Confesional";
        TipoPruebas tipoPrueba = new TipoPruebas();
        tipoPrueba.setNombre(nombreTipoPrueba);
        
        when(tipoPruebasRepository.findByNombre(nombreTipoPrueba)).thenReturn(Optional.of(tipoPrueba));

        Optional<TipoPruebas> result = tipoPruebasService.obtenerTipoPruebasPorNombre(nombreTipoPrueba);

        assertTrue(result.isPresent());
        assertEquals(nombreTipoPrueba, result.get().getNombre());
        
        verify(tipoPruebasRepository, times(1)).findByNombre(nombreTipoPrueba);
    }
}
