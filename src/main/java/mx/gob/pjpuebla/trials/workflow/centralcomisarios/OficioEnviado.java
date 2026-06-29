package mx.gob.pjpuebla.trials.workflow.centralcomisarios;

import mx.gob.pjpuebla.trials.util.enums.EstadoCentralComisario;

import java.io.Serializable;
import java.time.LocalDate;

public record OficioEnviado(
        Integer id,
        String expediente,
        String numero,
        String dependencia,
        String asunto,
        EstadoCentralComisario status,
        LocalDate fechaEmision,
        LocalDate fechaEntrega,
        Long personaId
) implements Serializable {

    public String getStatusEtiqueta() {
        return status != null ? status.getEtiqueta() : "";
    }
}
