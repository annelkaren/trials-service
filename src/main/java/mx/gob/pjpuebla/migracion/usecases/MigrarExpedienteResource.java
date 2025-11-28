package mx.gob.pjpuebla.migracion.usecases;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mx.gob.pjpuebla.migracion.readers.entradas.EntradasMigracionSaveRecord;
import mx.gob.pjpuebla.trials.error.ApiResponse;
import mx.gob.pjpuebla.trials.error.ApiResponseFactory;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/migracion/expediente")
@RequiredArgsConstructor
@SecurityRequirement(name = "keycloak")
public class MigrarExpedienteResource {

 private final MigrarExpedienteUseCase migrarExpedienteUseCase;

    // 1) Migrar solo expediente principal
    @PostMapping("/principal")
    public ResponseEntity<ApiResponse<MigracionExpedienteResult>> migrarExpedientePrincipal(
            @RequestBody @Valid EntradasMigracionSaveRecord request
    ) {
        MigracionExpedienteResult result = migrarExpedienteUseCase.migrarExpedientePrincipal(
                request.expediente(),
                request.year(),
                request.juzgado()
        );

        ApiResponse<MigracionExpedienteResult> response =
                ApiResponseFactory.success("Expediente principal migrado correctamente", result);

        return ResponseEntity
                .status(response.getStatus())
                .body(response);
    }

    // 2) Migrar expediente completo (expediente + documentos)
    @PostMapping("/completo")
    public ResponseEntity<ApiResponse<MigracionExpedienteResult>> migrarExpedienteCompleto(
            @RequestBody @Valid EntradasMigracionSaveRecord request
    ) {
        MigracionExpedienteResult result = migrarExpedienteUseCase.migrarExpedienteCompleto(
                request.expediente(),
                request.year(),
                request.juzgado()
        );

        ApiResponse<MigracionExpedienteResult> response =
                ApiResponseFactory.success("Expediente migrado completamente (expediente + documentos)", result);

        return ResponseEntity
                .status(response.getStatus())
                .body(response);
    }

}
