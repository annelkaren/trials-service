package mx.gob.pjpuebla.trials.error;

import java.time.LocalDateTime;
import lombok.Data;
import org.springframework.lang.Nullable;

@Data
public class ApiResponse<T> {
    private boolean success;
    private String message;
    private String code;
    private int status;

    @Nullable
    private T data;

    private LocalDateTime timestamp;

    public ApiResponse(boolean success, String message, String code, int status, @Nullable T data,
            LocalDateTime timestamp) {
        this.success = success;
        this.message = message;
        this.code = code;
        this.status = status;
        this.data = data;
        this.timestamp = timestamp;
    }

    public ApiResponse(boolean success, String message, String code, int status, @Nullable T data) {
        this(success, message, code, status, data, LocalDateTime.now());
    }

    public ApiResponse(boolean success,
            String message,
            String code,
            int status) {
        this.success = success;
        this.message = message;
        this.code = code;
        this.status = status;
        this.data = null;
        this.timestamp = LocalDateTime.now();
    }

}
