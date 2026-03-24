package mx.gob.pjpuebla.trials.workflow.documentos.promocionesSinExpediente.records;

import mx.gob.pjpuebla.trials.util.enums.PromocionSinExpedienteEnum;

public record PromocionSinExpedientePageRecord(
    Integer idPromocion,
    String folio,
    String expediente,
    String juzgado,
    String tipoJuicio,
    String estatus
) {

     public PromocionSinExpedientePageRecord(Integer idPromocion, String folio, String expediente, String juzgado, String tipoJuicio, PromocionSinExpedienteEnum estatus) {
       
        this(idPromocion, folio, expediente, juzgado, tipoJuicio, estatus.getEtiqueta());
    }
}
