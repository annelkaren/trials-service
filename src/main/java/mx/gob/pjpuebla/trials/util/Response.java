package mx.gob.pjpuebla.trials.util;

import lombok.Data;
import org.springframework.validation.FieldError;

import java.util.ArrayList;
import java.util.List;

@Data
public class Response {

    private String message;
    private Object data;
    private List<FieldError> errors;

    public Response() {
    }

    public Response(List<FieldError> errors) {
        this.errors = new ArrayList<>();
        this.errors.addAll(errors);
    }

    public Response(Object data) {
        this.data = data;
    }
}
