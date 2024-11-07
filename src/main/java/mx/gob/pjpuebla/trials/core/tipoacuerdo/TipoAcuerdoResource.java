package mx.gob.pjpuebla.trials.core.tipoacuerdo;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/core")
@SecurityRequirement(name = "keycloak")
public class TipoAcuerdoResource {

    private final TipoAcuerdoService tipoAcuerdoService;

    @GetMapping(value = "/tipoacuerdo/{documentoId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<TipoAcuerdoRecord>> findByDocumentoId(@PathVariable Integer documentoId) {
        List<TipoAcuerdoRecord> tipoAcuerdo = tipoAcuerdoService.findByDocumentoId(documentoId);
        return ResponseEntity.ok(tipoAcuerdo);
    }

}
