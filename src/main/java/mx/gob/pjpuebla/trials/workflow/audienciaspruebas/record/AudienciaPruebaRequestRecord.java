package mx.gob.pjpuebla.trials.workflow.audienciaspruebas.record;

public record AudienciaPruebaRequestRecord(  
    Integer tipoPruebaId,
    Integer materiaPericialId,
    String nombreDeclarante,
    String descripcionInstrumento,
    String absolvente,
    String descripcionDocumento,
    String objeto,
    String urlDocumento,
    Integer idCarpeta
){ }