package mx.gob.pjpuebla.trials.workflow.documentos.records;
import mx.gob.pjpuebla.trials.workflow.personasdocumentos.PersonaDocumentoItemRecord;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public record DocumentoCreateDemandaPenalRecord(
    String nombreProvente,
    String apellidoPaternoProvente,
    String apellidoMaternoProvente,
    Integer tipoJuicio,
    Integer numOficio,
    Integer numCarpetaInv,
    String lugarHecho,
    LocalDate fechaHecho,
    LocalDate fechaPresentacion,
    LocalTime horaFormal,
    LocalTime horaMaterial,
    String lugarDisposicion,
    String tipoSolAudiencia,
    List<String> anexos,
    List<PersonaDocumentoItemRecord> victimas,
    List<PersonaDocumentoItemRecord> imputados,
    List<PersonaDocumentoItemRecord> ministerio,
    List<PersonaDocumentoItemRecord> terceroInvolucrado

) {}
