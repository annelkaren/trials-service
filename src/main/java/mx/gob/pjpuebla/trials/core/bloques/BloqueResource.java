package mx.gob.pjpuebla.trials.core.bloques;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@RequiredArgsConstructor
@RestController
@RequestMapping("/api/core/bloques")

public class BloqueResource {

    private final BloqueService bloqueService;

    @GetMapping
    public List<Bloque> getAll() {
        List<Bloque> bloques = bloqueService.findAll();
        System.out.println("Bloques: " + bloques); // O usa un logger

        return this.bloqueService.findAll();
    }
    
    
}
