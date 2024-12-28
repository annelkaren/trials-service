package mx.gob.pjpuebla.trials.core.tipopruebas;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import mx.gob.pjpuebla.trials.core.tipoprueba.TipoPruebas;
import mx.gob.pjpuebla.trials.core.tipoprueba.TipoPruebasRepository;
import mx.gob.pjpuebla.trials.core.tipoprueba.TipoPruebasService;

import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class TipoPruebasServiceTest {

    @Mock
    private TipoPruebasRepository tipoPruebasRepository;

    @InjectMocks
    private TipoPruebasService tipoPruebasService;

    private TipoPruebas tipoPrueba;

    @BeforeEach
    public void setUp() {
        tipoPrueba = new TipoPruebas();
        tipoPrueba.setNombre("Confesional");
    }

    @Test
    public void testObtenerTipoPruebasPorNombreFound() {
        when(tipoPruebasRepository.findByNombre("Confesional")).thenReturn(Optional.of(tipoPrueba));

        Optional<TipoPruebas> resultado = tipoPruebasService.obtenerTipoPruebasPorNombre("Confesional");

        assertTrue(resultado.isPresent(), "El resultado debe ser presente");
        assertEquals("Confesional", resultado.get().getNombre(), "El nombre de la prueba debe ser 'Confesional'");
    }

    @Test
    public void testObtenerTipoPruebasPorNombreNotFound() {
        when(tipoPruebasRepository.findByNombre("NoExiste")).thenReturn(Optional.empty());

        Optional<TipoPruebas> resultado = tipoPruebasService.obtenerTipoPruebasPorNombre("NoExiste");

        assertFalse(resultado.isPresent(), "El resultado debe ser vacío");
    }
}
