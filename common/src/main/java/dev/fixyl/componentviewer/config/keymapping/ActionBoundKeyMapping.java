package dev.fixyl.componentviewer.config.keymapping;

import com.mojang.blaze3d.platform.InputConstants.Key;

/**
 * An {@link ActionBoundKeyMapping} fires a pre-defined {@link Runnable} once
 * the associated key is pressed/held while being in-game.
 * <p>
 * Since it's a {@link TickedKeyMapping}, make sure to call
 * {@link ActionBoundKeyMapping#onEndClientTick()} once per client-tick!
 *
 * @see TickedKeyMapping
 */
public class ActionBoundKeyMapping extends AdvancedKeyMapping implements TickedKeyMapping {

    private final Runnable action;

    public ActionBoundKeyMapping(String translationKey, int keyCode, String category, ConflictContext conflictContext, Runnable action) {
        super(translationKey, keyCode, category, conflictContext);

        this.action = action;
    }

    public ActionBoundKeyMapping(String translationKey, int keyCode, String category, Runnable action) {
        super(translationKey, keyCode, category);

        this.action = action;
    }

    public ActionBoundKeyMapping(String translationKey, Key key, String category, ConflictContext conflictContext, Runnable action) {
        super(translationKey, key, category, conflictContext);

        this.action = action;
    }

    public ActionBoundKeyMapping(String translationKey, Key key, String category, Runnable action) {
        super(translationKey, key, category);

        this.action = action;
    }

    @Override
    public void onEndClientTick() {
        while (this.consumeClick()) {
            this.action.run();
        }
    }
}
