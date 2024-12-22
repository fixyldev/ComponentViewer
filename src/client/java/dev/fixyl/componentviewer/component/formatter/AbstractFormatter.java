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

    protected boolean isIndentationSet;
    private int indentation;
    private String indentPrefix;

    protected List<Text> textList;

    protected AbstractFormatter() {
        this.isIndentationSet = false;

        this.setIndentation(Configs.TOOLTIPS_INDENTATION.intValue());

        this.textList = new ArrayList<>(AbstractFormatter.INITIAL_TEXT_LIST_CAPACITY);
    }

    public void setIndentation(int indentation) {
        if (indentation < 0)
            indentation = 0;

        this.indentation = indentation;
        this.indentPrefix = " ".repeat(indentation);
        this.isIndentationSet = true;
    }

    public int getIndentation() {
        return this.indentation;
    }

    public String getIndentPrefix() {
        return this.indentPrefix;
    }
}
