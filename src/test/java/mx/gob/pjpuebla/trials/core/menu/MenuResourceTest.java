package mx.gob.pjpuebla.trials.core.menu;

import jakarta.ws.rs.core.MediaType;
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

import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MenuResource.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
class MenuResourceTest {

    @MockBean
    private MenuService menuService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void get_menu_by_user() throws Exception {
        MenuNode node = new MenuNode(1, "Catálogo", "");
        given(menuService.getMenuByUser()).willReturn(List.of(node));

        mockMvc.perform(
                get("/api/core/menu")
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());
    }
}
