package mx.gob.pjpuebla.trials.error;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@Getter
public class UserAlreadyExistException extends ResponseStatusException {

    private final String field;

    public UserAlreadyExistException(String reason, String field) {
        super(HttpStatus.CONFLICT, reason);
        this.field = field;
    }
}
