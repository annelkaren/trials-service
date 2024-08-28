package mx.gob.pjpuebla.trials.core.estadoCivil;


import jakarta.ws.rs.core.MediaType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(EstadoCivilResource.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
public class EstadoCivilResourceTest {

    @MockBean
    private EstadoCivilService mockEstadoCivilService;

    @Autowired
    private MockMvc mockMvc;

    private EstadoCivilRecord validEstadoCivilRecord;

    @BeforeEach
    void setUp() {
        validEstadoCivilRecord = new EstadoCivilRecord(1, "Civil Status");
    }

    @Test
    void getAll_success() throws Exception {
        given(mockEstadoCivilService.getAll(any(Pageable.class), any(EstadoCivil.class)))
                .willReturn(Collections.singletonList(validEstadoCivilRecord));

        mockMvc.perform(
                        MockMvcRequestBuilders.get("/api/core/estadocivil")
                                .param("materiaNombre", "Civil Status")
                                .accept(MediaType.APPLICATION_JSON)
                ).andExpect(status().isOk());
    }
}
