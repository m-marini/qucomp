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

import static java.util.Objects.requireNonNull;

/**
 * Locates the source context
 *
 * @param token      the token
 * @param line       the source line
 * @param lineNumber the line number
 * @param position   the token position
 */
public record SourceContext(String token, String line, int lineNumber, int position) {

    private static final Logger logger = LoggerFactory.getLogger(SourceContext.class);

    /**
     * Creates the source context
     *
     * @param token      the token
     * @param line       the source line
     * @param lineNumber the line number
     * @param position   the token position
     */
    public SourceContext(String token, String line, int lineNumber, int position) {
        this.token = requireNonNull(token);
        this.line = requireNonNull(line);
        this.lineNumber = lineNumber;
        this.position = position;
    }

    /**
     * Returns the parser exception
     *
     * @param pattern the pattern
     * @param args    the argument
     */
    public QuExecException execException(String pattern, Object... args) {
        String msg = pattern.formatted(args);
        for (String m : fullReportMessage(msg)) {
            logger.atError().log("{}", m);
        }
        return new QuExecException(msg, this);
    }

    /**
     * Return the full report message
     *
     * @param message the message
     */
    public String[] fullReportMessage(String message) {
        String[] result = new String[2];
        StringBuilder bfr = new StringBuilder();
        String lineNum = String.valueOf(lineNumber);
        result[0] = bfr.append(lineNum).append(":").append(line).toString();
        bfr.setLength(0);
        result[1] = bfr.repeat(" ", lineNum.length())
                .append(":")
                .repeat("-", position)
                .append("^ ")
                .append(reportMessage(message))
                .toString();
        return result;
    }

    /**
     * Returns the parser exception
     *
     * @param pattern the pattern
     * @param args    the argument
     */
    public QuParseException parseException(String pattern, Object... args) {
        String msg = pattern.formatted(args);
        for (String m : fullReportMessage(msg)) {
            logger.atError().log("{}", m);
        }
        return new QuParseException(msg, this);
    }

    /**
     * Returns the report message
     *
     * @param message the message
     */
    public String reportMessage(String message) {
        return message + " token(\"" + token + "\")";
    }

    @Override
    public String toString() {
        return token;
    }
}
