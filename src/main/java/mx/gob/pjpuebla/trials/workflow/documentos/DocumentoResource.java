package mx.gob.pjpuebla.trials.workflow.documentos;

import com.google.zxing.WriterException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mx.gob.pjpuebla.trials.workflow.anexos.AnexoRecord;
import mx.gob.pjpuebla.trials.workflow.documentos.amparos.AmparoGetRecord;
import mx.gob.pjpuebla.trials.workflow.documentos.amparos.AmparoRecord;
import mx.gob.pjpuebla.trials.workflow.documentos.amparos.AmparoRecordResponse;
import mx.gob.pjpuebla.trials.workflow.documentos.amparos.AmparoUpdateRecord;
import mx.gob.pjpuebla.trials.workflow.documentos.records.*;
import mx.gob.pjpuebla.trials.workflow.carpeta.records.ApelacionRecord;
import mx.gob.pjpuebla.trials.workflow.carpeta.records.CarpetaCatalogoRecord;
import mx.gob.pjpuebla.trials.workflow.sello.OficioService;
import mx.gob.pjpuebla.trials.workflow.sello.SelloCaratulaService;
import mx.gob.pjpuebla.trials.workflow.sello.SelloGenerator;
import net.sf.jasperreports.engine.JRException;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/workflow")
@SecurityRequirement(name = "Keycloak")
public class DocumentoResource {

    private final SelloGenerator selloGenerator;
    private final SelloCaratulaService caratulaGenerator;
    private final DocumentoService documentoService;
    private final DigitalizacionService digitalizacion2Service;
    private final OficioService oficioService;

    private static final Logger logger = LoggerFactory.getLogger(DocumentoResource.class);

    @PostMapping("/demanda")
    public DocumentoRecord createDemanda(@RequestBody DocumentoSaveRecord documentoSaveRecord) {
        return this.documentoService.createDemanda(documentoSaveRecord);
    }

    @PostMapping("/crear/demandaPenal")
    public DocumentoGenericRecord crearDemandaPenal(@RequestBody DocumentoCreateDemandaPenalRecord demanda) {
        return this.documentoService.createDemandaPenal(demanda);
    }

    @PatchMapping(value = "/demanda/{id}/anexos", produces = MediaType.APPLICATION_JSON_VALUE)
    public DocumentoRecord editAnexos(@PathVariable Integer id, @RequestBody AnexoRecord anexoRecord) {
        return documentoService.editarAnexos(id, anexoRecord.anexos(), anexoRecord.motivoEdita(),
                anexoRecord.procedencia());
    }

    @GetMapping(value = "/demanda/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<DocumentoResponseRecord> getDemandaById(@PathVariable Integer id) {
        DocumentoResponseRecord editDocumento = documentoService.getDemandaById(id);
        return ResponseEntity.ok(editDocumento);
    }

    @GetMapping(value = "/documentos/{id}/sello", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<byte[]> exportPdf(@PathVariable Integer id) throws JRException, IOException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("sello", id + "_sello.pdf");
        return ResponseEntity.ok().headers(headers).body(selloGenerator.exportToPdf(id));
    }

    @PostMapping("/documentos/digitalizacion/{documentoId}")
    public DigitalizacionRecord digitizationDocument(
            @RequestParam("file") MultipartFile file,
            @PathVariable("documentoId") Integer documentoId) {
        return digitalizacion2Service.guardarArchivo(file, documentoId);
    }

    @GetMapping(value = "/documentos/digitalizacion/{documentoId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<byte[]> getFile(@PathVariable Integer documentoId) throws IOException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("sello", documentoId + "_documento.pdf");
        return ResponseEntity.ok().headers(headers).body(digitalizacion2Service.getDocumento(documentoId));
    }

    @GetMapping(value = "/documentos/digitalizacion/legacy/{promocionId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<byte[]> getFilePromocionLegacy(@PathVariable Integer promocionId) throws IOException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("sello", promocionId + "_documento.pdf");
        return ResponseEntity.ok().headers(headers).body(digitalizacion2Service.getPromocionMigrada(promocionId));
    }

    @GetMapping(value = "/documentos/{id}/caratula", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<byte[]> exportCaratulaPdf(@PathVariable Integer id) throws JRException, IOException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("caratula", id + "_caratula.pdf");
        return ResponseEntity.ok().headers(headers).body(caratulaGenerator.exportToPdf(id));
    }

    @GetMapping("/bandeja/entrada2")
    public Page<DocumentoGridRecord> getAll(Pageable pageable,
            @RequestParam(value = "key", required = false) String key,
            @RequestParam(value = "tipoEntrada", required = false) String tipoEntrada) {
        return this.documentoService.getAll(key, pageable, tipoEntrada);
    }

    @GetMapping("/bandeja/salida2")
    public Page<DocumentoSalidaResponseRecord> getAllBandejaSalida(@PageableDefault(size = 20) Pageable pageable,
            @RequestParam(value = "key", required = false) String key) {
        return this.documentoService.getAllBandejaSalida(key, pageable);
    }

    @PatchMapping("/bandeja/{id}/status/{status}")
    public DocumentoRecord updateStatus(@PathVariable Integer id, @PathVariable Integer status) {
        return this.documentoService.updateStatus(id, status);
    }

    @GetMapping(value = "/bandeja/historial2", produces = MediaType.APPLICATION_JSON_VALUE)
    public Page<DocumentoGridRecord> getAllHistorial(
            @PageableDefault(size = 20) Pageable pageable,
            @RequestParam(value = "key", required = false) String key) {
        return documentoService.getAllHistorial(key, pageable);
    }

    @PostMapping(value = "/documento/promocion", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public DocumentoPromocionResponseRecord createPromocion(
            @RequestPart(value = "file", required = false) MultipartFile file,
            @RequestPart("documentoPromocionRecord") String documentoPromocionRecordJson)
            throws JsonProcessingException {
        DocumentoPromocionRecord documentoPromocionRecord = new ObjectMapper().readValue(documentoPromocionRecordJson,
                DocumentoPromocionRecord.class);
        return this.documentoService.createPromocion(documentoPromocionRecord, file);
    }

    @GetMapping("/documento/promocion/{id}")
    public DocPromocionInfoRecord getInfoPromocion(@PathVariable Integer id) {
        return this.documentoService.getInfoPromocion(id);
    }

    @PostMapping("/exhorto")
    public DocumentoRecord createExhorto(@RequestBody DocumentoExhortoRecord documentoExhortoRecord) {
        return documentoService.createExhorto(documentoExhortoRecord);
    }

    @PostMapping("/apelacion")
    public DocumentoRecord create(@RequestBody @Valid ApelacionRecord apelacionRecord) {
        return this.documentoService.createApelacion(apelacionRecord);
    }

    @GetMapping("/bandeja/recepcion")
    public Page<DocumentoBandejaRecepcionRecord> getAllBandejaRecepcion(
            @RequestParam(value = "key", required = false) String key,
            @RequestParam(value = "folio", required = false) String folio,
            @RequestParam(value = "expediente", required = false) String expediente,
            @RequestParam(value = "tipoEntrada", required = false) String tipoEntrada,
            @RequestParam(value = "origen", required = false) String origen,
            @RequestParam(value = "motivoTurnado", required = false) String motivoTurnado, 
            @RequestParam(value = "fechaFrom", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaFrom,
            @RequestParam(value = "fechaTo", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaTo,
            @PageableDefault(size = 20) Pageable pageable) {
        return this.documentoService.getAllBandejaRecepcion(key, folio, expediente, tipoEntrada, origen, motivoTurnado, fechaFrom, fechaTo, pageable);
    }

    @PostMapping("/bandeja/recepcion/movimiento")
    public List<MovimientoPersonalJuzgadoRecord> movimientoPersonalJuzgado(
            @RequestBody List<PersonalJuzgadoRecord> personalJuzgadoRecord) {
        return this.documentoService.movimientoPersonalJuzgadoList(personalJuzgadoRecord);
    }

    @GetMapping("/bandeja/asignados")
    public Page<DocumentoAsignadoResponseRecord> getAllBandejaAsignados(
            @RequestParam(value = "key", required = false) String key,
            @PageableDefault(size = 20) Pageable pageable,
            @RequestParam(value = "personaId", required = false) Long personaId,
            @RequestParam(value = "tipoEntrada", required = false) String tipoEntrada) {
        return this.documentoService.getAllAsignado(key, personaId, pageable, tipoEntrada);
    }

    @PostMapping("/bandeja/asignados/movimiento")
    public List<MovimientoPersonalJuzgadoRecord> turnadoPersonalJuzgado(
            @RequestBody @Valid List<AsignadoTurnadoRecord> records) {
        return documentoService.turnadoPersonalJuzgado(records);
    }

    @PostMapping("/bandeja/salida")
    public String sendToBandejaRecepcion(@RequestBody @Valid SalidaSentToRecepcionRecord salidaSentToRecepcionRecord) {
        return this.documentoService.sendToBandejaRecepcion(salidaSentToRecepcionRecord.idList(),
                salidaSentToRecepcionRecord.personaCarrito());
    }

    @GetMapping("/bandeja/oficios")
    public Page<OficioResponseRecord> getAllOficios(
            @RequestParam(value = "key", required = false) String key,
            @PageableDefault(size = 20) Pageable pageable) {
        return this.documentoService.getAllOficios(key, pageable);
    }

    @PostMapping("/oficio")
    public Integer generarOficio(@RequestBody DocumentoOficioRecord oficio) {
        return documentoService.createOficio(oficio.institucionId(), oficio.fechaEmision(), oficio.asunto(),
                oficio.carpetaId());
    }

    @PatchMapping("/bandeja/oficio/{id}")
    public String updateCancelOficio(@PathVariable Integer id) {
        return this.documentoService.cancelOficio(id);
    }

    @GetMapping(value = "/documentos/indicadores", produces = MediaType.APPLICATION_JSON_VALUE)
    public IndicadoresRecord getIndicadores(@RequestParam Boolean isRecepcion) {
        if (isRecepcion == Boolean.TRUE)
            return this.documentoService.getIndicadores();

        return this.documentoService.getIndicadoresAsignados();
    }

    @GetMapping("/bandeja/recepcion/anexos/{movimientoId}")
    public DocumentoRecepcionRecord getDataDocumentoRecepcion(@PathVariable Integer movimientoId) {
        return documentoService.getDataDocumentoRecepcion(movimientoId);
    }

    @GetMapping(value = "/documentos/oficio/{oficioId}", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> exportOficioPdf(@PathVariable Integer oficioId)
            throws JRException, IOException, WriterException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("oficio", oficioId + "_documento.pdf");
        return ResponseEntity.ok().headers(headers).body(oficioService.getOficio(oficioId));
    }

    @DeleteMapping("/bandeja/asignados/{id}")
    public void delete(@PathVariable Integer id) {
        this.documentoService.deleteAsignado(id);
    }

    @PostMapping(value = "/documentos/amparo")
    public AmparoRecordResponse crearAmparo(@RequestBody AmparoRecord amparoRecord) {
        return documentoService.createAmparo(amparoRecord);

    }

    @PostMapping(value = "/registro", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public DocumentoRecord createDemandaAntigua(
            @RequestPart("documentoSaveRecord") String documentoSaveRecordJson,
            @RequestPart("file") MultipartFile file) throws JsonProcessingException {

        DocumentoAntiguoSaveRecord documentoSaveRecord = new ObjectMapper().readValue(documentoSaveRecordJson,
                DocumentoAntiguoSaveRecord.class);
        return this.documentoService.createDemandaAntigua(documentoSaveRecord, file);
    }

    @GetMapping(value = "/exhorto/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ExhortoResponseRecord> getExhortoById(@PathVariable Integer id) {
        ExhortoResponseRecord editDocumento = documentoService.getExhortoById(id);
        return ResponseEntity.ok(editDocumento);
    }

    @PostMapping(value = "/exhorto/salida", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public DocumentoPromocionResponseRecord createExhortoSalida(
            @RequestPart(value = "file", required = false) MultipartFile file,
            @RequestPart("documentoExhortoSalida") String documentoExhortoSalidaRecordJson)
            throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        DocumentoExhortoSalidaRecord documentoExhortoSalidaRecord = objectMapper
                .readValue(documentoExhortoSalidaRecordJson, DocumentoExhortoSalidaRecord.class);
        return documentoService.createExhortoSalida(documentoExhortoSalidaRecord, file);
    }

    @PostMapping(value = "/documentos/promocion/{promocionId}/adjuntar")
    public DocumentoPromocionResponseRecord adjuntarPromocion(@PathVariable Integer promocionId) {
        return documentoService.adjuntarPromocion(promocionId);
    }

    @PostMapping(value = "/documentos/sentencia/publica/{docId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void saveSentenciaPublica(
            @RequestPart("file") MultipartFile file,
            @PathVariable("docId") Integer docId) {
        documentoService.saveSentenciaPublica(docId, file);
    }

    @GetMapping("/documentos/amparo/{id}")
    public ResponseEntity<AmparoGetRecord> getAmparoById(@PathVariable Integer id) {
        AmparoGetRecord amparoRecord = documentoService.getAmparoById(id);
        if (amparoRecord == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(amparoRecord);
    }

    @PutMapping("/documentos/amparo/update/{id}")
    public ResponseEntity<String> updateAmparoData(@PathVariable Integer id,
            @RequestBody AmparoUpdateRecord amparoUpdate) {
        try {
            documentoService.updateAmparoData(id, amparoUpdate);
            return ResponseEntity.ok("Documento actualizado con éxito");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Documento no encontrado con ID: " + id);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al actualizar el documento");
        }
    }

    @GetMapping("/bandeja/devueltos/ocp")
    public Page<DocumentoBandejaDevueltos> getBandejaDevueltosOCP(Pageable pageable,
            @RequestParam(value = "key", required = false) String key) {
        return this.documentoService.getBandejaDevueltosOCP(key, pageable);
    }

    @PostMapping("/devolver/bandejas")
    public ResponseEntity<String> devolverABandejas(@RequestBody DevolucionBandejasRecord devolucion) {
        this.documentoService.devolverABandejas(devolucion);
        return ResponseEntity.ok("Devolución completa");
    }

    @GetMapping("/filtroBandeja")
    public List<CarpetaCatalogoRecord> filtroTiposEntradas(@RequestParam String bandeja) {
        return documentoService.getTipoEntradas(bandeja);
    }
}