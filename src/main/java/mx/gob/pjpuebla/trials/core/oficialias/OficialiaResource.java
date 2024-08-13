package mx.gob.pjpuebla.trials.core.oficialias;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import mx.gob.pjpuebla.trials.util.Response;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/core/oficialias")
@SecurityRequirement(name = "Keycloak")
public class OficialiaResource {

    private final OficialiaService oficialiaService;

    @GetMapping
    public Response getAll(@PageableDefault(size = 20) Pageable pageable) {
        return this.oficialiaService.getAll(pageable);
    }

    @GetMapping("/{id}")
    public Response getById(@PathVariable Integer id) {
        return this.oficialiaService.findById(id);
    }

    @PostMapping
    public Response create(@RequestBody Oficialia oficialia, BindingResult bindingResult) {
        return this.oficialiaService.create(oficialia, bindingResult);
    }

    @PutMapping
    public Response update(@RequestBody Oficialia oficialia) {
        return this.oficialiaService.update(oficialia);
    }

    @DeleteMapping("/{id}")
    public Response delete(@PathVariable Integer id) {
        return this.oficialiaService.delete(id);
    }
}