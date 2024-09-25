package mx.gob.pjpuebla.trials.core.instituciones;
import mx.gob.pjpuebla.trials.core.distritos.DistritoRecord;
import mx.gob.pjpuebla.trials.core.domicilios.DomicilioRecord;
import mx.gob.pjpuebla.trials.util.enums.Estado;

public record InstitucionRecordResponse(
    Integer id,
    Integer version,
    String nombre,
    Estado estado,
    String telefono,
    String extension,
    DistritoRecord distrito,
    DomicilioRecord domicilio

) 
{}
