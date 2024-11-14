package mx.gob.pjpuebla.trials.workflow.carpeta;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import mx.gob.pjpuebla.trials.workflow.carpeta.records.*;
import mx.gob.pjpuebla.trials.workflow.documentos.records.DocumentoRecepcionMovimientosRecord;
import mx.gob.pjpuebla.trials.workflow.documentos.records.DocumentoRecord;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;



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

    @GetMapping(value= "/piezas/numPieza")
    public String getConsecutivoPiezas(@RequestParam String clavePieza, @RequestParam Integer carpetaId) {
        return this.carpetaService.consecutivoPieza(carpetaId, clavePieza);
    }
    
    
}
