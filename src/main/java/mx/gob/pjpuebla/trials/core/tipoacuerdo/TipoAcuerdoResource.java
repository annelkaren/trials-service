package mx.gob.pjpuebla.trials.core.tipoacuerdo;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/core/tipoacuerdo")
@SecurityRequirement(name = "keycloak")
public class TipoAcuerdoResource {

    private final TipoAcuerdoService tipoAcuerdoService;

    @GetMapping(value = "/{carpetaId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<TipoAcuerdoRecord>> findByDocumentoId(@PathVariable Integer carpetaId) {
        List<TipoAcuerdoRecord> tipoAcuerdo = tipoAcuerdoService.findByDocumentoId(carpetaId);
        return ResponseEntity.ok(tipoAcuerdo);
    }

    @GetMapping(value = "/filtro", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<TipoAcuerdoRecord> getTiposAcuerdo(
            @RequestParam Integer juzgadoId,
            @RequestParam Integer materiaId
    ) {
        return tipoAcuerdoService.findTiposAcuerdoParaFiltro(juzgadoId, materiaId);
    }
}
