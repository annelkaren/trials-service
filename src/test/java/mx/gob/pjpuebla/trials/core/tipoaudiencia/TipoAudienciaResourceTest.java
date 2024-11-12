package mx.gob.pjpuebla.trials.core.tipoaudiencia;

import jakarta.ws.rs.core.MediaType;
import mx.gob.pjpuebla.trials.error.NotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TipoAudienciaResource.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
class TipoAudienciaResourceTest {

    @MockBean
    private TipoAudienciaService mocktipoAudienciaService;

    @Autowired
    private MockMvc mockMvc;

    private TipoAudienciaRecord tipoAudienciaRecord;

    void setUp() {
        tipoAudienciaRecord = TipoAudienciaSetUp.createTipoAudienciaRecord();
    }

    @Test
    void getAll_success() throws Exception {
        given(mocktipoAudienciaService.getAll(any(Pageable.class)))
                .willReturn(new PageImpl<>(Collections.singletonList(tipoAudienciaRecord)));

        mockMvc.perform(
                get("/api/core/tipoaudiencia")
                        .param("nombre", "T")
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());
    }

    @Test
    void getById_success() throws Exception {
        given(mocktipoAudienciaService.findById(anyInt()))
                .willReturn(tipoAudienciaRecord);

        mockMvc.perform(
                get("/api/core/tipoaudiencia/1")
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());
    }

    @Test
    void getById_not_found() throws Exception {
        given(mocktipoAudienciaService.findById(anyInt()))
                .willThrow(NotFoundException.class);

        mockMvc.perform(
                get("/api/core/tipoaudiencia/0")
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isNotFound());
    }

    @Test
    void getById_invalid() throws Exception {
        given(mocktipoAudienciaService.findById(anyInt()))
                .willThrow(MethodArgumentTypeMismatchException.class);

        mockMvc.perform(
                get("/api/core/tipoaudiencia/X")
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isBadRequest());
    }

    @Test
    void findDocumentoById_success() throws Exception {
        given(mocktipoAudienciaService.findTipoAudienciaByDocumentoId(51, PageRequest.of(0, 10), "any"))
                .willReturn(new PageImpl<>(Collections.singletonList(tipoAudienciaRecord)));

        mockMvc.perform(
                get("/api/core/tipoaudiencia/autocomplete/51")
                        .param("page", "0")
                        .param("size", "10")
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());
    }

}