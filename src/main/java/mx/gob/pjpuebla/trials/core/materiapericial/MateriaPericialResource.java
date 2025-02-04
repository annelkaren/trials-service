package mx.gob.pjpuebla.trials.core.materiapericial;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/core/materiapericial")
@SecurityRequirement(name = "Keycloak")
public class MateriaPericialResource {

    private final MateriaPericialService materialPericialService;


    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<MateriaPericial> getMateriaPericialList(@RequestParam(value = "nombre", required = false) String nombre) {
        return materialPericialService.getallMateriaParicial(nombre);
    }
}