package ink.icoding.mvc.entitys;

/**
 * Defines how temporal deserialization handles values with missing date or time components.
 *
 * <p>{@link #FILL_MISSING} supplies deterministic defaults such as the first day of a month
 * or midnight, while {@link #REJECT} requires the complete configured input pattern.</p>
 */
public enum IncompleteDateTimePolicy {
    FILL_MISSING,
    REJECT
}
