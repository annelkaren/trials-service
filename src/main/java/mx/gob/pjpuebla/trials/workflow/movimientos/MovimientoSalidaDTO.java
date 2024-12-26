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
    private String oficialia;

    public MovimientoSalidaDTO(MovimientoSalidaRecord recordMovimiento) {
        String folioTmp = (recordMovimiento.documentoFolio() != null) ? recordMovimiento.documentoFolio() : recordMovimiento.folio();
        String tipo = (recordMovimiento.tipoDocumento() != null) ? recordMovimiento.tipoDocumento().name() : recordMovimiento.tipoCarpeta().name();
        String expedienteTmp = (recordMovimiento.expediente() != null) ? recordMovimiento.expediente() : recordMovimiento.expedienteDoc();
        String observacionesTmp = "";

        DocumentoData data = (DocumentoData) recordMovimiento.data();

        if (data != null && data.getExhortoObservaciones() != null) {
            observacionesTmp = data.getExhortoObservaciones().isEmpty() ? "" : data.getExhortoObservaciones();
        }

        this.setUuid(recordMovimiento.uuid());
        this.setTipoDocumento(tipo);
        this.setFolio(folioTmp);
        this.setExpediente(expedienteTmp);
        this.setFecha(recordMovimiento.fecha());
        this.setJuzgado(recordMovimiento.juzgado());
        this.setObservaciones(observacionesTmp);
        this.setOficialia(recordMovimiento.oficialia());
    }
}
