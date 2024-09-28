package mx.gob.pjpuebla.trials.error;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@Getter
public class NotFoundException extends ResponseStatusException {

    private final String field;

    public NotFoundException(String reason, String field) {
        super(HttpStatus.NOT_FOUND, reason);
        this.field = field;
    }
}
