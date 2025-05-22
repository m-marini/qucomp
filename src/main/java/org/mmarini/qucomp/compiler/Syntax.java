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
     * &lt;primary-exp> ::= "i" | "+" | "-" | "-" "i" | &lt;int-literal>
     * </pre>
     */
    Expression stateLiteralExp = options("<state-literal>",
            TerminalExp.optOp("+").opt(
                    operate("<plus-state-literal>", (context, token) ->
                            context.add(new Command.PushKet(token.context(), Ket.plus())))),
            TerminalExp.optIdentifier("i").opt(
                    operate("<i-state-literal>", (context, token) ->
                            context.add(new Command.PushKet(token.context(), Ket.i())))),
            TerminalExp.optOp("-").opt(
                    options("<minus-state-exp-literal>",
                            TerminalExp.optIdentifier("i").opt(
                                    operate("<minus-i-state-literal>", ((context, token) ->
                                            context.add(new Command.PushKet(token.context(), Ket.minus_i()))))),
                            operate("<minus-state-literal>", ((context, token) ->
                                    context.add(new Command.PushKet(token.context(), Ket.minus())))
                            ))),
            TerminalExp.optStateIntLiteral)
            .require("Missing state literal");

    /**
     * <pre>
     * &lt;opt-ket-literal> ::= "|" &lt;state-literal> ">" | ""
     * </pre>
     */
    Expression optKet = TerminalExp.optOp("|").opt(stateLiteralExp, TerminalExp.op(">"));

    /**
     * <pre>
     * &lt;opt-ket-literal> ::= "<" &lt;state-literal> "|" | ""
     * </pre>
     */
    Expression optBra = TerminalExp.optOp("<").opt(
            stateLiteralExp.postOp((context, token) ->
                    context.add(new Command.Conj(token.context()))),
            TerminalExp.op("|"));

    /**
     * <pre>
     * &lt;exp-exp> ::= &lt;unary-exp>
     * </pre>
     */
    static Expression exp() {
        return new NonTerminalExp("<exp>") {
            @Override
            public boolean test(ParseContext context) throws Throwable {
                logger.atDebug().log("{} entry token=\"{}\"", this, context.currentToken());
                boolean result = sumExp.test(context);
                logger.atDebug().log("{} exit={}", this, result);
                return result;
            }
        };
    }

    /**
     * <pre>
     * &lt;primary-exp> ::= "(" &lt;exp> ")"
     *                   | &lt;real-literal>
     *                   | &lt;int-literal>
     *                   | &lt;ket>
     *                   | &lt;bra>
     *                   | "i"
     *                   | &lt;var-identifier>
     * </pre>
     */
    Expression primaryExp = options("<primary-exp>",
            TerminalExp.optOp("(").opt(exp(), TerminalExp.op(")")),
            TerminalExp.optRealLiteral,
            TerminalExp.optIntLiteral,
            TerminalExp.optILiteral,
            optKet,
            optBra,
            TerminalExp.optVarIdentifier.postOp(((context, token) ->
                    context.add(new Command.RetrieveVar(token.context())))))
            .require("Missing primary expression");

    /**
     * <pre>
     * &lt;unary-exp> ::= "+" &lt;unary-exp>
     *                 | "-"  &lt;unary-exp>
     *                 | &lt;primary-exp>
     * </pre>
     */
    Expression unaryExp = new Expression("<unary-exp>") {
        @Override
        public boolean test(ParseContext context) throws Throwable {
            logger.atDebug().log("{} entry token=\"{}\"", this, context.currentToken());
            boolean result = switch (context.currentToken()) {
                case Token.OperatorToken opTok when opTok.token().equals("+") -> {
                    logger.atDebug().log("{}   match", this);
                    context.popToken();
                    test(context);
                    yield true;
                }
                case Token.OperatorToken opTok when opTok.token().equals("-") -> {
                    logger.atDebug().log("{}   match", this);
                    context.popToken();
                    test(context);
                    context.add(new Command.Negate(opTok.context()));
                    yield true;
                }
                case null, default -> primaryExp.test(context);
            };
            logger.atDebug().log("{} exit={}", this, result);
            return result;
        }
    };

    /**
     * <pre>
     * &lt;mul-tail-exp> ::= "*" &lt;mul-tail-exp> | "/" &lt;mul-tail-exp> | ""
     * </pre>
     */
    Expression prodTailExp = options("<prod-tail>",
            optOp("*").opt(
                    unaryExp.postOp((context, token) ->
                            context.add(new Command.Multiply(token.context())))),
            optOp("/").opt(
                    unaryExp.postOp((context, token) ->
                            context.add(new Command.Divide(token.context()))))
    ).whileMatch();

    /**
     * <pre>
     * &lt;mul-exp> ::= &lt;unary-exp> &lt;mul-exp-tail>
     * </pre>
     */
    Expression prodExp = all("<prod-exp>", unaryExp, prodTailExp);

    /**
     * <pre>
     * &lt;mul-tail-exp> ::= "*" &lt;mul-tail-exp> | "/" &lt;mul-tail-exp> | ""
     * </pre>
     */
    Expression sumTailExp = options("<sum-tail>",
            optOp("+").opt(
                    prodExp.postOp((context, token) ->
                            context.add(new Command.Add(token.context())))),
            optOp("-").opt(
                    prodExp.postOp((context, token) ->
                            context.add(new Command.Sub(token.context()))))
    ).whileMatch();

    /**
     * <pre>
     * &lt;mul-exp> ::= &lt;unary-exp> &lt;mul-exp-tail>
     * </pre>
     */
    Expression sumExp = all("<sum-exp>", prodExp, sumTailExp);

    Expression optAssignExp = TerminalExp.optIdentifier("let").opt(
            TerminalExp.optVarIdentifier.require("Missing variable identifier")
                    .opt(TerminalExp.op("="), exp(), TerminalExp.op(";"))
                    .postOp((context, token) -> {
                        context.add(new Command.Assign(token.context()));
                        context.add(new Command.Consume(token.context()));
                    }));

    /**
     * <pre>
     * &lt;code-unit> ::= <assign
     * </pre>
     */
    Expression codeUnitExp = optNotEmpty.opt(
                    options("<code-unit>",
                            optAssignExp,
                            all("<code-exp>", exp(),
                                    op(";").postOp((context, token) ->
                                            context.add(new Command.Consume(token.context()))))))
            .whileMatch();
}