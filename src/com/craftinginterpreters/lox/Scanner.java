package com.craftinginterpreters.lox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import jlox.TokenType; // this is the same as com.craftinginterpreters.lox.TokenType.*; in the book
// because we didn't import TokenType statically, we have to type TokenType.EOF (example)

public class Scanner {
    private final String source; // the source code
    private final List<Token> tokens = new ArrayList<>(); // stores the tokens we generate
    private int start = 0; // first character being scanned
    private int current = 0; // character currently being considered
    private int line = 1;

    private static final Map<String, TokenType> keywords;

    static {
        keywords = new HashMap<>();
        keywords.put("and", TokenType.AND);
        keywords.put("class", TokenType.CLASS);
        keywords.put("else", TokenType.ELSE);
        keywords.put("false", TokenType.FALSE);
        keywords.put("for", TokenType.FOR);
        keywords.put("fun", TokenType.FUN);
        keywords.put("if", TokenType.IF);
        keywords.put("nil", TokenType.NIL);
        keywords.put("or", TokenType.OR);
        keywords.put("print", TokenType.PRINT);
        keywords.put("return", TokenType.RETURN);
        keywords.put("super", TokenType.SUPER);
        keywords.put("this", TokenType.THIS);
        keywords.put("true", TokenType.TRUE);
        keywords.put("var", TokenType.VAR);
        keywords.put("while", TokenType.WHILE);
    }
    /**
     * Parameterized constructor for a scanner
     * @param source
     */
    Scanner (String source) {
        this.source = source;
    }
    
    /**
     * Fills our list with the tokens we find
     * @return
     */
    List<Token> scanTokens() {
        while (!isAtEnd()) {
            // We are at the beginning of the next lexeme
            start = current;
            scanToken();
        }

        tokens.add(new Token(TokenType.EOF, "", null, line));
        return tokens;
    }

    /**
     * Scans individual tokens
     */
    private void scanToken() {
        char c = advance();
        switch (c) {
            case '(': addToken(TokenType.LEFT_PAREN); break;
            case ')': addToken(TokenType.RIGHT_PAREN); break;
            case '{': addToken(TokenType.LEFT_BRACE); break;
            case '}': addToken(TokenType.RIGHT_BRACE); break;
            case ',': addToken(TokenType.COMMA); break;
            case '.': addToken(TokenType.DOT); break;
            case '-': addToken(TokenType.MINUS); break;
            case '+': addToken(TokenType.PLUS); break;
            case ';': addToken(TokenType.SEMICOLON); break;
            case '*': addToken(TokenType.STAR); break;

            case '!':
                addToken(match('=') ? TokenType.BANG_EQUAL : TokenType.BANG);
                break;
            case '=':
                addToken(match('=') ? TokenType.EQUAL_EQUAL : TokenType.EQUAL);
                break;
            case '<':
                addToken(match('=') ? TokenType.LESS_EQUAL : TokenType.LESS);
                break;
            case '>':
                addToken(match('=') ? TokenType.GREATER_EQUAL : TokenType.GREATER);
                break;

            case '/':
                if (match('/')) { // If it's a comment, consume until we reach end of line
                    while (peek() != '\n' && !isAtEnd()) advance();
                } else {
                    addToken(TokenType.SLASH);
                }
                break;

            case ' ':
            case '\r':
            case '\t':
                // Ignore whitespace
                break;
            
            case '\n':
                line++;
                break;

            // Handling string literals
            case '"': string(); break;



            // So that when the user gives the interpreter an invalid character, we report an error
            default:
                if (isDigit(c)) {
                    number();
                } else if (isAlpha(c)) {
                    // assumes any lexeme starting with a letter or underscore is an identifier
                    identifier();
                } else {
                    Lox.error(line, "Unexpected character.");
                }
                break;
        }
    }

    /**
     * Deals with string literals
     */
    private void string() {
        while (peek() != '"' && !isAtEnd()) {
            if (peek() == '\n') line++;
            advance();
        }

        if (isAtEnd()) {
            Lox.error(line, "Unterminated string");
        }

        advance(); // Advance past the closing "

        // Trim surrounding values
        String value = source.substring(start + 1, current - 1);
        addToken(TokenType.STRING, value);
    }

    /**
     * Deals with number literals
     */
    private void number() {
        while (isDigit(peek())) advance();

        // Look for a fractional part
        if (peek() == '.' && isDigit(peekNext())) {
            // Consume the '.'
            advance();
        }

        while (isDigit(peek())) advance();

        addToken(TokenType.NUMBER,
            Double.parseDouble(source.substring(start, current)));
    }

    /**
     * Does something
     */
    private void identifier() {
        while (isAlphaNumeric(peek())) advance();

        String text = source.substring(start, current);
        TokenType type = keywords.get(text);
        if (type == null) type = TokenType.IDENTIFIER;
        addToken(TokenType.IDENTIFIER);
    }

    /**
     * Like a conditional advance, only consume next character if it's what we're looking for
     * @param expected
     * @return
     */
    private boolean match (char expected) {
        if (isAtEnd()) return false;
        if (source.charAt(current) != expected) return false;

        current++;
        return true;
    }

    /**
     * Like advance, but doesn't consume character
     * Single-character lookahead
     * @return The current character
     */
    private char peek() {
        if (isAtEnd()) return '\0';
        return source.charAt(current);
    }

    /**
     * Two-character lookahead
     * @return The next character
     */
    private char peekNext() {
        if (current + 1 >= source.length()) return '\0';
        return source.charAt(current + 1);
    }

    /**
     * Checks if a character c is a letter or underscore
     * @param c
     * @return True if c is a letter or underscore, False otherwise
     */
    private boolean isAlpha(char c) {
        return (c >= 'a' && c <= 'z') ||
            (c >= 'A' && c <= 'Z') || 
            c == '_';
    }

    /**
     * Checks if a character is alphanumeric
     * @param c
     * @return True if c is alphanumeric, False otherwise
     */
    private boolean isAlphaNumeric(char c) {
        return isAlpha(c) || isDigit(c);
    }

    /**
     * Checks if c is a digit 0-9
     * @param c
     * @return True if c is a digit 0-9, False otherwise
     */
    private boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }

    /**
     * Tells us if we've reached the end of our code
     * @return True if we have
     */
    private boolean isAtEnd() {
        return current >= source.length();
    }

    /**
     * Consumes the next character in the source file and returns it
     * @return the next character in the source file
     */
    private char advance() {
        return source.charAt(current++);
    }

    /**
     * Adds a token to our list of tokens with no literal
     * Grabs the text of the current lexeme and creates a new token
     * @param type
     */
    private void addToken(TokenType type) {
        addToken(type, null);
    }

    /**
     * Adds a token to our list of tokens
     * @param type
     * @param literal
     */
    private void addToken(TokenType type, Object literal) {
        String text = source.substring(start, current);
        tokens.add(new Token(type, text, literal, line));
    }
}