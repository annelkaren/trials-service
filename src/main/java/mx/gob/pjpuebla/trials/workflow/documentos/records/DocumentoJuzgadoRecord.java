package mx.gob.pjpuebla.trials.workflow.documentos.records;

import java.io.Serializable;

public record DocumentoJuzgadoRecord(
        String nombreDistrito,
        String nombreJuzgado
) implements Serializable {}
