package mx.gob.pjpuebla.trials.error;

import java.time.LocalDateTime;
import java.util.Optional;

import lombok.Data;

@Data
public class ApiResponse<T> {
    private boolean success;
    private String message;
    private String code;
    private int status;
    private Optional<T> data;
    private LocalDateTime timestamp;

    public ApiResponse(boolean success, String message, String code, int status, T data, LocalDateTime timestamp) {
        this.success = success;
        this.message = message;
        this.code = code;
        this.status = status;
        this.data = Optional.ofNullable(data);
        this.timestamp = timestamp;
    }

}