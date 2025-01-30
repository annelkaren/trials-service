package mx.gob.pjpuebla.trials.workflow.movimientos;

import mx.gob.pjpuebla.trials.util.enums.TipoCarpeta;
import mx.gob.pjpuebla.trials.util.enums.TipoDocumento;
import mx.gob.pjpuebla.trials.workflow.anexos.AnexoBandejaRecepcionRecord;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public record MovimientoSalidaRecord(
    UUID uuid,
    TipoCarpeta tipoCarpeta,
    String folio,
    String expediente,
    LocalDateTime fecha,
    String juzgado,
    Object data,
    String documentoFolio,
    TipoDocumento tipoDocumento,
    String expedienteDoc,
    String oficialia,
    String responsable,
    String observaciones,
    Integer documentoId,
    Integer carpetaId,
    List<AnexoBandejaRecepcionRecord> anexos
) implements Serializable {
    public MovimientoSalidaRecord(
        UUID uuid,
        TipoCarpeta tipoCarpeta,
        String folio,
        String expediente,
        LocalDateTime fechaAsignacion,
        String juzgado,
        Object data,
        String documentoFolio,
        TipoDocumento tipoDocumento,
        String expedienteDoc,
        String oficialia,
        String responsable,
        String observaciones,
        Integer documentoId,
        Integer carpetaId
    ) {
        this(uuid, tipoCarpeta, folio, expediente, fechaAsignacion, juzgado, data, documentoFolio, tipoDocumento, expedienteDoc, oficialia, responsable, observaciones, documentoId, carpetaId, Collections.emptyList());
    }
}
