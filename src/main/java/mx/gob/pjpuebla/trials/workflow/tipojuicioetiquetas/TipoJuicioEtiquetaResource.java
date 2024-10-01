package mx.gob.pjpuebla.trials.workflow.tipojuicioetiquetas;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/workflow/etiquetas")
@SecurityRequirement(name = "Keycloak")
public class TipoJuicioEtiquetaResource {

    private final TipoJuicioEtiquetaService tipoJuicioEtiquetaService;

    @GetMapping(value = "/{tipoJuicioId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Cacheable(value = "etiquetas", key = "#tipoJuicioId")
    public List<TipoJuicioEtiquetaItem> getAllByTipoJuicioId(@PathVariable Integer tipoJuicioId) {
        return this.tipoJuicioEtiquetaService.getAllByTipoJuicioId(tipoJuicioId);
    }
}
