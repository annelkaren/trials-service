package mx.gob.pjpuebla.trials.workflow.movimientos;

import mx.gob.pjpuebla.trials.workflow.documentos.records.DocumentoData;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class MovimientoSalidaDTO {
    private UUID uuid;
    private String tipoDocumento;
    private String folio;
    private String expediente;
    private LocalDateTime fecha;
    private String juzgado;
    private String observaciones;

    public MovimientoSalidaDTO(MovimientoSalidaRecord recordMovimiento){
        String folioTmp = recordMovimiento.folio();
        String observacionesTmp = "";

        DocumentoData data = (DocumentoData) recordMovimiento.data();

        if (data!=null){
            folioTmp = data.getPromocionFolio().isEmpty()?recordMovimiento.folio():data.getPromocionFolio();
            observacionesTmp = data.getExhortoObservaciones().isEmpty()?"":data.getExhortoObservaciones();
        }
        
        this.setUuid(recordMovimiento.uuid());
        this.setTipoDocumento(recordMovimiento.tipoDocumento().name());
        this.setFolio(folioTmp);
        this.setExpediente(recordMovimiento.expediente());
        this.setFecha(recordMovimiento.fecha());
        this.setJuzgado(recordMovimiento.juzgado());
        this.setObservaciones(observacionesTmp);
    }
}
