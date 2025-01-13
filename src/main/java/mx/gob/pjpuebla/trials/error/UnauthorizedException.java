package mx.gob.pjpuebla.trials.error;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@Getter
public class UnauthorizedException extends ResponseStatusException {

    private final String field;

    public UnauthorizedException(String reason, String field) {
        super(HttpStatus.UNAUTHORIZED, reason);
        this.field = field;
    }
}
