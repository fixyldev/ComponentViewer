package dev.fixyl.componentviewer.event;

import net.neoforged.bus.api.Event;

import dev.fixyl.componentviewer.control.Selection.CycleType;

public final class KeyboardEvents {

    private KeyboardEvents() {}

    public static class CycleComponentEvent extends Event {

        public final CycleType cycleType;

        public CycleComponentEvent(CycleType cycleType) {
            this.cycleType = cycleType;
        }
    }

    public static class CopyActionEvent extends Event {}
}
