package dev.fixyl.componentviewer.control.component;

/**
 * A {@link ComponentContext} is used to specify
 * what kind of components we are currently talking about.
 */
public enum ComponentContext {

    /**
     * Components which currently exist.
     */
    NORMAL,

    /**
     * Components which exist by default.
     */
    PROTOTYPE,

    /**
     * Components which have been added,
     * modified or removed.
     */
    PATCH
}
