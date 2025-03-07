package mx.gob.pjpuebla.trials.workflow.generadorQR;

import lombok.Data;

@Data
public class GeneradorQRDTO {
    private String codigo1;
    private String codigo2;
    private String codigo3;

    // Constructor con parámetros
    public GeneradorQRDTO(String codigo1, String codigo2, String codigo3) {
        this.codigo1 = codigo1;
        this.codigo2 = codigo2;
        this.codigo3 = codigo3;
    }

    public GeneradorQRDTO(){
        
    }

}
