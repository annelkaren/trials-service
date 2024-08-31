package mx.gob.pjpuebla.trials.error;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class OptimisticLockingFailureException extends ResponseStatusException {

    @Getter
    private final String field;

    public OptimisticLockingFailureException(String reason, String field) {
        super(HttpStatus.BAD_REQUEST, reason);
        this.field = field;
    }
}
