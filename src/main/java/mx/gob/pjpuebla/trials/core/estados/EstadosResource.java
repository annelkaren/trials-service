package mx.gob.pjpuebla.trials.core.estados;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${inegi.url}")
    private String getInegiPath;

    @Value("${inegi.states-path}")
    private String getStatesPath;

    @Value("${inegi.mun-path}")
    private String getMunPath;

    private final RestTemplate restTemplate;

    @GetMapping
    @Cacheable("estados")
    public List<Estado> getStates() {
        EstadoRecord response = restTemplate.getForObject(
                getInegiPath + getStatesPath, EstadoRecord.class, new HashMap<>());
        return (response != null) ? response.datos() : new ArrayList<>();
    }

    @GetMapping(value = "/{id}/municipios")
    @Cacheable(value = "municipios", key = "#id")
    public List<Municipio> getMunByState(@PathVariable String id) {
        MunicipioRecord response = restTemplate.getForObject(
                getInegiPath + getMunPath + id, MunicipioRecord.class, new HashMap<>());
        return (response != null && response.datos() != null) ? response.datos() : new ArrayList<>();
    }
}
