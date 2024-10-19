package mx.gob.pjpuebla.trials.error;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import static mx.gob.pjpuebla.trials.util.Messages.OPTIMISTIC_LOCKING_ERROR;

@Getter
public class InvalidVersionException extends ResponseStatusException {

    private final String entity;

    public InvalidVersionException(String entity) {
        super(HttpStatus.BAD_REQUEST, OPTIMISTIC_LOCKING_ERROR);
        this.entity = entity;
    }
}
