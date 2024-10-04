package mx.gob.pjpuebla.trials.workflow.carpeta;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import mx.gob.pjpuebla.trials.workflow.carpeta.records.CarpetaResponseRecord;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/workflow/carpeta")
@SecurityRequirement(name = "Keycloak")
public class CarpetaResource {

    private final CarpetaService carpetaService;

    @GetMapping()
    public ResponseEntity<CarpetaResponseRecord> getCarpetaByExpedienteAndJuzgadoId(
            @RequestParam String numExpediente,
            @RequestParam Integer year,
            @RequestParam Integer idJuzgado
    ) {
        CarpetaResponseRecord carpetaResponseRecord = carpetaService.getCarpetaResponseByNumExpYearJuzgado(
                numExpediente + "/" + year, idJuzgado);
        return ResponseEntity.ok(carpetaResponseRecord);
    }

}
