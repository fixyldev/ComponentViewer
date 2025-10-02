package dev.fixyl.componentviewer.config.keymapping;

/**
 * All ticked key mappings define a {@link TickedKeyMapping#onEndClientTick()}
 * method, which should be called once per client-tick.
 * <p>
 * Ticked key mappings rely in some way or another to be ticked each
 * client-tick. If not, they most likely don't work as intended.
 */
public interface TickedKeyMapping {

    /**
     * Call this method once per client-tick!
     */
    void onEndClientTick();
}
