package mx.gob.pjpuebla.migracion.juzgados;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/migracion/juzgados")
@RequiredArgsConstructor
@SecurityRequirement(name = "keycloak")
public class JuzgadosMigracionResource {
    private final JuzgadosMigracionService juzgadosService;

/**
     * Endpoint para obtener una lista completa de todos los juzgados.
     * @return Una respuesta con la lista completa de juzgados.
     */
    @GetMapping
    public ResponseEntity<List<JuzgadosMigracionRecord>> obtenerTodosLosJuzgados() {
        List<JuzgadosMigracionRecord> listaDeJuzgados = juzgadosService.buscarTodos();
        return ResponseEntity.ok(listaDeJuzgados);
    }

}
