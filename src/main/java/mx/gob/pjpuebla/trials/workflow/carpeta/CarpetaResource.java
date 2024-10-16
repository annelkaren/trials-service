package mx.gob.pjpuebla.trials.workflow.carpeta;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import mx.gob.pjpuebla.trials.workflow.anexos.AnexoBandejaRecepcionRecord;
import mx.gob.pjpuebla.trials.workflow.carpeta.records.*;
import mx.gob.pjpuebla.trials.workflow.documentos.records.DocumentoRecord;

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
            @RequestParam Integer idJuzgado
    ) {
        CarpetaResponseRecord carpetaResponseRecord = carpetaService.getCarpetaResponseByNumExpYearJuzgado(
                numExpediente + "/" + year, idJuzgado);
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
            @RequestBody List<AnexoBandejaRecepcionRecord> anexos,
            @PathVariable Integer documentoId) {
        return this.carpetaService.actualizarInformacionAnexos(anexos, documentoId);
    }
    
}
