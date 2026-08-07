package ink.icoding.mvc.exceptions;

import ink.icoding.mvc.entitys.HttpStatusCode;

/**
 * Represents request content encoded with a media type that the server does not support.
 *
 * <p>The exception maps to HTTP 415 and enables application-level content checks to produce
 * the same error envelope as Spring MVC message-conversion failures.</p>
 */
public class UnsupportedMediaTypeException extends SmartMvcException {
    public UnsupportedMediaTypeException(String message) {
        super(HttpStatusCode.UNSUPPORTED_MEDIA_TYPE, "UNSUPPORTED_MEDIA_TYPE", message);
    }
}
