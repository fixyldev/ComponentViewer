package dev.fixyl.componentviewer.event;

import dev.fixyl.componentviewer.control.Selection.CycleType;

/**
 * Defines an event dispatcher used to dispatch
 * keyboard-tied events only.
 */
public interface KeyboardEventDispatcher {

    void invokeCycleComponentEvent(CycleType cycleType);
    void invokeCopyActionEvent();
}
