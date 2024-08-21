package mx.gob.pjpuebla.trials.core.listavalor;

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
@RequestMapping("/api/core/listavalor")
@SecurityRequirement(name = "Keycloak")
public class ListaValorResource {

    private final ListaValorService listaValorService;

    @GetMapping
    public Response getAll(@PageableDefault(size = 20) Pageable pageable) {
        return this.listaValorService.getAll(pageable);
    }

    @GetMapping("/{id}")
    public Response getById(@PathVariable Integer id) {
        return this.listaValorService.findById(id);
    }

    @PostMapping
    public Response create(@RequestBody @Valid ListaValor listaValor, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return new Response(bindingResult.getFieldErrors());
        }
        return this.listaValorService.create(listaValor);
    }

    @PutMapping
    public Response update(@RequestBody @Valid ListaValor listaValor, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return new Response(bindingResult.getFieldErrors());
        }
        return this.listaValorService.update(listaValor);
    }

    @DeleteMapping("/{id}")
    public Response delete(@PathVariable Integer id) {
        return this.listaValorService.delete(id);
    }
}
