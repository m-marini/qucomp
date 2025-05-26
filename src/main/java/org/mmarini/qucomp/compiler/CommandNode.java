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

import java.util.List;

public interface CommandNode {
    static CommandNode add(Token token, CommandNode left, CommandNode right) {
        return new Add(token.context(), left, right);
    }

    static CommandNode assign(Token token, String id, CommandNode arg) {
        return new Assign(token.context(), id, arg);
    }

    static CommandNode codeUnit(Token token, List<CommandNode> commands) {
        return new CodeUnit(token.context(), commands);
    }

    static CommandNode conj(Token token, CommandNode arg) {
        return new Conj(token.context(), arg);
    }

    static CommandNode cross(Token token, CommandNode left, CommandNode right) {
        return new Cross(token.context(), left, right);
    }

    static CommandNode div(Token token, CommandNode left, CommandNode right) {
        return new Div(token.context(), left, right);
    }

    static CommandNode function(Token token, CommandNode arg) {
        return new CallFunction(token.context(), token.token(), arg);
    }

    static CommandNode intToState(Token token, CommandNode intState) {
        return new IntToKet(token.context(), intState);
    }

    static CommandNode mul(Token token, CommandNode left, CommandNode right) {
        return new Mul(token.context(), left, right);
    }

    static CommandNode negate(Token token, CommandNode arg) {
        return new CommandNode.Negate(token.context(), arg);
    }

    static CommandNode retrieveVar(Token token) {
        return new CommandNode.RetrieveVar(token.context(), token.token());
    }

    static CommandNode sub(Token token, CommandNode left, CommandNode right) {
        return new Sub(token.context(), left, right);
    }

    static CommandNode value(Token token, Complex value) {
        return new Value(token.context(), value);
    }

    static CommandNode value(Token token, Ket value) {
        return new Value(token.context(), value);
    }

    static CommandNode.Value value(Token token) {
        return new Value(token.context(), switch (token) {
            case Token.IntegerToken tok -> tok.value();
            case Token.RealToken tok -> Complex.create(tok.value());
            default -> token.token();
        });
    }

    SourceContext context();

    interface UnaryNode extends CommandNode {
        CommandNode arg();
    }

    interface BinaryNode extends CommandNode {
        CommandNode left();

        CommandNode right();
    }

    record Add(SourceContext context, CommandNode left, CommandNode right) implements BinaryNode {
    }

    record Sub(SourceContext context, CommandNode left, CommandNode right) implements BinaryNode {
    }

    record Mul(SourceContext context, CommandNode left, CommandNode right) implements BinaryNode {
    }

    record Div(SourceContext context, CommandNode left, CommandNode right) implements BinaryNode {
    }

    record Cross(SourceContext context, CommandNode left, CommandNode right) implements BinaryNode {
    }

    record Clear(SourceContext context) implements CommandNode {
    }

    record Value(SourceContext context, Object value) implements CommandNode {
    }

    record CodeUnit(SourceContext context, List<CommandNode> commands) implements CommandNode {
    }

    record Assign(SourceContext context, String id, CommandNode arg) implements UnaryNode {
    }

    record RetrieveVar(SourceContext context, String id) implements CommandNode {
    }

    record IntToKet(SourceContext context, CommandNode arg) implements UnaryNode {
    }

    record Conj(SourceContext context, CommandNode arg) implements UnaryNode {
    }

    record Negate(SourceContext context, CommandNode arg) implements UnaryNode {
    }

    record CallFunction(SourceContext context, String id, CommandNode arg) implements UnaryNode {
    }
}
