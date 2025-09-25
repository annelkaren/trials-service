package mx.gob.pjpuebla.trials.error;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import lombok.Getter;

@Getter
public class InternalServerError extends ResponseStatusException {
    
    public InternalServerError(String reason) {
        super(HttpStatus.INTERNAL_SERVER_ERROR, reason);
    }
}
