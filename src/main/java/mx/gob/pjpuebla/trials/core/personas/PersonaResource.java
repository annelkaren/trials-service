package mx.gob.pjpuebla.trials.core.personas;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mx.gob.pjpuebla.trials.util.Response;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/core/personas")
@SecurityRequirement(name = "Keycloak")
public class PersonaResource {

    private final PersonaService personaService;

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public PersonaRecord getById(@PathVariable Long id) {
        return this.personaService.findById(id);
    }

    @PostMapping
    @ResponseBody
    public PersonaRecord create(@RequestBody @Valid Persona persona) {
        return this.personaService.create(persona);
    }

    @PutMapping
    public PersonaRecord update(@RequestBody @Valid Persona persona) {
        return this.personaService.update(persona);
    }
}