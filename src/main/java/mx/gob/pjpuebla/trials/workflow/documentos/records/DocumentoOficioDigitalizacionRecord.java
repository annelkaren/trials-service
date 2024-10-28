package mx.gob.pjpuebla.trials.workflow.documentos.records;
import java.time.LocalDate;

import mx.gob.pjpuebla.trials.util.enums.EstadoCarpeta;

public record DocumentoOficioDigitalizacionRecord(
    String numeroFolio,
    String expediente,
    LocalDate fechaEmision,
    Integer idOficio,
    Integer dependencia,
    LocalDate fechaEntrega,
    EstadoCarpeta estatus,
    String asunto,
    char tamanioPapel, 
    char oficioPublicado,
    String nombreAcuse,
    String comentario,
    String textoEditor
) {}