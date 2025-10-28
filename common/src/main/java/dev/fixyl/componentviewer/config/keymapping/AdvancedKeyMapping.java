package dev.fixyl.componentviewer.config.keymapping;

import com.mojang.blaze3d.platform.InputConstants.Key;

import net.minecraft.client.KeyMapping;

/**
 * An {@link AdvancedKeyMapping} is a regular {@link KeyMapping} with
 * added functionality.
 * <p>
 * This key mapping is the baseline for key mappings used by this mod.
 */
public class AdvancedKeyMapping extends KeyMapping {

    private final ConflictContext conflictContext;

    private boolean isDownAnywhere = false;

    public AdvancedKeyMapping(String translationKey, int keyCode, String category, ConflictContext conflictContext) {
        super(translationKey, keyCode, category);

        this.conflictContext = conflictContext;
    }

    public AdvancedKeyMapping(String translationKey, int keyCode, String category) {
        this(translationKey, keyCode, category, ConflictContext.getDefault());
    }

    public AdvancedKeyMapping(String translationKey, Key key, String category, ConflictContext conflictContext) {
        super(translationKey, key.getType(), key.getValue(), category);

        this.conflictContext = conflictContext;
    }

    public AdvancedKeyMapping(String translationKey, Key key, String category) {
        this(translationKey, key, category, ConflictContext.getDefault());
    }

    /**
     * Get the {@link ConflictContext} in which this key mapping
     * consideres itself conflicting with others.
     * <p>
     * Conflict contexts have no use-case on the Fabric platform.
     *
     * @return the conflict context of this mapping
     */
    public ConflictContext getConfictContext() {
        return this.conflictContext;
    }

    /**
     * Check whether the provided key matches the one that is currently
     * associated with this key mapping.
     *
     * @param key the key to match
     * @return {@code true} if the key matches, {@code false} otherwise
     */
    public boolean matchesKey(Key key) {
        return this.key.equals(key);
    }

    /**
     * Check whether the key, associated with this mapping, is currently
     * held down while the game is running.
     * <p>
     * Unlike {@link KeyMapping#isDown()}, this method will
     * also return {@code true} if the key is pressed inside screens,
     * or basically anywhere from within the game.
     *
     * @return {@code true} if the key is currently held down, {@code false} otherwise
     */
    public boolean isDownAnywhere() {
        return this.isDownAnywhere;
    }

    /**
     * Set whether the key, associated with this mapping, is currently
     * held down while the game is running.
     *
     * @param isDownAnywhere whether the key is pressed
     */
    public void setDownAnywhere(boolean isDownAnywhere) {
        this.isDownAnywhere = isDownAnywhere;
    }

    /**
     * Defines on what layer keys are considered conflicting.
     * <p>
     * This has no use-case on the Fabric platform.
     */
    public enum ConflictContext {
        /**
         * Everywhere
         */
        UNIVERSAL,
        /**
         * When in a screen
         */
        IN_SCREEN,
        /**
         * When not in any screen (in game)
         */
        IN_GAME;

        public static ConflictContext getDefault() {
            return UNIVERSAL;
        }
    }
}
