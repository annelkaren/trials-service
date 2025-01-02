package mx.gob.pjpuebla.trials.workflow.notificaciones.records;

import lombok.Data;

@Data
public class ListaExpedientesRutaDTO {
    private String expediente;
    private String concepto;
    private String nota;


    public ListaExpedientesRutaDTO(String expediente, String concepto, String nota){
        this.expediente = expediente;
        this.concepto = concepto;
        this.nota = nota;
    }
}


