package mx.gob.pjpuebla.trials.core.personas;

import lombok.Data;
import mx.gob.pjpuebla.trials.core.roles.RoleRecord;

import java.util.List;

@Data
public class PersonaDTO {

    private Persona persona;
    private List<RoleRecord> roles;
    private String rolPrincipal;
}
