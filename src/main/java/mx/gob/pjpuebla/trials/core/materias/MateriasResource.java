package mx.gob.pjpuebla.trials.core.materias;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import mx.gob.pjpuebla.trials.util.Response;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/core/materias")
@SecurityRequirement(name = "Keycloak")
public class MateriasResource {

    private final MateriaService materiaService;

    @GetMapping
    public Response getAll(@PageableDefault(size = 20) Pageable pageable) {
        return this.materiaService.getAll(pageable);
    }

    @GetMapping("/{id}")
    public Response getById(@PathVariable Integer id) {
        return this.materiaService.findById(id);
    }

    @PostMapping
    public Response create(@RequestBody Materia materia, BindingResult bindingResult) {
        return this.materiaService.create(materia, bindingResult);
    }

    @PutMapping
    public Response update(@RequestBody Materia materia) {
        return this.materiaService.update(materia);
    }

    @DeleteMapping("/{id}")
    public Response delete(@PathVariable Integer id) {
        return this.materiaService.delete(id);
    }


}
