package mx.gob.pjpuebla.trials.core.domicilios;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record DomicilioRecord(
        Long id,
        String calle,
        String exterior,
        String interior,
        String estadoRepublica,
        String municipio,
        String localidad,
        String colonia,
        String codigoPostal,
        String referencia,
        String ciudad
) implements Serializable {
}
