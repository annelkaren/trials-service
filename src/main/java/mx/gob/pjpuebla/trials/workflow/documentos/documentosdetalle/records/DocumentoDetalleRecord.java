package mx.gob.pjpuebla.trials.workflow.documentos.documentosdetalle.records;

import org.springframework.web.multipart.MultipartFile;

import mx.gob.pjpuebla.trials.util.enums.EstadoAcuse;

import java.time.LocalDate;

public record DocumentoDetalleRecord(
    Integer documentoId,
    MultipartFile file,
    EstadoAcuse estado,
    LocalDate fechaEntrega,
    String comentario
) {}
