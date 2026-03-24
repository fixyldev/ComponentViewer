package dev.fixyl.componentviewer.config.keymapping;

import com.mojang.blaze3d.platform.InputConstants.Key;

import dev.fixyl.componentviewer.control.Selection.CycleType;

public class CycleSelectionKeyMapping extends AdvancedKeyMapping {

    private final CycleType cycleType;

    public CycleSelectionKeyMapping(String translationKey, int keyCode, Category category, ConflictContext conflictContext, CycleType cycleType) {
        super(translationKey, keyCode, category, conflictContext);

        this.cycleType = cycleType;
    }

    public CycleSelectionKeyMapping(String translationKey, int keyCode, Category category, CycleType cycleType) {
        super(translationKey, keyCode, category);

        this.cycleType = cycleType;
    }

    public CycleSelectionKeyMapping(String translationKey, Key key, Category category, ConflictContext conflictContext, CycleType cycleType) {
        super(translationKey, key, category, conflictContext);

        this.cycleType = cycleType;
    }

    public CycleSelectionKeyMapping(String translationKey, Key key, Category category, CycleType cycleType) {
        super(translationKey, key, category);

        this.cycleType = cycleType;
    }

    public CycleType getCycleType() {
        return this.cycleType;
    }
}
