package mx.gob.pjpuebla.trials.core.lenguasindigenas;

import jakarta.ws.rs.core.MediaType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(LenguaIndigenaResource.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
class LenguaIndigenaResourceTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private LenguaIndigenaService lenguaIndigenaService;
    private LenguaIndigenaRecord lenguaIndigenaRecord;

    @BeforeEach
    public void setUp() {
        lenguaIndigenaRecord = new LenguaIndigenaRecord(1, "Kaqchikel");
    }

    @Test
    void getAll() throws Exception {
        given(lenguaIndigenaService.getAll(any())).willReturn(Arrays.asList(lenguaIndigenaRecord));

        mockMvc.perform(
                get("/api/core/lenguasindigenas")
                        .param("key", "a")
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());
    }
}
