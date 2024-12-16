package mx.gob.pjpuebla.trials.workflow.anexos;

import java.io.Serializable;
import java.util.List;

public record AnexoRecord (
        List<String> anexos,
        String motivoEdita,
        String procedencia
) implements Serializable {
}
