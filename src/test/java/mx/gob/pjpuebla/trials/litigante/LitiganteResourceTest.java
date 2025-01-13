package mx.gob.pjpuebla.trials.litigante;

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

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(LitiganteResource.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
class LitiganteResourceTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private LitiganteService litiganteService;

    @Test
    void getExpedientesRelacionados() throws Exception {
        LitiganteExpedientesRecord litiganteExpedientesRecord = new LitiganteExpedientesRecord(
                100, "000001/2025", "MERCANTIL", "Mercantil (Tradicional)",
                "", "", "Juzgado 5 Mercantil TEST", 0L);
        given(litiganteService.getExpedientesRelacionados(any(Pageable.class)))
                .willReturn(new PageImpl<>(Collections.singletonList(litiganteExpedientesRecord)));

        mockMvc.perform(
                get("/api/litigante/expedientes")
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());
    }

}
