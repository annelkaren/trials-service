package mx.gob.pjpuebla.trials.core.estadoCivil;

import mx.gob.pjpuebla.trials.util.Response;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.BDDMockito.given;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(EstadoCivilResource.class)
public class EstadoCivilResourceTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EstadoCivilService estadoCivilService;

    @MockBean
    private SecurityFilterChain securityFilterChain;

    @Test
    public void getAll() throws Exception {
        EstadoCivil estadoCivil = createEstadoCivil();
        Page<EstadoCivil> page = new PageImpl<>(Collections.singletonList(estadoCivil));
        Response response = new Response(page);

        given(estadoCivilService.getAll(any(Pageable.class))).willReturn(response);

        mockMvc.perform(get("/api/core/estadocivil"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content[0].id").value(estadoCivil.getId()))
                .andExpect(jsonPath("$.data.content[0].nombre").value(estadoCivil.getNombre()))
                .andExpect(jsonPath("$.data.content[0].estado").value(estadoCivil.getEstado()));
    }

    private EstadoCivil createEstadoCivil() {
        return EstadoCivil.builder()
                .id(1)
                .version(1)
                .nombre("Casado")
                .estado("A")
                .build();
    }
}
