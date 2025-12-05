package mx.gob.pjpuebla.trials.error;


public class ApiResponseFactory {

    // Códigos de negocio
    public static final String SUCCESS = "SUCCESS";
    public static final String VALIDATION_ERROR = "VALIDATION_ERROR";
    public static final String NOT_FOUND = "NOT_FOUND";
    public static final String INTERNAL_ERROR = "INTERNAL_ERROR";
    public static final String UNAUTHORIZED = "UNAUTHORIZED";
    public static final String FORBIDDEN = "FORBIDDEN";
    public static final String CONFLICT = "CONFLICT";
    public static final String SUCCESS_ALREADY_ASSIGNED = "SUCCESS_ALREADY_ASSIGNED";

    // Status HTTP
    private static final int HTTP_OK = 200;
    private static final int HTTP_BAD_REQUEST = 400;
    private static final int HTTP_NOT_FOUND = 404;
    private static final int HTTP_CONFLICT = 409;
    private static final int HTTP_UNPROCESSABLE = 422;

    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(true, message, SUCCESS, HTTP_OK, data);
    }

    public static ApiResponse<Void> success(String message) {
        return new ApiResponse<>(true, message, SUCCESS, HTTP_OK);
    }

    public static <T> ApiResponse<T> error(String message, String code, int status, T data) {
        return new ApiResponse<>(false, message, code, status, data);
    }

    public static ApiResponse<Void> error(String message, String code, int status) {
        return error(message, code, status);
    }

    public static ApiResponse<Void> error(String message, String code) {
        return error(message, code, HTTP_BAD_REQUEST);
    }

    public static ApiResponse<Void> notFound(String message) {
        return error(message, NOT_FOUND, HTTP_NOT_FOUND);
    }

    public static ApiResponse<Void> unprocessable(String message) {
        return error(message, VALIDATION_ERROR, HTTP_UNPROCESSABLE);
    }
    public static ApiResponse<Void> conflict(String message) {
        return error(message, CONFLICT, HTTP_CONFLICT);
    }

    public static <T> ApiResponse<T> conflict(String message, T data) {
        return error(message, CONFLICT, HTTP_CONFLICT, data);
    }
}
