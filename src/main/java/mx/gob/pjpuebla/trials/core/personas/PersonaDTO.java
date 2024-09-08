package mx.gob.pjpuebla.trials.core.personas;

import lombok.Data;

import java.util.List;

@Data
public class PersonaDTO {

    private Persona persona;
    private List<String> roles;
}
