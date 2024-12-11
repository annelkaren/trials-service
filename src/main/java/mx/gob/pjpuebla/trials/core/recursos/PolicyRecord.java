package mx.gob.pjpuebla.trials.core.recursos;

import java.io.Serializable;

public record PolicyRecord(
        String name,
        String scopes) implements Serializable {
}
