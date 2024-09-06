package mx.gob.pjpuebla.trials.core.sedes;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.ws.rs.core.MediaType;
import mx.gob.pjpuebla.trials.error.NotFoundException;
import mx.gob.pjpuebla.trials.error.OptimisticLockingFailureException;
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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SedeResource.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
class SedeResourceTest {

    @MockBean
    private SedeService mockSedeService;

    @Autowired
    private MockMvc mockMvc;

    private SedeRecordResponse sedeRecordResponse;
    private SedeRecord sedeRecord;

    @BeforeEach
    void setUp() {
        sedeRecordResponse = SedeSetUp.sedeRecordResponse();
        sedeRecord = SedeSetUp.sedeRecord();
    }

    @Test
    void getAllByNameAndActive_success() throws Exception {
        given(mockSedeService.getAll(any(Sede.class), any(Pageable.class)))
                .willReturn(new PageImpl<>(Collections.singletonList(sedeRecordResponse)));

        mockMvc.perform(
                get("/api/core/sedes")
                        .param("nombre", "S")
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());
    }

    @Test
    void getById_success() throws Exception {
        given(mockSedeService.findById(anyInt()))
                .willReturn(sedeRecord);

        mockMvc.perform(
                get("/api/core/sedes/1")
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());
    }

    @Test
    void getById_not_found() throws Exception {
        given(mockSedeService.findById(anyInt()))
                .willThrow(NotFoundException.class);

        mockMvc.perform(
                get("/api/core/sedes/0")
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isNotFound());
    }

    @Test
    void getById_invalid() throws Exception {
        given(mockSedeService.findById(anyInt()))
                .willThrow(MethodArgumentTypeMismatchException.class);

        mockMvc.perform(
                get("/api/core/sedes/X")
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isBadRequest());
    }

    @Test
    void create_success() throws Exception {
        given(mockSedeService.create(SedeSetUp.createSede(Estado.ACTIVE)))
                .willReturn(sedeRecordResponse);

        mockMvc.perform(
                post("/api/core/sedes")
                        .content(asJsonString(SedeSetUp.createSede(Estado.ACTIVE)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());
    }

    @Test
    void update_success() throws Exception {
        given(mockSedeService.create(SedeSetUp.createSede(Estado.ACTIVE)))
                .willReturn(sedeRecordResponse);

        mockMvc.perform(
                put("/api/core/sedes")
                        .content(asJsonString(SedeSetUp.createSede(Estado.ACTIVE)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());
    }

    @Test
    void update_error() throws Exception {
        given(mockSedeService.update(SedeSetUp.createSede(Estado.ACTIVE)))
                .willThrow(OptimisticLockingFailureException.class);

        mockMvc.perform(
                put("/api/core/sedes")
                        .content(asJsonString(SedeSetUp.createSede(Estado.ACTIVE)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());
    }

    @Test
    void delete_success() throws Exception {
        mockMvc.perform(
                delete("/api/core/sedes/1")
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());
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
}
