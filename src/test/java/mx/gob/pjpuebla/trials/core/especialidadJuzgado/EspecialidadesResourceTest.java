package mx.gob.pjpuebla.trials.core.especialidadJuzgado;

import jakarta.ws.rs.core.MediaType;
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

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(EspecialidadesResource.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
class EspecialidadesResourceTest {

    @MockBean
    private EspecialidadesService mockEspecialidadesService;

    @Autowired
    private MockMvc mockMvc;

    private EspecialidadesRecord validEspecialidadesRecord;

    @BeforeEach
    void setUp() {
        validEspecialidadesRecord = EspecialidadesSetUp.createEspecialidadesRecord();
    }

    @Test
    void getAllByNameAndActive_success() throws Exception {
        given(mockEspecialidadesService.getAllActive(any(Pageable.class), any(Especialidades.class)))
                .willReturn(new PageImpl<>(Collections.singletonList(validEspecialidadesRecord)));

        mockMvc.perform(
                get("/api/core/especialidades")
                        .param("especialidadesName", "PE")
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());
    }

    @Test
    void getById_success() throws Exception {
        given(mockEspecialidadesService.findById(anyInt()))
                .willReturn(validEspecialidadesRecord);

        mockMvc.perform(
                get("/api/core/especialidades/1")
                        .param("especialidadesName", "PE")
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());
    }

}