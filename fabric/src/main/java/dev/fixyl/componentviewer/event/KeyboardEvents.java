package dev.fixyl.componentviewer.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

import dev.fixyl.componentviewer.control.Selection;

public final class KeyboardEvents {

    private KeyboardEvents() {}

    public static final Event<CycleComponentCallback> CYCLE_COMPONENT_EVENT = EventFactory.createArrayBacked(CycleComponentCallback.class, listeners -> cycleType -> {
        for (CycleComponentCallback listener : listeners) {
            listener.onCycleComponent(cycleType);
        }
    });

    public static final Event<CopyActionCallback> COPY_ACTION_EVENT = EventFactory.createArrayBacked(CopyActionCallback.class, listeners -> () -> {
        for (CopyActionCallback listener : listeners) {
            listener.onCopyAction();
        }
    });

    @FunctionalInterface
    public static interface CycleComponentCallback {
        void onCycleComponent(Selection.CycleType cycleType);
    }

    @FunctionalInterface
    public static interface CopyActionCallback {
        void onCopyAction();
    }
}
