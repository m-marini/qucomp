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

import org.mmarini.Consumer2Throws;
import org.mmarini.qucomp.apis.Complex;
import org.mmarini.qucomp.apis.Ket;

import java.io.IOException;
import java.util.*;

/**
 * Generates the tree code from parser matching rules
 */
public class Compiler implements CompilerContext {

    /**
     * Returns the code generator for the qu syntax parser
     */
    public static Compiler create() throws QuException {
        Compiler gen = new Compiler();
        gen.add("<int-literal>", (context, token) -> {
                    if (token instanceof Token.IntegerToken tok) {
                        context.push(new CommandNode.Value(token.context(), tok.value()));
                    } else {
                        throw token.context().parseException("Integer toke expected %s", token);
                    }
                })
                .add("<real-literal>", (context, token) -> {
                    if (token instanceof Token.RealToken tok) {
                        context.push(new CommandNode.Value(token.context(), Complex.create(tok.value())));
                    } else {
                        throw token.context().parseException("Real token expected %s", token);
                    }
                })
                .add("<im-state>", (context, token) ->
                        context.push(CommandNode.value(token, Ket.i())))
                .add("<plus-state>", (context, token) ->
                        context.push(CommandNode.value(token, Ket.plus())))
                .add("<clear-stm>", (context, token) ->
                        context.push(new CommandNode.Clear(token.context())))
                .add("<assign-var-identifier>", (context, token) ->
                        context.push(CommandNode.value(token)))
                .add("<assign-stm>", (context, token) -> {
                    CommandNode value = context.pop();
                    CommandNode.Value id = context.pop();
                    context.push(CommandNode.assign(token, id.value().toString(), value));
                })
                .add("<code-unit>", (context, token) -> {
                    List<CommandNode> values = context.popAll();
                    context.push(CommandNode.codeUnit(token, values));
                })
                .add("<minus-tail>", (context, token) -> {
                    CommandNode right = context.pop();
                    CommandNode left = context.pop();
                    context.push(CommandNode.sub(token, left, right));
                })
                .add("<cross-tail-opt>", (context, token) -> {
                    CommandNode right = context.pop();
                    CommandNode left = context.pop();
                    context.push(CommandNode.cross(token, left, right));
                })
                .add("<multiply-tail>", (context, token) -> {
                    CommandNode right = context.pop();
                    CommandNode left = context.pop();
                    context.push(CommandNode.mul(token, left, right));
                })
                .add("<divide-tail>", (context, token) -> {
                    CommandNode right = context.pop();
                    CommandNode left = context.pop();
                    context.push(CommandNode.div(token, left, right));
                })
                .add("<plus-tail>", (context, token) -> {
                    CommandNode right = context.pop();
                    CommandNode left = context.pop();
                    context.push(CommandNode.add(token, left, right));
                })
                .add("<negate-exp>", (context, token) ->
                        context.push(CommandNode.negate(token, context.pop())))
                .add("^", (context, token) ->
                        context.push(CommandNode.conj(token, context.pop())))
                .add("<minus-state>", (context, token) ->
                        context.push(CommandNode.value(token, Ket.minus())))
                .add("<minus-im-state>", (context, token) ->
                        context.push(CommandNode.value(token, Ket.minus_i())))
                .add("<int-state>", (context, token) ->
                        context.push(CommandNode.intToState(token, context.pop())))
                .add("<bra>", (context, token) ->
                        context.push(CommandNode.conj(token, context.pop())))
                .add("<im-unit>", (context, token) ->
                        context.push(CommandNode.value(token, Complex.i())))
                .add("pi", (context, token) ->
                        context.push(CommandNode.value(token, Complex.create((float) Math.PI))))
                .add("e", (context, token) ->
                        context.push(CommandNode.value(token, Complex.create((float) Math.E))))
                .add("<function>", (context, token) -> {
                    CommandNode arg = context.pop();
                    context.push(CommandNode.function(token, arg));
                })
                .add("<var-identifier>", (context, token) ->
                        context.push(CommandNode.retrieveVar(token)));
        return gen;
    }

    private final Map<String, Consumer2Throws<CompilerContext, Token, QuParseException>> operators;
    private final Deque<CommandNode> stack;

    /**
     * Creates the empty the code generator
     */
    public Compiler() {
        this.operators = new HashMap<>();
        this.stack = new LinkedList<>();
    }

    /**
     * Add the operator joint to a rule to the code generator
     *
     * @param rule      the rule identifier
     * @param generator the generator
     */
    public Compiler add(String rule, Consumer2Throws<CompilerContext, Token, QuParseException> generator) throws QuException {
        if (operators.containsKey(rule)) {
            throw new QuException("Rule " + rule + " already mapped");
        }
        operators.put(rule, generator);
        return this;
    }

    /**
     * Returns the parse context for the give tokenizer
     *
     * @param tokenizer the tokenizer
     */
    public ParseContext createParseContext(Tokenizer tokenizer) {
        return new ParseContext() {
            @Override
            public Token currentToken() {
                return tokenizer.currentToken();
            }

            @Override
            public void join(Token token, SyntaxRule rule) throws QuParseException {
                process(token, rule.id());
            }

            @Override
            public void popToken() throws IOException {
                tokenizer.popToken();
            }
        };
    }

    @Override
    public CommandNode pop() {
        return stack.removeLast();
    }

    @Override
    public List<CommandNode> popAll() {
        List<CommandNode> list = stack.stream().toList();
        stack.clear();
        return list;
    }

    /**
     * Applies the associated operator if exits
     *
     * @param token the token
     * @param rule  the rule identifer
     */
    public void process(Token token, String rule) throws QuParseException {
        Consumer2Throws<CompilerContext, Token, QuParseException> operator = operators.get(rule);
        if (operator != null) {
            operator.accept(this, token);
        }
    }

    @Override
    public void push(CommandNode value) {
        stack.offer(value);
    }

    /**
     * Returns the stack
     */
    Deque<CommandNode> stack() {
        return stack;
    }
}
