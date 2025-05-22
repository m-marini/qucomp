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

import io.reactivex.functions.BiConsumer;
import io.reactivex.rxjava3.functions.Predicate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Function;

import static java.util.Objects.requireNonNull;

/**
 * The abstract expression in the interpreter design pattern
 */
public abstract class Expression implements Predicate<ParseContext> {
    private static final Logger logger = LoggerFactory.getLogger(Expression.class);
    protected final String id;

    /**
     * Create the expression
     *
     * @param id the expressing identifier
     */
    protected Expression(String id) {
        this.id = requireNonNull(id);
    }

    /**
     * Returns the mapped expression
     *
     * @param mapper the mapper function
     * @param <T>    the type of return
     */
    public <T> T map(Function<? super Expression, T> mapper) {
        return mapper.apply(this);
    }

    /**
     * Returns the condition expression
     * that applies all the expression if the condition expression matches
     * <pre>
     *     &lt;opt-exp> ::= &lt;opt-cond-exp> &lt;expression-list> | ""
     * </pre>
     *
     * @param expressions the list of expressions
     */
    public NonTerminalExp ifMatch(Expression... expressions) {
        Expression cond = this;
        return new NonTerminalExp(cond.id + "?") {
            @Override
            public boolean test(ParseContext context) throws Throwable {
                boolean result = cond.test(context);
                if (result) {
                    logger.atDebug().log("{}", this);
                    for (Expression expression : expressions) {
                        expression.test(context);
                    }
                }
                return result;
            }
        };
    }

    /**
     * Evaluates the expression changing the process context with source context just before expression evaluation
     * <pre>
     *     &lt;postOp> ::= &lt;exp>
     * </pre>
     *
     * @param op the process context operator
     */
    public NonTerminalExp postOp(BiConsumer<ParseContext, Token> op) {
        Expression expression = this;
        return new NonTerminalExp(expression.id + "{postOp}") {
            @Override
            public boolean test(ParseContext context) throws Throwable {
                Token tok = context.currentToken();
                boolean result = expression.test(context);
                if (result) {
                    op.accept(context, tok);
                    logger.atDebug().log("{}", this);
                }
                return result;
            }
        };
    }

    /**
     * Returns the expression requiring the match of optional expression.
     * If the expression does not match, the message error is generated
     * <pre>
     *     &lt;require-exp> ::= &lt;exp>
     * </pre>
     *
     * @param pattern the error message pattern
     * @param args    the error message arguments
     */
    public NonTerminalExp require(String pattern, Object... args) {
        Expression expression = this;
        return new NonTerminalExp(expression.id) {
            @Override
            public boolean test(ParseContext context) throws Throwable {
                if (!expression.test(context)) {
                    throw context.currentToken().context().exception(pattern, args);
                }
                logger.atDebug().log("{}", this);
                return true;
            }
        };
    }

    @Override
    public String toString() {
        return id;
    }

    /**
     * Returns the expression that repetitively applies the expression while it matches
     * The resulting expression returns true if the expression matches at least one time
     * <pre>
     *     &lt;while-exp> ::= &lt;while-exp> | &lt;exp> | ""
     * </pre>
     */
    public NonTerminalExp whileMatch() {
        Expression expression = this;
        return new NonTerminalExp(expression.id + "*") {
            @Override
            public boolean test(ParseContext context) throws Throwable {
                boolean result = false;
                while (expression.test(context)) {
                    result = true;
                }
                if (result) {
                    logger.atDebug().log("{}", this);
                }
                return result;
            }
        };
    }

}