package mx.gob.pjpuebla.migracion.readers.exhortoCapital;

import java.time.LocalDate;
import java.util.List;

public record ExhortoCapitalMigracionRecordSave(
    String folio,
    String expediente,
    LocalDate fechaAsignacion,
    String observaciones,
    String procedencia,
    List<String> anexos
) {}

//esto seria foraneo

// id_ef -> folio
// numero + amo 
// fecha re 
// obse 
// procedencia descrip
//nuevos campos
// tramite
// partes ->  
