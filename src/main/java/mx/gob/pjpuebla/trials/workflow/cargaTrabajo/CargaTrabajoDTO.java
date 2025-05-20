package mx.gob.pjpuebla.trials.workflow.cargaTrabajo;

import java.time.LocalDate;

import lombok.Data;

@Data
public class CargaTrabajoDTO {

    private String folio;
    private String expediente;
    private String tipoEntrada;
    private LocalDate fechaEnvio;
    private LocalDate fechaLimite;
    private String responsable;
    private String estilo;

    public CargaTrabajoDTO(String folio, String expediente, String tipoEntrada,
            LocalDate fechaEnvio, LocalDate fechaLimite,
            String responsable, String estilo) {
        this.folio = folio;
        this.expediente = expediente;
        this.tipoEntrada = tipoEntrada;
        this.fechaEnvio = fechaEnvio;
        this.fechaLimite = fechaLimite;
        this.responsable = responsable;
        this.estilo = estilo;
    }
}
