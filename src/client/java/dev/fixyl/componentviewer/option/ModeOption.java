/*
 * MIT License
 *
 * Copyright (c) 2024 fixyldev
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

package dev.fixyl.componentviewer.option;

import java.util.function.IntFunction;

import com.google.gson.annotations.SerializedName;

import net.minecraft.util.TranslatableOption;
import net.minecraft.util.function.ValueLists;

public enum ModeOption implements TranslatableOption {
    @SerializedName("snbt") SNBT(0, "componentviewer.config.mode.snbt"),
    @SerializedName("class") CLASS(1, "componentviewer.config.mode.class");

    private static final IntFunction<ModeOption> BY_ID;
    private final int id;
    private final String translationKey;

    private ModeOption(int id, String translationKey) {
        this.id = id;
        this.translationKey = translationKey;
    }

    @Override
    public int getId() {
        return this.id;
    }

    @Override
    public String getTranslationKey() {
        return this.translationKey;
    }

    public static ModeOption byId(int id) {
        return BY_ID.apply(id);
    }

    static {
        BY_ID = ValueLists.createIdToValueFunction(ModeOption::getId, ModeOption.values(), ValueLists.OutOfBoundsHandling.WRAP);
    }
}
