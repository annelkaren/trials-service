package mx.gob.pjpuebla.trials.core.documentoidentificacion;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/core/documentoidentificacion")
@SecurityRequirement(name = "Keycloak")
public class DocumentoIdentificacionResource {

    private final DocumentoIdentificacionService documentoIdentificacionService;

    @GetMapping
    public List<IdentificacionDocRecord> getAll(){return this.documentoIdentificacionService.getAll();}
}
