package mx.gob.pjpuebla.trials.core.tipopartes;

import mx.gob.pjpuebla.trials.core.listavalor.ListaValorResource;
import mx.gob.pjpuebla.trials.util.Response;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;


import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@RunWith(SpringRunner.class)
@WebMvcTest(ListaValorResource.class)
@MockBean(SecurityFilterChain.class)
public class TipoPartesResourceTest {

    @Autowired
    public MockMvc mockMvc;

    @MockBean
    TipoPartesService tipopartesService;

    @Test
    public void getById() throws Exception {
        Response response = new Response((TipoPartes) createTipoPartes());
        given(tipopartesService.findById(anyInt())).willReturn(response);

        TipoPartes entity = (TipoPartes) response.getData();
        mockMvc.perform(get("/api/core/tipopartes/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(entity.getId()))
                .andExpect(jsonPath("$.data.nombre").value(entity.getNombre()));
    }

    private TipoPartes createTipoPartes() {
        return new TipoPartes()
                .setEstado(                     "A")
                .setNombre(RandomStringUtils.random(5, true, true));
    }
}
