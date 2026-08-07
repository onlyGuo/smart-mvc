package ink.icoding.mvc.entitys;

/**
 * Immutable response envelope used to produce a consistent JSON API contract.
 *
 * <p>The envelope records success state, a machine-readable code, a human-readable message,
 * an optional payload, and the creation timestamp. Factory methods create successful and
 * failed responses without exposing mutable response state.</p>
 *
 * @param <T> response payload type
 */
public final class ApiResponse<T> {

    private final boolean success;
    private final String code;
    private final String message;
    private final T data;
    private final long timestamp;

    private ApiResponse(boolean success, String code, String message, T data) {
        this.success = success;
        this.code = code;
        this.message = message;
        this.data = data;
        this.timestamp = System.currentTimeMillis();
    }

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<T>(true, "OK", "success", data);
    }

    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<T>(true, "OK", message, data);
    }

    public static <T> ApiResponse<T> failure(String code, String message) {
        return new ApiResponse<T>(false, code, message, null);
    }

    public static <T> ApiResponse<T> failure(String code, String message, T data) {
        return new ApiResponse<T>(false, code, message, data);
    }

    public boolean isSuccess() {
        return success;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public T getData() {
        return data;
    }

    public long getTimestamp() {
        return timestamp;
    }
}
