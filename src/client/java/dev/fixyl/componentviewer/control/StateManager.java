/*
 * MIT License
 *
 * Copyright (c) 2025 fixyldev
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package dev.fixyl.componentviewer.control;

import dev.fixyl.componentviewer.keyboard.KeyCombos;

public final class StateManager {
    private int selectedComponentIndex;
    private boolean previousCycleAction;
    private boolean previousCopyAction;

    public StateManager() {
        this.selectedComponentIndex = 0;
        this.previousCycleAction = false;
        this.previousCopyAction = false;
    }

    public int cycleSelectedComponentIndex(int numberOfComponents) {
        int newComponentIndex = this.getIndexBasedOnKeysPressed(numberOfComponents);

        if (newComponentIndex == this.selectedComponentIndex) {
            this.previousCycleAction = false;
        } else if (!this.previousCycleAction) {
            this.selectedComponentIndex = newComponentIndex;
            this.previousCycleAction = true;
        }

        if (this.selectedComponentIndex < 0) {
            this.selectedComponentIndex = numberOfComponents - 1;
        } else if (this.selectedComponentIndex >= numberOfComponents) {
            this.selectedComponentIndex = 0;
        }

        return this.selectedComponentIndex;
    }

    public boolean shouldPerformCopyAction() {
        boolean copyAction = KeyCombos.isCopyActionPressed();
        boolean shouldPerformCopyAction = false;

        if (!this.previousCopyAction && copyAction) {
            shouldPerformCopyAction = true;
        }

        this.previousCopyAction = copyAction;
        return shouldPerformCopyAction;
    }

    private int getIndexBasedOnKeysPressed(int numberOfComponents) {
        int newComponentIndex = this.selectedComponentIndex;

        if (KeyCombos.isCycleNextPressed()) {
            newComponentIndex++;
        } else if (KeyCombos.isCyclePreviousPressed()) {
            newComponentIndex--;
        } else if (KeyCombos.isCycleFirstPressed()) {
            newComponentIndex = 0;
        } else if (KeyCombos.isCycleLastPressed()) {
            newComponentIndex = numberOfComponents - 1;
        }

        return newComponentIndex;
    }
}
