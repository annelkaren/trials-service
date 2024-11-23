package mx.gob.pjpuebla.trials.workflow.personadetalle;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(PersonaDetalleResource.class)
@AutoConfigureMockMvc(addFilters = false)
class PersonaDetalleResourceTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PersonaDetalleService personaDetalleService;

    @Autowired
    private ObjectMapper objectMapper; 

    private PersonaDTO mockPersonaDTO;
    private PersonaDetalleRecord mockPersonaDetalleRecord;

    @BeforeEach
    void setUp() {
        mockPersonaDTO = createMockPersonaDTO();
        mockPersonaDetalleRecord = new PersonaDetalleRecord(1, 2, 3L);
    }

    @Test
    void testCreatePersonaDetalle_returnsCreatedPersonaDetalle() throws Exception {
        when(personaDetalleService.createPersonaDetalle(any(PersonaDTO.class)))
                .thenReturn(mockPersonaDetalleRecord);

        mockMvc.perform(post("/api/workflow/personaDetalle/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(mockPersonaDTO)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").exists()) 
                .andExpect(jsonPath("$.id").value(mockPersonaDetalleRecord.id()));

        verify(personaDetalleService, times(1)).createPersonaDetalle(any(PersonaDTO.class));
    }

    private PersonaDTO createMockPersonaDTO() {
        PersonaDTO.DatosGenerales datosGenerales = new PersonaDTO.DatosGenerales();
        datosGenerales.setTipo(List.of("Actor"));
        datosGenerales.setNombre("Susana");
        datosGenerales.setApellidoPaterno("Reyes");
        datosGenerales.setFechaNacimiento(LocalDate.of(1999, 2, 9));
        datosGenerales.setIdCarpeta(1);

        PersonaDTO.DatosContacto datosContacto = new PersonaDTO.DatosContacto();
        datosContacto.setCorreoElectronico("susana@gmail.com");

        PersonaDTO.DatosEstadistica datosEstadistica = new PersonaDTO.DatosEstadistica();
        datosEstadistica.setEscolaridad(1);

        PersonaDTO personaDTO = new PersonaDTO();
        personaDTO.setDatosGenerales(datosGenerales);
        personaDTO.setDatosContacto(datosContacto);
        personaDTO.setDatosEstadistica(datosEstadistica);

        return personaDTO;
    }
}
