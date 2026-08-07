package ink.icoding.mvc.auth;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a controller type or handler method as accessible without authentication.
 *
 * <p>The Spring integration gives a method-level declaration precedence over its
 * declaring type and skips identity creation and authorization for anonymous handlers.</p>
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.ANNOTATION_TYPE})
public @interface Anonymous {
}
