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

import java.util.List;

import static java.util.Objects.requireNonNull;

public interface CommandNode {
    SourceContext context();

    abstract class AbstractNode implements CommandNode {
        private final SourceContext context;

        protected AbstractNode(SourceContext context) {
            this.context = requireNonNull(context);
        }

        @Override
        public SourceContext context() {
            return context;
        }
    }

    class Value extends AbstractNode {
        private final Object value;

        protected Value(SourceContext context, Object value) {
            super(context);
            this.value = requireNonNull(value);
        }

        public Object value() {
            return value;
        }
    }

    class Clear extends AbstractNode {
        protected Clear(SourceContext context) {
            super(context);
        }
    }

    abstract class UnaryNode extends AbstractNode {
        private final CommandNode arg;

        protected UnaryNode(SourceContext context, CommandNode arg) {
            super(context);
            this.arg = requireNonNull(arg);
        }

        public CommandNode arg() {
            return arg;
        }
    }

    abstract class BinaryNode extends AbstractNode {
        private final CommandNode right;
        private final CommandNode left;

        protected BinaryNode(SourceContext context, CommandNode right, CommandNode left) {
            super(context);
            this.right = requireNonNull(right);
            this.left = requireNonNull(left);
        }

        public CommandNode left() {
            return left;
        }

        public CommandNode right() {
            return right;
        }
    }

    class Add extends BinaryNode {

        protected Add(SourceContext context, CommandNode right, CommandNode left) {
            super(context, right, left);
        }
    }

    class Sub extends BinaryNode {

        protected Sub(SourceContext context, CommandNode right, CommandNode left) {
            super(context, right, left);
        }
    }

    class Mul extends BinaryNode {

        protected Mul(SourceContext context, CommandNode right, CommandNode left) {
            super(context, right, left);
        }
    }

    class Div extends BinaryNode {

        protected Div(SourceContext context, CommandNode right, CommandNode left) {
            super(context, right, left);
        }
    }

    class Cross extends BinaryNode {

        protected Cross(SourceContext context, CommandNode right, CommandNode left) {
            super(context, right, left);
        }
    }

    class Assign extends BinaryNode {

        protected Assign(SourceContext context, CommandNode right, CommandNode left) {
            super(context, right, left);
        }
    }

    class CallFunction extends UnaryNode {
        private final String identifier;

        protected CallFunction(SourceContext context, CommandNode arg, String identifier) {
            super(context, arg);
            this.identifier = requireNonNull(identifier);
        }
    }

    class Conj extends UnaryNode {

        protected Conj(SourceContext context, CommandNode command) {
            super(context, command);
        }
    }

    class Negate extends UnaryNode {

        protected Negate(SourceContext context, CommandNode command) {
            super(context, command);
        }
    }

    class Consume extends UnaryNode {

        protected Consume(SourceContext context, CommandNode command) {
            super(context, command);
        }
    }

    class CodeUnit extends AbstractNode {

        private final List<CommandNode> commands;

        protected CodeUnit(SourceContext context, List<CommandNode> commands) {
            super(context);
            this.commands = requireNonNull(commands);
        }

        public List<CommandNode> commands() {
            return commands;
        }

    }

    class RetrieveVar extends UnaryNode {

        protected RetrieveVar(SourceContext context, CommandNode command) {
            super(context, command);
        }
    }
}
