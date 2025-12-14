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

import net.minecraft.ChatFormatting;
import net.minecraft.core.component.TypedDataComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.world.item.ItemStack;

import dev.fixyl.componentviewer.util.ResultCache;

public class ObjectFormatter implements Formatter {

    private static final Map<TokenType, Style> TOKEN_STYLES = Map.ofEntries(
        Map.entry(TokenType.ANY, Style.EMPTY.withColor(ChatFormatting.AQUA)),
        Map.entry(TokenType.SPECIAL, Style.EMPTY.withColor(ChatFormatting.WHITE)),
        Map.entry(TokenType.OPENING_BRACKET, Style.EMPTY.withColor(ChatFormatting.WHITE)),
        Map.entry(TokenType.CLOSING_BRACKET, Style.EMPTY.withColor(ChatFormatting.WHITE)),
        Map.entry(TokenType.COMMA, Style.EMPTY.withColor(ChatFormatting.WHITE)),
        Map.entry(TokenType.QUOTE, Style.EMPTY.withColor(ChatFormatting.WHITE)),
        Map.entry(TokenType.STRING, Style.EMPTY.withColor(ChatFormatting.GREEN)),
        Map.entry(TokenType.INTEGER, Style.EMPTY.withColor(ChatFormatting.GOLD)),
        Map.entry(TokenType.FLOAT, Style.EMPTY.withColor(ChatFormatting.GOLD)),
        Map.entry(TokenType.HEX, Style.EMPTY.withColor(ChatFormatting.GOLD)),
        Map.entry(TokenType.BOOLEAN, Style.EMPTY.withColor(ChatFormatting.GOLD)),
        Map.entry(TokenType.NULL, Style.EMPTY.withColor(ChatFormatting.BLUE))
    );

    private static final Map<Character, Character> BRACKET_PAIR = Map.of(
        '(', ')',
        '{', '}',
        '[', ']'
    );

    private final ResultCache<String> stringResultCache;
    private final ResultCache<List<Component>> textResultCache;

    private final Tokenizer tokenizer;
    private final Map<Integer, String> newLinePrefixCache;

    private int indentation;
    private boolean colored;

    private String indentPrefix;
    private String linePrefix;
    private boolean isNewLinePrefixSet;

    private List<Token> tokens;
    private int currentIndex;
    private Token currentToken;
    private int indentLevel;
    private List<Character> bracketHistory;
    private State state;

    private boolean formatAsText;
    private List<Component> textList;
    private MutableComponent textLine;
    private StringBuilder stringBuilder;

    public ObjectFormatter() {
        this.stringResultCache = new ResultCache<>();
        this.textResultCache = new ResultCache<>();

        this.tokenizer = new Tokenizer();
        this.newLinePrefixCache = new HashMap<>();

        this.isNewLinePrefixSet = false;
    }

    @Override
    public <T> String componentToString(TypedDataComponent<T> component, int indentation, String linePrefix) {
        return this.valueToString(component.value().toString(), indentation, linePrefix);
    }

    @Override
    public <T> List<Component> componentToText(TypedDataComponent<T> component, int indentation, boolean colored, String linePrefix) {
        return this.valueToText(component.value().toString(), indentation, colored, linePrefix);
    }

    @Override
    public String itemStackToString(ItemStack itemStack, int indentation, String linePrefix) {
        return this.valueToString(itemStack.toString(), indentation, linePrefix);
    }

    @Override
    public List<Component> itemStackToText(ItemStack itemStack, int indentation, boolean colored, String linePrefix) {
        return this.valueToText(itemStack.toString(), indentation, colored, linePrefix);
    }

    private String valueToString(String value, int indentation, String linePrefix) {
        return this.stringResultCache.cache(() -> {
            if (indentation <= 0) {
                return linePrefix + value;
            }

            List<Token> tokenList = this.tokenizer.tokenize(value);

            return this.formatTokensAsString(tokenList, indentation, linePrefix);
        }, value, indentation, linePrefix);
    }

    private List<Component> valueToText(String value, int indentation, boolean colored, String linePrefix) {
        return Collections.unmodifiableList(this.textResultCache.cache(() -> {
            if (indentation <= 0 && !colored) {
                return List.of(Component.literal(linePrefix + value).withStyle(NO_COLOR_STYLE));
            }

            List<Token> tokenList = this.tokenizer.tokenize(value);

            if (indentation <= 0) {
                MutableComponent line = Component.literal(linePrefix);

                for (Token token : tokenList) {
                    line.append(Component.literal(token.content()).withStyle(TOKEN_STYLES.get(token.tokenType())));
                }

                return List.of(line);
            }

            return this.formatTokensAsText(tokenList, indentation, colored, linePrefix);
        }, value, indentation, colored, linePrefix));
    }

    private String formatTokensAsString(List<Token> tokens, int indentation, String linePrefix) {
        this.formatAsText = false;

        this.stringBuilder = new StringBuilder(linePrefix);

        this.formatTokens(tokens, indentation, linePrefix);

        return this.stringBuilder.toString();
    }

    private List<Component> formatTokensAsText(List<Token> tokens, int indentation, boolean colored, String linePrefix) {
        this.formatAsText = true;

        this.textList = new ArrayList<>();
        this.textLine = Component.literal(linePrefix);
        this.colored = colored;

        if (!this.colored) {
            this.textLine.withStyle(NO_COLOR_STYLE);
        }

        this.formatTokens(tokens, indentation, linePrefix);

        if (!this.textLine.getString().isEmpty()) {
            this.textList.add(this.textLine);
        }

        return this.textList;
    }

    private void updateNewLinePrefix(int indentation, String linePrefix) {
        if (this.isNewLinePrefixSet && this.indentation == indentation && this.linePrefix.equals(linePrefix)) {
            return;
        }

        this.indentation = indentation;
        this.indentPrefix = " ".repeat(indentation);
        this.linePrefix = linePrefix;

        this.isNewLinePrefixSet = true;

        if (this.newLinePrefixCache != null) {
            this.newLinePrefixCache.clear();
        }
    }

    private String getNewLinePrefix() {
        if (this.indentLevel <= 0) {
            return this.linePrefix;
        }

        return this.newLinePrefixCache.computeIfAbsent(this.indentLevel, key -> this.linePrefix + this.indentPrefix.repeat(key));
    }

    private void formatTokens(List<Token> tokens, int indentation, String linePrefix) {
        this.updateNewLinePrefix(indentation, linePrefix);

        this.tokens = tokens;

        this.indentLevel = 0;
        this.bracketHistory = new ArrayList<>();

        this.state = State.DEFAULT;

        for (this.currentIndex = 0; this.currentIndex < this.tokens.size(); this.currentIndex++) {
            this.currentToken = this.tokens.get(this.currentIndex);
            this.processToken();
        }

        if (this.indentLevel != 0) {
            throw new FormattingException(String.format(
                "Indent level must end up being zero! But it was %s.",
                this.indentLevel
            ));
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
        this.createNewLine(0);
    }

    private void processOpeningBracketToken() {
        this.bracketHistory.add(this.currentToken.content().charAt(0));

        this.addCurrentToken();

        if (this.currentIndex < this.tokens.size() - 1 && this.tokens.get(this.currentIndex + 1).tokenType() == TokenType.CLOSING_BRACKET) {
            this.state = State.EMPTY_BRACKETS;
            return;
        }

        this.createNewLine(1);
    }

    private void processClosingBracketToken() {
        char bracketCharacter = this.currentToken.content().charAt(0);

        if (this.bracketHistory.isEmpty() || !BRACKET_PAIR.get(this.bracketHistory.getLast()).equals(bracketCharacter)) {
            throw new FormattingException(String.format(
                "Unexpected bracket '%s' encountered! Either no pair was to be closed, or a different bracket opened this pair.",
                bracketCharacter
            ));
        }

        this.bracketHistory.removeLast();

        if (this.state == State.EMPTY_BRACKETS) {
            this.state = State.DEFAULT;
        } else {
            this.createNewLine(-1);
        }

        this.addCurrentToken();
    }

    private void createNewLine(int indentChange) {
        this.indentLevel += indentChange;

        if (this.formatAsText) {
            this.textList.add(this.textLine);
            this.textLine = Component.literal(this.getNewLinePrefix());

            if (!this.colored) {
                this.textLine.withStyle(NO_COLOR_STYLE);
            }
        } else {
            this.stringBuilder.append(System.lineSeparator()).append(this.getNewLinePrefix());
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
            this.textLine.append(Component.literal(tokenContent).withStyle((this.colored) ? TOKEN_STYLES.get(this.currentToken.tokenType()) : NO_COLOR_STYLE));
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
        private static final Pattern INTEGER_PATTERN = Pattern.compile("^(-?\\d+[bBsSiIlL]?)(?![\\p{L}\\p{N}_.\\-])");
        private static final Pattern FLOAT_PATTERN = Pattern.compile("^(-?\\d+\\.\\d+[fFdD]?)(?![\\p{L}\\p{N}_.\\-])");
        private static final Pattern HEX_PATTERN = Pattern.compile("^([a-fA-F\\d]{2,})(?![\\p{L}\\p{N}_.])");
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
            return this.patternMatcherMap.computeIfAbsent(pattern, key -> key.matcher(this.currentString));
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
            if (!this.matchRegex(NON_WORD_CHAR_PATTERN, CURLY_BRACKET_STRING_BEGIN_PATTERN, TokenType.ANY)) {
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
            return this.matchRegex(NON_WORD_DOT_DASH_CHAR_PATTERN, INTEGER_PATTERN, TokenType.INTEGER)
                || this.matchRegex(NON_WORD_DOT_DASH_CHAR_PATTERN, FLOAT_PATTERN, TokenType.FLOAT)
                || this.matchRegex(NON_WORD_DOT_CHAR_PATTERN, HEX_PATTERN, TokenType.HEX);
        }

        private boolean matchBoolean() {
            return this.matchRegex(NON_WORD_CHAR_PATTERN, BOOLEAN_PATTERN, TokenType.BOOLEAN);
        }

        private boolean matchNull() {
            return this.matchRegex(NON_WORD_CHAR_PATTERN, NULL_PATTERN, TokenType.NULL);
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
            return ch == '"'
                || ch == '\'';
        }

        private static boolean isCurlyBracketStringBeginCharacter(char ch) {
            return ch == 'k'
                || ch == 'l'
                || ch == 'p';
        }

        private static boolean isNumberCharacter(char ch) {
            return ch >= '0' && ch <= '9'
                || ch >= 'A' && ch <= 'F'
                || ch >= 'a' && ch <= 'f'
                || ch == '-';
        }

        private static boolean isBooleanCharacter(char ch) {
            return ch == 'f'
                || ch == 't';
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
            SPECIAL,
            OPENING_BRACKET,
            CLOSING_BRACKET,
            COMMA,
            QUOTE
        );
    }

    private static record Token(TokenType tokenType, String content) {}
}
