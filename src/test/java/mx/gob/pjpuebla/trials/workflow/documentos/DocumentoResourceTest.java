package mx.gob.pjpuebla.trials.workflow.documentos;

import jakarta.ws.rs.core.MediaType;
import mx.gob.pjpuebla.trials.core.tipojuicio.TipoJuicio;
import mx.gob.pjpuebla.trials.core.utils.resource.ResourceUtilTest;
import mx.gob.pjpuebla.trials.util.enums.*;
import mx.gob.pjpuebla.trials.workflow.anexos.AnexoRecord;
import mx.gob.pjpuebla.trials.workflow.carpeta.Carpeta;
import mx.gob.pjpuebla.trials.workflow.carpeta.CarpetaRepository;
import mx.gob.pjpuebla.trials.workflow.carpeta.CarpetaSetUp;
import mx.gob.pjpuebla.trials.workflow.documentos.amparos.AmparoGetRecord;
import mx.gob.pjpuebla.trials.workflow.documentos.amparos.AmparoRecordResponse;
import mx.gob.pjpuebla.trials.workflow.documentos.amparos.AmparoUpdateRecord;
import mx.gob.pjpuebla.trials.workflow.documentos.records.*;
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

import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
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
    private DocumentoRepository documentoRepository;

    @MockBean
    private CarpetaRepository carpetaRepository;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;


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
                "Motivo de edición",
                "procedencia1"
        );

        DocumentoRecord documentoRecord = new DocumentoRecord(1, "000001/2024", TipoCarpeta.DEMANDA);

        given(documentoService.editarAnexos(any(Integer.class), any(), any(), any()))
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

        given(documentoService.editarAnexos(1, anexos, "ejemplo", ""))
                .willReturn(documentRecord);

        mockMvc.perform(
                        get("/api/workflow/demanda/1")
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void createPromocion() throws Exception {
        String documentoPromocionRecord = """
                    {
                        "carpetaId": "1",
                        "tipoPromocion": "OFICIO",
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
        MockMultipartFile documentoPromocionSave = new MockMultipartFile(
                "documentoPromocionRecord",
                "documentoPromocionRecord",
                "application/json",
                documentoPromocionRecord.getBytes()
        );
        DocumentoPromocionResponseRecord documentoPromocionResponseRecord = new DocumentoPromocionResponseRecord(
                1, "1", TipoDocumento.PROMOCION);

        given(documentoService.createPromocion(any(), any()))
                .willReturn(documentoPromocionResponseRecord);
        mockMvc.perform(multipart("/api/workflow/documento/promocion")
                        .file(file)
                        .file(documentoPromocionSave)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.folio").value("1"))
                .andExpect(jsonPath("$.tipoDocumento").value(TipoDocumento.PROMOCION.name()));

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
                materiaNombre, tipoEntrada, LocalDateTime.now(), SelloEstatus.VALIDO, estatus, true, "Juzgado 1", "", "");

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

        DocumentoAsignadoResponseRecord documentoRecord = new DocumentoAsignadoResponseRecord(1, 1, 1, expediente, folio, expediente, expediente, LocalDateTime.now(), LocalDateTime.now(), folio, expediente, true, "prorroga", EstadoProrroga.AUTORIZADA, "ejemplo", "red", "","");

        given(documentoService.getAllAsignado(anyString(), anyLong(), any(Pageable.class), anyString() ))
                .willReturn(new PageImpl<>(Collections.singletonList(documentoRecord)));

        mockMvc.perform(
                        get("/api/workflow/bandeja/asignados")
                                .accept(MediaType.APPLICATION_JSON))

                .andExpect(status().isOk());

    }

    @Test
    void getDataDocumentoRecepcion() throws Exception {
        given(documentoService.getDataDocumentoRecepcion(1))
                .willReturn(new DocumentoRecepcionRecord("1", "00000/2024", "ENTRADA", "prueba.pdf", "", null));

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
                EstadoCarpeta.CREADO.getEtiqueta(),
                LocalDate.now(),
                LocalDate.now(),
                false,
                false,
                'C',
                "000001/2025"
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
    // Crear una lista de PersonalJuzgadoRecord
    List<PersonalJuzgadoRecord> personalJuzgadoRecords = List.of(new PersonalJuzgadoRecord(51, 2, "DEMANDA", 3));

    // Configurar el mock para el servicio
    given(documentoService.movimientoPersonalJuzgadoList(personalJuzgadoRecords))
            .willReturn(List.of(DocumentoSetUp.createMovimientoPersonalJuzgadoRecord()));

    // Realizar la petición POST
    mockMvc.perform(
                    post("/api/workflow/bandeja/recepcion/movimiento")
                            .content(ResourceUtilTest.asJsonString(personalJuzgadoRecords)) // Ahora enviamos una lista
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
        given(documentoService.createDemandaAntigua(any(DocumentoAntiguoSaveRecord.class), any(MultipartFile.class)))
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

        verify(documentoService).createDemandaAntigua(any(DocumentoAntiguoSaveRecord.class), any(MultipartFile.class));
    }

    @Test
    void crear_amparo() throws Exception {
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
    void getInfoPromocion() throws Exception {
        List<String> anexos = List.of("Anexo1", "Anexo2");
        DocPromocionInfoRecord docPromocionInfoRecord = new DocPromocionInfoRecord(
                "000001",
                2024,
                "Juzgado 1",
                "actor 1",
                "demandado 1",
                "ESCRITO",
                anexos
        );

        given(documentoService.getInfoPromocion(anyInt()))
                .willReturn(docPromocionInfoRecord);

        mockMvc.perform(
                        get("/api/workflow/demanda/1")
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }


    @Test
    void getExhortoById() throws Exception {
        List<String> anexos = List.of("Anexo1", "Anexo2");

        given(documentoService.getExhortoById(1)).willReturn(new ExhortoResponseRecord(anexos, "Obeservaciones", "Procedencia", "Exhorto"));


        mockMvc.perform(
                        get("/api/workflow/exhorto/{id}", 1)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void createExhortoSalida() throws Exception {
        String documentoExhortoSalidaRecordJson = """
                    {
                        "carpetaId": "1",
                        "destino": "Juzgado 1",
                        "tramite": "Nombre del exhorto",
                        "observaciones": "Sin observaciones",
                        "fechaEntrega": "2024-01-01",
                        "fechaDevolucion": "2024-01-02"
                    }
                """;
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test-file.pdf",
                "application/pdf",
                "Contenido del archivo".getBytes()
        );
        MockMultipartFile documentoExhortoSalidaRecord = new MockMultipartFile(
                "documentoExhortoSalida",
                "documentoExhortoSalida",
                "application/json",
                documentoExhortoSalidaRecordJson.getBytes()
        );
        DocumentoPromocionResponseRecord expectedResponse = new DocumentoPromocionResponseRecord(1, "12345", TipoDocumento.EXHORTO_SALIDA);
        given(documentoService.createExhortoSalida(any(DocumentoExhortoSalidaRecord.class), any(MultipartFile.class)))
                .willReturn(expectedResponse);
        mockMvc.perform(multipart("/api/workflow/exhorto/salida")
                        .file(file)
                        .file(documentoExhortoSalidaRecord)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.folio").value("12345"))
                .andExpect(jsonPath("$.tipoDocumento").value(TipoDocumento.EXHORTO_SALIDA.name()));
    }

    @Test
    void testAdjuntarPromocion() throws Exception {
        DocumentoPromocionResponseRecord responseRecord = new DocumentoPromocionResponseRecord(10, "1", TipoDocumento.PROMOCION);

        given(documentoService.adjuntarPromocion(anyInt())).willReturn(responseRecord);

        mockMvc.perform(post("/api/workflow/documentos/promocion/10/adjuntar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    void saveSentenciaPublica() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test-file.pdf",
                "application/pdf",
                "Contenido del archivo".getBytes()
        );

        mockMvc.perform(multipart("/api/workflow/documentos/sentencia/publica/" + 1)
                        .file(file)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void getAmparoById_Found() throws Exception {
        Documento documento = new Documento();
        documento.setId(1);

        DocumentoData data = new DocumentoData();
        data.setAmparoTipo("AmparoTipo");
        data.setAmparoFechaPresentacion(LocalDate.of(2022, 1, 1));
        data.setAmparoFechaTermino(LocalDate.of(2022, 12, 31));
        data.setAmparoImpugnacion(1);
        data.setAmparoSentido("Sentido");
        data.setAmparoSentidoImpugnacion("Sentido de impugnación");
        data.setAmparoQuejoso("Quejoso");
        data.setAmparoTribunalId(123);
        data.setAmparoSalaId(456);

        Carpeta carpeta = new Carpeta();
        carpeta.setId(52);
        documento.setCarpeta(carpeta);

        documento.setData(data);

        given(documentoService.getAmparoById(1)).willReturn(new AmparoGetRecord(
                carpeta.getId(),
                data.getAmparoTipo(),
                data.getAmparoFechaPresentacion(),
                data.getAmparoFechaTermino(),
                data.getAmparoImpugnacion(),
                data.getAmparoSentido(),
                data.getAmparoSentidoImpugnacion(),
                data.getAmparoQuejoso(),
                data.getAmparoTribunalId(),
                data.getAmparoSalaId()
        ));

        mockMvc.perform(get("/api/workflow/documentos/amparo/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()) // Verifica que sea un 200 OK
                .andExpect(jsonPath("$.carpetaId").value(52)) // Verifica el id de la carpeta en la respuesta
                .andExpect(jsonPath("$.tipoAmparo").value("AmparoTipo"));
    }

    @Test
    void getAmparoById_NotFound() throws Exception {
        given(documentoService.getAmparoById(1)).willReturn(null);

        mockMvc.perform(
                        get("/api/documentos/amparo/1")
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }


    @Test
    void updateAmparoData_Success() throws Exception {
        AmparoUpdateRecord amparoUpdate = new AmparoUpdateRecord(
                LocalDate.of(2024, 12, 8),
                LocalDate.of(2025, 12, 8),
                1,
                "Sentido de amparo",
                "Sentido de impugnacion",
                "Juan Perez",
                2,
                3,
                "Amparo Directo"
        );

        doNothing().when(documentoService).updateAmparoData(1, amparoUpdate);

        mockMvc.perform(
                        put("/api/workflow/documentos/amparo/update/{id}", 1)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(amparoUpdate)))
                .andExpect(status().isOk())
                .andExpect(content().string("Documento actualizado con éxito"));
    }

}
