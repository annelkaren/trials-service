package mx.gob.pjpuebla.trials.workflow.listaestrados;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/workflow/listaestrado")
@SecurityRequirement(name = "Keycloak")
public class ListaEstradoResource {

    private final ListaEstradoService listaEstradoService;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public Page<ListaEstradoRecord> getAll(
            @PageableDefault(size = 20) Pageable pageable,
            @RequestParam(value = "listaEstrado", required = false) Integer listaEstrado,
            @RequestParam(value = "searchQuery", required = false) String searchQuery
    ) {
        return listaEstradoService.findAllByListaEstradoId(listaEstrado, searchQuery, pageable);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getReporteListaEstrados(@PathVariable("id") Integer id) {
        return listaEstradoService.getReporteListaEstrados(id);
    }
}
