package mx.gob.pjpuebla.trials.workflow.personadetalle;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import mx.gob.pjpuebla.trials.workflow.personadetalle.DTO.PersonaDTO;
import mx.gob.pjpuebla.trials.workflow.personadetalle.DTO.PersonaDTOGet;

import org.eclipse.jetty.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/workflow/personaDetalle")
@SecurityRequirement(name = "Keycloak")
public class PersonaDetalleResource {
    private final PersonaDetalleService personaDetalleService;
    
    @PostMapping("/create")
    public PersonaDetalleRecord createPersonaDetalle(@RequestBody PersonaDTO personaDTO) {
        return personaDetalleService.createPersonaDetalle(personaDTO);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PersonaDTOGet> getParticipanteById(@PathVariable Integer id) {
        PersonaDTOGet personaDTO = personaDetalleService.getParticipante(id);
        return ResponseEntity.ok(personaDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> actualizarPersona(@PathVariable Integer id, @RequestBody PersonaDTO personaDTO) {
        try {
            personaDetalleService.updateParticipante(id, personaDTO);
            return ResponseEntity.ok("Entidad actualizada exitosamente");
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND_404).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR_500).body("Error interno del servidor");
        }
    }
} 
