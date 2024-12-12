package mx.gob.pjpuebla.trials.workflow.carpeta.records;

import java.io.Serializable;
import java.util.List;
import mx.gob.pjpuebla.trials.util.enums.TipoCarpeta;
import mx.gob.pjpuebla.trials.workflow.anexos.AnexoBandejaRecepcionRecord;

public record BandejaRecepcionRecord(
    Integer documentoId,
    String folio,
    String expediente,
    TipoCarpeta tipo,
    String rutaDigitalizacion,
    List<AnexoBandejaRecepcionRecord> anexos
) implements Serializable {}