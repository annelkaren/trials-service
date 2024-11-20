package mx.gob.pjpuebla.trials.workflow.documentos;

import jakarta.ws.rs.core.MediaType;
import mx.gob.pjpuebla.trials.core.tipojuicio.TipoJuicio;
import mx.gob.pjpuebla.trials.core.utils.resource.ResourceUtilTest;
import mx.gob.pjpuebla.trials.util.enums.*;
import mx.gob.pjpuebla.trials.workflow.anexos.AnexoRecord;
import mx.gob.pjpuebla.trials.workflow.carpeta.CarpetaSetUp;
import mx.gob.pjpuebla.trials.workflow.documentos.amparos.AmparoRecordResponse;
import mx.gob.pjpuebla.trials.workflow.documentos.records.*;
import mx.gob.pjpuebla.trials.workflow.sello.AcuerdoService;
import mx.gob.pjpuebla.trials.workflow.sello.OficioService;
import mx.gob.pjpuebla.trials.workflow.sello.SelloCaratulaService;
import mx.gob.pjpuebla.trials.workflow.sello.SelloGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_PDF;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DocumentoResource.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
class DocumentoResourceTest {

    @MockBean
    private DocumentoService documentoService;

    @MockBean
    private SelloGenerator selloGenerator;

    @MockBean
    private SelloCaratulaService caratulaGenerator;

    @MockBean
    private DigitalizacionService digitalizacion2Service;

    @MockBean
    private OficioService oficioService;

    @MockBean
    private AcuerdoService acuerdoService;

    @Autowired
    private MockMvc mockMvc;


    @Value("classpath:jasper/OficioCarta.jasper")
    private Resource oficioCarta;


    @Test
    void create_demanda() throws Exception {
        Documento demanda = DocumentoSetUp.create(new TipoJuicio().setId(1));
        demanda.getCarpeta().setTipoCarpeta(TipoCarpeta.DEMANDA);
        demanda.getCarpeta().setFolio("1");
        DocumentoRecord documentoRecord = new DocumentoRecord(1, demanda.getCarpeta().getFolio(),
                TipoCarpeta.DEMANDA);

        given(documentoService.createDemanda(any(DocumentoSaveRecord.class)))
                .willReturn(documentoRecord);

        mockMvc.perform(
                        post("/api/workflow/demanda")
                                .content(ResourceUtilTest.asJsonString(documentoRecord))
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void getAll() throws Exception {
        Documento demanda = DocumentoSetUp.create(new TipoJuicio().setId(1));
        demanda.getCarpeta().setTipoCarpeta(TipoCarpeta.DEMANDA);
        demanda.getCarpeta().setFolio("1");
        DocumentoGridRecord documentoGridRecord = new DocumentoGridRecord(1, demanda.getCarpeta().getFolio(),
                demanda.getCarpeta().getExpediente(),
                "Laboral", TipoCarpeta.DEMANDA.name(), LocalDateTime.now(), SelloEstatus.VALIDO,
                EstadoCarpeta.CAPTURA,
                true);

        given(documentoService.getAll(any(), any(Pageable.class)))
                .willReturn(new PageImpl<>(Collections.singletonList(documentoGridRecord)));

        mockMvc.perform(
                        get("/api/workflow/bandeja/entrada")
                                .content(ResourceUtilTest.asJsonString(documentoGridRecord))
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void update_status() throws Exception {
        Documento demanda = DocumentoSetUp.create(new TipoJuicio().setId(1));
        demanda.getCarpeta().setTipoCarpeta(TipoCarpeta.DEMANDA);
        demanda.getCarpeta().setFolio("1");
        DocumentoRecord documentoRecord = new DocumentoRecord(demanda.getId(), demanda.getCarpeta().getFolio(),
                TipoCarpeta.DEMANDA);

        given(documentoService.updateStatus(demanda.getId(), 1))
                .willReturn(documentoRecord);

        mockMvc.perform(
                        patch("/api/workflow/bandeja/1/status/1")
                                .content(ResourceUtilTest.asJsonString(documentoRecord))
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void edit_anexos() throws Exception {

        AnexoRecord anexoRecord = new AnexoRecord(
                Arrays.asList("Anexo1", "Anexo2"),
                "Motivo de edición");

        DocumentoRecord documentoRecord = new DocumentoRecord(1, "000001/2024", TipoCarpeta.DEMANDA);

        given(documentoService.editarAnexos(any(Integer.class), any(), any()))
                .willReturn(documentoRecord);

        mockMvc.perform(
                        patch("/api/workflow/demanda/1/anexos")
                                .content(ResourceUtilTest.asJsonString(anexoRecord))
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void getEditDocumento() throws Exception {

        List<String> anexos = List.of("Anexo1", "Anexo2");

        DocumentoRecord documentRecord = new DocumentoRecord(1, "000005/2024", TipoCarpeta.DEMANDA);

        given(documentoService.editarAnexos(1, anexos, "ejemplo"))
                .willReturn(documentRecord);

        mockMvc.perform(
                        get("/api/workflow/demanda/1")
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void createPromocion() throws Exception {
        List<String> anexos = List.of("Anexo1", "Anexo2");
        DocumentoPromocionRecord documentoPromocionRecord = new DocumentoPromocionRecord(1,
                TipoPromocion.OFICIO, anexos);
        DocumentoPromocionResponseRecord documentoPromocionResponseRecord = new DocumentoPromocionResponseRecord(
                1, "1", TipoDocumento.PROMOCION);

        given(documentoService.createPromocion(any()))
                .willReturn(documentoPromocionResponseRecord);

        mockMvc.perform(
                        post("/api/workflow/documento/promocion")
                                .content(ResourceUtilTest.asJsonString(documentoPromocionRecord))
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

    }

    @Test
    void create_exhorto() throws Exception {
        Documento demanda = DocumentoSetUp.create(new TipoJuicio().setId(1));
        demanda.getCarpeta().setTipoCarpeta(TipoCarpeta.EXHORTO);
        demanda.getCarpeta().setFolio("1");
        DocumentoRecord documentoRecord = new DocumentoRecord(1, demanda.getCarpeta().getFolio(),
                TipoCarpeta.EXHORTO);

        given(documentoService.createExhorto(any(DocumentoExhortoRecord.class)))
                .willReturn(documentoRecord);

        mockMvc.perform(
                        post("/api/workflow/exhorto")
                                .content(ResourceUtilTest.asJsonString(documentoRecord))
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void getAllHistorial() throws Exception {

        String folio = "45";
        String expediente = "000001/2024";
        EstadoCarpeta estatus = EstadoCarpeta.CAPTURA;
        String tipoEntrada = "DEMANDA";
        String materiaNombre = "MERCANTIL";

        DocumentoGridRecord documentoGridRecord = new DocumentoGridRecord(1, folio, expediente,
                materiaNombre, tipoEntrada, LocalDateTime.now(), SelloEstatus.VALIDO, estatus, true);

        given(documentoService.getAllHistorial(any(String.class), any(Pageable.class)))
                .willReturn(new PageImpl<>(Collections.singletonList(documentoGridRecord)));

        mockMvc.perform(
                        get("/api/workflow/bandeja/historial")
                                .param("folio", folio)
                                .param("expediente", expediente)
                                .param("estatus", estatus.name())
                                .param("tipoEntrada", tipoEntrada)
                                .param("materiaNombre", materiaNombre)
                                .accept(MediaType.APPLICATION_JSON))

                .andExpect(status().isOk());

    }

    @Test
    void create_apelacion_success() throws Exception {
        DocumentoRecord documentoRecord = new DocumentoRecord(1, "", TipoCarpeta.APELACION);

        given(documentoService.createApelacion(CarpetaSetUp.apelacionRecord()))
                .willReturn(documentoRecord);
        mockMvc.perform(
                        post("/api/workflow/apelacion")
                                .content(ResourceUtilTest.asJsonString(documentoRecord))
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void getAll_bandeja_salida_success() throws Exception {
        DocumentoSalidaResponseRecord documentoRecord = new DocumentoSalidaResponseRecord(
                1,
                1,
                "1",
                "000001/2024",
                1,
                "Juzgado Primero",
                "LABORAL",
                "DEMANDA",
                LocalDateTime.now(),
                SelloEstatus.VALIDO,
                EstadoCarpeta.TURNADO);

        given(documentoService.getAllBandejaSalida(any(String.class), any(PageRequest.class)))
                .willReturn(new PageImpl<>(Collections.singletonList(documentoRecord)));
        mockMvc.perform(
                        get("/api/workflow/bandeja/salida")
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void getOficioAdministrativo() throws Exception {
        Integer institucionId = 1;
        LocalDate fechaEmision = LocalDate.now();
        String asunto = "Prueba de asunto jurisdiccional";
        Integer carpetaId = null;
        Integer folio = 1;
        Map<String, Object> data = new HashMap<>();
        data.put("institucionId", institucionId);
        data.put("fechaEmision", fechaEmision.toString());
        data.put("asunto", asunto);
        data.put("carpetaId", carpetaId);
        given(documentoService.createOficio(institucionId, fechaEmision, asunto, carpetaId))
                .willReturn(folio);

        mockMvc.perform(
                        post("/api/workflow/oficio")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(ResourceUtilTest.asJsonString(data))
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void getOficioJurisdiccional() throws Exception {
        Integer institucionId = 1;
        LocalDate fechaEmision = LocalDate.now();
        String asunto = "Prueba de asunto administrativo";
        Integer carpetaId = 1;
        Integer folio = 1;

        Map<String, Object> data = new HashMap<>();
        data.put("institucionId", institucionId);
        data.put("fechaEmision", fechaEmision.toString());
        data.put("asunto", asunto);
        data.put("carpetaId", carpetaId);
        String jsonContent = ResourceUtilTest.asJsonString(data);
        System.out.println(jsonContent);
        given(documentoService.createOficio(institucionId, fechaEmision, asunto, carpetaId))
                .willReturn(folio);

        mockMvc.perform(
                        post("/api/workflow/oficio")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(ResourceUtilTest.asJsonString(data))
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void send_to_bandeja_recepcion_success() throws Exception {
        SalidaSentToRecepcionRecord docInputRecord = new SalidaSentToRecepcionRecord(List.of(1, 2, 3), 1);
        mockMvc.perform(
                post("/api/workflow/bandeja/salida")
                        .content(ResourceUtilTest.asJsonString(docInputRecord))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());
    }

    @Test
    void getIndicadores_success() throws Exception {
        IndicadoresRecord indicadoresRecord = DocumentoSetUp.createIndicadoresRecord();

        when(documentoService.getIndicadores()).thenReturn(indicadoresRecord);
        mockMvc.perform(
                        get("/api/workflow/documentos/indicadores?isRecepcion=true")
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void get_oficio_pdf() throws Exception {
        Integer oficioId = 89734;
        byte[] mockPdf = new byte[]{1, 2, 3};

        given(oficioService.getOficio(oficioId)).willReturn(mockPdf);

        mockMvc.perform(get("/api/workflow/documentos/oficio/{oficioId}", oficioId)
                        .accept(APPLICATION_PDF))
                .andExpect(status().isOk());
    }

    @Test
    void getAllAsignados() throws Exception {

        String folio = "1";
        String expediente = "000001/2024";

        DocumentoAsignadoResponseRecord documentoRecord = new DocumentoAsignadoResponseRecord(1, 1, expediente, folio, expediente, expediente, LocalDateTime.now(), LocalDateTime.now(), folio, expediente);

        given(documentoService.getAllAsignado(anyString(), any(Pageable.class)))
                .willReturn(new PageImpl<>(Collections.singletonList(documentoRecord)));

        mockMvc.perform(
                        get("/api/workflow/bandeja/asignados")
                                .accept(MediaType.APPLICATION_JSON))

                .andExpect(status().isOk());

    }

    @Test
    void getDataDocumentoRecepcion() throws Exception {
        given(documentoService.getDataDocumentoRecepcion(1))
                .willReturn(new DocumentoRecepcionRecord("1", "00000/2024", "ENTRADA", "prueba.pdf", null));

        mockMvc.perform(
                        get("/api/workflow/bandeja/recepcion/anexos/{id}", 1)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }


    @Test
    void updateCancelOficio() throws Exception {
        given(documentoService.cancelOficio(1))
                .willReturn("d8945bc4-af8e-4eb0-b742-7ee13beb43e0");

        mockMvc.perform(
                        patch("/api/workflow/bandeja/oficio/1")
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void getAllOficios() throws Exception {
        OficioResponseRecord oficioResponseRecord = new OficioResponseRecord(
                1,
                "1",
                "institucion1",
                "asunto1",
                EstadoCarpeta.CREADO,
                LocalDate.now(),
                LocalDate.now(),
                false,
                false
        );
        given(documentoService.getAllOficios(any(), any()))
                .willReturn(new PageImpl<>(Collections.singletonList(oficioResponseRecord)));

        mockMvc.perform(
                        get("/api/workflow/bandeja/oficios")
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void movimientoPersonalJuzgado_success() throws Exception {
        PersonalJuzgadoRecord personalJuzgadoRecord = new PersonalJuzgadoRecord(51, 2);

        given(documentoService.movimientoPersonalJuzgado(personalJuzgadoRecord))
                .willReturn(DocumentoSetUp.createMovimientoPersonalJuzgadoRecord());
        mockMvc.perform(
                        post("/api/workflow/bandeja/recepcion/movimiento")
                                .content(ResourceUtilTest.asJsonString(personalJuzgadoRecord))
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void turnadoPersonalJuzgado_success() throws Exception {
        AsignadoTurnadoRecord asignadoTurnadoRecord = new AsignadoTurnadoRecord(52, 150, 1, 7, Prioridad.NORMAL);
        List<AsignadoTurnadoRecord> records = Collections.singletonList(asignadoTurnadoRecord);
        List<MovimientoPersonalJuzgadoRecord> mockResponse = Collections.singletonList(DocumentoSetUp.createMovimientoPersonalJuzgadoRecord());

        given(documentoService.turnadoPersonalJuzgado(Collections.singletonList(asignadoTurnadoRecord)))
                .willReturn(mockResponse);
        mockMvc.perform(
                        post("/api/workflow/bandeja/asignados/movimiento")
                                .content(ResourceUtilTest.asJsonString(records))
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }


    @Test
    void delete_success() throws Exception {
        mockMvc.perform(
                delete("/api/workflow/bandeja/asignados/1")
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());
    }

    @Test
    void create_demanda_antigua() throws Exception {

        String documentoSaveRecordJson = """
        {
            "actor": {
                "nombre": "Carlos",
                "apellidoPaterno": "González",
                "apellidoMaterno": "Hernández",
                "pseudonimo": "carlitos",
                "tipoPersona": "fisica",
                "tipoParte": 1
            },
            "demandado": {
                "nombre": "María",
                "apellidoPaterno": "López",
                "apellidoMaterno": "Ramírez",
                "pseudonimo": "mary",
                "tipoPersona": "fisica",
                "tipoParte": 2
            },
            "anexos": [
                "Anexo1",
                "Anexo2"
            ]
        }
    """;


        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test-file.pdf",
                "application/pdf",
                "Contenido del archivo".getBytes()
        );

        MockMultipartFile documentoSaveRecord = new MockMultipartFile(
                "documentoSaveRecord",
                "documentoSaveRecord",
                "application/json",
                documentoSaveRecordJson.getBytes()
        );

        DocumentoRecord expectedResponse = new DocumentoRecord(1, "12345", TipoCarpeta.DEMANDA);
        given(documentoService.createDemandaAntigua(any(DocumentoSaveRecord.class), any(MultipartFile.class)))
                .willReturn(expectedResponse);

        mockMvc.perform(multipart("/api/workflow/registro")
                        .file(file)
                        .file(documentoSaveRecord)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.folio").value("12345"))
                .andExpect(jsonPath("$.tipoCarpeta").value(TipoCarpeta.DEMANDA.name()));

        verify(documentoService).createDemandaAntigua(any(DocumentoSaveRecord.class), any(MultipartFile.class));
    }

    @Test
    void crear_amparo() throws Exception{
        String pieza = "000001/2024/AD01";
        AmparoRecordResponse amparoRecordResponse = new AmparoRecordResponse(1, 1, pieza, LocalDateTime.now());

        given(documentoService.createAmparo(any())).willReturn(amparoRecordResponse);

        mockMvc.perform(
                        post("/api/workflow/documentos/amparo")
                                .content(ResourceUtilTest.asJsonString(amparoRecordResponse))
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void get_acuerdo_pdf() throws Exception {
        Integer documentoId = 89734;
        byte[] mockPdf = new byte[]{1, 2, 3};

        given(oficioService.getOficio(documentoId)).willReturn(mockPdf);

        mockMvc.perform(get("/api/workflow/documentos/acuerdos/{documentoId}", documentoId)
                        .accept(APPLICATION_PDF))
                .andExpect(status().isOk());
    }

}
