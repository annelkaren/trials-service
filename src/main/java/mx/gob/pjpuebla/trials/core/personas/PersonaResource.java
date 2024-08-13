package mx.gob.pjpuebla.trials.core.personas;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mx.gob.pjpuebla.trials.util.Response;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/core/personas")
@SecurityRequirement(name = "Keycloak")
public class PersonaResource {

    private final PersonaService personaService;

    @GetMapping
    public Response getAll(@PageableDefault(size = 20) Pageable pageable) {
        return this.personaService.getAll(pageable);
    }

    @GetMapping("/{id}")
    public Response getById(@PathVariable Long id) {
        return this.personaService.findById(id);
    }

    @PostMapping
    @ResponseBody
    public Response create(@RequestBody @Valid Persona persona, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return new Response(bindingResult.getFieldErrors());
        }
        return this.personaService.create(persona);
    }

    @PutMapping
    public Response update(@RequestBody @Valid Persona persona, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return new Response(bindingResult.getFieldErrors());
        }
        return this.personaService.update(persona);
    }

    @DeleteMapping("/{id}")
    public Response delete(@PathVariable Long id) {
        return this.personaService.delete(id);
    }
}
