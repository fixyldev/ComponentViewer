package dev.fixyl.componentviewer.config.option;

/**
 * Defines an object which holds various types of options
 * or configurations.
 */
public interface Options {

    /**
     * Get all options this instance holds.
     *
     * @implNote
     * SonarQube warning for returning a wildcard generic in non-private methods is suppressed.
     * Every option can have a different data type. The exact type is therefore not known.
     *
     * @return an array of all options
     */
    @SuppressWarnings("java:S1452")
    AdvancedOption<?>[] getOptions();
}
