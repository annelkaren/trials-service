package mx.gob.pjpuebla.trials.core.personas;

import mx.gob.pjpuebla.trials.core.domicilios.DomicilioRecord;
import mx.gob.pjpuebla.trials.util.enums.Estado;
import mx.gob.pjpuebla.trials.util.enums.Sexo;

import java.time.LocalDate;

public record PersonaRecord
        (Long id,
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
         DomicilioRecord domicilio,
         String usuario
        ) {
}
