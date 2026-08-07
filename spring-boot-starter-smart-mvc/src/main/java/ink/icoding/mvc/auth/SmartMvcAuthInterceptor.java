package ink.icoding.mvc.auth;

import ink.icoding.mvc.autoconfigure.SmartMvcProperties;
import ink.icoding.mvc.exceptions.ForbiddenException;
import ink.icoding.mvc.exceptions.UnauthorizedException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.lang.reflect.Method;

/**
 * Spring MVC dispatcher that applies the configured authentication strategy to handler methods.
 *
 * <p>The dispatcher resolves method and type annotations, honors global versus annotated-only
 * mode, invokes the application authentication extension, performs authorization, and binds
 * the resulting principal to {@link CurrentAuth}. It also clears request identity state before
 * and after processing to make servlet thread reuse safe.</p>
 */
public class SmartMvcAuthInterceptor implements HandlerInterceptor {

    private final AuthInterceptor<Object> authInterceptor;
    private final CurrentAuth currentAuth;
    private final SmartMvcProperties properties;

    @SuppressWarnings("unchecked")
    public SmartMvcAuthInterceptor(AuthInterceptor<?> authInterceptor, CurrentAuth currentAuth,
                                   SmartMvcProperties properties) {
        this.authInterceptor = (AuthInterceptor<Object>) authInterceptor;
        this.currentAuth = currentAuth;
        this.properties = properties;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
                             Object handler) {
        currentAuth.clear();
        if (!properties.getAuth().isEnabled() || !(handler instanceof HandlerMethod)) {
            return true;
        }

        HandlerMethod handlerMethod = (HandlerMethod) handler;
        ResolvedRequirement resolved = resolveRequirement(handlerMethod);
        if (resolved.anonymous || (resolved.requirement == null
                && properties.getAuth().getMode() == AuthenticationMode.ANNOTATED)) {
            return true;
        }

        String token = authInterceptor.resolveToken(request, properties.getAuth());
        AuthPrincipal<Object> principal = authInterceptor.authenticate(token, request);
        if (principal == null) {
            throw new UnauthorizedException();
        }
        if (!authInterceptor.authorize(principal, resolved.requirement, request,
                properties.getAuth())) {
            throw new ForbiddenException(
                    "The authenticated identity lacks a required role or permission");
        }
        authInterceptor.bind(principal, currentAuth);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                Object handler, Exception ex) {
        authInterceptor.clear(currentAuth);
    }

    private ResolvedRequirement resolveRequirement(HandlerMethod handlerMethod) {
        Method method = handlerMethod.getMethod();
        if (AnnotatedElementUtils.findMergedAnnotation(method, Anonymous.class) != null) {
            return ResolvedRequirement.anonymous();
        }
        Auth methodAuth = AnnotatedElementUtils.findMergedAnnotation(method, Auth.class);
        if (methodAuth != null) {
            return ResolvedRequirement.auth(methodAuth);
        }
        Class<?> beanType = handlerMethod.getBeanType();
        if (AnnotatedElementUtils.findMergedAnnotation(beanType, Anonymous.class) != null) {
            return ResolvedRequirement.anonymous();
        }
        return ResolvedRequirement.auth(
                AnnotatedElementUtils.findMergedAnnotation(beanType, Auth.class));
    }

    /**
     * Immutable internal resolution result that distinguishes explicitly anonymous handlers
     * from handlers that simply have no authentication annotation.
     */
    private static final class ResolvedRequirement {

        private final boolean anonymous;
        private final Auth requirement;

        private ResolvedRequirement(boolean anonymous, Auth requirement) {
            this.anonymous = anonymous;
            this.requirement = requirement;
        }

        private static ResolvedRequirement anonymous() {
            return new ResolvedRequirement(true, null);
        }

        private static ResolvedRequirement auth(Auth requirement) {
            return new ResolvedRequirement(false, requirement);
        }
    }
}
