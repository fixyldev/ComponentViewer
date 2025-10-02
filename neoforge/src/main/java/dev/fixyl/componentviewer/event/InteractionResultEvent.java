package dev.fixyl.componentviewer.event;

import net.minecraft.world.InteractionResult;

import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.ICancellableEvent;

public abstract class InteractionResultEvent extends Event implements ICancellableEvent {

    private InteractionResult result;

    protected InteractionResultEvent() {
        this.result = InteractionResult.PASS;
    }

    public void setResult(InteractionResult result) {
        this.setCanceled(result != InteractionResult.PASS);
        this.result = result;
    }

    public InteractionResult getResult() {
        return this.result;
    }
}
