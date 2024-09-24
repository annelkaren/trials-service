package mx.gob.pjpuebla.trials.core.instituciones;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/core/instituciones")
@SecurityRequirement(name = "keycloak")
public class InstitucionResource {

    private final InstitucionService institucionService;

    @GetMapping
    public Page<InstitucionRecord> getAll(
        @PageableDefault(size = 20) Pageable pageable,
        @RequestParam(value = "nombre", required = false) String nombre) {
          
        return this.institucionService.getAll(new Institucion().setNombre(nombre), pageable);
    }

    @GetMapping("/{id}")
    public InstitucionRecordResponse  getById(@PathVariable Integer id) {
        return this.institucionService.findById(id);
    }

    @PostMapping
    public Integer create(@RequestBody @Valid Institucion institucion) {
        return this.institucionService.create(institucion);
    }

    @PutMapping
    public Integer update(@RequestBody Institucion institucion) {
        return this.institucionService.update(institucion);
    }
    
}
