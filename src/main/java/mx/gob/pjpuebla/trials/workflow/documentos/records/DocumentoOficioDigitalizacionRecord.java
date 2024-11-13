package mx.gob.pjpuebla.trials.workflow.documentos.records;
import java.time.LocalDate;

import mx.gob.pjpuebla.trials.util.enums.EstadoAcuse;
import mx.gob.pjpuebla.trials.util.enums.EstadoCarpeta;

public record DocumentoOficioDigitalizacionRecord(
    String numeroFolio,
    String expediente,
    LocalDate fechaEmision,
    Integer idOficio,
    Integer dependencia,
    LocalDate fechaEntrega,
    EstadoCarpeta estatus,
    EstadoAcuse estatusAcuse,
    String asunto,
    Character tamanioPapel, 
    Character oficioPublicado,
    String nombreAcuse,
    String comentario,
    String textoEditor,
    String rutaAcuse
) {}