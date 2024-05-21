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

package dev.fixyl.componentviewer.component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class ComponentFormatter {
	final public static int DEFAULT_INDENT_SIZE = 4;
	final public static int DEFAULT_PRE_CACHE_INDENT_LEVEL = 8;

	final private static int INITIAL_BRACKET_HISTORY_CAPACITY = 12;
    final private static int INITIAL_LINES_CAPACITY = 16;
    final private static int INITIAL_LINE_CAPACITY = 16;

	final private static Map<Character, Character> BRACKET_PAIR = Map.of(
		'{', '}',
		'[', ']'
	);

    private int indentSize;
    private String indent;
	private Map<Integer, String> indentCache = new HashMap<Integer, String>();

	private String componentValue;
	private List<String> lines;
	private StringBuilder line;
	private int indentLevel;
	private int currentIndex;
	private char currentChar;
	private boolean leftTrim;
	private List<Character> bracketHistory;
	private char closingBracket;
	private char closingQuote;
	private boolean inEmptyBrackets;
	private boolean inString;
	private boolean inCurlyBracketString;
	private boolean formattingError;

    public ComponentFormatter(int indentSize, int preCacheIndentLevel) {
		if (indentSize < 0)
			throw new IllegalArgumentException(String.format("Invalid 'indentSize' argument being '%s' when constructing '%s' object.", indentSize, this.getClass().getName()));

        this.indentSize = indentSize;
		this.formattingError = false;

        this.calculateIndent();
		this.preCacheIndent(preCacheIndentLevel);
    }

    private void calculateIndent() {
        StringBuilder indentBuilder = new StringBuilder(this.indentSize);

        for (int i = 0; i < this.indentSize; i++)
        indentBuilder.append(' ');

        this.indent = indentBuilder.toString();
    }

	public void preCacheIndent(int preCacheIndentLevel) {
		if (preCacheIndentLevel < 0)
			throw new IllegalArgumentException(String.format("Invalid 'preCacheIndentLevel' argument being '%s' when calling 'preCacheIndent' of class '%s'.", preCacheIndentLevel, this.getClass().getName()));

		for (int indentLevel = 1; indentLevel <= preCacheIndentLevel; indentLevel++) {
			if (!this.indentCache.containsKey(indentLevel))
				this.indentCache.put(indentLevel, indent.repeat(indentLevel));
		}
	}

	private String getIndentFromLevel(int indentLevel) {
		if (indentLevel <= 0)
			return "";

		if (this.indentCache.containsKey(indentLevel))
			return this.indentCache.get(indentLevel);

		return this.indentCache.put(indentLevel, indent.repeat(indentLevel));
	}

    public List<String> formatComponentValue(String componentValue) {
		this.initializeFormattingVariables(componentValue);

		for (this.currentIndex = 0; this.currentIndex < componentValue.length(); this.currentIndex++) {
			this.currentChar = this.componentValue.charAt(this.currentIndex);

			this.processCharacter();
		}

		if (this.line.length() != 0)
			this.lines.add(this.line.toString());

		if (this.formattingError || this.indentLevel != 0 || this.inString || this.inCurlyBracketString) {
			this.formattingError = true;
			this.lines.clear();
			this.lines.add(this.componentValue);
		}

		return this.lines;
    }

	private void initializeFormattingVariables(String componentValue) {
		this.componentValue = componentValue;
		this.lines = new ArrayList<String>(ComponentFormatter.INITIAL_LINES_CAPACITY);
		this.line = new StringBuilder(ComponentFormatter.INITIAL_LINE_CAPACITY);
		this.indentLevel = 0;
		this.leftTrim = false;
		this.bracketHistory = new ArrayList<Character>(ComponentFormatter.INITIAL_BRACKET_HISTORY_CAPACITY);
		this.closingBracket = ' ';
		this.closingQuote = ' ';
		this.inEmptyBrackets = false;
		this.inString = false;
		this.inCurlyBracketString = false;
		this.formattingError = false;
	}

	private void processCharacter() {
		if (this.inEmptyBrackets) {
			this.appendCurrentCharacter();
			this.inEmptyBrackets = false;
			this.inCurlyBracketString = false;
			return;
		}

		if (this.leftTrim && this.currentChar == ' ')
			return;
		this.leftTrim = false;

		if (this.inString)
			this.formatInsideOfString();
		else if (this.inCurlyBracketString)
			this.formatInsideOfCurlyBracketString();
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

	private void formatInsideOfCurlyBracketString() {
		switch (this.currentChar) {
			case '}' -> this.processCurlyBracketStringEnd();
			default -> this.appendCurrentCharacter();
		}
	}

	private void formatOutsideOfString() {
		switch (this.currentChar) {
			case ',', ';' -> this.processComma();
			case '{', '[' -> this.processOpeningBracket();
			case '}', ']' -> this.processClosingBracket();
			case '"', '\'' -> this.processQuote();
			case 'l' -> this.processCurlyBracketStringBegin("literal");
			case 'k' -> this.processCurlyBracketStringBegin("keybind");
			case 'p' -> this.processCurlyBracketStringBegin("pattern");
			default -> this.appendCurrentCharacter();
		}
	}

	private void processComma() {
		this.appendCurrentCharacter();
		this.prepareNewLine();
	}

	private void processOpeningBracket() {
		if (this.currentIndex + 1 < this.componentValue.length() && (ComponentFormatter.BRACKET_PAIR.get(this.currentChar) == this.componentValue.charAt(this.currentIndex + 1))) {
			this.appendCurrentCharacter();
			this.inEmptyBrackets = true;
			return;
		}

		this.bracketHistory.add(this.currentChar);
		this.indentLevel++;
		this.appendCurrentCharacter();
		this.prepareNewLine();
	}

	private void processClosingBracket() {
		if (!this.bracketHistory.isEmpty()) {
			if (this.currentChar != ComponentFormatter.BRACKET_PAIR.get(this.bracketHistory.getLast())) {
				this.formattingError = true;
				this.appendCurrentCharacter();
				return;
			}
			this.bracketHistory.removeLast();
		}

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

	private void processCurlyBracketStringBegin(String beginSequence) {
		int beginSequenceLength = beginSequence.length();

		if (this.currentIndex + beginSequenceLength >= this.componentValue.length()) {
			this.appendCurrentCharacter();
			return;
		}

		String subSequence = this.componentValue.substring(this.currentIndex, this.currentIndex + beginSequenceLength + 1);

		if (!(beginSequence + '{').equals(subSequence)) {
			this.appendCurrentCharacter();
			return;
		}

		this.line.append(beginSequence);
		this.currentIndex += beginSequenceLength;
		this.inCurlyBracketString = true;

		this.currentChar = '{';
		this.processOpeningBracket();
	}

	private void processCurlyBracketStringEnd() {
		this.inCurlyBracketString = false;

		this.processClosingBracket();
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

	public boolean isFormattingError() {
		return this.formattingError;
	}
}