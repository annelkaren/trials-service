package mx.gob.pjpuebla.trials.core.templates;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mx.gob.pjpuebla.trials.core.templates.placeholders.Placeholders;
import mx.gob.pjpuebla.trials.core.templates.placeholders.PlaceholdersService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/core/templates")
@SecurityRequirement(name = "Keycloak")
public class TemplatesResource {

    private final PlaceholdersService placeholdersService;
    private final TemplatesService templatesService;

    @GetMapping("/placeholders")
    public List<Placeholders> getAllPlaceholders() {
        return this.placeholdersService.getPlaceholderList();
    }

    @GetMapping("/")
    public List<TempletesPlaceholderRecord> getTemplatesAndPlaceholders() {
        return  templatesService.getTempletesAndPlaceholder();
    }

    @PostMapping("/create")
    public TempleteRecordResponse create(@RequestBody @Valid Templates templates) {
        return this.templatesService.create(templates);
    }

    @PutMapping
    public TempleteRecordResponse update(@RequestBody @Valid Templates templates) {
        return this.templatesService.update(templates);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {
        this.templatesService.delete(id);
    }

}
