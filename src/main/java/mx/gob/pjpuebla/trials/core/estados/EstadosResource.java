package mx.gob.pjpuebla.trials.core.estados;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
    public List<Estado> getStates() {
        RestTemplate restTemplate = new RestTemplate();
        EstadoRecord response = restTemplate.getForObject(
                INEGI_PATH + STATES_PATH, EstadoRecord.class, new HashMap<>());
        return (response != null) ? response.datos() : new ArrayList<>();
    }

    @GetMapping(value = "/{id}/municipios")
    @Cacheable(value = "municipios", key = "#id")
    public List<Municipio> getMunByState(@PathVariable String id) {
        RestTemplate restTemplate = new RestTemplate();
        MunicipioRecord response = restTemplate.getForObject(
                INEGI_PATH + MUN_PATH + id, MunicipioRecord.class, new HashMap<>());
        return (response != null && response.datos() != null) ? response.datos() : new ArrayList<>();
    }
}
