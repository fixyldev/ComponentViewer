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

package dev.fixyl.componentviewer.formatting;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.minecraft.component.Component;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import dev.fixyl.componentviewer.util.ResultCache;

public class ObjectFormatter implements Formatter {
    private static final Map<TokenType, Style> TOKEN_STYLES = Map.ofEntries(
            Map.entry(TokenType.ANY, Style.EMPTY.withColor(Formatting.AQUA)),
            Map.entry(TokenType.SPECIAL, Style.EMPTY.withColor(Formatting.WHITE)),
            Map.entry(TokenType.OPENING_BRACKET, Style.EMPTY.withColor(Formatting.WHITE)),
            Map.entry(TokenType.CLOSING_BRACKET, Style.EMPTY.withColor(Formatting.WHITE)),
            Map.entry(TokenType.COMMA, Style.EMPTY.withColor(Formatting.WHITE)),
            Map.entry(TokenType.QUOTE, Style.EMPTY.withColor(Formatting.WHITE)),
            Map.entry(TokenType.STRING, Style.EMPTY.withColor(Formatting.GREEN)),
            Map.entry(TokenType.INTEGER, Style.EMPTY.withColor(Formatting.GOLD)),
            Map.entry(TokenType.FLOAT, Style.EMPTY.withColor(Formatting.GOLD)),
            Map.entry(TokenType.HEX, Style.EMPTY.withColor(Formatting.GOLD)),
            Map.entry(TokenType.BOOLEAN, Style.EMPTY.withColor(Formatting.GOLD)),
            Map.entry(TokenType.NULL, Style.EMPTY.withColor(Formatting.BLUE))
    );

    private static final Map<Character, Character> BRACKET_PAIR = Map.of(
            '(', ')',
            '{', '}',
            '[', ']'
    );

    private final ResultCache<String> stringResultCache;
    private final ResultCache<List<Text>> textResultCache;

    private final Tokenizer tokenizer;
    private final Map<Integer, String> lineAndIndentPrefixCache;

    private int indentation;
    private boolean colored;

    private String indentPrefix;
    private String linePrefix;
    private boolean isLinePrefixAndIndentationSet;

    private List<Token> tokens;
    private int currentIndex;
    private Token currentToken;
    private int indentLevel;
    private List<Character> bracketHistory;
    private State state;

    private boolean formatAsText;
    private List<Text> textList;
    private MutableText textLine;
    private StringBuilder stringBuilder;

    public ObjectFormatter() {
        this.stringResultCache = new ResultCache<>();
        this.textResultCache = new ResultCache<>();

        this.tokenizer = new Tokenizer();
        this.lineAndIndentPrefixCache = new HashMap<>();

        this.isLinePrefixAndIndentationSet = false;
    }

    @Override
    public <T> String componentToString(Component<T> component, int indentation, String linePrefix) {
        return this.stringResultCache.cache(() -> {
            String componentValue = component.value().toString();

            if (indentation <= 0) {
                return linePrefix + componentValue;
            }

            List<Token> tokenList = this.tokenizer.tokenize(componentValue);

            return this.formatTokensAsString(tokenList, indentation, linePrefix);
        }, component, indentation, linePrefix);
    }

    @Override
    public <T> List<Text> componentToText(Component<T> component, int indentation, boolean colored, String linePrefix) {
        return Collections.unmodifiableList(this.textResultCache.cache(() -> {
            String componentValue = component.value().toString();

            if (indentation <= 0 && !colored) {
                return List.of(Text.literal(linePrefix + componentValue).fillStyle(Formatter.NO_COLOR_STYLE));
            }

            List<Token> tokenList = this.tokenizer.tokenize(componentValue);

            if (indentation <= 0) {
                MutableText line = Text.literal(linePrefix);

                for (Token token : tokenList) {
                    line.append(Text.literal(token.content()).fillStyle(ObjectFormatter.TOKEN_STYLES.get(token.tokenType())));
                }

                return List.of(line);
            }

            return this.formatTokensAsText(tokenList, indentation, colored, linePrefix);
        }, component, indentation, colored, linePrefix));
    }

    private String formatTokensAsString(List<Token> tokens, int indentation, String linePrefix) {
        this.formatAsText = false;

        this.stringBuilder = new StringBuilder(linePrefix);

        this.formatTokens(tokens, indentation, linePrefix);

        return this.stringBuilder.toString();
    }

    private List<Text> formatTokensAsText(List<Token> tokens, int indentation, boolean colored, String linePrefix) {
        this.formatAsText = true;

        this.textList = new ArrayList<>();
        this.textLine = Text.literal(linePrefix);
        this.colored = colored;

        if (!this.colored) {
            this.textLine.fillStyle(Formatter.NO_COLOR_STYLE);
        }

        this.formatTokens(tokens, indentation, linePrefix);

        if (!this.textLine.getString().isEmpty()) {
            this.textList.add(this.textLine);
        }

        return this.textList;
    }

    private void updateLinePrefixAndIndentation(int indentation, String linePrefix) {
        if (this.isLinePrefixAndIndentationSet && this.indentation == indentation && this.linePrefix.equals(linePrefix)) {
            return;
        }

        this.indentation = indentation;
        this.indentPrefix = " ".repeat(indentation);
        this.linePrefix = linePrefix;

        this.isLinePrefixAndIndentationSet = true;

        if (this.lineAndIndentPrefixCache != null) {
            this.lineAndIndentPrefixCache.clear();
        }
    }

    private String getLineAndIndentPrefixFromLevel(int indentLevel) {
        if (indentLevel <= 0) {
            return this.linePrefix;
        }

        return this.lineAndIndentPrefixCache.computeIfAbsent(indentLevel, key -> this.linePrefix + this.indentPrefix.repeat(key));
    }

    private void formatTokens(List<Token> tokens, int indentation, String linePrefix) {
        this.updateLinePrefixAndIndentation(indentation, linePrefix);

        this.tokens = tokens;

        this.indentLevel = 0;
        this.bracketHistory = new ArrayList<>();

        this.state = State.DEFAULT;

        for (this.currentIndex = 0; this.currentIndex < this.tokens.size(); this.currentIndex++) {
            this.currentToken = this.tokens.get(this.currentIndex);
            this.processToken();
        }

        if (this.indentLevel != 0) {
            throw new FormattingException(String.format("Indent level must end up being zero! But it was %s.", this.indentLevel));
        }
    }

    private void processToken() {
        switch (this.currentToken.tokenType()) {
            case COMMA -> this.processCommaToken();
            case OPENING_BRACKET -> this.processOpeningBracketToken();
            case CLOSING_BRACKET -> this.processClosingBracketToken();
            default -> this.addCurrentToken();
        }
    }

    private void processCommaToken() {
        this.addCurrentToken();
        this.createNewLineBreak(0);
    }

    private void processOpeningBracketToken() {
        this.bracketHistory.add(this.currentToken.content().charAt(0));

        this.addCurrentToken();

        if (this.currentIndex < this.tokens.size() - 1 && this.tokens.get(this.currentIndex + 1).tokenType() == TokenType.CLOSING_BRACKET) {
            this.state = State.EMPTY_BRACKETS;
            return;
        }

        this.createNewLineBreak(1);
    }

    private void processClosingBracketToken() {
        char bracketCharacter = this.currentToken.content().charAt(0);

        if (this.bracketHistory.isEmpty() || !ObjectFormatter.BRACKET_PAIR.get(this.bracketHistory.getLast()).equals(bracketCharacter)) {
            throw new FormattingException(String.format("Unexpected bracket '%s' encountered! Either no pair was to be closed, or a different bracket opened this pair.", bracketCharacter));
        }

        this.bracketHistory.removeLast();

        if (this.state == State.EMPTY_BRACKETS) {
            this.state = State.DEFAULT;
        } else {
            this.createNewLineBreak(-1);
        }

        this.addCurrentToken();
    }

    private void createNewLineBreak(int indentChange) {
        this.indentLevel += indentChange;

        if (this.formatAsText) {
            this.textList.add(this.textLine);
            this.textLine = Text.literal(this.getLineAndIndentPrefixFromLevel(this.indentLevel));

            if (!this.colored) {
                this.textLine.fillStyle(Formatter.NO_COLOR_STYLE);
            }
        } else {
            this.stringBuilder.append(System.lineSeparator()).append(this.getLineAndIndentPrefixFromLevel(this.indentLevel));
        }

        this.state = State.NEW_LINE;
    }

    private void addCurrentToken() {
        String tokenContent = this.currentToken.content();

        if (this.state == State.NEW_LINE) {
            tokenContent = tokenContent.stripLeading();
            this.state = State.DEFAULT;
        }

        if (this.formatAsText) {
            this.textLine.append(Text.literal(tokenContent).fillStyle((this.colored) ? ObjectFormatter.TOKEN_STYLES.get(this.currentToken.tokenType()) : Formatter.NO_COLOR_STYLE));
        } else {
            this.stringBuilder.append(tokenContent);
        }
    }

    private enum State {
        DEFAULT,
        NEW_LINE,
        EMPTY_BRACKETS
    }

    private static class Tokenizer {
        private static final Pattern NON_WORD_CHAR_PATTERN = Pattern.compile("^[^\\p{L}\\p{N}_]$");
        private static final Pattern NON_WORD_DOT_CHAR_PATTERN = Pattern.compile("^[^\\p{L}\\p{N}_.]$");
        private static final Pattern NON_WORD_DOT_DASH_CHAR_PATTERN = Pattern.compile("^[^\\p{L}\\p{N}_.\\-]$");

        private static final Pattern CURLY_BRACKET_STRING_BEGIN_PATTERN = Pattern.compile("^(keybind|literal|pattern)\\{");
        private static final Pattern INTEGER_PATTERN = Pattern.compile("^(-?\\d+)(?![\\p{L}\\p{N}_.\\-])");
        private static final Pattern FLOAT_PATTERN = Pattern.compile("^(-?\\d+\\.\\d+)(?![\\p{L}\\p{N}_.\\-])");
        private static final Pattern HEX_PATTERN = Pattern.compile("^([a-fA-F\\d]+)(?![\\p{L}\\p{N}_.])");
        private static final Pattern BOOLEAN_PATTERN = Pattern.compile("^(true|false)(?![\\p{L}\\p{N}_])");
        private static final Pattern NULL_PATTERN = Pattern.compile("^(null)(?![\\p{L}\\p{N}_])");

        private Map<Pattern, Matcher> patternMatcherMap;

        private String currentString;
        private int stringLength;

        private int currentIndex;
        private char currentChar;

        private List<Token> tokens;
        private StringBuilder currentTokenContent;
        private TokenType currentTokenType;

        private TokenizerState tokenizerState;
        private char currentOpeningQuote;

        public List<Token> tokenize(String string) {
            this.initializeTokenizerVariables(string);

            while (this.currentIndex < this.stringLength) {
                this.currentChar = string.charAt(this.currentIndex);
                this.processCharacter();

                this.currentIndex++;
            }

            this.finishCurrentToken();

            return this.tokens;
        }

        private void initializeTokenizerVariables(String string) {
            this.patternMatcherMap = new IdentityHashMap<>();

            this.currentString = string;
            this.stringLength = string.length();

            this.currentIndex = 0;

            this.tokens = new ArrayList<>();
            this.currentTokenContent = new StringBuilder();
            this.currentTokenType = TokenType.ANY;

            this.tokenizerState = TokenizerState.DEFAULT;
            this.currentOpeningQuote = '\0';
        }

        private Matcher getMatcherFromPattern(Pattern pattern) {
            if (!this.patternMatcherMap.containsKey(pattern)) {
                this.patternMatcherMap.put(pattern, pattern.matcher(this.currentString));
            }

            return this.patternMatcherMap.get(pattern);
        }

        private void processCharacter() {
            switch (this.tokenizerState) {
                case DEFAULT -> this.processDefaultTokenizerState();
                case STRING -> this.processStringTokenizerState();
                case CURLY_BRACKET_STRING -> this.processCurlyBracketStringTokenizerState();
            }
        }

        private void processDefaultTokenizerState() {
            // May match based on context
            if (Tokenizer.isCurlyBracketStringBeginCharacter(this.currentChar) && this.matchCurlyBracketStringBegin()) {
                return;
            }

            if (Tokenizer.isNumberCharacter(this.currentChar) && this.matchNumber()) {
                return;
            }

            if (Tokenizer.isBooleanCharacter(this.currentChar) && this.matchBoolean()) {
                return;
            }

            if (Tokenizer.isNullCharacter(this.currentChar) && this.matchNull()) {
                return;
            }

            // Will always match
            switch (this.currentChar) {
                case ',', ';' -> this.processComma();
                case '(', '{', '[' -> this.processOpeningBracket();
                case ')', '}', ']' -> this.processClosingBracket();
                case '"', '\'' -> this.processOpeningQuote();
                case '+', '-', '*', '/', '=', '.', ':', '!', '?', '@', '#', '&', '%', '~', '<', '>', '|', '^', '\\' -> this.processSpecialCharacter();
                default -> this.addCurrentCharacter(TokenType.ANY);
            }
        }

        private void processStringTokenizerState() {
            // May match based on context
            if (Tokenizer.isQuoteCharacter(this.currentChar) && this.matchClosingQuote()) {
                return;
            }

            // Will always match
            this.addCurrentCharacter(TokenType.STRING);
        }

        private void processCurlyBracketStringTokenizerState() {
            // Will always match
            if (this.currentChar == '}') {
                this.processCurlyBracketStringEnd();
            } else {
                this.addCurrentCharacter(TokenType.STRING);
            }
        }

        private void processSpecialCharacter() {
            this.finishCurrentToken();

            this.addCurrentCharacter(TokenType.SPECIAL);
        }

        private void processComma() {
            this.finishCurrentToken();

            this.addCurrentCharacter(TokenType.COMMA);
        }

        private void processOpeningBracket() {
            this.finishCurrentToken();

            this.addCurrentCharacter(TokenType.OPENING_BRACKET);
        }

        private void processClosingBracket() {
            this.finishCurrentToken();

            this.addCurrentCharacter(TokenType.CLOSING_BRACKET);
        }

        private void processOpeningQuote() {
            this.finishCurrentToken();

            this.addCurrentCharacter(TokenType.QUOTE);
            this.currentOpeningQuote = this.currentChar;

            this.tokenizerState = TokenizerState.STRING;
        }

        private boolean matchClosingQuote() {
            if (this.currentChar != this.currentOpeningQuote || this.currentString.charAt(this.currentIndex - 1) == '\\') {
                return false;
            }

            this.finishCurrentToken(TokenType.STRING);

            this.tokenizerState = TokenizerState.DEFAULT;

            this.addCurrentCharacter(TokenType.QUOTE);
            this.currentOpeningQuote = '\0';

            return true;
        }

        private boolean matchCurlyBracketStringBegin() {
            if (!this.matchRegex(Tokenizer.NON_WORD_CHAR_PATTERN, Tokenizer.CURLY_BRACKET_STRING_BEGIN_PATTERN, TokenType.ANY)) {
                return false;
            }

            this.currentIndex++;
            this.currentChar = '{';
            this.processOpeningBracket();

            this.tokenizerState = TokenizerState.CURLY_BRACKET_STRING;

            return true;
        }

        private void processCurlyBracketStringEnd() {
            this.tokenizerState = TokenizerState.DEFAULT;

            this.processClosingBracket();
        }

        private boolean matchNumber() {
            return this.matchRegex(Tokenizer.NON_WORD_DOT_DASH_CHAR_PATTERN, Tokenizer.INTEGER_PATTERN, TokenType.INTEGER) ||
                   this.matchRegex(Tokenizer.NON_WORD_DOT_DASH_CHAR_PATTERN, Tokenizer.FLOAT_PATTERN, TokenType.FLOAT) ||
                   this.matchRegex(Tokenizer.NON_WORD_DOT_CHAR_PATTERN, Tokenizer.HEX_PATTERN, TokenType.HEX);
        }

        private boolean matchBoolean() {
            return this.matchRegex(Tokenizer.NON_WORD_CHAR_PATTERN, Tokenizer.BOOLEAN_PATTERN, TokenType.BOOLEAN);
        }

        private boolean matchNull() {
            return this.matchRegex(Tokenizer.NON_WORD_CHAR_PATTERN, Tokenizer.NULL_PATTERN, TokenType.NULL);
        }

        private boolean matchRegex(Pattern leadingCharPattern, Pattern contentPattern, TokenType tokenType) {
            Matcher leadingCharMatcher = this.getMatcherFromPattern(leadingCharPattern);
            Matcher contentMatcher = this.getMatcherFromPattern(contentPattern);

            boolean isValidLeadingChar = true;

            if (this.currentIndex > 0) {
                leadingCharMatcher.region(this.currentIndex - 1, this.currentIndex);
                isValidLeadingChar = leadingCharMatcher.matches();
            }

            contentMatcher.region(this.currentIndex, this.stringLength);

            if (!isValidLeadingChar || !contentMatcher.find()) {
                return false;
            }

            this.finishCurrentToken();
            this.addCharacters(contentMatcher.group(1), tokenType);
            this.finishCurrentToken(tokenType);

            return true;
        }

        private void addCurrentCharacter(TokenType tokenType) {
            this.currentTokenContent.append(this.currentChar);

            if (TokenType.singleCharacterTokenTypes.contains(tokenType)) {
                this.finishCurrentToken(tokenType);
            }

            this.currentTokenType = tokenType;
        }

        private void addCharacters(String characters, TokenType tokenType) {
            this.currentTokenContent.append(characters);

            this.currentTokenType = tokenType;
            this.currentIndex += characters.length() - 1;
        }

        private void finishCurrentToken() {
            this.finishCurrentToken(this.currentTokenType);
        }

        private void finishCurrentToken(TokenType tokenType) {
            if (!this.currentTokenContent.isEmpty()) {
                Token token = new Token(tokenType, this.currentTokenContent.toString());

                this.currentTokenContent.setLength(0);
                this.tokens.add(token);
            }

            this.currentTokenType = TokenType.ANY;
        }

        private static boolean isQuoteCharacter(char ch) {
            return ch == '"' ||
                   ch == '\'';
        }

        private static boolean isCurlyBracketStringBeginCharacter(char ch) {
            return ch == 'k' ||
                   ch == 'l' ||
                   ch == 'p';
        }

        private static boolean isNumberCharacter(char ch) {
            return ch >= '0' && ch <= '9' ||
                   ch >= 'A' && ch <= 'F' ||
                   ch >= 'a' && ch <= 'f' ||
                   ch == '-';
        }

        private static boolean isBooleanCharacter(char ch) {
            return ch == 'f' ||
                   ch == 't';
        }

        private static boolean isNullCharacter(char ch) {
            return ch == 'n';
        }

        private enum TokenizerState {
            DEFAULT,
            STRING,
            CURLY_BRACKET_STRING
        }
    }

    private enum TokenType {
        ANY,
        SPECIAL,
        OPENING_BRACKET,
        CLOSING_BRACKET,
        COMMA,
        QUOTE,
        STRING,
        INTEGER,
        FLOAT,
        HEX,
        BOOLEAN,
        NULL;

        private static EnumSet<TokenType> singleCharacterTokenTypes = EnumSet.of(
            TokenType.SPECIAL,
            TokenType.OPENING_BRACKET,
            TokenType.CLOSING_BRACKET,
            TokenType.COMMA,
            TokenType.QUOTE
        );
    }

    private static record Token(TokenType tokenType, String content) {}
}
