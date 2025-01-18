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

import net.minecraft.client.gui.screen.Screen;

public final class StateManager {
    private int selectedIndex;
    private boolean previousAltDown;

    public StateManager() {
        this.selectedIndex = 0;
        this.previousAltDown = false;
    }

    public void cycleSelectedIndex(int numberOfComponents) {
        boolean currentAltDown = Screen.hasAltDown();

        if (!currentAltDown) {
            this.previousAltDown = false;
        } else if (!this.previousAltDown) {
            this.selectedIndex += (Screen.hasShiftDown()) ? -1 : 1;
            this.previousAltDown = true;
        }

        if (this.selectedIndex < 0) {
            this.selectedIndex = numberOfComponents - 1;
        } else if (this.selectedIndex >= numberOfComponents) {
            this.selectedIndex = 0;
        }
    }

    public int getSelectedIndex() {
        return this.selectedIndex;
    }
}
