package mx.gob.pjpuebla.trials.workflow.movimientos;

import mx.gob.pjpuebla.trials.workflow.documentos.records.DocumentoData;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.stream.Collectors;

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
    private String responsable;
    private String anexos;

    public MovimientoSalidaDTO(MovimientoSalidaRecord recordMovimiento) {
        String folioTmp = (recordMovimiento.documentoFolio() != null) ? recordMovimiento.documentoFolio()
                : recordMovimiento.folio();
        String tipo = (recordMovimiento.tipoDocumento() != null) ? recordMovimiento.tipoDocumento().getPlural()
                : recordMovimiento.tipoCarpeta().getPlural();
        String expedienteTmp = (recordMovimiento.expediente() != null) ? recordMovimiento.expediente()
                : recordMovimiento.expedienteDoc();
        String observacionesTmp = "Sin observaciones.";

        DocumentoData data = (DocumentoData) recordMovimiento.data();

        if (data != null && data.getExhortoObservaciones() != null) {
            observacionesTmp = data.getExhortoObservaciones().isEmpty() ? "Sin observaciones." : data.getExhortoObservaciones() + ".";
        }

        // agregamos lo de anexos:
        String anexosString = recordMovimiento.anexos()
                .stream()
                .map(anexo -> anexo.nombre())
                .collect(Collectors.collectingAndThen(
                        Collectors.joining(", "),
                        result -> result.isEmpty() ? "Sin anexos" : result + "."                                                              
                ));

        this.setUuid(recordMovimiento.uuid());
        this.setTipoDocumento(tipo);
        this.setFolio(folioTmp);
        this.setExpediente(expedienteTmp);
        this.setFecha(recordMovimiento.fecha());
        this.setJuzgado(recordMovimiento.juzgado());
        this.setObservaciones(observacionesTmp + "<br/>");
        this.setOficialia(recordMovimiento.oficialia());
        this.setResponsable(recordMovimiento.responsable());
        this.setAnexos(anexosString);
    }
}
