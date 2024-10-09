package mx.gob.pjpuebla.trials.workflow.movimientos;

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

    public MovimientoSalidaDTO(MovimientoSalidaRecord record){
        this.setUuid(record.uuid());
        this.setTipoDocumento(record.tipoDocumento().name());
        this.setFolio(record.folio());
        this.setExpediente(record.expediente());
        this.setFecha(record.fecha());
        this.setJuzgado(record.juzgado());
        this.setObservaciones(record.observaciones());
    }
}
