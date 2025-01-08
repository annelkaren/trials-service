package mx.gob.pjpuebla.trials.core.tipoprueba;

import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RequestMapping("/api/core/tipoPruebas")
@SecurityRequirement(name = "Keycloak")
@RestController
public class TipoPruebaResource {

    private final TipoPruebasService tipoPruebasService;

    @GetMapping("/by-name/{nombre}")
    public ResponseEntity<TipoPruebas> obtenerTipoPruebasPorNombre(@PathVariable String nombre) {
        Optional<TipoPruebas> tipoPruebas = tipoPruebasService.obtenerTipoPruebasPorNombre(nombre);
        return tipoPruebas.map(ResponseEntity::ok)
                        .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/by-tipo-juicio/{carpetaId}")
    public ResponseEntity<List<TipoPruebas>> getTipoPruebasByTipoJuicioId(@PathVariable Integer carpetaId) {
        List<TipoPruebas> tipoPruebas = tipoPruebasService.getTipoPruebasByTipoJuicioId(carpetaId);
        
        if (tipoPruebas.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(tipoPruebas);
    }
}