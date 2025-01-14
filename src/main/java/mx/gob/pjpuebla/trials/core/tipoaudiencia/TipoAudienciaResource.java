package mx.gob.pjpuebla.trials.core.tipoaudiencia;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RequiredArgsConstructor
@RestController
@RequestMapping("/api/core/tipoaudiencia")
@SecurityRequirement(name = "keycloak")
public class TipoAudienciaResource {

    private final TipoAudienciaService tipoAudienciaService;

    @GetMapping
    public Page<TipoAudienciaRecord> getAll(@PageableDefault(size = 20) Pageable pageable) {
        return this.tipoAudienciaService.getAll(pageable);
    }

    @GetMapping("/{id}")
    public TipoAudienciaRecord findById(@PathVariable Integer id) {
        return this.tipoAudienciaService.findById(id);
    }

    @GetMapping("/autocomplete/{idDocumento}")
    public Page<TipoAudienciaRecord> findTipoAudienciaByDocumentoId(
            @PathVariable Integer idDocumento,
            @PageableDefault Pageable pageable,
            @RequestParam(value = "nombre", required = false) String nombre) {
        return this.tipoAudienciaService.findTipoAudienciaByDocumentoId(idDocumento, pageable, nombre);
    }

    @GetMapping("/autocomplete")
    public List<TipoAudienciaRecord> getAllAutocomplete(
            @PageableDefault(size = 20) Pageable pageable,
            @RequestParam(value = "nombre", required = false) String nombre) {
        return this.tipoAudienciaService.getAll(pageable, nombre);
    }

    @GetMapping("/materia/{materiaId}")
    public List<TipoAudienciaRecord> getTipoAudienciaByMateria(@PathVariable Integer materiaId) {
        return this.tipoAudienciaService.getTipoAudienciaByMateria(materiaId);
    }
    
}
