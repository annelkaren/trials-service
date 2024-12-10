package mx.gob.pjpuebla.trials.workflow.notificaciones;

import jakarta.ws.rs.core.MediaType;
import mx.gob.pjpuebla.trials.core.utils.resource.ResourceUtilTest;
import mx.gob.pjpuebla.trials.util.enums.TipoNotificacion;
import mx.gob.pjpuebla.trials.workflow.notificaciones.DTO.NotificacionDto;
import mx.gob.pjpuebla.trials.workflow.notificaciones.records.NotificacionSaveRecord;

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

import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Collections;
import java.util.Date;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;



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

    @Test
    void create_success() throws Exception {
        NotificacionDto notificacionDto = new NotificacionDto();
        notificacionDto.setCalle("Av. Principal");
        notificacionDto.setCiudad("Puebla");
        notificacionDto.setCodigoPostal("72000");
        notificacionDto.setColonia("Centro");
        notificacionDto.setCorreo("usuario@correo.com");
        notificacionDto.setEstadoRepublica("Puebla");
        notificacionDto.setExterior("123");
        notificacionDto.setIdDomicilio(1L);
        notificacionDto.setInterior("A");
        notificacionDto.setLatitud("19.0413");
        notificacionDto.setLongitud("-98.2062");
        notificacionDto.setMetodo(TipoNotificacion.CORREO_ELECTRONICO);
        notificacionDto.setMunicipio("Puebla");
        notificacionDto.setPersonId(101);
        notificacionDto.setUsarCorreoRegistrado(true);

        doNothing().when(notificacionService).create(any(NotificacionDto.class));
       
        mockMvc.perform(post("/api/workflow/notificaciones/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(notificacionDto)))
                .andExpect(status().isOk());
    }

    @Test
    void create_notificacion_acuerdo() throws Exception {
        NotificacionSaveRecord notificacion = NotificacionSetUp.createNotificacionSaveRecord();

        mockMvc.perform(
            post("/api/workflow/documentos/enviarNotificacion")
                .contentType(MediaType.APPLICATION_JSON)
                .content(ResourceUtilTest.asJsonString(notificacion)))
                .andExpect(status().isOk());
    }

}