package mx.gob.pjpuebla.trials.workflow.documentos.records;

import java.time.LocalDateTime;

public record AcusePromocionDetailRecord(
    String juzgado,
    String expediente,
    Integer folio,
    String fechaEnvio, 
    String horaEnvio,
    String nombreReceptor,
    LocalDateTime fechaRecepcion,
    String horaRecepcion,
    String receptor,
    String puestoReceptor,
    String promovente,
    String tipoPromocion
    
) {

    public AcusePromocionDetailRecord(String nombreReceptor, String puestoReceptor, LocalDateTime fechaRecepcion) {
       this("", "", null, "", "", nombreReceptor, fechaRecepcion,  "", "", puestoReceptor, "", "");
    }
}
