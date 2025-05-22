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
import org.mmarini.qucomp.apis.Ket;

import static java.util.Objects.requireNonNull;

/**
 *
 */
public interface Command {
    /**
     * Returns the source context
     */
    SourceContext sourceContext();

    abstract class AbstractCommand implements Command {
        private final SourceContext sourceContext;

        /**
         * Creates the abstract command
         *
         * @param sourceContext the source context
         */
        protected AbstractCommand(SourceContext sourceContext) {
            this.sourceContext = requireNonNull(sourceContext);
        }

        @Override
        public SourceContext sourceContext() {
            return sourceContext;
        }
    }

    class PushInt extends AbstractCommand {
        private final int value;

        /**
         * Creates the abstract command
         *
         * @param sourceContext the source context
         * @param value         the value to push
         */
        public PushInt(SourceContext sourceContext, int value) {
            super(sourceContext);
            this.value = value;
        }

        /**
         * Returns the value
         */
        public int value() {
            return value;
        }
    }

    class PushComplex extends AbstractCommand {
        private final Complex value;

        /**
         * Creates the abstract command
         *
         * @param sourceContext the source context
         * @param value         the value to push
         */
        public PushComplex(SourceContext sourceContext, Complex value) {
            super(sourceContext);
            this.value = requireNonNull(value);
        }

        /**
         * Returns the value
         */
        public Complex value() {
            return value;
        }
    }

    class PushKet extends AbstractCommand {
        private final Ket value;

        /**
         * Creates the abstract command
         *
         * @param sourceContext the source context
         * @param value         the value to push
         */
        public PushKet(SourceContext sourceContext, Ket value) {
            super(sourceContext);
            this.value = requireNonNull(value);
        }

        /**
         * Returns the value
         */
        public Ket value() {
            return value;
        }
    }

    class CallFunction extends AbstractCommand {
        private final String value;

        /**
         * Creates the abstract command
         *
         * @param sourceContext the source context
         * @param value         the value to push
         */
        public CallFunction(SourceContext sourceContext, String value) {
            super(sourceContext);
            this.value = requireNonNull(value);
        }

        /**
         * Returns the value
         */
        public String value() {
            return value;
        }

    }

    class PushString extends AbstractCommand {
        private final String value;

        /**
         * Creates the abstract command
         *
         * @param sourceContext the source context
         * @param value         the value to push
         */
        public PushString(SourceContext sourceContext, String value) {
            super(sourceContext);
            this.value = requireNonNull(value);
        }

        /**
         * Returns the value
         */
        public String value() {
            return value;
        }
    }

    class Add extends AbstractCommand {

        /**
         * Creates the abstract command
         *
         * @param sourceContext the source context
         */
        public Add(SourceContext sourceContext) {
            super(sourceContext);
        }
    }

    class Sub extends AbstractCommand {

        /**
         * Creates the abstract command
         *
         * @param sourceContext the source context
         */
        public Sub(SourceContext sourceContext) {
            super(sourceContext);
        }
    }

    class Multiply extends AbstractCommand {

        /**
         * Creates the abstract command
         *
         * @param sourceContext the source context
         */
        public Multiply(SourceContext sourceContext) {
            super(sourceContext);
        }
    }

    class Divide extends AbstractCommand {

        /**
         * Creates the abstract command
         *
         * @param sourceContext the source context
         */
        public Divide(SourceContext sourceContext) {
            super(sourceContext);
        }
    }

    class Cross extends AbstractCommand {

        /**
         * Creates the abstract command
         *
         * @param sourceContext the source context
         */
        public Cross(SourceContext sourceContext) {
            super(sourceContext);
        }
    }

    class Assign extends AbstractCommand {

        /**
         * Creates the abstract command
         *
         * @param sourceContext the source context
         */
        public Assign(SourceContext sourceContext) {
            super(sourceContext);
        }
    }

    class Conj extends AbstractCommand {

        /**
         * Creates the abstract command
         *
         * @param sourceContext the source context
         */
        public Conj(SourceContext sourceContext) {
            super(sourceContext);
        }
    }

    class RetrieveVar extends AbstractCommand {

        /**
         * Creates the abstract command
         *
         * @param sourceContext the source context
         */
        public RetrieveVar(SourceContext sourceContext) {
            super(sourceContext);
        }
    }

    class Negate extends AbstractCommand {

        /**
         * Creates the abstract command
         *
         * @param sourceContext the source context
         */
        public Negate(SourceContext sourceContext) {
            super(sourceContext);
        }
    }

    class Consume extends AbstractCommand {

        /**
         * Creates the consume command
         *
         * @param sourceContext the source context
         */
        public Consume(SourceContext sourceContext) {
            super(sourceContext);
        }
    }

    class Clear extends AbstractCommand {

        /**
         * Creates the clear command
         *
         * @param sourceContext the source context
         */
        public Clear(SourceContext sourceContext) {
            super(sourceContext);
        }
    }
}