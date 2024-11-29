package mx.gob.pjpuebla.trials.core.salas;

import mx.gob.pjpuebla.trials.core.utils.resource.ResourceUtilTest;
import mx.gob.pjpuebla.trials.error.InvalidVersionException;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SalaResource.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
class SalaResourceTest {

    @MockBean
    private SalaService mockSalaService;

    @Autowired
    private MockMvc mockMvc;

    private SalaRecord salaRecord;

    @BeforeEach
    void setUp() {
        salaRecord = SalaSetUp.salaRecord();
    }

    @Test
    void getAllByNameAndActive_success() throws Exception {
        given(mockSalaService.getAll(any(Sala.class), any(Pageable.class)))
                .willReturn(new PageImpl<>(Collections.singletonList(salaRecord)));

        mockMvc.perform(
                        get("/api/core/salas")
                                .param("nombre", "1")
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void getById_not_found() throws Exception {
        given(mockSalaService.findById(anyInt()))
                .willThrow(NotFoundException.class);

        mockMvc.perform(
                        get("/api/core/salas/0")
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void getById_invalid() throws Exception {
        given(mockSalaService.findById(anyInt()))
                .willThrow(MethodArgumentTypeMismatchException.class);

        mockMvc.perform(
                        get("/api/core/salas/Y")
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void create_success() throws Exception {
        Integer expectedId = 1;
        given(mockSalaService.create(SalaSetUp.createSala(Estado.ACTIVE)))
                .willReturn(expectedId);

        mockMvc.perform(
                        post("/api/core/salas")
                                .content(ResourceUtilTest.asJsonString(SalaSetUp.createSala(Estado.ACTIVE)))
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void update_success() throws Exception {
        Integer expectedId = 1;
        given(mockSalaService.create(SalaSetUp.createSala(Estado.ACTIVE)))
                .willReturn(expectedId);

        mockMvc.perform(
                        put("/api/core/salas")
                                .content(ResourceUtilTest.asJsonString(SalaSetUp.createSala(Estado.ACTIVE)))
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void update_error() throws Exception {
        given(mockSalaService.update(SalaSetUp.createSala(Estado.ACTIVE)))
                .willThrow(InvalidVersionException.class);

        mockMvc.perform(
                        put("/api/core/salas")
                                .content(ResourceUtilTest.asJsonString(SalaSetUp.createSala(Estado.ACTIVE)))
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

    }

    @Test
    void getAllbyJuzgado_success() throws Exception {
        given(mockSalaService.getAllbyJuzgado(any(String.class), any(Pageable.class), anyInt()))
                .willReturn(new PageImpl<>(Collections.singletonList(salaRecord)));

        mockMvc.perform(
                        get("/api/core/salas/juzgado/1")
                                .param("nombre", "A")
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}
