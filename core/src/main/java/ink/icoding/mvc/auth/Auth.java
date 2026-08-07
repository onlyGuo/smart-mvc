package ink.icoding.mvc.auth;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Declares that a controller type or handler method requires an authenticated identity.
 *
 * <p>Optional role and permission requirements are evaluated by {@link AuthPolicy} using
 * the configured {@link AuthMode}. The annotation remains independent of Spring so it can
 * be shared by the core model and the Spring Boot integration.</p>
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.ANNOTATION_TYPE})
public @interface Auth {

    String[] roles() default {};

    String[] permissions() default {};

    AuthMode mode() default AuthMode.ALL;
}
