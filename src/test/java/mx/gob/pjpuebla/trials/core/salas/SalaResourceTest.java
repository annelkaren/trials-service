package mx.gob.pjpuebla.trials.core.salas;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import mx.gob.pjpuebla.trials.error.NotFoundException;
import mx.gob.pjpuebla.trials.error.OptimisticLockingFailureException;

import mx.gob.pjpuebla.trials.util.Estado;

import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

@WebMvcTest(SalaResource.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
public class SalaResourceTest {
    
    @MockBean
    private SalaService mockSalaService;

    @Autowired
    private MockMvc mockMvc;

    private SalaRecord salaRecord;
    private SalaRecordResponse salaRecordResponse;

    @BeforeEach
    void setUp(){
        salaRecordResponse = SalaSetUp.salaRecordResponse();
        salaRecord = SalaSetUp.salaRecord();
    }

    @Test
    void getAllByNameAndActive_success() throws Exception {
        given(mockSalaService.getAll(any(Sala.class), any(Pageable.class)))
        .willReturn(new PageImpl<>(Collections.singletonList(salaRecord)));

        mockMvc.perform(
            get("/api/core/salas")
                .param("nombre", "1")
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());
    }

    @Test
    void getById_success() throws Exception {
        given(mockSalaService.findById(anyInt()))
            .willReturn(salaRecordResponse);
    }

    @Test 
    void getById_not_found() throws Exception {
        given(mockSalaService.findById(anyInt()))
            .willThrow(NotFoundException.class);

        mockMvc.perform(
            get("/api/core/salas/0")
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isNotFound());
    }

    @Test
    void getById_invalid() throws Exception {
        given(mockSalaService.findById(anyInt()))
        .willThrow(MethodArgumentTypeMismatchException.class);
        
        mockMvc.perform(
            get("/api/core/salas/Y")
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isBadRequest());
    }

    @Test
    void create_success() throws Exception {
        Integer expectedId = 1;
        given(mockSalaService.create(SalaSetUp.createSala(Estado.ACTIVE)))
        .willReturn(expectedId);

        mockMvc.perform(
            post("/api/core/salas")
                .content(asJsonString(SalaSetUp.createSala(Estado.ACTIVE)))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());
    }

    @Test
    void update_success() throws Exception {
        Integer expectedId = 1;
        given(mockSalaService.create(SalaSetUp.createSala(Estado.ACTIVE)))
            .willReturn(expectedId);

        mockMvc.perform(
            put("/api/core/salas")
                .content(asJsonString(SalaSetUp.createSala(Estado.ACTIVE)))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)   
        ).andExpect(status().isOk());
    }

    @Test
    void update_error() throws Exception {
        given(mockSalaService.update(SalaSetUp.createSala(Estado.ACTIVE)))
            .willThrow(OptimisticLockingFailureException.class);

        mockMvc.perform(
            put("/api/core/salas")
                .content(asJsonString(SalaSetUp.createSala(Estado.ACTIVE)))
                .contentType(MediaType.APPLICATION_JSON)
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

    /*
     * 
    @PutMapping
    public Integer update(@RequestBody  Sala sala) {
        System.out.println(sala);
        return this.salaService.update(sala);
    }
     */

    


}
