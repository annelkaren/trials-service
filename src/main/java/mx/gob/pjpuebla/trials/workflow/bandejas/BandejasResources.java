package mx.gob.pjpuebla.trials.workflow.bandejas;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import mx.gob.pjpuebla.trials.util.enums.EstadoMigracion;
import mx.gob.pjpuebla.trials.workflow.bandejas.records.BandejaMigracionFilter;
import mx.gob.pjpuebla.trials.workflow.bandejas.records.BandejaMigracionResponse;
import mx.gob.pjpuebla.trials.workflow.migracion.MigracionesService;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/workflow/bandeja")
@SecurityRequirement(name = "Keycloak")
public class BandejasResources {

    private final MigracionesService migracionesService;

    @GetMapping("/migracion")
    public Page<BandejaMigracionResponse> bandeja(
            @RequestParam(required = false) EstadoMigracion estado,
            @RequestParam(required = false) Integer juzgadoId,
            @RequestParam(required = false) Integer carpetaId,
            @RequestParam(required = false) String expediente,
            @RequestParam(required = false) String q,
            Pageable pageable // acepta ?page=0&size=20&sort=estatus,asc&sort=id,desc
    ) {
        var filter = new BandejaMigracionFilter(estado, juzgadoId, carpetaId, expediente, q);
        return migracionesService.listar(filter, pageable);
    }
}
