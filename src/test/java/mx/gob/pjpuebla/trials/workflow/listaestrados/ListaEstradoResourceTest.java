package mx.gob.pjpuebla.trials.workflow.listaestrados;

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
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(ListaEstradoResource.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
class ListaEstradoResourceTest {

    @MockBean
    private ListaEstradoService mocklistaEstradoService;

    @Autowired
    private MockMvc mockMvc;

    private ListaEstrado listaEstradoA;

    @BeforeEach
    void setUp() {
        listaEstradoA = ListaEstradoSetUp.createLisEstrado();
    }

    @Test
    void getAll_success() throws Exception {
        given(mocklistaEstradoService.findAllByListaEstradoId(any(), any(), any(Pageable.class)))
                .willReturn(new PageImpl<>(Collections.singletonList(listaEstradoA)));

        mockMvc.perform(get("/api/workflow/listaestrado")
                        .param("searchQuery", "a")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()) ;
     }

}