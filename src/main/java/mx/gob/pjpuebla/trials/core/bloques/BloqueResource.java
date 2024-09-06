package mx.gob.pjpuebla.trials.core.bloques;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import mx.gob.pjpuebla.trials.core.salas.Sala;
import mx.gob.pjpuebla.trials.core.salas.SalaRecord;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;



@RequiredArgsConstructor
@RestController
@RequestMapping("/api/core/bloques")
@SecurityRequirement(name = "keycloak")
public class BloqueResource {

    private final BloqueService bloqueService;


    @GetMapping
    public List<Bloque> getAll() {
                    
        return this.bloqueService.getAll2();
    }
    
    
}
