package mx.gob.pjpuebla.trials.error;

import java.time.LocalDateTime;

public class ApiResponseFactory {
    public static final String SUCCESS = "SUCCESS";
    public static final String VALIDATION_ERROR = "VALIDATION_ERROR";
    public static final String NOT_FOUND = "NOT_FOUND";
    public static final String INTERNAL_ERROR = "INTERNAL_ERROR";
    public static final String UNAUTHORIZED = "UNAUTHORIZED";
    public static final String FORBIDDEN = "FORBIDDEN";


    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(true, message, SUCCESS, 200, data, LocalDateTime.now());
    }

    public static <T> ApiResponse<T> success(String message) {
        return new ApiResponse<>(true, message, SUCCESS, 200, null, LocalDateTime.now());
    }

    public static <T> ApiResponse<T> error(String message, String code, int status) {
        return new ApiResponse<>(false, message, code, status, null, LocalDateTime.now());
    }

    public static <T> ApiResponse<T> error(String message, String code) {
        return error(message, code, 400);
    }
}
