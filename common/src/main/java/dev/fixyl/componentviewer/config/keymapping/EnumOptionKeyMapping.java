package dev.fixyl.componentviewer.config.keymapping;

import com.mojang.blaze3d.platform.InputConstants.Key;

import net.minecraft.client.gui.components.toasts.Toast.Visibility;
import net.minecraft.util.OptionEnum;

import dev.fixyl.componentviewer.annotation.NullPermitted;
import dev.fixyl.componentviewer.config.option.EnumOption;
import dev.fixyl.componentviewer.control.notification.EnumOptionToast;

public class EnumOptionKeyMapping<E extends Enum<E> & OptionEnum> extends AdvancedKeyMapping implements TickedKeyMapping {

    private final EnumOption<E> option;

    private @NullPermitted EnumOptionToast<E> optionToast;

    public EnumOptionKeyMapping(String translationKey, int keyCode, String category, ConflictContext conflictContext, EnumOption<E> option) {
        super(translationKey, keyCode, category, conflictContext);

        this.option = option;
    }

    public EnumOptionKeyMapping(String translationKey, int keyCode, String category, EnumOption<E> option) {
        super(translationKey, keyCode, category);

        this.option = option;
    }

    @Override
    public void onEndClientTick() {
        if (this.optionToast != null && this.optionToast.getWantedVisibility() == Visibility.HIDE) {
            this.optionToast = null;
        }
    }

    /**
     * Clears the currently playing toast. This means that
     * repeated cycling immediately spawns a new toast
     * instead of updating the text inside the current one.
     */
    public void clearToast() {
        this.optionToast = null;
    }

    /**
     * Cycle the associated enum option to the next value.
     */
    public void cycleEnum() {
        this.option.cycleValue();

        if (this.optionToast == null) {
            this.optionToast = EnumOptionToast.dispatch(option, this.getName());
        } else {
            this.optionToast.resetTimer();
        }
    }

    /**
     * Cycle the associated enum option to the next value,
     * if the provided key matches the one currently
     * associated with this key mapping.
     *
     * @param key the key to check for
     */
    public void cycleEnumIfKeyMatches(Key key) {
        if (this.matchesKey(key)) {
            this.cycleEnum();
        }
    }
}
