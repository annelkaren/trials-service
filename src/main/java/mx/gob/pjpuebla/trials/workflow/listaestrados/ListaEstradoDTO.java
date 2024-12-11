package mx.gob.pjpuebla.trials.workflow.listaestrados;

import lombok.Data;

@Data
public class ListaEstradoDTO {
    private String juzgado;
    private String diaPublicado;
    private String asunto;
    private String notificacion;

    public ListaEstradoDTO(String juzgado, String asunto, String notificacion, String diaPublicado) {
        this.setJuzgado(juzgado);
        this.setAsunto(asunto);
        this.setNotificacion(notificacion);
        this.setDiaPublicado(diaPublicado);
    }
}
