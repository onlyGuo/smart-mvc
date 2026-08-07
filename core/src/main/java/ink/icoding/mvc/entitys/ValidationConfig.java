package ink.icoding.mvc.entitys;

/**
 * Controls whether the Spring MVC integration performs request parameter validation.
 *
 * <p>Disabling this option causes the starter to install a no-operation validator while
 * leaving the core configuration model free of Jakarta Validation and Spring dependencies.</p>
 */
public class ValidationConfig {

    private boolean enabled = true;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
