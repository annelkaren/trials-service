package mx.gob.pjpuebla.trials.core.roles;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mx.gob.pjpuebla.trials.core.personas.PersonaService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/core/roles")
@SecurityRequirement(name = "Keycloak")
public class RoleResource {

    private final RoleService roleService;
    private final PersonaService personaService;

    @GetMapping
    public List<RoleRecord> getAll() {
        return roleService.getAll();
    }

    @GetMapping("/{userId}/{tipoCentroTrabajo}")
    public List<RoleRecord> getAllAvailablesByUserId(
            @PathVariable String userId,
            @PathVariable String tipoCentroTrabajo) {
        if (userId.isEmpty() || userId.equalsIgnoreCase("undefined")) {
            return getAll();
        }
        return roleService.getAllAvailablesByUserId(userId, tipoCentroTrabajo);
    }
}
