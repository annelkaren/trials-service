package mx.gob.pjpuebla.trials.workflow.carpeta;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CarpetaResponseRecord> getCarpetaByExpedienteAndJuzgadoId(@RequestBody CarpetaSearchRecord carpetaSearchRecord) {
        CarpetaResponseRecord carpetaResponseRecord = carpetaService.getCarpetaResponseByNumExpYearJuzgado(carpetaSearchRecord.numExpediente() + "/" + carpetaSearchRecord.year(), carpetaSearchRecord.idJuzgado());
        return ResponseEntity.ok(carpetaResponseRecord);
    }

    @GetMapping("/apelacion/{carpetaId}")
    public List<ApelacionRecordResponse> getPersonasDocumentoByCarpetaId(@PathVariable Integer carpetaId) {
        return this.carpetaService.getPersonasDocumentoByCarpetaId(carpetaId);
    }

    @PostMapping("/apelacion")
    public DocumentoRecord create(@RequestBody @Valid ApelacionRecord apelacionRecord) {
        return this.carpetaService.createApelacion(apelacionRecord);
    }


}
