package mx.gob.pjpuebla.trials.core.especialidadJuzgado;

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
public class EspecialidadesResource {
    private final EspecialidadesService especialidadesService;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public Page<EspecialidadesRecord> getAll(@PageableDefault(size = 20) Pageable pageable, @RequestParam(value = "especialidadesNombre", required = false) String especialidadesNombre) {
        return especialidadesService.getAllActive(pageable, new Especialidades().setNombre(especialidadesNombre));
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public EspecialidadesRecord getById(@PathVariable Integer id) {
        return especialidadesService.findById(id);
    }


}
