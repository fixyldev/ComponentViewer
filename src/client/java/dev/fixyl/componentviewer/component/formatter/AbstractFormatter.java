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

import java.util.ArrayList;
import java.util.List;

import net.minecraft.text.Text;

import dev.fixyl.componentviewer.config.Configs;

public abstract class AbstractFormatter {
    protected static final int INITIAL_TEXT_LIST_CAPACITY = 16;

    private Integer indentSize;
    private String indentPrefix;

    protected List<Text> textList;

    protected AbstractFormatter() {
        this.setIndentSize(Configs.TOOLTIPS_INDENT_SIZE.value());

        this.textList = new ArrayList<>(AbstractFormatter.INITIAL_TEXT_LIST_CAPACITY);
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
}
