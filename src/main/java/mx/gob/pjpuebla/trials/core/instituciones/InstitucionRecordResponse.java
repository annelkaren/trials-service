package mx.gob.pjpuebla.trials.core.instituciones;
import mx.gob.pjpuebla.trials.core.domicilios.DomicilioRecord;
import mx.gob.pjpuebla.trials.util.enums.Estado;

public record InstitucionRecordResponse(
    Integer id,
    String nombre,
    String telefono,
    DomicilioRecord domicilio,
    Estado estado

) 
{}
