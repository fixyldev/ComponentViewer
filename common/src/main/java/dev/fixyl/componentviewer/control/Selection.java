package dev.fixyl.componentviewer.control;

import net.minecraft.client.ScrollWheelHandler;

public class Selection {

    private int amount;
    private int selectedIndex;

    public Selection(int amount) {
        this.amount = Selection.assertPositiveAmount(amount);
        this.selectedIndex = 0;
    }

    /**
     * Set the amount of items to select from.
     * This effectively set the max possible index to {@code amount - 1}.
     * It also clamps the currently selected index to fit within the new range.
     *
     * @param amount the amount of items possible to select
     */
    public void setAmount(int amount) {
        this.amount = Selection.assertPositiveAmount(amount);
        this.selectedIndex = Math.clamp(this.selectedIndex, 0, amount - 1);
    }

    /**
     * Get the amount of items to select from with this {@link Selection}.
     * This effectively specifies the max possible index to select ({@code amount - 1}).
     *
     * @return the amount of items to select from
     */
    public int getAmount() {
        return this.amount;
    }

    /**
     * Get the currently selected index.
     *
     * @return the selected index
     */
    public int getSelectedIndex() {
        return this.selectedIndex;
    }

    /**
     * Update the selected index based on a mouse scroll distance.
     * Only the scroll direction (sign) is relevant though.
     *
     * @param scrollDistance the distance whose sign specifies direction
     */
    public void updateByScrolling(double scrollDistance) {
        this.selectedIndex = ScrollWheelHandler.getNextScrollWheelSelection(scrollDistance, this.selectedIndex, this.amount);
    }

    /**
     * Update the selected index based on a {@link CycleType}.
     * This effectively allows cycling through the selection.
     *
     * @param cycleType the type specifying how to cycle
     */
    public void updateByCycling(CycleType cycleType) {
        int newIndex = switch (cycleType) {
            case NEXT -> this.selectedIndex + 1;
            case PREVIOUS -> this.selectedIndex - 1;
            case FIRST -> 0;
            case LAST -> this.amount - 1;
        };

        if (newIndex >= this.amount) {
            this.selectedIndex = 0;
        } else if (newIndex < 0) {
            this.selectedIndex = this.amount - 1;
        } else {
            this.selectedIndex = newIndex;
        }
    }

    /**
     * Update the selected index by providing the new index directly.
     * The new index will be clamped to fit within the selection range.
     *
     * @param newIndex the new index
     */
    public void updateByValue(int newIndex) {
        this.selectedIndex = Math.clamp(newIndex, 0, this.amount - 1);
    }

    private static int assertPositiveAmount(int amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Selection amount must be > 0");
        }

        return amount;
    }

    public enum CycleType {
        NEXT,
        PREVIOUS,
        FIRST,
        LAST
    }
}
