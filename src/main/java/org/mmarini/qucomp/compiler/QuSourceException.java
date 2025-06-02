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

import static java.util.Objects.requireNonNull;

/**
 * Collects the location of source code related to exceptions
 */
public class QuSourceException extends QuException {
    private final SourceContext context;

    /**
     * Create exception
     *
     * @param message the message
     * @param context the source context
     * @param cause   the cause
     */
    public QuSourceException(String message, SourceContext context, Throwable cause) {
        super(message, cause);
        this.context = context;
    }

    /**
     * Creates the exception
     *
     * @param message the message
     * @param context the context
     */
    public QuSourceException(String message, SourceContext context) {
        super(requireNonNull(message));
        this.context = (requireNonNull(context));
    }

    /**
     * Returns the source context
     */
    public SourceContext context() {
        return context;
    }

    /**
     * Returns the source context
     */
    public String getMessage() {
        return context.reportMessage(super.getMessage());
    }
}
