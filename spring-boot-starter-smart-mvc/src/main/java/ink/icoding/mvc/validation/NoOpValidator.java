package ink.icoding.mvc.validation;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

/**
 * No-operation Spring {@link Validator} used when SmartMVC request validation is disabled.
 *
 * <p>The validator declares support for every target type but never adds validation errors,
 * allowing Spring MVC binding to continue without invoking Bean Validation constraints.</p>
 */
public class NoOpValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return true;
    }

    @Override
    public void validate(Object target, Errors errors) {
        // Validation is intentionally disabled.
    }
}
