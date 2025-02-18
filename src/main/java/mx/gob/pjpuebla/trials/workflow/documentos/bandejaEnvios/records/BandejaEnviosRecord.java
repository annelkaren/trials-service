package mx.gob.pjpuebla.trials.workflow.documentos.bandejaEnvios.records;

import mx.gob.pjpuebla.trials.util.enums.EstadoCarpeta;
import mx.gob.pjpuebla.trials.util.enums.EstadoEnvio;

public record BandejaEnviosRecord(
    Integer documentoId,
    String folio,
    String destino,
    String origen, 
    String mensajero,
    String estatusOficio,
    String estatusEnvio
) {
    public BandejaEnviosRecord(Integer documentoId, String folio, String destino, String origen, String mensajero, EstadoCarpeta estatusOficio, EstadoEnvio estadoEnvio) {
        this(documentoId, folio, destino, origen, mensajero, estatusOficio.getEtiqueta(), estadoEnvio != null ? estadoEnvio.getEtiqueta() : "");
    }
}
