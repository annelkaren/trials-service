package mx.gob.pjpuebla.trials.core.oficialias;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import mx.gob.pjpuebla.trials.core.tipooficialias.TipoOficialia;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/core/oficialias")
@SecurityRequirement(name = "Keycloak")
public class OficialiaResource {

    private final OficialiaService oficialiaService;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public Page<OficialiaRecord> getAll(
            @PageableDefault(size = 20) Pageable pageable,
            @RequestParam(value = "nombre", required = false) String nombre,
            @RequestParam(value = "tipoOficialiaNombre", required = false) String tipoOficialiaId
    ) {
        return oficialiaService.getAllActive(pageable, new Oficialia()
                .setNombre(nombre)
                .setTipoOficialia(new TipoOficialia().setNombre(tipoOficialiaId))
        );

    }

    @GetMapping("/{id}")
    public OficialiaRecord getById(@PathVariable Integer id) {
        return this.oficialiaService.findById(id);
    }

    @PostMapping
    public OficialiaRecordResponse create(@RequestBody Oficialia oficialia) {
        return this.oficialiaService.create(oficialia);
    }

    @PutMapping
    public OficialiaRecordResponse update(@RequestBody Oficialia oficialia) {
        return this.oficialiaService.update(oficialia);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {
        this.oficialiaService.delete(id);
    }

    @GetMapping("/oficialiaMateria")
    public Page<OficialiaMateriaRecord> getOficialiaMateria(@PageableDefault(size = 20) Pageable pageable) {
        return this.oficialiaService.getAllByOficialiaMateria(pageable);
    }

}