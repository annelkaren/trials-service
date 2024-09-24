package mx.gob.pjpuebla.trials.error;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@Getter
public class InvalidVersionException extends ResponseStatusException {

    private final String entity;

    public InvalidVersionException(String entity) {
        super(HttpStatus.BAD_REQUEST, "Version modificada por otro usuario");
        this.entity = entity;
    }
}
