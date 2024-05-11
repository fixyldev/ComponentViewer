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

package dev.fixyl.componentviewer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ComponentFormatter {
    final private static int INITIAL_LINES_CAPACITY = 16;
    final private static int INITIAL_LINE_CAPACITY = 16;

    private int indentSize;
    private String indent;
	private Map<Integer, String> indentCache = new HashMap<>();

	private String componentValue;
	private List<String> lines;
	private StringBuilder line;
	private int indentLevel;
	private int currentIndex;
	private char currentChar;
	private boolean leftTrim;
	private char closingBracket;
	private char closingQuote;
	private boolean inEmptyBrackets;
	private boolean inString;

    public ComponentFormatter(int indentSize) {
        this.indentSize = indentSize;

        this.calculateIndent();
    }

    private void calculateIndent() {
        StringBuilder indentBuilder = new StringBuilder(this.indentSize);

        for (int i = 0; i < this.indentSize; i++)
        indentBuilder.append(' ');

        this.indent = indentBuilder.toString();
    }

	private String getIndentFromLevel(int indentLevel) {
		if (indentLevel <= 0)
			return "";

		if (this.indentCache.containsKey(indentLevel))
			return this.indentCache.get(indentLevel);

		return this.indentCache.put(indentLevel, indent.repeat(indentLevel));
	}

    public List<String> formatComponentValue(String componentValue) {
		this.componentValue = componentValue;
		this.lines = new ArrayList<String>(ComponentFormatter.INITIAL_LINES_CAPACITY);
		this.line = new StringBuilder(ComponentFormatter.INITIAL_LINE_CAPACITY);
		this.indentLevel = 0;
		this.leftTrim = false;
		this.closingBracket = ' ';
		this.closingQuote = ' ';
		this.inEmptyBrackets = false;
		this.inString = false;

		for (this.currentIndex = 0; this.currentIndex < componentValue.length(); this.currentIndex++) {
			this.currentChar = this.componentValue.charAt(this.currentIndex);

			this.processCharacter();
		}

		if (this.line.length() != 0)
			this.lines.add(this.line.toString());
		
		return this.lines;
    }

	private void processCharacter() {
		if (this.inEmptyBrackets) {
			this.appendCurrentCharacter();
			this.inEmptyBrackets = false;
			return;
		}

		if (this.leftTrim && this.currentChar == ' ')
			return;
		this.leftTrim = false;

		if (this.inString)
			this.formatInsideOfString();
		else
			this.formatOutsideOfString();
		
		this.onNewLine();
	}

	private void formatInsideOfString() {
		switch (this.currentChar) {
			case '"', '\'' -> this.processQuote();
			default -> this.appendCurrentCharacter();
		}
	}

	private void formatOutsideOfString() {
		switch (this.currentChar) {
			case ',', ';' -> this.processComma();
			case '{', '[' -> this.processOpeningBracket();
			case '}', ']' -> this.processClosingBracket();
			case '"', '\'' -> this.processQuote();
			default -> this.appendCurrentCharacter();
		}
	}

	private void processComma() {
		this.appendCurrentCharacter();
		this.prepareNewLine();
	}

	private void processOpeningBracket() {
		if (this.currentIndex + 1 < this.componentValue.length() && (this.componentValue.charAt(this.currentIndex + 1) == '}' || this.componentValue.charAt(this.currentIndex + 1) == ']')) {
			this.appendCurrentCharacter();
			this.inEmptyBrackets = true;
			return;
		}

		this.indentLevel++;
		this.appendCurrentCharacter();
		this.prepareNewLine();
	}

	private void processClosingBracket() {
		this.indentLevel--;
		this.closingBracket = this.currentChar;
		this.prepareNewLine();
	}

	private void processQuote() {
		if (!this.inString) {
			this.inString = true;
			this.closingQuote = this.currentChar;
		} else if (this.currentChar == this.closingQuote && this.componentValue.charAt(this.currentIndex - 1) != '\\')
			this.inString = false;

		this.appendCurrentCharacter();
	}

	private void appendCurrentCharacter() {
		this.line.append(this.currentChar);
	}

	private void prepareNewLine() {
		this.lines.add(this.line.toString());
		this.line.setLength(0);
	}

	private void onNewLine() {
		if (this.line.length() != 0)
			return;
		
		line.append(this.getIndentFromLevel(this.indentLevel));

		if (this.closingBracket != ' ') {
			line.append(this.closingBracket);
			this.closingBracket = ' ';
		}

		this.leftTrim = true;
	}
}
