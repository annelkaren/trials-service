package mx.gob.pjpuebla.trials.core.listavalor;

import mx.gob.pjpuebla.trials.util.Response;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ListaValorResource.class)
@MockBean(SecurityFilterChain.class)
class ListaValorResourceTest {

    @Autowired
    public MockMvc mockMvc;

    @MockBean
    ListaValorService listaValorService;

    @Test
    public void getById() throws Exception {
        Response response = new Response((ListaValor) createListaValor());
        given(listaValorService.findById(anyInt())).willReturn(response);

        ListaValor entity = (ListaValor) response.getData();
        mockMvc.perform(get("/api/core/listavalor/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(entity.getId()))
                .andExpect(jsonPath("$.data.nombre").value(entity.getNombre()));
    }

    private ListaValor createListaValor() {
        return ListaValor.builder()
                .id(1)
                .estado("A")
                .nombre(RandomStringUtils.random(5, true, true))
                .build();
    }
}
