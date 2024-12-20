package mx.gob.pjpuebla.trials.core.materialpericial;

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
@RequestMapping("/api/core")
@SecurityRequirement(name = "Keycloak")
public class MaterialPericialResource {

    private final MaterialPericialService materialPericialService;


    @GetMapping(value = "/materialpericial", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<MaterialPericial> getMaterialPericialList(@RequestParam(value = "nombre", required = false) String nombre) {
        return materialPericialService.getallMaterialParicial(nombre);
    }
}