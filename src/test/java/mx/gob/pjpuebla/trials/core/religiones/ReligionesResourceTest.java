package mx.gob.pjpuebla.trials.core.religiones;

import jakarta.ws.rs.core.MediaType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ReligionesResource.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
class ReligionesResourceTest {

    @MockitoBean
    private ReligionesService religionesService;

    @Autowired
    private MockMvc mockMvc;

    private ReligionesRecord religionesRecord;

    @BeforeEach
    void setUp() {
        religionesRecord = new ReligionesRecord(1, "Católica");
    }

    @Test
    void findAllByreligionesAutocomplete_success() throws Exception {
        given(religionesService.findAllByReligionesAutocomplete(any()))
                .willReturn(Collections.singletonList(religionesRecord));

        mockMvc.perform(
                get("/api/core/religiones/autocomplete")
                        .param("key", "Católica")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}
