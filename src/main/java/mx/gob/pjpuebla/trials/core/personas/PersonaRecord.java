package mx.gob.pjpuebla.trials.core.personas;

import com.fasterxml.jackson.annotation.JsonInclude;
import mx.gob.pjpuebla.trials.core.domicilios.DomicilioRecord;
import mx.gob.pjpuebla.trials.core.roles.RoleRecord;
import mx.gob.pjpuebla.trials.util.enums.Estado;
import mx.gob.pjpuebla.trials.util.enums.Sexo;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record PersonaRecord(
        Long id,
        Integer version,
        String nombre,
        String apellidoPaterno,
        String apellidoMaterno,
        String curp,
        String rfc,
        LocalDate fechaNacimiento,
        String correoElectronico,
        String telefono,
        String celular,
        Sexo sexo,
        String ocupacion,
        Estado estado,
        Integer estadoCivilId,
        Integer escolaridadId,
        Integer juzgadoId,
        Integer oficialiaId,
        DomicilioRecord domicilio,
        String usuario,
        String rolPrincipal,
        List<RoleRecord> permisos
) implements Serializable {

    public PersonaRecord withRoles(List<RoleRecord> permisos) {

        return new PersonaRecord(id(), version(), nombre(), apellidoPaterno(),
                apellidoMaterno(), curp(), rfc(), fechaNacimiento(), correoElectronico(),
                telefono(), celular(), sexo(), ocupacion(), estado(), estadoCivilId(),
                escolaridadId(), juzgadoId(), oficialiaId(), domicilio(), usuario(), rolPrincipal(), permisos);
    }
}
