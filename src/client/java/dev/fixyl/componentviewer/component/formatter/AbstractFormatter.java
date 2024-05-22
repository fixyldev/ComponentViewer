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

package dev.fixyl.componentviewer.component.formatter;

import dev.fixyl.componentviewer.config.Config;
import net.minecraft.util.Formatting;

public abstract class AbstractFormatter {
    public static final int INITIAL_TEXT_LIST_CAPACITY = 16;
    protected static final Formatting GENERAL_FORMATTING = Formatting.DARK_GRAY;

    private static boolean formattingError;

    private Integer indentSize;
    private String indentPrefix;

    public AbstractFormatter() {
        AbstractFormatter.setFormattingError(false);
        this.setIndentSize(Config.INDENT_SIZE.getValue());
    }

    public void setIndentSize(Integer indentSize) {
        if (indentSize < 0)
            indentSize = 0;

        this.indentSize = indentSize;
        this.indentPrefix = " ".repeat(indentSize);
    }

    public Integer getIndentSize() {
        return this.indentSize;
    }

    public String getIndentPrefix() {
        return this.indentPrefix;
    }

    protected static void setFormattingError(boolean isFormattingError) {
        AbstractFormatter.formattingError = isFormattingError;
    }

    public static boolean isFormattingError() {
        return AbstractFormatter.formattingError;
    }
}
