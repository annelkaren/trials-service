package mx.gob.pjpuebla.trials.workflow.documentos.records;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public record AcusePromocionDetailRecord(
    String juzgado,
    String expediente,
    Integer folio,
    String fechaEnvio, 
    String horaEnvio,
    LocalDateTime fechaRecepcion,
    String horaRecepcion,
    String receptor,
    String puestoReceptor,
    String promovente,
    String tipoPromocion
    
) {

    public AcusePromocionDetailRecord(String receptor, String puestoReceptor, LocalDateTime fechaRecepcion) {
       this("","", null, "","",fechaRecepcion, "", receptor,
        puestoReceptor,"promovente","tipopromocion");
    }

    public AcusePromocionDetailRecord(
        String juzgado,
        String expediente,
        Integer folio,
        LocalDate fechaEnvioLocalDate,
        String horaEnvio,
        LocalDate fechaRecepcion,
        String horaRecepcion,
        String receptor,
        String puestoReceptor,
        String promovente,
        String tipoPromocion
    ) {
        this(
            juzgado,
            expediente,
            folio,
            fechaEnvioLocalDate != null
                ? fechaEnvioLocalDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                : null,
            horaEnvio,
            fechaRecepcion != null 
            ? fechaRecepcion.atTime(LocalTime.now()) 
            : null,
            horaRecepcion,
            receptor,
            puestoReceptor,
            promovente,
            tipoPromocion
        );
    }

}
