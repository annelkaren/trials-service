package mx.gob.pjpuebla.trials.core.juzgados;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mx.gob.pjpuebla.trials.core.sedes.Sede;
import mx.gob.pjpuebla.trials.util.Response;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/core/juzgados")
@SecurityRequirement(name = "Keycloak")
public class JuzgadosResource {

    private final JuzgadoService juzgadoService;

    @GetMapping
    public Page<JuzgadoRecordResponse> getAll(
            @PageableDefault(size = 20) Pageable pageable,
            @RequestParam(value = "nombre", required = false) String nombre) {
        return this.juzgadoService.getAll(new Juzgado().setNombre(nombre), pageable);
    }

    @GetMapping("/{id}")
    public JuzgadoRecord getById(@PathVariable Integer id) {
        return this.juzgadoService.findById(id);
    }

    @PostMapping
    public Response create(@RequestBody @Valid Juzgado juzgado, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return new Response(bindingResult.getFieldErrors());
        }
        return this.juzgadoService.create(juzgado);
    }

    @PutMapping
    public Response update(@RequestBody @Valid Juzgado juzgado, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return new Response(bindingResult.getFieldErrors());
        }
        return this.juzgadoService.update(juzgado);
    }

    @DeleteMapping("/{id}")
    public Response delete(@PathVariable Integer id) {
        return this.juzgadoService.delete(id);
    }
}
