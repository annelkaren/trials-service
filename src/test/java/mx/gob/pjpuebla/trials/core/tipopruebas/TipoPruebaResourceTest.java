package mx.gob.pjpuebla.trials.core.tipopruebas;


import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.*;

import java.util.Optional;

import mx.gob.pjpuebla.trials.core.tipoprueba.TipoPruebas;
import mx.gob.pjpuebla.trials.core.tipoprueba.TipoPruebaResource;
import mx.gob.pjpuebla.trials.core.tipoprueba.TipoPruebasService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.http.MediaType;

public class TipoPruebaResourceTest {

    private MockMvc mockMvc;

    @Mock
    private TipoPruebasService tipoPruebasService;

    @InjectMocks
    private TipoPruebaResource tipoPruebaResource;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = standaloneSetup(tipoPruebaResource).build();
    }

    @Test
    public void testObtenerTipoPruebasPorNombreFound() throws Exception {
        
        String nombre = "Confesional";
        TipoPruebas tipoPrueba = new TipoPruebas();
        tipoPrueba.setNombre(nombre);

        when(tipoPruebasService.obtenerTipoPruebasPorNombre(nombre))
                .thenReturn(Optional.of(tipoPrueba));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/core/tipoPruebas/by-name/{nombre}", nombre)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        verify(tipoPruebasService, times(1)).obtenerTipoPruebasPorNombre(nombre);
    }

    @Test
    public void testObtenerTipoPruebasPorNombreNotFound() throws Exception {
        String nombre = "Confesional";

        when(tipoPruebasService.obtenerTipoPruebasPorNombre(nombre))
                .thenReturn(Optional.empty());

        mockMvc.perform(MockMvcRequestBuilders.get("/api/core/tipoPruebas/by-name/{nombre}", nombre)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andReturn();

        verify(tipoPruebasService, times(1)).obtenerTipoPruebasPorNombre(nombre);
    }
}

