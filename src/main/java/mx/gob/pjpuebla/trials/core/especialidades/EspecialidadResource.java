package mx.gob.pjpuebla.trials.core.especialidades;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/core/especialidades")
@SecurityRequirement(name = "Keycloak")
public class EspecialidadResource {
    private final EspecialidadService especialidadService;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public Page<EspecialidadRecord> getAll(@PageableDefault(size = 20) Pageable pageable, @RequestParam(value = "especialidadNombre", required = false) String especialidadNombre) {
        return especialidadService.getAllActive(pageable, new Especialidad().setNombre(especialidadNombre));
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public EspecialidadRecord getById(@PathVariable Integer id) {
        return especialidadService.findById(id);
    }


}
