package mx.gob.pjpuebla.trials.workflow.carpeta;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import mx.gob.pjpuebla.trials.workflow.carpeta.records.*;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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
    public BandejaRecepcionRecord obtenerBandejaRecepcion(@RequestParam String folio) {
        return this.carpetaService.getBandejaRecepcionByFolio(folio);
    }
    
}
