package mx.gob.pjpuebla.trials.core.paises;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.client.RestTemplate;

import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.core.ParameterizedTypeReference;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/core/paises")
@SecurityRequirement(name = "Keycloak")
public class PaisResource {

    private static final String COUNTRIES_PATH = "https://restcountries.com/v3.1";
    private static final String ALL_PATH   = "/translation/all?fields=translations,cca2,ccn3,name";
    private final RestTemplate restTemplate = new RestTemplate();

    @GetMapping
    @Cacheable("paises")
    public List<PaisRecord> getPaises() {

        ResponseEntity<List<Pais>> responseEntity = restTemplate.exchange(
                COUNTRIES_PATH + ALL_PATH,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Pais>>() {}
        );

        List<Pais> paises = responseEntity.getBody();

        if (paises == null) {
            paises = new ArrayList<>();
        }

        return paises.stream().map(pais -> {
            String name = (pais.getTranslations() != null && pais.getTranslations().getSpa() != null)
                    ? pais.getTranslations().getSpa().getOfficial()
                    : "Desconocido";

            String codeAlpha2 = pais.getCodeAlpha2() != null ? pais.getCodeAlpha2() : "N/A";
            String codeNumeric = pais.getCodeNumeric() != null ? pais.getCodeNumeric() : "000";

            return new PaisRecord(name, codeAlpha2, codeNumeric);
        }).toList();
    }


}
