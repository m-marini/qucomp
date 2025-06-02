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

package org.mmarini.qucomp.compiler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

import static java.util.Objects.requireNonNull;

public class Tokenizer {
    private static final Logger logger = LoggerFactory.getLogger(Tokenizer.class);

    /**
     * Returns the tokenizer of a text
     *
     * @param text the text
     */
    public static Tokenizer create(String text) {
        return create(new StringReader(text));
    }

    /**
     * Returns the tokenizer of a file
     *
     * @param file the file
     */
    public static Tokenizer create(File file) throws FileNotFoundException {
        return create(new FileReader(file));
    }

    /**
     * Returns the tokenizer of a file
     *
     * @param reader the reader
     */
    public static Tokenizer create(Reader reader) {
        return new Tokenizer(new BufferedReader(reader));
    }

    public BufferedReader reader;
    public Token currentToken;
    public String tokenLine;
    private int tokenLineNumber;
    private int tokenPos;
    private String currentLine;
    private int currentPos;
    private int lineNumber;

    /**
     * Creates the tokeniser
     *
     * @param reader the reader
     */
    protected Tokenizer(BufferedReader reader) {
        this.reader = requireNonNull(reader);
        this.currentLine = "";
        logger.atDebug().log("Tokenizer created.");
    }

    /**
     * Returns the source context of the curent character
     */
    private SourceContext createCharContext() {
        char ch = currentChar();

        String chStr = switch (ch) {
            case 0 -> "";
            case '\n' -> "\\n";
            case '\r' -> "\\r";
            case '\t' -> "\\t";
            case 1, 2, 3, 4, 5, 6, 7, 8, 11, 12, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30,
                 31 -> "\\0x" + Integer.toString(ch, 16);
            default -> String.valueOf(ch);
        };
        return new SourceContext(chStr, currentLine, lineNumber, currentPos);
    }

    /**
     * Returns the source context of the token
     *
     * @param token the token
     */
    private SourceContext createTokenContext(String token) {
        return new SourceContext(token,
                tokenLine != null ? tokenLine.substring(0, tokenLine.length() - 1) : "",
                tokenLineNumber, tokenPos);
    }

    /**
     * Returns current char
     */
    private char currentChar() {
        return currentLine != null ? currentLine.charAt(currentPos) : 0;
    }

    /**
     * Returns the current token
     */
    public Token currentToken() {
        return currentToken;
    }

    /**
     * Returns true if eof has reached
     */
    private boolean eof() {
        return currentLine == null && currentToken == null;
    }

    /**
     * Opens the Tokenizer
     */
    public Tokenizer open() throws IOException {
        return popChar().popToken();
    }

    /**
     * Parse exponent
     *
     * @param bfr the buffer
     */
    private void parseExponent(StringBuilder bfr) throws IOException {
        bfr.append(currentChar());
        popChar();
        if (eof()) {
            throw createCharContext().parseException("Missing exponent");
        }
        char ch = currentChar();
        if (ch == '+' || ch == '-') {
            bfr.append(ch);
            popChar();
        }
        if (eof()) {
            throw createCharContext().parseException("Missing exponent");
        }
        ch = currentChar();
        if (!Character.isDigit(ch)) {
            throw createCharContext().parseException("Missing exponent");
        }
        do {
            bfr.append(currentChar());
            popChar();
        } while (!eof() && Character.isDigit(currentChar()));
        currentToken = new Token.RealToken(createTokenContext(bfr.toString()));
    }

    /**
     * Parse fractional number
     *
     * @param bfr the buffer
     */
    private void parseFract(StringBuilder bfr) throws IOException {
        do {
            bfr.append(currentChar());
            popChar();
        } while (!eof() && Character.isDigit(currentChar()));
        if (eof()) {
            currentToken = new Token.RealToken(createTokenContext(bfr.toString()));
        } else {
            char ch = currentChar();
            if (ch == 'E' || ch == 'e') {
                parseExponent(bfr);
            } else {
                currentToken = new Token.RealToken(createTokenContext(bfr.toString()));
            }
        }
    }

    /**
     * Parse the identifier
     */
    private void parseIdentifier() throws IOException {
        StringBuilder bfr = new StringBuilder();
        do {
            bfr.append(currentChar());
            popChar();
        } while (!eof() && Character.isLetterOrDigit(currentChar()));
        currentToken = new Token.IdentifierToken(createTokenContext(bfr.toString()));
    }

    /**
     * Parse the number
     */
    private void parseNumber() throws IOException {
        StringBuilder bfr = new StringBuilder();
        do {
            bfr.append(currentChar());
            popChar();
        } while (!eof() && Character.isDigit(currentChar()));
        if (eof()) {
            currentToken = new Token.IntegerToken(createTokenContext(bfr.toString()));
        } else {
            char ch = currentChar();
            if (ch == '.') {
                parseFract(bfr);
            } else if (ch == 'E' || ch == 'e') {
                parseExponent(bfr);
            } else {
                currentToken = new Token.IntegerToken(createTokenContext(bfr.toString()));
            }
        }
    }

    /**
     * Parse slash character looking for comment
     */
    private void parseSlash() throws IOException {
        popChar();
        if (eof()) {
            currentToken = new Token.OperatorToken(createTokenContext("/"));
        } else {
            char ch = currentChar();
            if (ch == '/') {
                readNextLine();
            } else if (ch == '*') {
                skipComment();
            } else {
                currentToken = new Token.OperatorToken(createTokenContext("/"));
            }
        }
    }

    /**
     * Pops the current character
     */
    private Tokenizer popChar() throws IOException {
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
    public Tokenizer popToken() throws IOException {
        if (!(currentToken instanceof Token.EOFToken)) {
            currentToken = null;
            while (currentToken == null) {
                if (eof()) {
                    currentToken = new Token.EOFToken(createTokenContext(""));
                    break;
                }
                tokenLineNumber = lineNumber;
                tokenPos = currentPos;
                tokenLine = currentLine;
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
                    currentToken = new Token.OperatorToken(createTokenContext(String.valueOf(ch)));
                    popChar();
                }
            }
        }
        return this;
    }

    /**
     * Read the next line
     */
    private void readNextLine() throws IOException {
        currentLine = reader.readLine();
        if (currentLine != null) {
            currentLine += "\n";
            currentPos = 0;
            lineNumber++;
        }
    }

    /**
     * Skips all blank characters
     */
    private void skipBlanks() throws IOException {
        while (!eof() && Character.isWhitespace(currentChar())) {
            popChar();
        }
    }

    /**
     * Skip a comment block
     */
    private void skipComment() throws IOException {
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
}
