package mx.gob.pjpuebla.migracion.readers.entradas;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
@RestController
@RequestMapping("/api/migracion/expediente")
@RequiredArgsConstructor
@SecurityRequirement(name = "keycloak")
public class EntradasMigracionResource {

    private final EntradasMigracionReader service;

    @GetMapping
    public ResponseEntity<EntradasMigracionRecord> buscarPorFiltros(
            @RequestParam String expediente,
            @RequestParam Integer year,
            @RequestParam String juzgado
    ) {
        return ResponseEntity.ok(service.buscarPorFiltros(expediente, year, juzgado));
    }    
}