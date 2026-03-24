package mx.gob.pjpuebla.trials.core.juzgados;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class JuzgadoContadorConfig implements Serializable {
    private Integer tipoJuicioId;
    private String tipoJuicioNombre;
    private Integer maxAsignaciones;
    private Integer contadorAsignaciones;
}
