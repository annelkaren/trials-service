package mx.gob.pjpuebla.trials.core.tiposistema;

import mx.gob.pjpuebla.trials.core.tipooficialias.TipoOficialiaResource;
import mx.gob.pjpuebla.trials.util.Response;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(TipoSistemaResource.class)
public class TipoSistemaResourceTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TipoSistemaService tipoSistemaService;

    @MockBean
    private SecurityFilterChain securityFilterChain;

    @Test
    public void getAll() throws Exception {
        TipoSistema tipoSistema = createTipoSistema();
        List<TipoSistema> TipoSistemas = Collections.singletonList(tipoSistema);

        given(tipoSistemaService.getAll(any(Pageable.class))).willReturn(TipoSistemas);

        mockMvc.perform(get("/api/core/tiposistema"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(tipoSistema.getId()))
                .andExpect(jsonPath("$[0].nombre").value(tipoSistema.getNombre()))
                .andExpect(jsonPath("$[0].estado").value(tipoSistema.getEstado()));

    }

    private TipoSistema  createTipoSistema(){
        return TipoSistema.builder()
                .id(1)
                .version(1)
                .nombre("Mixto")
                .estado("A")
                .build();
    }
}