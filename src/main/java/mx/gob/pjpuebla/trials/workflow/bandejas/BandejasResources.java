package mx.gob.pjpuebla.trials.workflow.bandejas;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import mx.gob.pjpuebla.trials.error.ApiResponse;
import mx.gob.pjpuebla.trials.error.ApiResponseFactory;
import mx.gob.pjpuebla.trials.util.enums.EstadoMigracion;
import mx.gob.pjpuebla.trials.workflow.bandejas.records.BandejaMigracionFilter;
import mx.gob.pjpuebla.trials.workflow.bandejas.records.BandejaMigracionResponse;
import mx.gob.pjpuebla.trials.workflow.bandejas.records.BandejaRequest;
import mx.gob.pjpuebla.trials.workflow.bandejas.records.entrada.BandejaEntradaResponse;
import mx.gob.pjpuebla.trials.workflow.migracion.MigracionesService;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/workflow/bandeja")
@SecurityRequirement(name = "Keycloak")
public class BandejasResources {

    private final MigracionesService migracionesService;
    private final BandejasService bandejasService;

    @GetMapping("/migracion")
    public Page<BandejaMigracionResponse> bandeja(
            @RequestParam(required = false) EstadoMigracion estado,
            @RequestParam(required = false) Integer juzgadoId,
            @RequestParam(required = false) Integer carpetaId,
            @RequestParam(required = false) String expediente,
            @RequestParam(required = false) String key,
            Pageable pageable) {
        var filter = new BandejaMigracionFilter(estado, juzgadoId, carpetaId, expediente, key);
        return migracionesService.listar(filter, pageable);
    }

    @PostMapping("/migracion/turnar")
    public ResponseEntity<ApiResponse<String>> turnarExpedienteMigracion(@RequestBody BandejaRequest req) {

        ApiResponse<String> result = migracionesService.turnarExpedienteMigrado(req.migracionId(), req.personaId());

        // si el servicio decide que ya estaba asignado
        if (result.getCode().equals(ApiResponseFactory.SUCCESS_ALREADY_ASSIGNED)) {
            return ResponseEntity.ok(result); // 200 OK
        }

        // si efectivamente se creó el turnado
        return ResponseEntity.status(HttpStatus.CREATED).body(result); // 201 Created
    }

    //Bandeja de entrada:
    @GetMapping("/entrada")
    public Page<BandejaEntradaResponse> listarBandejaEntrada(Pageable pageable){
        return bandejasService.listarBandejaEntrada(pageable);
    }
}