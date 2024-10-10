package mx.gob.pjpuebla.trials.workflow.movimientos;

import mx.gob.pjpuebla.trials.workflow.documentos.records.DocumentoData;
import lombok.Data;

import java.util.Date;
import java.util.UUID;

@Data
public class MovimientoSalidaDTO {
    private UUID uuid;
    private String tipoDocumento;
    private String folio;
    private String expediente;
    private Date fecha;
    private String juzgado;
    private String observaciones;

    public MovimientoSalidaDTO(){}

    public MovimientoSalidaDTO(MovimientoSalidaRecord recordMovimiento){
        String observacionesExhorto = "";
        DocumentoData data = null;

        if (recordMovimiento.data()!=null){

            data = (DocumentoData) recordMovimiento.data();

            observacionesExhorto = data.getExhortoObservaciones();
        }
        
        this.setUuid(recordMovimiento.uuid());
        this.setTipoDocumento(recordMovimiento.tipoDocumento().name());
        this.setFolio(recordMovimiento.folio());
        this.setExpediente(recordMovimiento.expediente());
        this.setFecha(recordMovimiento.fecha());
        this.setJuzgado(recordMovimiento.juzgado());
        this.setObservaciones(observacionesExhorto);
    }
}
