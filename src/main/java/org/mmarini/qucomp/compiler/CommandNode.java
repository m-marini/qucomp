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

import org.mmarini.Tuple2;
import org.mmarini.qucomp.apis.Complex;
import org.mmarini.qucomp.apis.Matrix;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.requireNonNull;

/**
 * Identifies the command to be executed in the processor.
 * The composite commands are:
 * <ul>
 * <li>
 *     unary node processes the result of a command e.g.: negation operation
 * </li>
 * <li>
 *     binary node processes the result of two commands e.g.: add two operands
 * </li>
 * <li>
 *     composite node processes the result of a list of commands e.g.: arguments of function call
 * </li>
 *
 * </ul>
 */
public interface CommandNode {
    /**
     * Returns the command to add two operands
     *
     * @param token the token referencing the source location of command
     * @param left  the left operand
     * @param right the right operand
     */
    static Add add(Token token, CommandNode left, CommandNode right) {
        return new Add(token.context(), left, right);
    }

    /**
     * Returns the command to assign a value to a variable
     *
     * @param token the token referencing the source location of command
     * @param id    the variable identifier
     * @param arg   the value to assign
     */
    static Assign assign(Token token, String id, CommandNode arg) {
        return new Assign(token.context(), id, arg);
    }

    /**
     * Returns the command to execute a list of command
     *
     * @param token the token referencing the source location of command
     */
    static CommandList commandList(Token token) {
        return new CommandList(token.context());
    }

    /**
     * Returns the command to compute the conjugate of the argument command
     *
     * @param token the token referencing the source location of command
     * @param arg   the command argument
     */
    static Dagger dagger(Token token, CommandNode arg) {
        return new Dagger(token.context(), arg);
    }

    /**
     * Returns the command to cross two operands
     *
     * @param token the token referencing the source of add command
     * @param left  the left operand
     * @param right the right operand
     */
    static Cross cross(Token token, CommandNode left, CommandNode right) {
        return new Cross(token.context(), left, right);
    }

    /**
     * Returns the command to divide two operands
     *
     * @param token the token referencing the source of add command
     * @param left  the left operand
     * @param right the right operand
     */
    static Div div(Token token, CommandNode left, CommandNode right) {
        return new Div(token.context(), left, right);
    }

    /**
     * Returns the command to call a function
     *
     * @param token the token referencing the source of add command
     * @param id    the function identifier
     * @param arg   the command list of arguments
     */
    static CallFunction function(Token token, String id, CompositeNode arg) {
        return new CallFunction(token.context(), id, arg);
    }

    /***
     * Returns the command to convert the integer state value to Ket
     *
     * @param token the token referencing the source of add command
     * @param intState the command of integer state
     */
    static IntToKet intToState(Token token, CommandNode intState) {
        return new IntToKet(token.context(), intState);
    }

    /**
     * Returns the command to multiply two operands
     *
     * @param token the token referencing the source of add command
     * @param left  the left operand
     * @param right the right operand
     */
    static Mul mul(Token token, CommandNode left, CommandNode right) {
        return new Mul(token.context(), left, right);
    }

    /**
     * Returns the command to negate the argument
     *
     * @param token the token referencing the source of add command
     * @param arg   the argument command
     */
    static Negate negate(Token token, CommandNode arg) {
        return new Negate(token.context(), arg);
    }

    /**
     * Returns the command to retrieve the variable value referenced by token
     *
     * @param token the token referencing the source of add command
     */
    static RetrieveVar retrieveVar(Token token) {
        return new RetrieveVar(token.context(), token.token());
    }

    /**
     * Returns the command to subtract two operands
     *
     * @param token the token referencing the source of command
     * @param left  the left operand
     * @param right the right operand
     */
    static Sub sub(Token token, CommandNode left, CommandNode right) {
        return new Sub(token.context(), left, right);
    }

    /**
     * Returns the command to return the constant complex value
     *
     * @param token the token referencing the source of command
     * @param value the value
     */
    static Value value(Token token, Complex value) {
        return new Value(token.context(), value);
    }


    /**
     * Returns the command to return the constant Ket value
     *
     * @param token the token referencing the source of command
     * @param value the value
     */
    static Value value(Token token, Matrix value) {
        return new Value(token.context(), value);
    }


    /**
     * Returns the command to return the constant token value
     *
     * @param token the token referencing the source of command
     */
    static Value value(Token token) {
        return new Value(token.context(), switch (token) {
            case Token.IntegerToken tok -> tok.value();
            case Token.RealToken tok -> Complex.create(tok.value());
            default -> token.token();
        });
    }

    /**
     * Returns the command source context
     */
    SourceContext context();

    /**
     * Returns the result of execution command
     *
     * @param context the execution context
     */
    Object evaluate(ExecutionContext context) throws QuExecException;

    /**
     * Single argument command node
     */
    interface UnaryNode extends CommandNode {
        /**
         * Returns the command argument
         */
        CommandNode arg();
    }

    /**
     * Interface of binary arguments command node
     */
    interface BinaryNode extends CommandNode {
        /**
         * Returns the left argument command
         */
        CommandNode left();

        /**
         * Returns the left argument command
         */
        CommandNode right();
    }

    /**
     * Interface of commands with a list of argument commands
     */
    interface CompositeNode extends CommandNode {
        /**
         * Returns the command with an added argument command
         *
         * @param command the command to add
         */
        CompositeNode add(CommandNode command);

        /**
         * Returns the list of arguments
         */
        List<CommandNode> commands();
    }

    /**
     * Commands to add two arguments
     *
     * @param context the command source reference
     * @param left    the left argument
     * @param right   the right argument
     */
    record Add(SourceContext context, CommandNode left, CommandNode right) implements BinaryNode {

        @Override
        public Object evaluate(ExecutionContext context) throws QuExecException {
            return context.add(this.context, left.evaluate(context), right.evaluate(context));
        }
    }

    /**
     * Commands to subtract two arguments
     *
     * @param context the command source reference
     * @param left    the left argument
     * @param right   the right argument
     */
    record Sub(SourceContext context, CommandNode left, CommandNode right) implements BinaryNode {

        @Override
        public Object evaluate(ExecutionContext context) throws QuExecException {
            return context.sub(this.context, left.evaluate(context), right.evaluate(context));
        }
    }

    /**
     * Commands to multiply two arguments
     *
     * @param context the command source reference
     * @param left    the left argument
     * @param right   the right argument
     */
    record Mul(SourceContext context, CommandNode left, CommandNode right) implements BinaryNode {

        @Override
        public Object evaluate(ExecutionContext context) throws QuExecException {
            return context.mul(this.context, left.evaluate(context), right.evaluate(context));
        }
    }

    /**
     * Commands to divide two arguments
     *
     * @param context the command source reference
     * @param left    the left argument
     * @param right   the right argument
     */
    record Div(SourceContext context, CommandNode left, CommandNode right) implements BinaryNode {

        @Override
        public Object evaluate(ExecutionContext context) throws QuExecException {
            return context.div(this.context, left.evaluate(context), right.evaluate(context));
        }
    }

    /**
     * Commands to cross multiply two arguments
     *
     * @param context the command source reference
     * @param left    the left argument
     * @param right   the right argument
     */
    record Cross(SourceContext context, CommandNode left, CommandNode right) implements BinaryNode {

        @Override
        public Object evaluate(ExecutionContext context) throws QuExecException {
            return context.cross(this.context, left.evaluate(context), right.evaluate(context));
        }
    }


    /**
     * Commands to clear all variables
     *
     * @param context the command source reference
     */
    record Clear(SourceContext context) implements CommandNode {

        @Override
        public Object evaluate(ExecutionContext context) throws QuExecException {
            return context.clear(this.context);
        }
    }

    /**
     * Commands to return a constant value
     *
     * @param context the command source reference
     * @param value   the value maybe Integer, String, Complex, Ket, Bra
     */
    record Value(SourceContext context, Object value) implements CommandNode {

        @Override
        public Object evaluate(ExecutionContext context) {
            return value;
        }
    }

    /**
     * Commands with multiple arguments
     */
    class CommandList implements CompositeNode {
        private final List<CommandNode> commands;
        private final SourceContext context;

        /**
         * Creates the command
         *
         * @param context the context
         */
        public CommandList(SourceContext context) {
            this.context = requireNonNull(context);
            this.commands = new ArrayList<>();
        }

        @Override
        public CompositeNode add(CommandNode command) {
            commands.add(command);
            return this;
        }

        @Override
        public List<CommandNode> commands() {
            return commands;
        }

        @Override
        public SourceContext context() {
            return context;
        }

        @Override
        public Object evaluate(ExecutionContext context) throws QuExecException {
            Object[] values = new Object[commands.size()];
            for (int i = 0; i < commands.size(); i++) {
                values[i] = commands.get(i).evaluate(context);
            }
            return values;
        }
    }

    /**
     * Commands to assign a variable value
     *
     * @param context the command source reference
     * @param id      the variable identifier
     * @param arg     the value
     */
    record Assign(SourceContext context, String id, CommandNode arg) implements UnaryNode {

        @Override
        public Object evaluate(ExecutionContext context) throws QuExecException {
            return context.assign(this.context, id, arg.evaluate(context));
        }
    }

    /**
     * Commands to retrieve variable value
     *
     * @param context the command source reference
     * @param id      the variable identifier
     */
    record RetrieveVar(SourceContext context, String id) implements CommandNode {

        @Override
        public Object evaluate(ExecutionContext context) throws QuExecException {
            return context.retrieveVar(this.context, id);
        }
    }

    /**
     * Commands to convert an integer value to Ket
     *
     * @param context the command source reference
     * @param arg     the integer argument
     */
    record IntToKet(SourceContext context, CommandNode arg) implements UnaryNode {

        @Override
        public Object evaluate(ExecutionContext context) throws QuExecException {
            return context.intToKet(this.context, arg.evaluate(context));
        }
    }

    /**
     * Commands to compute the conjugate
     *
     * @param context the command source reference
     * @param arg     the integer argument
     */
    record Dagger(SourceContext context, CommandNode arg) implements UnaryNode {

        @Override
        public Object evaluate(ExecutionContext context) throws QuExecException {
            return context.dagger(this.context, arg.evaluate(context));
        }
    }

    /**
     * Commands to compute the negation
     *
     * @param context the command source reference
     * @param arg     the integer argument
     */
    record Negate(SourceContext context, CommandNode arg) implements UnaryNode {

        @Override
        public Object evaluate(ExecutionContext context) throws QuExecException {
            return context.negate(this.context, arg.evaluate(context));
        }
    }

    /**
     * Commands to call a function
     *
     * @param context the command source reference
     * @param id      the function identifier
     * @param arg     the composite command with function arguments
     */
    record CallFunction(SourceContext context, String id, CommandNode.CompositeNode arg) implements UnaryNode {

        @Override
        public Object evaluate(ExecutionContext context) throws QuExecException {
            List<CommandNode> commands = arg.commands();
            Tuple2<Object, SourceContext>[] args = new Tuple2[commands.size()];
            for (int i = 0; i < commands.size(); i++) {
                CommandNode argCommand = commands.get(i);
                args[i] = Tuple2.of(argCommand.evaluate(context), argCommand.context());
            }
            return context.function(this.context, id, args);
        }
    }
}
