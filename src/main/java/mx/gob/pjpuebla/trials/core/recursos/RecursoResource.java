package mx.gob.pjpuebla.trials.core.recursos;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import mx.gob.pjpuebla.trials.core.recursos.menu.Node;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/core/recursos")
@SecurityRequirement(name = "Keycloak")
public class RecursoResource {

    private final RecursoService recursoService;

    @GetMapping
    public Node getAll() {
        return this.recursoService.getMenu();
    }
}
