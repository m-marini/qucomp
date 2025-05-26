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
 * Keeps the context and the token result of Tokenizer
 */
public interface Token {
    /**
     * Returns the source context
     */
    SourceContext context();

    /**
     * Returns the token value
     */
    default String token() {
        return context().token();
    }

    /**
     * The eof token
     *
     * @param context the source context
     */
    record EOFToken(SourceContext context) implements Token {
        /**
         * Creates the integer token
         *
         * @param context the source context
         */
        public EOFToken(SourceContext context) {
            this.context = requireNonNull(context);
        }

        @Override
        public String toString() {
            return context().token();
        }
    }

    /**
     * Keeps the identifier token
     *
     * @param context the source context
     */
    record IdentifierToken(SourceContext context) implements Token {
        /**
         * Creates the identifier token
         *
         * @param context the source context
         */
        public IdentifierToken(SourceContext context) {
            this.context = requireNonNull(context);
        }

        @Override
        public String toString() {
            return context().token();
        }
    }

    /**
     * Keeps the operator token
     *
     * @param context the source context
     */
    record OperatorToken(SourceContext context) implements Token {
        /**
         * Creates the operator token
         *
         * @param context the source context
         */
        public OperatorToken(SourceContext context) {
            this.context = requireNonNull(context);
        }

        @Override
        public String toString() {
            return context().token();
        }
    }

    /**
     * Keeps the integer token
     *
     * @param context the source context
     */
    record IntegerToken(SourceContext context) implements Token {
        /**
         * Creates the integer token
         *
         * @param context the source context
         */
        public IntegerToken(SourceContext context) {
            this.context = requireNonNull(context);
        }

        @Override
        public String toString() {
            return context().token();
        }

        /**
         * Returns the value
         */
        public int value() {
            return Integer.parseInt(token());
        }
    }

    /**
     * Keeps the real token
     *
     * @param context
     */
    record RealToken(SourceContext context) implements Token {
        /**
         * Creates the integer token
         *
         * @param context the source context
         */
        public RealToken(SourceContext context) {
            this.context = requireNonNull(context);
        }

        @Override
        public String toString() {
            return context().token();
        }

        /**
         * Returns the value
         */
        public float value() {
            return Float.parseFloat(token());
        }
    }
}
