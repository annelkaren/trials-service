package mx.gob.pjpuebla.trials.core.personas;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mx.gob.pjpuebla.trials.error.UnauthorizedException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/core/personas")
@SecurityRequirement(name = "Keycloak")
public class PersonaResource {

    @Value("${keycloak.server-url}")
    private String serverUrl;

    @Value("${keycloak.client-id}")
    private String clientId;

    @Value("${keycloak.client-secret}")
    private String clientSecret;

    private final RestTemplate restTemplate;
    private final PersonaService personaService;

    @GetMapping
    public Page<PersonaRecordResponse> getAll(
            @PageableDefault(size = 25) Pageable pageable,
            @RequestParam(value = "nombre", required = false) String nombre,
            @RequestParam(value = "searchQuery", required = false) String searchQuery) {
        return this.personaService.findAllByCentroTrabajo(nombre, searchQuery, pageable);
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public PersonaRecord getById(@PathVariable Long id) {
        return this.personaService.findById(id);
    }

    @GetMapping("/jueces/{juzgadoId}")
    public List<JuezRecord> getJueces(@PathVariable Integer juzgadoId) {
        return this.personaService.findAllJueces(juzgadoId);
    }

    @GetMapping("/jueces/materia/{materiaId}")
    public List<JuezRecord> getJuecesByJuzgadoOfPersonaLogueada(@PathVariable Integer materiaId) {
        return this.personaService.findByOficialiaOfPersonaLogueada(materiaId);
    }

    @GetMapping("/encargadocarrito")
    public List<EncargadoCarritoRecord> getEncargadosCarrito() {
        return this.personaService.findAllEncargadosCarrito();
    }

    @PostMapping
    public PersonaRecordResponse create(@RequestBody @Valid PersonaDTO persona) {
        return this.personaService.create(persona);
    }

    @PutMapping
    public PersonaRecordResponse update(@RequestBody @Valid PersonaDTO persona) {
        return this.personaService.update(persona);
    }

    @GetMapping(value = "/curp/{curp}", produces = MediaType.APPLICATION_JSON_VALUE)
    public PersonaRecord findByCurp(@PathVariable String curp) {
        return this.personaService.findByCurp(curp);
    }

    @GetMapping("/centrostrabajo")
    public List<CentroTrabajoRecord> getCentroTrabajo(
            @RequestParam(value = "nombre", required = false) String nombre) {
        return this.personaService.findAllCentroTrabajo(nombre);
    }

    @GetMapping("/turnado")
    public List<PersonaRecordResponse> getPersonalTurnado() {
        return this.personaService.getPersonalTurnado();
    }

    @PostMapping("/login")
    public ResponseEntity<String> loginAsLitigante (
            @RequestBody PersonaLoginRecord personaLoginRecord
    ) {
        if (personaService.verifyIfUserExistsAndIsLitigante(personaLoginRecord.username())) { //inicia proceso de crear sesion

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
            map.add("client_id", clientId);
            map.add("client_secret", clientSecret);
            map.add("grant_type", "password");
            map.add("username", personaLoginRecord.username());
            map.add("password", personaLoginRecord.password());

            HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(map, headers);
            try {
                return restTemplate.exchange(serverUrl + "/realms/trials-realm/protocol/openid-connect/token",
                        HttpMethod.POST,
                        entity,
                        String.class);
            } catch (HttpClientErrorException ex) {
                throw new UnauthorizedException("Credenciales inválidas. Por favor, inténtelo de nuevo.", "invalid_grant");
            }
        }
        throw new UnauthorizedException("No tiene permiso para acceder a este portal", "".concat(personaLoginRecord.username()));
    }

    @GetMapping("/mensajeros")
    public List<PersonaRecordResponse> findAllMensajeros(){
        return personaService.findAllMensajeros();
    }

    @GetMapping("/centroTrabajo/login")
    public List<CentroTrabajoRecord> getCentroTrabajoPersonaLogueada() {
        return personaService.findCentroTrabajoByPersonCurrent();
    }

}