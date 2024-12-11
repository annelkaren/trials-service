package mx.gob.pjpuebla.trials.workflow.carpeta;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import mx.gob.pjpuebla.trials.workflow.carpeta.records.*;
import mx.gob.pjpuebla.trials.workflow.documentos.records.DocumentoDetalleCarpetaResponse;
import mx.gob.pjpuebla.trials.workflow.documentos.records.DocumentoRecepcionMovimientosRecord;
import mx.gob.pjpuebla.trials.workflow.documentos.records.DocumentoRecord;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/workflow/carpeta")
@SecurityRequirement(name = "Keycloak")
public class CarpetaResource {

    private final CarpetaService carpetaService;

    @GetMapping
    public ResponseEntity<CarpetaResponseRecord> getCarpetaByExpedienteAndJuzgadoId(
            @RequestParam String numExpediente,
            @RequestParam Integer year,
            @RequestParam(required = false, name = "idJuzgado") Integer juzgadoId) {

        CarpetaResponseRecord carpetaResponseRecord = carpetaService.getCarpetaResponseByNumExpYearJuzgado(
                numExpediente + "/" + year, juzgadoId);
        return ResponseEntity.ok(carpetaResponseRecord);
    }

    @GetMapping(value = "/personas/{carpetaId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ApelacionRecordResponse> getPersonasDocumentoByCarpetaId(@PathVariable Integer carpetaId) {
        return this.carpetaService.getPersonasDocumentoByCarpetaId(carpetaId);
    }

    @GetMapping(value = "/recepcion")
    public BandejaRecepcionRecord obtenerBandejaRecepcion(@RequestParam Integer documentoId) {
        return this.carpetaService.getBandejaRecepcionByDocumentoId(documentoId);
    }

    @PostMapping(value = "/recepcion/{documentoId}")
    public DocumentoRecord recepcionAnexos(
            @RequestBody DocumentoRecepcionMovimientosRecord docRecepcionMovimientosRecord,
            @PathVariable Integer documentoId) {
        return this.carpetaService.actualizarInformacionAnexos(docRecepcionMovimientosRecord, documentoId);
    }

    @GetMapping(value = "/enums/{catalogo}", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<CarpetaCatalogoRecord> getListCatalogo(@PathVariable String catalogo){
        return this.carpetaService.getCatalogoList(catalogo);
    }

    @GetMapping(value = "/recepcion/{docId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public InfoExpedienteRecord getInfoRecepcionExpediente(@PathVariable Integer docId){
        return this.carpetaService.getInfoExpediente(docId);
    }

    @GetMapping(value = "/expediente/detalle/{docId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public InfoExpedienteDetalleRecord getInfoExpedienteDetalle(@PathVariable Integer docId){
        return this.carpetaService.getInfoExpedienteDetalle(docId);
    }

    @PostMapping(value = "/expediente/detalle/{docId}")
    public void saveExpedienteDetalle(
            @RequestBody SaveExpedienteDetalleRecord infoExpedienteDetalleRecord,
            @PathVariable Integer docId) {
        this.carpetaService.saveExpedienteDetalle(infoExpedienteDetalleRecord, docId);
    }

    @GetMapping(value= "/piezas/numPieza", produces = MediaType.APPLICATION_JSON_VALUE)
    public NumPiezaRecord getConsecutivoPiezas(@RequestParam String clavePieza, @RequestParam Integer carpetaId) {
        String numPieza = this.carpetaService.consecutivoPieza(carpetaId, clavePieza);

        return new NumPiezaRecord(numPieza);
    }

    @PostMapping(value = "/piezas/adjuntar", produces = MediaType.APPLICATION_JSON_VALUE)
    public PiezaRecordResponse createPieza(@RequestParam Integer carpetaId, @RequestBody PiezaRecord piezaRecord){
        Carpeta pieza = carpetaService.createPieza(carpetaId, piezaRecord);

        return  new PiezaRecordResponse(pieza.getId(), pieza.getExpediente(), pieza.getTipoPieza().getTipo(), pieza.getEstatus());
    }

    @PutMapping(value = "/piezas/adjuntar", produces = MediaType.APPLICATION_JSON_VALUE)
    public PiezaRecordResponse adjuntarPieza(@RequestParam Integer piezaId, @RequestBody PiezaRecord piezaRecord){
        return carpetaService.adjuntarPiezaDocumentos(piezaId, piezaRecord);
    }

    @GetMapping(value= "/piezas", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<PiezaRecordResponse> getPiezas(@RequestParam Integer documentoId) {

        return  this.carpetaService.getPiezas(documentoId);

    }

    @GetMapping(value = "/documentos/{carpetaId}")
    public Page<DocumentoDetalleCarpetaResponse> getAllDocumentosByCarpeta(
            @PathVariable Integer carpetaId,
            @RequestParam(value = "key", required = false) String key,
            @PageableDefault(size = 20) @SortDefault.SortDefaults({
                    @SortDefault(sort = "fechaRegistro", direction = Sort.Direction.ASC)
            }) Pageable pageable){

        return this.carpetaService.getAllDocumentosPiezas(key, carpetaId, pageable);
    }

    @PostMapping(value="/piezas/acoplar", produces = MediaType.APPLICATION_JSON_VALUE)
    public PiezaRecordResponse acoplarPieza(
            @RequestParam("piezaId") Integer piezaId,
            @RequestParam("estatus") String estadoPieza){
            return this.carpetaService.acoplarPieza(piezaId, estadoPieza);
    }

    @GetMapping("/librogobierno")
    public Page<LibroGobiernoRecord> getLibroDeGobierno(
            @PageableDefault(size = 20) Pageable pageable,
            @RequestParam(value = "key", required = false) String key) {
        return carpetaService.libroDeGobierno(key, pageable);
    }

    @GetMapping(value = "/sentencia")
    public ResponseEntity<SentenciaPublicaResponseRecord> getCarpetaByExpedienteAndSentencia(
            @RequestParam String numExpediente,
            @RequestParam Integer year
    ){
        SentenciaPublicaResponseRecord sentenciaResponse = carpetaService.getCarpetaByExpedienteAndSentencia(
                numExpediente + "/" + year);
        return ResponseEntity.ok(sentenciaResponse);
    }
}
