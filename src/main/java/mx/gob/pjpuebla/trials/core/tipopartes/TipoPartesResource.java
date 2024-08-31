package mx.gob.pjpuebla.trials.core.tipopartes;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import mx.gob.pjpuebla.trials.util.Estado;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/core/tipopartes")
@SecurityRequirement(name = "Keycloak")
public class TipoPartesResource {
    private final TipoPartesService tipoPartesService;

    @GetMapping
    public Page<TipoPartesRecord> getAll(@PageableDefault(size = 20) Pageable pageable,
                                         @RequestParam(value = "tipoPartesNombre", required = false) String tipoPartesNombre) {
        return tipoPartesService.getAll(pageable,  new TipoPartes().setNombre(tipoPartesNombre));
    }

    @GetMapping("/{id}")
    public TipoPartesRecord getById(@PathVariable Integer id) {
        return this.tipoPartesService.findById(id);
    }

    @GetMapping("/materias/{materiaId}")
    public List<TipoPartesRecord> getByMateriaId(@PathVariable Integer materiaId) {
        return this.tipoPartesService.findByMateriaId(materiaId);
    }

}
