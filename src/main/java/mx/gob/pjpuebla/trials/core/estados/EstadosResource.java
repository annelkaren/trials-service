package mx.gob.pjpuebla.trials.core.estados;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import mx.gob.pjpuebla.trials.util.Response;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/core/estados")
@SecurityRequirement(name = "Keycloak")
public class EstadosResource {

    private static final String INEGI_PATH = "https://gaia.inegi.org.mx/wscatgeo";
    private static final String STATES_PATH = "/mgee";
    private static final String MUN_PATH = "/mgem/";

    @GetMapping
    @Cacheable("estados")
    public Response getStates() {
        RestTemplate restTemplate = new RestTemplate();
        EstadosDTO response = restTemplate.getForObject(
                INEGI_PATH + STATES_PATH, EstadosDTO.class, new HashMap<>());
        return new Response(response.getDatos());
    }

    @GetMapping(value = "/{id}/municipios")
    @Cacheable(value = "municipios", key = "#id")
    public Response getMunByState(@PathVariable String id) {
        RestTemplate restTemplate = new RestTemplate();
        MunicipiosDTO response = restTemplate.getForObject(
                INEGI_PATH + MUN_PATH + id, MunicipiosDTO.class, new HashMap<>());
        return new Response(response.getDatos());
    }
}
