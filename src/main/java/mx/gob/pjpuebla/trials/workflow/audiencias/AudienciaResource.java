package mx.gob.pjpuebla.trials.workflow.audiencias;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import mx.gob.pjpuebla.trials.workflow.audiencias.record.AudienciasGeneralesResponseRecord;
import mx.gob.pjpuebla.trials.workflow.carpeta.records.CarpetaCatalogoRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/workflow")
@SecurityRequirement(name = "Keycloak")
public class AudienciaResource {

    private final AudienciaService audienciaService;

    @GetMapping("/bandeja/audienciasgenerales")
    public Page<AudienciasGeneralesResponseRecord> getAllAudienciasGenerales(
            @RequestParam(value = "key", required = false) String key,
            @PageableDefault(size = 20) Pageable pageable) {
        return this.audienciaService.getAllAudienciasGenerales(key, pageable);
    }

    @DeleteMapping("/bandeja/audienciasgenerales/{id}")
    public void delete(@PathVariable Integer id) {
        this.audienciaService.deleteAudiencia(id);
    }

    @GetMapping(value = "/audiencias/motivos", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<CarpetaCatalogoRecord> getAudienciasMotivos(){
        return this.audienciaService.getAudienciasMotivos();
    }

}
