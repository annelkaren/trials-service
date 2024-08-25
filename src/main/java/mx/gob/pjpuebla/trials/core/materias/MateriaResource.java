package mx.gob.pjpuebla.trials.core.materias;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import mx.gob.pjpuebla.trials.util.Response;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpServerErrorException;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/core/materias")
@SecurityRequirement(name = "Keycloak")
public class MateriaResource {

    private final MateriaService materiaService;

    @GetMapping
    public Response getAll(
            @PageableDefault(size = 20) Pageable pageable,
            @RequestParam(value = "materiaNombre", required = false) String materiaNombre
    ) {
        //return materiaService.getAll(pageable, new Materia().setNombre(materiaNombre));
        return null;
    }

    @GetMapping("/{id}")
    public Response getById(@PathVariable Integer id) {
        //return materiaService.findById(id);
        return null;
    }


}
