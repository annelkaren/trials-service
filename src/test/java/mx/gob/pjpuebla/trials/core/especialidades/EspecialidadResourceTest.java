package mx.gob.pjpuebla.trials.core.especialidades;

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


@WebMvcTest(EspecialidadResource.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
class EspecialidadResourceTest {

    @MockBean
    private EspecialidadService mockEspecialidadService;

    @Autowired
    private MockMvc mockMvc;

    private EspecialidadRecord validEspecialidadRecord;

    @BeforeEach
    void setUp() {
        validEspecialidadRecord = EspecialidadSetUp.createEspecialidadRecord();
    }

    @Test
    void getAllByNameAndActive_success() throws Exception {
        given(mockEspecialidadService.getAllActive(any(Pageable.class), any(Especialidad.class)))
                .willReturn(new PageImpl<>(Collections.singletonList(validEspecialidadRecord)));

        mockMvc.perform(
                get("/api/core/especialidades")
                        .param("especialidadName", "PE")
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());
    }

    @Test
    void getById_success() throws Exception {
        given(mockEspecialidadService.findById(anyInt()))
                .willReturn(validEspecialidadRecord);

        mockMvc.perform(
                get("/api/core/especialidades/1")
                        .param("especialidadName", "PE")
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());
    }

}