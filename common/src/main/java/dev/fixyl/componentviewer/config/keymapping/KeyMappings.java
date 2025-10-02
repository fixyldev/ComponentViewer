package dev.fixyl.componentviewer.config.keymapping;

import java.util.ArrayList;
import java.util.List;

/**
 * Defines an object which holds various types of key mappings.
 */
public interface KeyMappings {

    /**
     * Get all key mappings this instance holds.
     *
     * @return an array of all key mappings
     */
    AdvancedKeyMapping[] getKeyMappings();

    /**
     * Get all key mappings from a specific subclass of {@link AdvancedKeyMapping}.
     * <p>
     * Since this method heavily relies on reflection, frequent calls might
     * impact performance.
     *
     * @implNote
     * The generic type is not restricted to subclasses alone. This is done to allow
     * getting various key mappings based on interfaces instead of concrete classes.
     * This also means that inputting any interface, not implemented in any subclass,
     * will always result in an empty list since this instance is meant to only hold
     * advanced key mappings or derived classes.
     *
     * @param <T> the subclass type
     * @param subClass the subclass object
     * @return a list of all key mappings from the specified subclass
     */
    default <T> List<T> getSubClassKeyMappings(Class<T> subClass) {
        List<T> matchedKeyMappings = new ArrayList<>();

        for (AdvancedKeyMapping keyMapping : this.getKeyMappings()) {
            if (subClass.isInstance(keyMapping)) {
                matchedKeyMappings.add(subClass.cast(keyMapping));
            }
        }

        return matchedKeyMappings;
    }
}
