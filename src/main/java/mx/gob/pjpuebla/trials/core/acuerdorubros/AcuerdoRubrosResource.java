package mx.gob.pjpuebla.trials.core.acuerdorubros;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/core/acuerdorubros")
@SecurityRequirement(name = "keycloak")
public class AcuerdoRubrosResource {

    private final AcuerdoRubrosService acuerdoRubrosService;

    @GetMapping
    public Page<AcuerdoRubrosRecord> getAll(@PageableDefault(size = 20) Pageable pageable) {
        return this.acuerdoRubrosService.getAll(pageable);
    }

    @GetMapping("/{id}")
    public AcuerdoRubrosRecord findById(@PathVariable Integer id) {
        return this.acuerdoRubrosService.findById(id);
    }

    @GetMapping("/autocomplete/{idDocumento}")
    public Page<AcuerdoRubrosRecord> findDocumentoById(@PathVariable Integer idDocumento, @PageableDefault Pageable pageable) {
        return this.acuerdoRubrosService.findRubrosByDocumentoId(idDocumento, pageable);
    }
}
