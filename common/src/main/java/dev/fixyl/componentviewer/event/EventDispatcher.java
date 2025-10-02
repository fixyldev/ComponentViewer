package dev.fixyl.componentviewer.event;

/**
 * Defines an event dispatcher used to dispatch
 * all kinds of events tied to this mod.
 *
 * @see MixinEventDispatcher
 * @see KeyboardEventDispatcher
 */
public interface EventDispatcher extends MixinEventDispatcher, KeyboardEventDispatcher {}
