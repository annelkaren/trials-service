package mx.gob.pjpuebla.trials.core.distritos;

import jakarta.ws.rs.core.MediaType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DistritoResource.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
class DistritoResourceTest {

    @MockitoBean
    private DistritoService mockDistritoService;

    @Autowired
    private MockMvc mockMvc;

    private DistritoRecord distrito;

    @BeforeEach
    void setUp() {
        distrito = DistritoSetUp.createDistritoRecord();
    }

    @Test
    void getAllByActive_success() throws Exception {
        given(mockDistritoService.getAllActive(any(Pageable.class)))
                .willReturn(new PageImpl<>(Collections.singletonList(distrito)));

        mockMvc.perform(
                get("/api/core/distritos")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}
