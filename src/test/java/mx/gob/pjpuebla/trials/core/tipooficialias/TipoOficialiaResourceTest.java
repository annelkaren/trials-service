package mx.gob.pjpuebla.trials.core.tipooficialias;


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

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(TipoOficialiaResource.class)
public class TipoOficialiaResourceTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TipoOficialiaService tipoOficialiaService;

    @MockBean
    private SecurityFilterChain securityFilterChain;

    @Test
    public void getAll() throws Exception {
        TipoOficialias tipoOficialias = createTipoOficialias();
        Page<TipoOficialias> page = new PageImpl<>(Collections.singletonList(tipoOficialias));
        Response response = new Response(page);

        given(tipoOficialiaService.getAll(any(Pageable.class))).willReturn(response);

        mockMvc.perform(get("/api/core/tipooficialia"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content[0].id").value(tipoOficialias.getId()))
                .andExpect(jsonPath("$.data.content[0].nombre").value(tipoOficialias.getNombre()))
                .andExpect(jsonPath("$.data.content[0].estado").value(tipoOficialias.getEstado()));

    }

    private TipoOficialias createTipoOficialias(){
        return  TipoOficialias.builder()
                .id(1)
                .version(1)
                .nombre("Común")
                .estado("A")
                .build();
    }

}