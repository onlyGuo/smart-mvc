package ink.icoding.mvc.entitys;

/**
 * Defines the SLF4J severity used for SmartMVC HTTP request summaries.
 *
 * <p>The level is framework-neutral configuration data in the core module. The Spring starter
 * maps each value to the corresponding SLF4J logging method when a request completes.</p>
 */
public enum RequestLogLevel {
    TRACE,
    DEBUG,
    INFO,
    WARN,
    ERROR
}
