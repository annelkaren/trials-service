package mx.gob.pjpuebla.trials.core.recursos;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/core/recursos")
@SecurityRequirement(name = "Keycloak")
public class RecursoResource {

    private final RecursoService recursoService;

    @GetMapping
    public List<PolicyRecord> getPermission() {
        return recursoService.getPermission();
    }
}