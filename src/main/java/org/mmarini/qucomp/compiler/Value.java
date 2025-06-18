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

import org.mmarini.qucomp.apis.Complex;
import org.mmarini.qucomp.apis.Matrix;

import java.util.Arrays;

import static java.util.Objects.requireNonNull;

/**
 * Stores the processing value
 */
public interface Value {
    /**
     * Returns the source context
     */
    SourceContext source();

    Value source(SourceContext context);

    /**
     * Returns the value type id
     */
    String type();

    abstract class AbstractValue implements Value {
        private final SourceContext source;

        /**
         * Creates the value
         *
         * @param source the source context
         */
        protected AbstractValue(SourceContext source) {
            this.source = requireNonNull(source);
        }

        @Override
        public SourceContext source() {
            return source;
        }
    }

    /**
     * The integer value
     */
    class IntValue extends AbstractValue {
        private final int value;

        /**
         * Creates the value
         *
         * @param source the source context
         */
        protected IntValue(SourceContext source, int value) {
            super(source);
            this.value = value;
        }

        @Override
        public IntValue source(SourceContext context) {
            return new IntValue(context, value);
        }

        @Override
        public String toString() {
            return String.valueOf(value);
        }

        @Override
        public String type() {
            return "integer";
        }

        /**
         * Returns the int value
         */
        public int value() {
            return value;
        }
    }

    /**
     * The complex value
     */
    class ComplexValue extends AbstractValue {
        private final Complex value;

        /**
         * Creates the value
         *
         * @param source the source context
         */
        protected ComplexValue(SourceContext source, Complex value) {
            super(source);
            this.value = value;
        }

        @Override
        public ComplexValue source(SourceContext context) {
            return new ComplexValue(context, value);
        }

        @Override
        public String toString() {
            return String.valueOf(value);
        }

        @Override
        public String type() {
            return "complex";
        }

        /**
         * Returns the int value
         */
        public Complex value() {
            return value;
        }
    }

    /**
     * The integer value
     */
    class MatrixValue extends AbstractValue {
        private final Matrix value;

        /**
         * Creates the value
         *
         * @param source the source context
         */
        protected MatrixValue(SourceContext source, Matrix value) {
            super(source);
            this.value = value;
        }

        @Override
        public MatrixValue source(SourceContext context) {
            return new MatrixValue(context, value);
        }

        @Override
        public String toString() {
            return String.valueOf(value);
        }

        @Override
        public String type() {
            return "matrix";
        }

        /**
         * Returns the int value
         */
        public Matrix value() {
            return value;
        }
    }

    /**
     * The integer value
     */
    class ListValue extends AbstractValue {
        private final Value[] value;

        /**
         * Creates the value
         *
         * @param source the source context
         */
        protected ListValue(SourceContext source, Value... value) {
            super(source);
            this.value = value;
        }

        @Override
        public ListValue source(SourceContext context) {
            return new ListValue(context, value);
        }

        @Override
        public String toString() {
            return Arrays.toString(value);
        }

        @Override
        public String type() {
            return "list";
        }

        /**
         * Returns the int value
         */
        public Value[] value() {
            return value;
        }
    }
}
