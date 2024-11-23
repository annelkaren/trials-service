package mx.gob.pjpuebla.trials.workflow.personadetalle;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/workflow/personaDetalle")
@SecurityRequirement(name = "Keycloak")
public class PersonaDetalleResource {
    private final PersonaDetalleService personaDetalleService;
    
    @PostMapping("/create")
    public PersonaDetalleRecord createPersonaDetalle(@RequestBody PersonaDTO personaDTO) {
        return personaDetalleService.createPersonaDetalle(personaDTO);
    }
} 
