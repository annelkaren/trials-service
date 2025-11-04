package mx.gob.pjpuebla.migracion.usecases;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import mx.gob.pjpuebla.migracion.readers.entradas.EntradasMigracionSaveRecord;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/migracion/expediente")
@RequiredArgsConstructor
@SecurityRequirement(name = "keycloak")
public class MigrarExpedienteResource {

    private final MigrarExpedienteUseCase migrarExpediente;

    @PostMapping
    public ResponseEntity<String> migrarExpedienteCompleto(@RequestBody EntradasMigracionSaveRecord request) {
        migrarExpediente.migrarExpedienteCompleto(request.expediente(), request.year(), request.juzgado());

        return ResponseEntity.ok("Expediente migrado correctamente");
    }

    @PostMapping("/principal")
    public ResponseEntity<String> migrarExpediente(@RequestBody EntradasMigracionSaveRecord request) {

        migrarExpediente.migrarExpediente(request.expediente(), request.year(), request.juzgado());

        return ResponseEntity.ok("Expediente migrado correctamente");
    }

}
