package mx.gob.pjpuebla.trials.core.recursos;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mx.gob.pjpuebla.trials.core.personas.Persona;
import mx.gob.pjpuebla.trials.core.personas.PersonaService;
import mx.gob.pjpuebla.trials.core.roles.RoleRecord;
import mx.gob.pjpuebla.trials.core.roles.RoleService;
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
    private final RoleService roleService;
    private final PersonaService personaService;

    @GetMapping
    public PermissionRecord getPermission() {
        return new PermissionRecord(getRoles(), recursoService.getPermission());
    }

    private String getRoles() {
        List<String> roles;
        Persona persona = personaService.getAuditor();
        List<RoleRecord> list = roleService.getRolesByUserId(persona.getUsuario());
        roles = list.stream()
                .map(RoleRecord::id)
                .toList();
        return String.join("", roles);
    }
}