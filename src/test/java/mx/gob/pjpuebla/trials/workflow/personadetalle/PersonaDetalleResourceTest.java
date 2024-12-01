package mx.gob.pjpuebla.trials.workflow.personadetalle;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;

import com.fasterxml.jackson.databind.ObjectMapper;

import mx.gob.pjpuebla.trials.core.escolaridades.Escolaridad;
import mx.gob.pjpuebla.trials.workflow.personadetalle.DTO.PersonaDTO;
import mx.gob.pjpuebla.trials.workflow.personadetalle.DTO.PersonaDTOGet;

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
    private PersonaDTOGet mockPersonaDTOGet;

    @BeforeEach
    void setUp() {
        mockPersonaDTO = createMockPersonaDTO();
        mockPersonaDetalleRecord = new PersonaDetalleRecord(1, 2, 3L);
        mockPersonaDTOGet = createMockPersonaDTOGet();
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
        datosGenerales.setTipo(200);
        datosGenerales.setNombres("Susana");
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

    private PersonaDTOGet createMockPersonaDTOGet() {
    PersonaDTOGet personaDTOGet = new PersonaDTOGet();
    PersonaDTOGet.DatosGenerales datosGenerales = new PersonaDTOGet.DatosGenerales();
    datosGenerales.setTipo(200);
    datosGenerales.setNombre("Susana");
    datosGenerales.setApellidoPaterno("Reyes");
    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
    try {
        java.util.Date fechaNacimientoUtil = sdf.parse("09-02-1999");
        Date fechaNacimientoSql = new Date(fechaNacimientoUtil.getTime());
        datosGenerales.setFechaNacimiento(fechaNacimientoSql);
    } catch (ParseException e) {
        e.printStackTrace();
    }
    datosGenerales.setIdCarpeta(1);

    PersonaDTOGet.DatosContacto datosContacto = new PersonaDTOGet.DatosContacto();
    datosContacto.setCorreoElectronico("susana@gmail.com");

    PersonaDTOGet.DatosEstadistica datosEstadistica = new PersonaDTOGet.DatosEstadistica();
    Escolaridad escolaridad = new Escolaridad();
    escolaridad.setId(1); 
    datosEstadistica.setEscolaridad(escolaridad);
    personaDTOGet.setDatosGenerales(datosGenerales);
    personaDTOGet.setDatosContacto(datosContacto);
    personaDTOGet.setDatosEstadistica(datosEstadistica);

    return personaDTOGet;
}


    @Test
    void testGetParticipanteById_returnsOkAndPersonaDTO() throws Exception {
        PersonaDTOGet personaDTOGet = new PersonaDTOGet();
        PersonaDTOGet.DatosGenerales datosGenerales = new PersonaDTOGet.DatosGenerales();
        PersonaDTOGet.DatosContacto datosContacto = new PersonaDTOGet.DatosContacto();
        PersonaDTOGet.DatosEstadistica datosEstadistica = new PersonaDTOGet.DatosEstadistica();

        datosGenerales.setNombre("Susana");
        datosGenerales.setApellidoPaterno("Reyes");

        datosContacto.setCorreoElectronico("susana@gmail.com");
        personaDTOGet.setDatosGenerales(datosGenerales);
        personaDTOGet.setDatosContacto(datosContacto);
        personaDTOGet.setDatosEstadistica(datosEstadistica);

        when(personaDetalleService.getParticipante(1)).thenReturn(personaDTOGet);

        mockMvc.perform(get("/api/workflow/personaDetalle/1")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.datosGenerales.nombre").value("Susana"))
                .andExpect(jsonPath("$.datosGenerales.apellidoPaterno").value("Reyes"))
                .andExpect(jsonPath("$.datosContacto.correoElectronico").value("susana@gmail.com"));

        verify(personaDetalleService).getParticipante(1);
        }


    @Test
    void testActualizarPersona_returnsOk() throws Exception {

        PersonaDTO personaDTO = new PersonaDTO();
        PersonaDTO.DatosGenerales datosGenerales = new PersonaDTO.DatosGenerales();
        PersonaDTO.DatosContacto datosContacto = new PersonaDTO.DatosContacto();
        PersonaDTO.DatosEstadistica datosEstadistica = new PersonaDTO.DatosEstadistica();

        datosGenerales.setNombres("Juan");
        datosGenerales.setApellidoPaterno("Perez");
        datosContacto.setCorreoElectronico("juan.perez@gmail.com");
        datosEstadistica.setEscolaridad(1);

        personaDTO.setDatosGenerales(datosGenerales);
        personaDTO.setDatosContacto(datosContacto);
        personaDTO.setDatosEstadistica(datosEstadistica);

        doNothing().when(personaDetalleService).updateParticipante(1, personaDTO);

        mockMvc.perform(put("/api/workflow/personaDetalle/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(personaDTO)))
                .andExpect(status().isOk())
                .andExpect(content().string("Entidad actualizada exitosamente"));

        verify(personaDetalleService).updateParticipante(1, personaDTO);
    }

}
