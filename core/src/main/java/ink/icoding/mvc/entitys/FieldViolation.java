package ink.icoding.mvc.entitys;

/**
 * Immutable and serializable description of a parameter or field validation failure.
 *
 * <p>Each violation exposes the failing field name, rejected value, and validation message
 * so clients can associate a standardized error response with a specific input.</p>
 */
public final class FieldViolation {

    private final String field;
    private final Object rejectedValue;
    private final String message;

    public FieldViolation(String field, Object rejectedValue, String message) {
        this.field = field;
        this.rejectedValue = rejectedValue;
        this.message = message;
    }

    public String getField() {
        return field;
    }

    public Object getRejectedValue() {
        return rejectedValue;
    }

    public String getMessage() {
        return message;
    }
}
