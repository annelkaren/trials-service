package mx.gob.pjpuebla.trials.core.tipopartes;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import mx.gob.pjpuebla.trials.util.Response;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/api/core/tipopartes")
@SecurityRequirement(name = "Keycloak")
public class TipoPartesResource {
    private final TipoPartesService tipoPartesService;

    @GetMapping
    public Response getAll(@PageableDefault(size = 20) Pageable pageable) {
        return this.tipoPartesService.getAll(pageable);
    }

    @GetMapping("/{id}")
    public Response getById(@PathVariable Integer id) {
        return this.tipoPartesService.findById(id);
    }

    @PostMapping
    public Response create(@RequestBody TipoPartes tipoPartes, BindingResult bindingResult) {
        return this.tipoPartesService.create(tipoPartes, bindingResult);
    }

    @PutMapping
    public Response update(@RequestBody TipoPartes tipoPartes) {
        return this.tipoPartesService.update(tipoPartes);
    }

    @DeleteMapping("/{id}")
    public Response delete(@PathVariable Integer id) {
        return this.tipoPartesService.delete(id);
    }
}
