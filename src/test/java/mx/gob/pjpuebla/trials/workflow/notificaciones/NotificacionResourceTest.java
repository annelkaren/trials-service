package mx.gob.pjpuebla.trials.workflow.notificaciones;


import mx.gob.pjpuebla.trials.core.utils.resource.ResourceUtilTest;
import mx.gob.pjpuebla.trials.util.enums.TipoDocumento;
import mx.gob.pjpuebla.trials.util.enums.TipoNotificacion;
import mx.gob.pjpuebla.trials.workflow.notificaciones.records.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(NotificacionResource.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
class NotificacionResourceTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private NotificacionService notificacionService;

    private NotificacionRecord notificacionRecord;

    @MockBean
    private ListadoExpedientesRutaService listadoExpedientesRutaService;

    @BeforeEach
    void setUp() {
        notificacionRecord = NotificacionSetUp.createNotificacionRecord();
    }


    @Test
    void getAll_success() throws Exception {
        LocalDate localDate = LocalDate.now();
        DocumentoDetalleRecord documentoDetalleRecord = new DocumentoDetalleRecord(
                localDate.minusDays(10),
                localDate.minusDays(5)
        );
        List<String> concepto = Collections.singletonList("Audiencia");

        notificacionRecord = new NotificacionRecord(
                1,
                "000001/2024",
                concepto,
                "Notas Audiencia",
                TipoNotificacion.ESTRADO,
                documentoDetalleRecord,
                TipoDocumento.ACUERDO,
                1,
                1,
                LocalDateTime.now(),
                LocalDateTime.now(),
                "Domicilio 1",
                "",
                null
        );

        when(notificacionService.getAllNotificaciones(anyString(), anyString(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(Collections.singletonList(notificacionRecord)));

        mockMvc.perform(
                        get("/api/workflow/bandeja/notificaciones")
                                .param("key", "")
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void create_nota() throws Exception {
        doNothing().when(notificacionService).createNotaNotificacion(any(Integer.class), anyString());
        NotaResponse notaResponse = new NotaResponse(1, "nota nueva");
        String requestBody = new ObjectMapper().writeValueAsString(notaResponse);

        mockMvc.perform(post("/api/workflow/bandeja/notificaciones/createNota")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk());
    }

    @Test
    void createListaEstrado_success() throws Exception {

        doNothing().when(notificacionService).createListaEstrado(any(List.class), any(Date.class));
        ListaResponse listaResponse = new ListaResponse(List.of(1, 2, 3),
                Date.from(LocalDate.now().plusDays(5).atStartOfDay(ZoneId.systemDefault()).toInstant()));
        String requestBody = new ObjectMapper().writeValueAsString(listaResponse);


        mockMvc.perform(post("/api/workflow/bandeja/notificaciones/createLista")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
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

    @Test
    void getAllNotificaciones() throws Exception {
        NotificacionDetalleRecord notificacion = new NotificacionDetalleRecord(
                "Actor", "Juan Perez", "Domicilio 1"
        );

        when(notificacionService.getNotificacionDetalle(anyInt()))
                .thenReturn(notificacion);

        mockMvc.perform(
                        get("/api/workflow/bandeja/notificaciones/detalle/" + 1)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void updateBatchNotificacionSalida() throws Exception {
        mockMvc.perform(
                        patch("/api/workflow/bandeja/notificaciones/EN_RUTA")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(ResourceUtilTest.asJsonString(Collections.singletonList(1))))
                .andExpect(status().isOk());
    }


    @Test
    void testGetFileListaExpedientes() throws Exception {
        // Simulación de un PDF generado por el servicio
        byte[] mockPdf = "mock-pdf-content".getBytes();

        // Configuramos el mock para que retorne el contenido del PDF
        when(listadoExpedientesRutaService.exportToPdf()).thenReturn(mockPdf);

        // Simulamos la petición al endpoint y verificamos la respuesta
        mockMvc.perform(MockMvcRequestBuilders.get("/api/workflow/notificaciones/reporteListaExpedientes"))
                .andExpect(status().isOk());
    }

    @Test
    void acuerdoNotificaciones_success() throws Exception {
        PageImpl<AcuerdoNotificacionesRecord> page = new PageImpl<>(Collections.singletonList(NotificacionSetUp.acuerdoNotificacionesRecord()));

        when(notificacionService.acuerdoNotificaciones(eq(1), any(Pageable.class)))
                .thenReturn(page);
        mockMvc.perform(get("/api/workflow/acuerdo/notificaciones/{idNotificacion}", 1)
                        .param("page", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void digitalizarActaDomicilio_ShouldReturnOk() throws Exception {
        // Preparar el objeto NotificacionActaRecord
        NotificacionActaRecord notificacionActaRecord = new NotificacionActaRecord(1, "", "", "", "");
        String notificacionActaJson = new ObjectMapper().writeValueAsString(notificacionActaRecord);

        // Crear un archivo de prueba
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.pdf",
                MediaType.APPLICATION_PDF_VALUE,
                "contenido del archivo de prueba".getBytes()
        );

        // Crear el multipart para el JSON
        MockMultipartFile notificacionActaRequestPart = new MockMultipartFile(
                "notificacionActaJson",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                notificacionActaJson.getBytes()
        );

        // Configurar el servicio mock para simular la digitalización
        doNothing().when(notificacionService).digitalizarActaDomicilio(
                Mockito.any(NotificacionActaRecord.class),
                Mockito.any()
        );

        // Ejecutar la solicitud multipart
        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/workflow/bandeja/notificaciones")
                        .file(notificacionActaRequestPart)
                        .file(file)
                        .contentType(MediaType.MULTIPART_FORM_DATA_VALUE))
                .andExpect(status().isOk());
    }

    @Test
    void getFile_ShouldReturnPdfFile() throws Exception {
        Integer notificacionId = 1;

        byte[] pdfContent = "Contenido del PDF".getBytes();

        when(notificacionService.getActaDocumento(notificacionId)).thenReturn(pdfContent);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/workflow/bandeja/notificaciones/{notificacionId}/file", notificacionId))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void getNotificacionesTurnado_success() throws Exception {

        List<Integer> carpetaIdsSinNotificaciones = Arrays.asList(1, 3);
        when(notificacionService.notificacionesTurnado(anyList()))
                .thenReturn(carpetaIdsSinNotificaciones);
        mockMvc.perform(
                        MockMvcRequestBuilders.get("/api/workflow/bandeja/notificaciones/detalle/turnado")
                                .param("carpetaIds", "1,2,3")
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0]").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1]").value(3));
    }

}