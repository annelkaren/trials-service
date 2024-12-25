package mx.gob.pjpuebla.trials.workflow.documentos.documentosdetalle.records;

import org.springframework.web.multipart.MultipartFile;

import mx.gob.pjpuebla.trials.util.enums.EstadoAcuse;

import java.io.Serializable;
import java.time.LocalDate;

public record DocumentoDetalleRecord(
    Integer documentoId,
    MultipartFile file,
    EstadoAcuse estado,
    LocalDate fechaEntrega,
    String comentario
) implements Serializable {}
