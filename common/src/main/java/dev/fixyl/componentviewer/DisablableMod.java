package dev.fixyl.componentviewer;

/**
 * Defines a mod that can report whether it considers itself
 * to be disabled at the time of querying.
 * <p>
 * This might not actually mean that the mod is currently
 * disabled. This is because this self-report is usally also
 * used by components of the same mod to then actually enforce
 * it being disabled.
 */
public interface DisablableMod {

    /**
     * Get whether the mod considers itself as being disbled.
     *
     * @return {@code true} if the mod is disabled, {@code false otherwise}
     */
    boolean isModDisabled();
}
