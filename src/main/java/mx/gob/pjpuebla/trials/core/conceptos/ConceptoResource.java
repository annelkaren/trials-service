package mx.gob.pjpuebla.trials.core.conceptos;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/core/conceptos")
@SecurityRequirement(name = "keycloak")
public class ConceptoResource {

    private final ConceptoService conceptoService;

    @GetMapping("/carpetaId/{carpetaId}")
    public List<ConceptoRecordResponse> getAll(@PathVariable Integer carpetaId) {
        return this.conceptoService.getAll(carpetaId);
    }

    @GetMapping("/{id}")
    public ConceptoRecordResponse findById(@PathVariable Integer id) {
        return this.conceptoService.findById(id);
    }
}
