package mx.gob.pjpuebla.trials.core.oficialias;

import jakarta.ws.rs.core.MediaType;
import mx.gob.pjpuebla.trials.core.materias.*;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MateriaResource.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
class OficialiaResourceTest {

    @MockBean
    private MateriaService mockMateriaService;

    @Autowired
    private MockMvc mockMvc;

    private MateriaRecord validMateriaRecord;

    @BeforeEach
    void setUp() {
        validMateriaRecord = MateriaSetUp.createMateriaRecord();
    }

    @Test
    void getAllByNameAndActive_success() throws Exception {
        given(mockMateriaService.getAllActive(any(Pageable.class), any(Materia.class)))
                .willReturn(new PageImpl<>(Collections.singletonList(validMateriaRecord)));

        mockMvc.perform(
                get("/api/core/materias")
                        .param("materiaName", "PE")
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());
    }
}