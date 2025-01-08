package mx.gob.pjpuebla.trials.workflow.comentariosasistentes;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import mx.gob.pjpuebla.trials.workflow.comentariosasistentes.records.ComentariosAsistentesResponse;
import mx.gob.pjpuebla.trials.workflow.comentariosasistentes.records.ComentariosAsistentesRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/workflow/comentariosasistentes")
@SecurityRequirement(name = "Keycloak")
public class ComentariosAsistentesResource {

    private final ComentariosAsistentesService comentariosAsistentesService;

    @GetMapping("/comentariopersona/{personaDocumentoId}")
    public Page<ComentariosAsistentesResponse> getComentariosByPersonaDocumento(@PathVariable Integer personaDocumentoId, Pageable pageable) {
        return this.comentariosAsistentesService.getComentariosByPersonaDocumento(personaDocumentoId, pageable);
    }

    @PostMapping
    public ComentariosAsistentesResponse create(@RequestBody ComentariosAsistentesRecord comentariosAsistentes) {
        return this.comentariosAsistentesService.create(comentariosAsistentes);
    }

    @PatchMapping("/{id}")
    public ComentariosAsistentesResponse update(
            @PathVariable Integer id,
            @RequestBody ComentariosAsistentesRecord comentariosAsistentes) {
        return this.comentariosAsistentesService.update(id, comentariosAsistentes);
    }
}
