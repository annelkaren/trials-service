package mx.gob.pjpuebla.trials.core.tipopartes;

import jakarta.ws.rs.core.MediaType;
import mx.gob.pjpuebla.trials.error.NotFoundException;
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


import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TipoPartesResource.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
class TipoPartesResourceTest {

    @MockBean
    private TipoPartesService mockTipoPartesService;

    @Autowired
    private MockMvc mockMvc;

    private TipoPartesRecord validTipoPartesRecord;

    @BeforeEach
    void setUp() {
        validTipoPartesRecord = TipoPartesSetUp.createTipoPartesRecord();
    }

    @Test
    void getAllByNameAndActive_success() throws Exception {
        given(mockTipoPartesService.getAll(any(Pageable.class), any(TipoPartes.class)))
                .willReturn(new PageImpl<>(Collections.singletonList(validTipoPartesRecord)));

        mockMvc.perform(
                get("/api/core/tipopartes")
                        .param("tipoPartesName", "PE")
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());
    }

    @org.junit.jupiter.api.Test
    void getById_success() throws Exception {
        given(mockTipoPartesService.findById(anyInt()))
                .willReturn(validTipoPartesRecord);

        mockMvc.perform(
                get("/api/core/tipopartes/1")
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());
    }

    @org.junit.jupiter.api.Test
    void getById_not_found() throws Exception {
        given(mockTipoPartesService.findById(anyInt()))
                .willThrow(NotFoundException.class);

        mockMvc.perform(
                get("/api/core/tipopartes/0")
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isNotFound());
    }

    @Test
    void getById_invalid() throws Exception {
        given(mockTipoPartesService.findById(anyInt()))
                .willThrow(MethodArgumentTypeMismatchException.class);

        mockMvc.perform(
                get("/api/core/tipopartes/A")
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isBadRequest());
    }

    @Test
    void getByTipoJuicioId_success() throws Exception {
        List<TipoPartesRecord> list = Arrays.asList(validTipoPartesRecord);
        given(mockTipoPartesService.findByTipoJuicioId(anyInt()))
                .willReturn(list);

        mockMvc.perform(
                get("/api/core/tipopartes/tipojuicio/1")
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());
    }
}
