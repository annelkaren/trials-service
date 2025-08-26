package mx.gob.pjpuebla.migracion.expediente;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;




@RestController
@RequestMapping("/api/migracion/expediente")
@RequiredArgsConstructor
@SecurityRequirement(name = "keycloak")
public class EntradasMigracionResource {

    private final EntradasMigracionService service;

    @GetMapping
    public ResponseEntity<List<EntradasMigracionRecord>> buscarPorFiltros(
            @RequestParam String expediente,
            @RequestParam Integer year,
            @RequestParam String juzgado
    ) {
        return ResponseEntity.ok(service.buscarPorFiltros(expediente, year, juzgado));
    }

    @PostMapping
    public ResponseEntity<String> migrarExpediente(@RequestBody EntradasMigracionSaveRecord request) {
       
        return service.migrarExpediente(request.expediente(), request.year(), request.juzgado());
    }


    
    
}