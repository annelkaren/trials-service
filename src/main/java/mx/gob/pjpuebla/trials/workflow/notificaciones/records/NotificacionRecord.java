package mx.gob.pjpuebla.trials.workflow.notificaciones.records;

import com.fasterxml.jackson.annotation.JsonInclude;

import mx.gob.pjpuebla.trials.util.enums.TipoDocumento;
import mx.gob.pjpuebla.trials.util.enums.TipoNotificacion;


import java.io.Serializable;
import java.util.List;


@JsonInclude(JsonInclude.Include.NON_NULL)
public record NotificacionRecord(
        Integer id,
        String expediente,
        List<String> concepto,
        String notas,
        TipoNotificacion tipo,
        DocumentoDetalleRecord documentoDetalleRecord,
        TipoDocumento tipoDocumento,
        Integer documentoId,
        Integer carpetaId
) implements Serializable {
}
