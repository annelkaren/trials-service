package mx.gob.pjpuebla.trials.error;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@Getter
public class ConflictException extends ResponseStatusException {

    public ConflictException(String reason) {
        super(HttpStatus.CONFLICT, reason);
    }
}
