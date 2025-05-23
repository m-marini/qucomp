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

import org.mmarini.qucomp.apis.Ket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.mmarini.qucomp.compiler.NonTerminalExp.all;
import static org.mmarini.qucomp.compiler.NonTerminalExp.options;
import static org.mmarini.qucomp.compiler.TerminalExp.*;

/**
 * The qu language syntax
 */
public interface Syntax {
    Logger logger = LoggerFactory.getLogger(Syntax.class);


    /**
     * <pre>
     * &lt;clear-exp> ::= "clear" "(" ")" ";" | ""
     * </pre>
     */
    Expression exp1_clear = optIdentifier("clear").ifMatch(
            op("("),
            op(")"),
            op(";"),
            operate("<clear>", (context, token) ->
                    context.push(new CommandNode.Clear(token.context()))));

    /**
     * <pre>
     * &lt;primary-exp> ::= "i" | "+" | "-" | "-" "i" | &lt;int-literal>
     * </pre>
     */
    Expression exp1_stateLiteral = options("<state-literal>",
            optOp("+").ifMatch(
                    operate("<plus-state-literal>", (context, token) ->
                            context.push(new CommandNode.Value(token.context(), Ket.plus())))),
            optIdentifier("i").ifMatch(
                    operate("<i-state-literal>", (context, token) ->
                            context.push(new CommandNode.Value(token.context(), Ket.i())))),
            optOp("-").ifMatch(
                    options("<minus-state-exp-literal>",
                            optIdentifier("i").ifMatch(
                                    operate("<minus-i-state-literal>", ((context, token) ->
                                            context.push(new CommandNode.Value(token.context(), Ket.minus_i()))))),
                            operate("<minus-state-literal>", ((context, token) ->
                                    context.push(new CommandNode.Value(token.context(), Ket.minus())))
                            ))),
            optStateIntLiteral)
            .require("Missing state literal");

    /**
     * <pre>
     * &lt;opt-ket-literal> ::= "|" &lt;state-literal> ">" | ""
     * </pre>
     */
    Expression exp2_ket = optOp("|").ifMatch(exp1_stateLiteral, op(">"));

    /**
     * <pre>
     * &lt;opt-ket-literal> ::= "<" &lt;state-literal> "|" | ""
     * </pre>
     */
    Expression exp2_bra = optOp("<").ifMatch(
            exp1_stateLiteral.postOp((context, token) -> {
                CommandNode cmd = context.popCommand();
                context.push(new CommandNode.Conj(token.context(), cmd));
            }),
            op("|"));

    /**
     * <pre>
     * &lt;exp-exp> ::= &lt;unary-exp>
     * </pre>
     */
    static Expression exp() {
        return new NonTerminalExp("<exp>") {
            @Override
            public boolean test(ParseContext context) throws Throwable {
                boolean result = exp8_add.test(context);
                if (result) {
                    logger.atDebug().log("{}", this);
                }
                return result;
            }
        };
    }

    /**
     * <pre>
     * &lt;unary-exp> ::= "+" &lt;unary-exp>
     *                 | "-"  &lt;unary-exp>
     *                 | &lt;conj-exp>
     * </pre>
     */
    Expression exp5_unary = new Expression("<unary-exp>") {
        @Override
        public boolean test(ParseContext context) throws Throwable {
            boolean result = switch (context.currentToken()) {
                case Token.OperatorToken opTok when opTok.token().equals("+") -> {
                    context.popToken();
                    test(context);
                    yield true;
                }
                case Token.OperatorToken opTok when opTok.token().equals("-") -> {
                    context.popToken();
                    test(context);
                    context.push(new CommandNode.Negate(opTok.context(), context.popCommand()));
                    yield true;
                }
                case null, default -> exp4_conj.test(context);
            };
            if (result) {
                logger.atDebug().log("{}", this);
            }
            return result;
        }
    };

    /**
     * <pre>
     * &lt;cross-exp> ::= &lt;unary-exp> "x" &lt;cross-exp> | &lt;unary-exp>"
     * </pre>
     */
    Expression exp6_cross = all("cross-exp>",
            exp5_unary,
            optIdentifier("x").ifMatch(
                    exp5_unary.postOp((context, toket) -> {
                        CommandNode right = context.popCommand();
                        CommandNode left = context.popCommand();
                        context.push(new CommandNode.Cross(toket.context(), left, right));
                    })
            ).whileMatch()
    );
    /**
     * <pre>
     * &lt;prod-exp> ::= &lt;unary-exp> &lt;mul-exp-tail>
     * </pre>
     */
    Expression exp7_prod = all("<prod-exp>",
            exp6_cross,
            options("<prod-tail>",
                    optOp("*").ifMatch(
                            exp6_cross.postOp((context, token) -> {
                                CommandNode right = context.popCommand();
                                CommandNode left = context.popCommand();
                                context.push(new CommandNode.Mul(token.context(), left, right));
                            })),
                    optOp("/").ifMatch(
                            exp6_cross.postOp((context, token) -> {
                                CommandNode right = context.popCommand();
                                CommandNode left = context.popCommand();
                                context.push(new CommandNode.Mul(token.context(), left, right));
                            }))
            ).whileMatch());
    /**
     * <pre>
     * &lt;sum-exp> ::= &lt;prod-exp> &lt;sum-exp-tail>
     * </pre>
     */
    Expression exp8_add = all("<add-exp>",
            exp7_prod,
            options("<add-tail>",
                    optOp("+").ifMatch(
                            exp7_prod.postOp((context, token) -> {
                                CommandNode right = context.popCommand();
                                CommandNode left = context.popCommand();
                                context.push(new CommandNode.Add(token.context(), left, right));
                            })),
                    optOp("-").ifMatch(
                            exp7_prod.postOp((context, token) -> {
                                CommandNode right = context.popCommand();
                                CommandNode left = context.popCommand();
                                context.push(new CommandNode.Sub(token.context(), left, right));
                            }))
            ).whileMatch());

    /**
     * <pre>
     * &lt;opt-sqrt> ::= "sqrt" "(" &lt;exp> ")"
     *                | ""
     * </pre>
     */
    Expression exp1_sqrt = optIdentifier("sqrt")
            .ifMatch(all("<sqrt>",
                    op("("), exp(), op(")"))
                    .postOp((context, token) -> {
                        CommandNode arg = context.popCommand();
                        context.push(new CommandNode.CallFunction(token.context(), arg, "sqrt"));
                    }));

    /**
     * <pre>
     * &lt;func-exp> ::= &lt;opt-sqrt>
     * </pre>
     */
    Expression exp2_func = exp1_sqrt;
    /**
     * <pre>
     * &lt;primary-exp> ::= "(" &lt;exp> ")"
     *                   | &lt;real-literal>
     *                   | &lt;int-literal>
     *                   | &lt;ket>
     *                   | &lt;bra>
     *                   | "i"
     *                   | &ltfunc-exp>
     *                   | &lt;var-identifier>
     * </pre>
     */
    Expression exp3_primary = options("<primary-exp>",
            optOp("(").ifMatch(exp(), op(")")),
            optRealLiteral,
            optIntLiteral,
            optILiteral,
            exp2_ket,
            exp2_bra,
            exp2_func,
            optVarIdentifier.postOp(((context, token) ->
                    context.push(new CommandNode.RetrieveVar(token.context(), context.popCommand())))))
            .require("Missing primary expression");
    /**
     * <pre>
     * &lt;conj-exp> ::= &lt;conj-exp> "^"
     *                 | &ltprimary-exp>
     * </pre>
     */
    Expression exp4_conj = all("<conjExp>",
            exp3_primary,
            optOp("^").ifMatch(operate("<conj>",
                    (context, token) -> context.push(new CommandNode.Conj(token.context(), context.popCommand())))
            ).whileMatch());
    /**
     *
     */
    Expression exp1_assignExp = optIdentifier("let").ifMatch(
            optVarIdentifier.require("Missing variable identifier")
                    .ifMatch(op("="),
                            exp(),
                            op(";")).postOp((context, token) -> {
                        CommandNode right = context.popCommand();
                        CommandNode left = context.popCommand();
                        context.push(new CommandNode.Assign(token.context(),
                                left,
                                new CommandNode.Consume(right.context(), right)));
                    }));

    /**
     * <pre>
     * &lt;code-unit> ::= <assign
     * </pre>
     */
    Expression codeUnitExp = optNotEmpty.ifMatch(
                    options("<code-unit>",
                            exp1_assignExp,
                            exp1_clear,
                            all("<code-exp>", exp(),
                                    op(";").postOp((context, token) ->
                                            context.push(new CommandNode.Consume(token.context(), context.popCommand()))))))
            .whileMatch()
            .postOp(((context, token) ->
                    context.push(new CommandNode.CodeUnit(token.context(), context.popAllReversed()))
            ));
}