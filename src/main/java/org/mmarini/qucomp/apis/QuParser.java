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

import org.mmarini.Function3;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.IntFunction;

import static java.util.Objects.requireNonNull;

/**
 * Parses a quantum source code for list of gates
 */
public class QuParser {
    private static final Map<String, IntFunction<QuGate>> unaryGates = Map.of(
            "i", QuGate::i,
            "h", QuGate::h,
            "x", QuGate::x,
            "y", QuGate::y,
            "z", QuGate::z,
            "s", QuGate::s,
            "t", QuGate::t
    );
    private static final Map<String, BiFunction<Integer, Integer, QuGate>> binaryGates = Map.of(
            "swap", QuGate::swap,
            "cnot", QuGate::cnot
    );
    private static final Map<String, Function3<Integer, Integer, Integer, QuGate>> trinaryGates = Map.of(
            "ccnot", QuGate::ccnot
    );
    private static final Logger logger = LoggerFactory.getLogger(QuParser.class);

    /**
     * Returns the parser from the buffered reader
     *
     * @param is the buffered reader
     */
    public static QuParser create(Reader is) {
        return create(new BufferedReader(is));
    }

    /**
     * Returns the parser from the buffered reader
     *
     * @param is the buffered reader
     */
    public static QuParser create(BufferedReader is) {
        return new QuParser(new Tokenizer(is.lines().iterator()));
    }

    /**
     * Returns the parser from the file
     *
     * @param file the file
     */
    public static QuParser create(File file) throws FileNotFoundException {
        return create(new FileReader(file));
    }

    /**
     * Returns the parser from the text
     *
     * @param text the text
     */
    public static QuParser create(String text) {
        return create(new StringReader(text));
    }

    private final Tokenizer tokenizer;
    private final List<String> fullErrorMessages;

    /**
     * Creates the parser
     *
     * @param tokenizer the tokenizer
     */
    protected QuParser(Tokenizer tokenizer) {
        this.tokenizer = requireNonNull(tokenizer);
        fullErrorMessages = new ArrayList<>();
    }

    /**
     * Returns the full error messages
     */
    public List<String> fullErrorMessages() {
        return fullErrorMessages;
    }

    /**
     * Returns the list of gate parsing source
     */
    public List<QuGate> parse() {
        List<QuGate> result = new ArrayList<>();
        QuGate gate = parseOptGate();
        if (gate == null) {
            reportError("Expected gate");
        }
        do {
            result.add(gate);
            gate = parseOptGate();
        } while (gate != null);
        if (!tokenizer.eof()) {
            reportError("Unexpected token");
        }
        return result;
    }

    /**
     * Returns the required bit index
     */
    int parseBit() {
        String tok = tokenizer.currentToken();
        if (tokenizer.eof() || !Character.isDigit(tok.charAt(0))) {
            reportError("Expected number");
        }
        int port = Integer.parseInt(tok);
        tokenizer.popToken();
        return port;
    }

    /**
     * Returns the optional binary gate
     */
    QuGate parseOptBinaryGate() {
        BiFunction<Integer, Integer, QuGate> builder = parseOptBinaryId();
        if (builder != null) {
            requiredToken("(");
            int bit0 = parseBit();
            requiredToken(",");
            int bit1 = parseBit();
            requiredToken(")");
            return builder.apply(bit0, bit1);
        }
        return null;
    }

    /**
     * Returns the optional binary gate builder
     */
    BiFunction<Integer, Integer, QuGate> parseOptBinaryId() {
        if (!tokenizer.eof()) {
            BiFunction<Integer, Integer, QuGate> result = binaryGates.get(tokenizer.currentToken());
            if (result != null) {
                tokenizer.popToken();
            }
            return result;
        }
        return null;
    }

    /**
     * Returns the optional gate
     */
    QuGate parseOptGate() {
        QuGate result = parseOptUnaryGate();
        if (result == null) {
            result = parseOptBinaryGate();
            if (result == null) {
                result = parseOptTrinaryGate();
            }
        }
        return result;
    }

    /**
     * Returns the optional trinary gate
     */
    QuGate parseOptTrinaryGate() {
        Function3<Integer, Integer, Integer, QuGate> builder = parseOptTrinaryId();
        if (builder != null) {
            requiredToken("(");
            int bit0 = parseBit();
            requiredToken(",");
            int bit1 = parseBit();
            requiredToken(",");
            int bit2 = parseBit();
            requiredToken(")");
            return builder.apply(bit0, bit1, bit2);
        }
        return null;
    }

    /**
     * Returns the optional trinary gate builder
     */
    public Function3<Integer, Integer, Integer, QuGate> parseOptTrinaryId() {
        if (!tokenizer.eof()) {
            Function3<Integer, Integer, Integer, QuGate> result = trinaryGates.get(tokenizer.currentToken());
            if (result != null) {
                tokenizer.popToken();
            }
            return result;
        }
        return null;
    }

    /**
     * Returns the optional unary gate
     */
    QuGate parseOptUnaryGate() {
        IntFunction<QuGate> builder = parseOptUnaryId();
        if (builder != null) {
            requiredToken("(");
            QuGate gate = builder.apply(parseBit());
            requiredToken(")");
            return gate;
        }
        return null;
    }

    /**
     * Returns the gate
     */
    IntFunction<QuGate> parseOptUnaryId() {
        if (!tokenizer.eof()) {
            IntFunction<QuGate> result = unaryGates.get(tokenizer.currentToken());
            if (result != null) {
                tokenizer.popToken();
            }
            return result;
        }
        return null;
    }

    /**
     * Report an error message
     *
     * @param message the message
     */
    private void reportError(String message) {
        String tok = tokenizer.currentToken();
        if (tok.isEmpty()) {
            tok = "<EOF>";
        }
        String fullMessage = message + " (" + tok + ")";
        if (!tokenizer.eof()) {
            String lineNum = String.valueOf(tokenizer.tokenLineNumber());
            fullErrorMessages.add(
                    lineNum + ":" + tokenizer.tokenLine());
            fullErrorMessages.add(
                    new StringBuilder()
                            .repeat(" ", lineNum.length())
                            .append(":")
                            .repeat("-", tokenizer.tokenPos())
                            .append("^ ")
                            .append(fullMessage)
                            .toString());
        } else {
            fullErrorMessages.add(fullMessage);
        }
        fullErrorMessages.forEach(m -> logger.atError().log("{}", m));
        throw new IllegalArgumentException(fullMessage);
    }

    /**
     * Requires the token
     *
     * @param token the token
     */
    private QuParser requiredToken(String token) {
        String tok = tokenizer.currentToken();
        if (tokenizer.eof() || !tok.equals(token)) {
            reportError("Expected \"" + token + "\"");
        }
        tokenizer.popToken();
        return this;
    }
}
