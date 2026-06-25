package mx.gob.pjpuebla.trials.core.materias;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/core/materias")
@SecurityRequirement(name = "Keycloak")
public class MateriaResource {

    private final MateriaService materiaService;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public Page<MateriaRecord> getAll(
            @PageableDefault(size = 20) Pageable pageable,
            @RequestParam(value = "materiaNombre", required = false) String materiaNombre
    ) {
        return materiaService.getAllActive(pageable, new Materia().setNombre(materiaNombre));
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public MateriaRecord getById(@PathVariable Integer id) {
        return materiaService.findById(id);
    }

    @GetMapping(value = "/publicas", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<MateriaRecord> findMateriasPublicas() {
        return materiaService.findMateriasPublicas();
    }

    @GetMapping(value = "/countsentencias", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<SentenciasByMateriaRecord> getCountSentenciasByMaterias() {
        return materiaService.getCountSentenciasByMaterias();
    }

    @GetMapping("/select")
    public List<MateriaRecord> getMaterias() {
        return materiaService.getMaterias();
    }

    @GetMapping("/all")
    public List<Materia> getAllMateriasSinPaginar() {
        return materiaService.findMateriasAll();
    }
}
