package mx.gob.pjpuebla.trials.workflow.notificaciones;

import jakarta.ws.rs.core.MediaType;
import mx.gob.pjpuebla.trials.util.enums.TipoNotificacion;
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


import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Collections;
import java.util.Date;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;


@WebMvcTest(NotificacionResource.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
class NotificacionResourceTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private NotificacionService notificacionService;

    private NotificacionRecord notificacionRecord;

    @BeforeEach
    void setUp() {
         notificacionRecord = NotificacionSetUp.createNotificacionRecord();
    }


    @Test
    void getAll_success() throws Exception {
        LocalDate localDate = LocalDate.now();
        Date fecha = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        notificacionRecord = new NotificacionRecord("000001/2024", "Audiencia", "Notas Audiencia", TipoNotificacion.ESTRADO, fecha, fecha);

        when(notificacionService.getAllNotificaciones(anyString(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(Collections.singletonList(notificacionRecord)));

        mockMvc.perform(
                        get("/api/workflow/bandeja/notificaciones")
                                .param("key", "")
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

}