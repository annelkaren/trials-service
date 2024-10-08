package mx.gob.pjpuebla.trials.workflow.carpeta.records;


import java.util.List;
import mx.gob.pjpuebla.trials.util.enums.TipoCarpeta;
import mx.gob.pjpuebla.trials.workflow.anexos.Anexo;

public record BandejaRecepcionRecord(
    Integer documentoId,
    String folio,
    String expediente,
    TipoCarpeta tipo,
    String rutaDigitalizacion,
    List<Anexo> anexos
) {}