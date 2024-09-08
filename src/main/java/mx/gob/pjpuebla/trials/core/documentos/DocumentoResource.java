package mx.gob.pjpuebla.trials.core.documentos;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/core/documentos")
@SecurityRequirement(name = "Keycloak")
public class DocumentoResource {

    private final DocumentoService documentoService;

    @PostMapping
    public DocumentoRecord create(@RequestBody DocumentoDTO documentoDTO) {
        return this.documentoService.create(documentoDTO);
    }

}
