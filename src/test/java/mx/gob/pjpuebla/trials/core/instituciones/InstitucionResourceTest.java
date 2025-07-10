package mx.gob.pjpuebla.trials.core.instituciones;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import mx.gob.pjpuebla.trials.core.instituciones.records.InstitucionRecord;
import mx.gob.pjpuebla.trials.error.NotFoundException;
import mx.gob.pjpuebla.trials.util.enums.Estado;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(InstitucionResource.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
class InstitucionResourceTest {

    @MockBean
    private InstitucionService mockInstitucionService;

    @Autowired
    private MockMvc mockMvc;

    private InstitucionRecord institucionRecord;

    @BeforeEach
    void setUp() {
        institucionRecord = InstitucionSetUp.createInstitucionRecord();
    }

    @Test
    void getAllByNameAndActiveSuccess() throws Exception {
        given(mockInstitucionService.getAll(any(Institucion.class), any(Pageable.class)))
                .willReturn(new PageImpl<>(Collections.singletonList(institucionRecord)));

        mockMvc.perform(
                        get("/api/core/instituciones")
                                .param("nombre", "institución prueba")
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void getById_not_found() throws Exception {
        given(mockInstitucionService.findById(anyInt()))
                .willThrow(NotFoundException.class);

        mockMvc.perform(
                        get("/api/core/institucion/0")
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void getById_invalid() throws Exception {
        given(mockInstitucionService.findById(anyInt()))
                .willThrow(MethodArgumentTypeMismatchException.class);

        mockMvc.perform(
                        get("/api/core/instituciones/Y")
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void create_success() throws Exception {
        Integer expectedId = 1;
        given(mockInstitucionService.create(InstitucionSetUp.createInstitucion(Estado.ACTIVE)))
                .willReturn(expectedId);

        mockMvc.perform(
                        post("/api/core/instituciones")
                                .content(asJsonString(
                                        InstitucionSetUp.createInstitucion(Estado.ACTIVE)))
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void update_success() throws Exception {
        Integer expectedId = 1;
        given(mockInstitucionService.create(InstitucionSetUp.createInstitucion(Estado.ACTIVE)))
                .willReturn(expectedId);

        mockMvc.perform(
                        put("/api/core/instituciones")
                                .content(asJsonString(
                                        InstitucionSetUp.createInstitucion(Estado.ACTIVE)))
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void delete_success() throws Exception {

        doNothing().when(mockInstitucionService).delete(anyInt());

        mockMvc.perform(
                        delete("/api/core/instituciones/{id}", 1)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void getAllByEstadoAutocompleteSuccess() throws Exception {
        given(mockInstitucionService.getAllByEstadoAutocomplete(any(Institucion.class), any(Pageable.class)))
                .willReturn(new PageImpl<>(Collections.singletonList(institucionRecord)));

        mockMvc.perform(
                        get("/api/core/instituciones/autocomplete")
                                .param("nombre", "I")
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    private static String asJsonString(final Object obj) {
        try {
            final ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            return mapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void getAllByTipo() throws Exception {
        given(mockInstitucionService.getAll(eq(new Institucion().setNombre("").setTipoInstitucion("Tribunal Federal")), any()))
                .willReturn(new PageImpl<>(Collections.singletonList(institucionRecord)));

        mockMvc.perform(
                        get("/api/core/instituciones/tribunales")
                                .param("nombre", "I")
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

}
