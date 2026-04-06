package mx.gob.pjpuebla.trials.core.oficialias;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import mx.gob.pjpuebla.trials.core.oficialias.records.OficialiaRecord;
import mx.gob.pjpuebla.trials.core.oficialias.records.OficialiaRecordResponse;
import mx.gob.pjpuebla.trials.util.enums.Estado;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/core/oficialias")
@SecurityRequirement(name = "Keycloak")
public class OficialiaResource {

    private final OficialiaService oficialiaService;

    @GetMapping
    public Page<OficialiaMateriaRecord> getAll(
            @PageableDefault(size = 20) Pageable pageable,
            @RequestParam(value = "key", required = false) String key,
            @RequestParam(value = "nombre", required = false) String nombre,
            @RequestParam(value = "materia", required = false) String materia,
            @RequestParam(value = "tipo", required = false) String tipo,
            @RequestParam(value = "juzgado", required = false) String juzgado,
            @RequestParam(value = "estatus", required = false) Estado estatus) {
        return this.oficialiaService.getAllByOficialiaMateria(key, nombre, materia, tipo, juzgado, estatus,
                pageable);
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

}