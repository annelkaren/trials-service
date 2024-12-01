package mx.gob.pjpuebla.trials.workflow.carpeta;

import jakarta.ws.rs.core.MediaType;
import mx.gob.pjpuebla.trials.core.rubros.RubroRecord;
import mx.gob.pjpuebla.trials.core.tipopieza.TipoPieza;
import mx.gob.pjpuebla.trials.core.utils.resource.ResourceUtilTest;
import mx.gob.pjpuebla.trials.util.enums.*;
import mx.gob.pjpuebla.trials.util.enums.carpeta.CatalogoCondicionMigratoria;
import mx.gob.pjpuebla.trials.util.enums.carpeta.CatalogoDeterminacionJurisdiccional;
import mx.gob.pjpuebla.trials.workflow.anexos.AnexoBandejaRecepcionRecord;
import mx.gob.pjpuebla.trials.workflow.carpeta.records.*;
import mx.gob.pjpuebla.trials.workflow.documentos.records.DocumentoDetalleCarpeta;
import mx.gob.pjpuebla.trials.workflow.documentos.records.DocumentoDetalleCarpetaResponse;
import mx.gob.pjpuebla.trials.workflow.documentos.records.DocumentoRecepcionMovimientosRecord;
import mx.gob.pjpuebla.trials.workflow.documentos.records.DocumentoRecord;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.web.servlet.MockMvc;
import mx.gob.pjpuebla.trials.error.NotFoundException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(CarpetaResource.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
class CarpetaResourceTest {

    @MockBean
    private CarpetaService mockCarpetaService;

    @Autowired
    private MockMvc mockMvc;

    private CarpetaResponseRecord carpetaResponseRecord;
    private ApelacionRecordResponse apelacionRecordResponse;

    @BeforeEach
    void setUp() {
        carpetaResponseRecord = CarpetaSetUp.createCarpetaResponseRecord();
        apelacionRecordResponse = CarpetaSetUp.apelacionRecordResponse();
    }

    @Test
    void getCarpetaById_success() throws Exception {
        given(mockCarpetaService.getCarpetaResponseByNumExpYearJuzgado(anyString(), anyInt()))
                .willReturn(carpetaResponseRecord);
        mockMvc.perform(
                get("/api/workflow/carpeta")
                        .param("numExpediente", "000001")
                        .param("year", "2024")
                        .param("idJuzgado", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void getPersonaDocumentoById_success() throws Exception {
        List<ApelacionRecordResponse> expectedResponses = Collections.singletonList(apelacionRecordResponse);

        given(mockCarpetaService.getPersonasDocumentoByCarpetaId(anyInt()))
                .willReturn(expectedResponses);

        mockMvc.perform(
                get("/api/workflow/carpeta/personas/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void obtenerBandejaRecepcionSuccess() throws Exception {

        Integer documentoId = 1;

        BandejaRecepcionRecord expectedRecord = new BandejaRecepcionRecord(1, "1", "Expediente 1", TipoCarpeta.DEMANDA,
                "ruta/digitalizacion", List.of());

        when(mockCarpetaService.getBandejaRecepcionByDocumentoId(documentoId)).thenReturn(expectedRecord);
        
        mockMvc.perform(get("/api/workflow/carpeta/recepcion")
                .param("documentoId", "1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.folio").value("1"))
                .andExpect(jsonPath("$.expediente").value("Expediente 1"));
    }

    @Test
    void obtenerBandejaRecepcionNotFound() throws Exception {
        Integer documentoId = 110;

        when(mockCarpetaService.getBandejaRecepcionByDocumentoId(documentoId))
                .thenThrow(new NotFoundException("No se encontró la carpeta con el documentoId: " + documentoId, "documentoId"));

        mockMvc.perform(get("/api/workflow/carpeta/recepcion")
                .param("documentoId", "110")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void testRecepcionAnexos_Success() throws Exception {
        Integer documentoId = 123;
        List<AnexoBandejaRecepcionRecord> anexos = List.of(new AnexoBandejaRecepcionRecord(1, "INE", EstadoAnexo.ASIGNADO));
        DocumentoRecepcionMovimientosRecord docRecepcionMovimientosRecord = new DocumentoRecepcionMovimientosRecord(
                anexos,
                "Observacion 1",
                "recomendacion 1"
        );
        DocumentoRecord responseRecord = new DocumentoRecord(1, "000001/2", TipoCarpeta.DEMANDA);

        when(mockCarpetaService.actualizarInformacionAnexos(docRecepcionMovimientosRecord, documentoId))
                .thenReturn(responseRecord);

        mockMvc.perform(post("/api/workflow/carpeta/recepcion/" + documentoId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(ResourceUtilTest.asJsonString(docRecepcionMovimientosRecord)))
                .andExpect(status().isOk());
    }

    @Test
    void testGetListCatalogo() throws Exception {
        List<CarpetaCatalogoRecord> items = Arrays.stream(CatalogoCondicionMigratoria.values())
                .map(data -> new CarpetaCatalogoRecord(data.name(), data.getEtiqueta()))
                .toList();
        when(mockCarpetaService.getCatalogoList("catalogoCondicionMigratoria"))
                .thenReturn(items);

        mockMvc.perform(get("/api/workflow/carpeta/enums/catalogoCondicionMigratoria")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].clave").value("VIS_SIN_PER_ACT_REM"))
                .andExpect(jsonPath("$[0].etiqueta").value("Visitante sin permiso para realizar actividades remuneradas"))
                .andExpect(jsonPath("$.length()").value(CatalogoCondicionMigratoria.values().length));
    }

    @Test
    void testGetList_anoterCatalagp() throws Exception {
        List<CarpetaCatalogoRecord> items = Arrays.stream(CatalogoDeterminacionJurisdiccional.values())
                .map(data -> new CarpetaCatalogoRecord(data.name(), data.getEtiqueta()))
                .toList();
        when(mockCarpetaService.getCatalogoList("catalogoDeterminacionJurisdiccional"))
                .thenReturn(items);

        mockMvc.perform(get("/api/workflow/carpeta/enums/catalogoDeterminacionJurisdiccional")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].clave").value("PRESENTACION"))
                .andExpect(jsonPath("$[0].etiqueta").value("Presentación"))
                .andExpect(jsonPath("$.length()").value(CatalogoDeterminacionJurisdiccional.values().length));
    }

    @Test
    void testGetListCatalogo_NotFound() throws Exception {
        when(mockCarpetaService.getCatalogoList("catalagoErroneo"))
                .thenReturn(null);

        mockMvc.perform(get("/api/workflow/carpeta/enums/catalagoErroneo")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void getInfoRecepcionExpediente() throws Exception {
        EtapaProcesalRecord etapaProcesalRecord = new EtapaProcesalRecord(1, "Etapa 1");
        RubroRecord rubroRecord1 = new RubroRecord(1, "Rubro1");
        RubroRecord rubroRecord2 = new RubroRecord(2, "Rubro2");

        List<RubroRecord> rubroList = new ArrayList<>();
        rubroList.add(rubroRecord1);
        rubroList.add(rubroRecord2);

        InfoExpedienteRecord infoExpedienteRecord = new InfoExpedienteRecord(
                "000001/2024",
                "Oralidad Familiar",
                1,
                "Juez Perez",
                "01/01/2000 01:00:00",
                "asunto 1",
                "Procedimiento1, Procedimiento2",
                rubroList,
                etapaProcesalRecord,
                null,
                "",
                "materia 1",
                1,
                "tipoSistema 1",
                "Juzgado 1"
        );

        given(mockCarpetaService.getInfoExpediente(any()))
                .willReturn(infoExpedienteRecord);

        mockMvc.perform(get("/api/workflow/carpeta/recepcion/" + 1))
                .andExpect(status().isOk());
    }

    @Test
    void getInfoExpedienteDetalle() throws Exception {
        EtapaProcesalRecord etapaProcesalRecord = new EtapaProcesalRecord(1, "Etapa 1");

        InfoExpedienteDetalleRecord infoExpedienteRecord = new InfoExpedienteDetalleRecord(
                "determinacion 1",
                "01/01/2000 01:00:00",
                "01/01/2000 01:00:00",
                "ubicacion 1",
                "asunto 1",
                "fase 1",
                "observacion 1",
                "sentencia 1",
                "promovente",
                "123",
                "123",
                "",
                LocalDate.now(),
                1,
                "pesos",
                0,
                0,
                "",
                "",
                "",
                "",
                "",
                "",
                LocalTime.now(),
                LocalTime.now(),
                "",
                "",
                "",
                "",
                0,
                ""
        );

        given(mockCarpetaService.getInfoExpedienteDetalle(any()))
                .willReturn(infoExpedienteRecord);

        mockMvc.perform(get("/api/workflow/carpeta/expediente/detalle/1"))
                .andExpect(status().isOk());
    }

    @Test
    void saveExpedienteDetalle() throws Exception {
        EtapaProcesalRecord etapaProcesalRecord = new EtapaProcesalRecord(1, "Primera Etapa");

        RubroRecord rubroRecord1 = new RubroRecord(1, "Rubro1");
        RubroRecord rubroRecord2 = new RubroRecord(2, "Rubro2");
        List<RubroRecord> rubroList = new ArrayList<>();
        rubroList.add(rubroRecord1);
        rubroList.add(rubroRecord2);

        SaveExpedienteDetalleRecord saveExpedienteDetalleRecord = new SaveExpedienteDetalleRecord(
                CatalogoDeterminacionJurisdiccional.PRESENTACION,
                "01/01/2000 01:00:00",
                "01/01/2000 01:00:00",
                "ubicacion 1",
                "asunto 1",
                "fase 1",
                "observacion 1",
                "sentencia 1",
                "promovente",
                "123",
                "123",
                "",
                LocalDate.now(),
                0,
                "",
                0,
                0,
                "",
                "",
                "",
                "",
                "",
                "",
                LocalTime.now(),
                LocalTime.now(),
                "",
                PresentacionImputado.PRESENTACION_VOLUNTARIA,
                SolicitudAudiencia.SOLICITUD_AUDIENCIA_PRIVADA,
                "",
                "",
                "",
                "",
                "",
                "",
                1,
                etapaProcesalRecord,
                rubroList
        );

        mockMvc.perform(post("/api/workflow/carpeta/expediente/detalle/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(ResourceUtilTest.asJsonString(saveExpedienteDetalleRecord)))
                .andExpect(status().isOk());
    }

    @Test
    void testPostAdjuntarPieza() throws Exception{

        PiezaRecord request = new PiezaRecord(null, "AD", Collections.singletonList(1));
        TipoPieza tipoPieza = new TipoPieza().setId(1).setClave("AD").setTipo("Amparo");
        Carpeta pieza = CarpetaSetUp.create().setTipoPieza(tipoPieza);

        given(mockCarpetaService.createPieza(any(), any())).willReturn(pieza);

        mockMvc.perform(post("/api/workflow/carpeta/piezas/adjuntar?carpetaId=1")
                        .content(ResourceUtilTest.asJsonString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

    }

   @Test
    void testPutAdjuntarPieza() throws Exception{
        PiezaRecord request = new PiezaRecord(null, "AD", Collections.singletonList(1));
        TipoPieza tipoPieza = new TipoPieza().setId(1).setClave("AD").setTipo("Amparo");
        PiezaRecordResponse pieza = new PiezaRecordResponse(1, "000001/2024/AM01", "AD");

        given(mockCarpetaService.adjuntarPiezaDocumentos(any(), any())).willReturn(pieza);

        mockMvc.perform(put("/api/workflow/carpeta/piezas/adjuntar?piezaId=1")
                        .content(ResourceUtilTest.asJsonString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void testGetAllDocumentosPiezas() throws Exception{
        TipoPieza tipoPieza = new TipoPieza()
                .setId(1)
                .setClave("AD")
                .setTipo("Amparo Directo");
        DocumentoDetalleCarpeta documento = new DocumentoDetalleCarpeta(1,"1", TipoDocumento.PROMOCION,
                tipoPieza, LocalDateTime.now(), "DEMANDA_174FD31D-3E83-4F81-AE3D-0C4EA91D772E.PDF",
                1L, TipoCarpeta.DEMANDA);
        DocumentoDetalleCarpeta pieza = new DocumentoDetalleCarpeta(3, "000001/2024/AD01", null,
                null, LocalDateTime.now(), null, 1L, TipoCarpeta.PIEZA);

        List<DocumentoDetalleCarpeta> documentos = Collections.singletonList(documento);
        List<DocumentoDetalleCarpeta> piezas = Collections.singletonList(pieza);
        Page<DocumentoDetalleCarpetaResponse> lista = new PageImpl<>(
                Stream.concat( documentos.stream(), piezas.stream())
                        .map(e->new DocumentoDetalleCarpetaResponse(
                                e.id(),
                                "",
                                e.folio(),
                                e.fechaRegistro(),
                                e.ruta(),
                                "",
                                e.tipoCarpeta().name(),
                                Boolean.FALSE)).toList());

        given(mockCarpetaService.getAllDocumentosPiezas(null, 1, Pageable.ofSize(lista.getSize()))).willReturn(lista);

        mockMvc.perform(get("/api/workflow/carpeta/documentos/1"))
                .andExpect(status().isOk());
    }
}