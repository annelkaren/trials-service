package mx.gob.pjpuebla.trials.core.usuarios;

import lombok.Data;

@Data
public class Usuario {

    private String id;

    private String firstName;

    private String lastName;

    private String email;

    private String userName;

    private String password;
}
