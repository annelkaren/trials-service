package mx.gob.pjpuebla.trials.core.menu;

import jakarta.ws.rs.core.MediaType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MenuResource.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
class MenuResourceTest {

    @MockitoBean
    private MenuService menuService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void get_menu_by_user() throws Exception {
        MenuNode node = new MenuNode(1, "Catálogo", "");
        given(menuService.getMenuByUser()).willReturn(List.of(node));

        mockMvc.perform(
                get("/api/core/menu")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}
