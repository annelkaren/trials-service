package mx.gob.pjpuebla.trials.core.instituciones.records;

import com.fasterxml.jackson.annotation.JsonInclude;

import mx.gob.pjpuebla.trials.core.domicilios.Domicilio;
import mx.gob.pjpuebla.trials.util.enums.Estado;

import java.io.Serializable;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record InstitucionRecord(
                Integer id,
                String nombre,
                String domicilio,
                String telefono,
                String tipoInstitucion,
                Estado estado) implements Serializable {

        // Constructor personalizado para que reciba Domicilio y calcule la dirección
        public InstitucionRecord(Integer id, String nombre, Domicilio domicilio, String telefono,
                        String tipoInstitucion, Estado estado) {
                this(id, nombre, domicilio != null ? domicilio.getDireccionInstitucion() : null, telefono,
                                tipoInstitucion, estado);
        }
}