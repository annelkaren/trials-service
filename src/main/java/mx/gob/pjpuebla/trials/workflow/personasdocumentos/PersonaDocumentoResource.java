package mx.gob.pjpuebla.trials.workflow.personasdocumentos;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/workflow/personasdocumentos")
@SecurityRequirement(name = "Keycloak")
public class PersonaDocumentoResource {

    private final PersonaDocumentoService personaDocumentoService;

    @GetMapping("/{carpetaId}/{tipo}")
    public List<PersonaDocumentoNameRecord> getTipoPartesPrincipales(
            @PathVariable Integer carpetaId,
            @PathVariable String tipo) {
        return personaDocumentoService.getTipoPartesPrincipales(carpetaId, tipo);
    }
}
