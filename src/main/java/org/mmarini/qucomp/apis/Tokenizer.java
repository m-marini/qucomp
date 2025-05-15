/*
 * Copyright (c) 2025 Marco Marini, marco.marini@mmarini.org
 *
 *  Permission is hereby granted, free of charge, to any person
 * obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without
 * restriction, including without limitation the rights to use,
 * copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following
 * conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 *
 *    END OF TERMS AND CONDITIONS
 *
 */

package org.mmarini.qucomp.apis;

import java.util.Iterator;

import static java.util.Objects.requireNonNull;

/**
 * Tokenizes a stream of lines
 */
public class Tokenizer {
    private final Iterator<String> readLine;
    public String currentToken;
    public String tokenLine;
    private int tokenLineNumber;
    private int tokenPos;
    private String currentLine;
    private int currentPos;
    private int lineNumber;

    /**
     * Creates the tokeniser
     *
     * @param lines the iterator
     */
    protected Tokenizer(Iterator<String> lines) {
        this.readLine = requireNonNull(lines);
        this.currentLine = "";
        popChar().popToken();
    }

    /**
     * Returns current char
     */
    char currentChar() {
        return currentLine != null ? currentLine.charAt(currentPos) : 0;
    }

    /**
     * Returns the current token
     */
    public String currentToken() {
        return currentToken != null
                ? currentToken : "";
    }

    /**
     * Returns true if eof has reached
     */
    public boolean eof() {
        return currentLine == null && currentToken == null;
    }

    /**
     * Parse the identifier
     */
    private void parseIdentifier() {
        StringBuilder bfr = new StringBuilder();
        do {
            bfr.append(currentChar());
            popChar();
        } while (!eof() && Character.isLetterOrDigit(currentChar()));
        currentToken = bfr.toString();
    }

    /**
     * Parse the number
     */
    private void parseNumber() {
        StringBuilder bfr = new StringBuilder();
        do {
            bfr.append(currentChar());
            popChar();
        } while (!eof() && Character.isDigit(currentChar()));
        currentToken = bfr.toString();
    }

    /**
     * Parse slash character looking for comment
     */
    private void parseSlash() {
        popChar();
        if (eof()) {
            currentToken = "/";
        } else {
            char ch = currentChar();
            if (ch == '/') {
                readNextLine();
            } else if (ch == '*') {
                skipComment();
            } else {
                currentToken = "/";
            }
        }
    }

    /**
     * Pops the current character
     */
    private Tokenizer popChar() {
        if (!eof()) {
            if (currentLine != null) {
                currentPos++;
                if (currentPos >= currentLine.length()) {
                    readNextLine();
                }
            }
        }
        return this;
    }

    /**
     * Pops the current token
     */
    public Tokenizer popToken() {
        currentToken = null;
        while (currentToken == null && !eof()) {
            tokenLineNumber = lineNumber;
            tokenPos = currentPos;
            tokenLine = currentLine.substring(0, currentLine.length() - 1);
            char ch = currentChar();
            if (Character.isDigit(ch)) {
                parseNumber();
            } else if (Character.isAlphabetic(ch)) {
                parseIdentifier();
            } else if (ch == '/') {
                parseSlash();
            } else if (Character.isWhitespace(ch)) {
                skipBlanks();
            } else {
                currentToken = String.valueOf(ch);
                popChar();
            }
        }
        return this;
    }

    /**
     * Read the next line
     */
    private void readNextLine() {
        if (readLine.hasNext()) {
            currentLine = readLine.next() + "\n";
            currentPos = 0;
            lineNumber++;
        } else {
            currentLine = null;
        }
    }

    /**
     * Skips all blank characters
     */
    private void skipBlanks() {
        while (!eof() && Character.isWhitespace(currentChar())) {
            popChar();
        }
    }

    /**
     * Skip a comment block
     */
    private void skipComment() {
        popChar();
        while (!eof()) {
            char ch = currentChar();
            popChar();
            if (ch == '*' && !eof() && currentChar() == '/') {
                popChar();
                break;
            }
        }
    }

    /**
     * Returns the line containing the token
     */
    public String tokenLine() {
        return tokenLine != null ? tokenLine : "";
    }

    /**
     * Returns the token line number
     */
    public int tokenLineNumber() {
        return tokenLineNumber;
    }

    /**
     * /**
     * Returns the current character position
     */
    public int tokenPos() {
        return tokenPos;
    }
}
