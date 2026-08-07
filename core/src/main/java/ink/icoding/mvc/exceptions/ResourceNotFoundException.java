package ink.icoding.mvc.exceptions;

import ink.icoding.mvc.entitys.HttpStatusCode;

/**
 * Represents an application resource that cannot be found and maps to HTTP 404.
 *
 * <p>Constructors support either a complete message or a resource name and identifier, making
 * the exception suitable for consistent missing-entity responses across service layers.</p>
 */
public class ResourceNotFoundException extends SmartMvcException {
    public ResourceNotFoundException(String message) {
        super(HttpStatusCode.NOT_FOUND, "RESOURCE_NOT_FOUND", message);
    }

    public ResourceNotFoundException(String resource, Object id) {
        this(resource + " not found: " + id);
    }
}
