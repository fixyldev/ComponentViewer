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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.component.Component;
import net.minecraft.text.Text;

import dev.fixyl.componentviewer.component.ComponentDisplay;
import dev.fixyl.componentviewer.config.Config;

public class ClassFormatter extends AbstractFormatter {
	private static final int INITIAL_INDENT_CACHE_CAPACITY = 12;
    private static final int INITIAL_LINE_CAPACITY = 16;
	private static final int INITIAL_BRACKET_HISTORY_CAPACITY = 12;

	private static final Map<Character, Character> BRACKET_PAIR = Map.of(
		'{', '}',
		'[', ']'
	);

	private final Map<Integer, String> indentCache;

	private String componentValue;
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

	public ClassFormatter() {
		this.indentCache = new HashMap<>(ClassFormatter.INITIAL_INDENT_CACHE_CAPACITY);
	}

    @Override
    public void setIndentSize(Integer indentSize) {
        if (this.getIndentSize() == indentSize)
            return;

		super.setIndentSize(indentSize);

		if (this.indentCache != null)
			this.indentCache.clear();
	}

	public List<Text> formatComponent(Component<?> component) {
		this.setIndentSize(Config.INDENT_SIZE.getValue());

		this.initializeFormattingVariables(component);

		this.line.append(this.getIndentPrefixFromLevel(this.indentLevel));

		if (Config.INDENT_SIZE.getValue() == 0) {
			this.line.append(this.componentValue);
			this.textList.add((Text)Text.literal(this.line.toString()).formatted(ComponentDisplay.GENERAL_FORMATTING));

			return this.textList;
		}

		for (this.currentIndex = 0; this.currentIndex < componentValue.length(); this.currentIndex++) {
			this.currentChar = this.componentValue.charAt(this.currentIndex);

			this.processCharacter();
		}

		if (this.line.length() != 0)
			this.textList.add((Text)Text.literal(this.line.toString()).formatted(ComponentDisplay.GENERAL_FORMATTING));

		if (this.formattingError || this.indentLevel != 0 || this.inString || this.inCurlyBracketString) {
			this.textList.clear();
			this.textList.add((Text)Text.literal(this.getIndentPrefixFromLevel(0) + this.componentValue).formatted(ComponentDisplay.GENERAL_FORMATTING));
			this.textList.add((Text)Text.empty());
			this.textList.add((Text)Text.translatable("componentviewer.tooltips.components.error.class_formatting").formatted(ComponentDisplay.HEADER_FORMATTING));
		}

		return this.textList;
    }

	private String getIndentPrefixFromLevel(int indentLevel) {
		if (indentLevel <= 0)
			return ComponentDisplay.GENERAL_INDENT_PREFIX;

		if (this.indentCache.containsKey(indentLevel))
			return this.indentCache.get(indentLevel);

		return this.indentCache.put(indentLevel, ComponentDisplay.GENERAL_INDENT_PREFIX + this.getIndentPrefix().repeat(indentLevel));
	}

	private void initializeFormattingVariables(Component<?> component) {
		this.componentValue = component.value().toString();

		this.line = new StringBuilder(ClassFormatter.INITIAL_LINE_CAPACITY);
		this.textList = new ArrayList<Text>(AbstractFormatter.INITIAL_TEXT_LIST_CAPACITY);

		this.indentLevel = 0;
		this.leftTrim = false;
		this.bracketHistory = new ArrayList<>(ClassFormatter.INITIAL_BRACKET_HISTORY_CAPACITY);
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
		if (this.currentIndex + 1 < this.componentValue.length() && (ClassFormatter.BRACKET_PAIR.get(this.currentChar) == this.componentValue.charAt(this.currentIndex + 1))) {
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
			if (this.currentChar != ClassFormatter.BRACKET_PAIR.get(this.bracketHistory.getLast())) {
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
		this.textList.add((Text)Text.literal(this.line.toString()).formatted(ComponentDisplay.GENERAL_FORMATTING));
		this.line.setLength(0);
	}

	private void onNewLine() {
		if (this.line.length() != 0)
			return;

		line.append(this.getIndentPrefixFromLevel(this.indentLevel));

		if (this.closingBracket != ' ') {
			line.append(this.closingBracket);
			this.closingBracket = ' ';
		}

		this.leftTrim = true;
	}
}
