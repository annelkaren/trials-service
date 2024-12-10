package mx.gob.pjpuebla.trials.core.recursos;

import java.util.List;

public record PermissionRecord(
        String roles,
        List<PolicyRecord> permission) {
}
