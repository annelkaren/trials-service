package mx.gob.pjpuebla.trials.core.personas;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@RequiredArgsConstructor
@RestController
@RequestMapping("/api/core/personas")
@SecurityRequirement(name = "Keycloak")
public class PersonaResource {

    private final PersonaService personaService;

    @GetMapping
    public Page<PersonaRecordResponse> getAll(
            @PageableDefault(size = 20) Pageable pageable,
            @RequestParam(value = "nombre", required = false) String nombre) {
        return this.personaService.getAll(new Persona().setNombre(nombre), pageable);
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public PersonaRecord getById(@PathVariable Long id) {
        return this.personaService.findById(id);
    }

    @GetMapping("/jueces/{juzgadoId}")
    public List<JuezRecord> getJueces(@PathVariable Integer juzgadoId) {
        return this.personaService.findAllJueces(juzgadoId);
    }

    @PostMapping
    public PersonaRecordResponse create(@RequestBody @Valid PersonaDTO persona) {
        return this.personaService.create(persona.getPersona(), persona.getRoles());
    }

    @PutMapping
    public PersonaRecordResponse update(@RequestBody @Valid PersonaDTO persona) {
        return this.personaService.update(persona.getPersona(), persona.getRoles());
    }

    @GetMapping(value = "/curp/{curp}", produces = MediaType.APPLICATION_JSON_VALUE)
    public PersonaRecord findByCurp(@PathVariable String curp) {
        return this.personaService.findByCurp(curp);
    }

}