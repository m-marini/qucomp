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
     * Creates the tokenizer
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
        return currentLine.charAt(currentPos);
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
     * Pops the current character
     */
    private Tokenizer popChar() {
        if (!eof()) {
            if (currentLine != null) {
                currentPos++;
                if (currentPos >= currentLine.length()) {
                    if (readLine.hasNext()) {
                        currentLine = readLine.next();
                        currentPos = 0;
                        lineNumber++;
                    } else {
                        currentLine = null;
                    }
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
        skipBlanks();
        if (!eof()) {
            tokenLineNumber = lineNumber;
            tokenPos = currentPos;
            tokenLine = currentLine;
            char ch = currentChar();
            popChar();
            StringBuilder bfr = new StringBuilder();
            bfr.append(ch);
            if (Character.isAlphabetic(ch)) {
                // Read the identifier
                while (!eof() && Character.isLetterOrDigit(currentChar())) {
                    bfr.append(currentChar());
                    popChar();
                }
            } else if (Character.isDigit(ch)) {
                // Read the number
                while (!eof() && Character.isDigit(currentChar())) {
                    bfr.append(currentChar());
                    popChar();
                }
            }
            currentToken = bfr.toString();
        }
        return this;
    }

    /**
     * Skips all blank characters
     */
    private Tokenizer skipBlanks() {
        while (!eof() && Character.isSpaceChar(currentChar())) {
            popChar();
        }
        return this;
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
