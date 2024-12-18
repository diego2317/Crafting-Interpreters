package jlox;
// package com.craftinginterpreters.lox

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
    }

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

            // So that when the user gives the interpreter an invalid character, we report an error
            default:
            Lox.error(line, "Unexpected character.");
            break;
        }
    }

    /**
     * Tells us if we've reached the end of our code
     * @return true if we have
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
