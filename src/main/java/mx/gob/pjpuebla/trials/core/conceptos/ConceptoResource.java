package mx.gob.pjpuebla.trials.core.conceptos;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/core/conceptos")
@SecurityRequirement(name = "keycloak")
public class ConceptoResource {

    private final ConceptoService conceptoService;

    @GetMapping("/carpetaId/{carpetaId}")
    public List<ConceptoRecordResponse> getAll(@PathVariable Integer carpetaId) {
        return this.conceptoService.getAll(carpetaId);
    }

    @GetMapping("/{id}")
    public ConceptoRecordResponse findById(@PathVariable Integer id) {
        return this.conceptoService.findById(id);
    }

    @GetMapping(value = "/registros", produces = MediaType.APPLICATION_JSON_VALUE)
    public Page<ConceptoRecord> getallConceptos(
            @PageableDefault(size = 25) Pageable pageable,
            @RequestParam(value = "key", required = false) String key
    ) {
        return conceptoService.getAllConceptos(pageable, key);
    }

    @PatchMapping("/{id}/status/{status}")
    public ConceptoRecord updateStatus(@PathVariable Integer id, @PathVariable Integer status) {
        return conceptoService.updateStatus(id, status);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {
        conceptoService.delete(id);
    }

    @PostMapping
    public List<ConceptoRecord> create(@RequestBody ConceptoBulkRequest concepto) {
        return conceptoService.createConcepto(concepto);
    }

    @GetMapping("/conceptoJuicio/{id}")
    public ConceptoRecordJuicio findByIdConceptoJuicio(@PathVariable Integer id) {
        return this.conceptoService.findByConceptoById(id);
    }

    @PutMapping
    public ConceptoRecord update(@RequestBody ConceptoBulkRequest concepto) {
        return conceptoService.updateConcepto(concepto);
    }
}
