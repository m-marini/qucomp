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
     * @param id the expressin identifier
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
     *
     * @param expressions the list of expressions
     */
    public NonTerminalExp opt(Expression... expressions) {
        Expression cond = this;
        return new NonTerminalExp(cond.id + "?") {
            @Override
            public boolean test(ParseContext context) throws Throwable {
                logger.atDebug().log("{} entry toke=\"{}\"", this, context.currentToken());
                boolean result = cond.test(context);
                if (result) {
                    for (Expression expression : expressions) {
                        expression.test(context);
                    }
                }
                logger.atDebug().log("{} exit={}", this, result);
                return result;
            }
        };
    }

    /**
     * Evaluates the expression changing the process context with source context just before expression evaluation
     *
     * @param op the process context operator
     */
    public NonTerminalExp postOp(BiConsumer<ParseContext, Token> op) {
        Expression expression = this;
        return new NonTerminalExp(expression.id + "{postOp}") {
            @Override
            public boolean test(ParseContext context) throws Throwable {
                Token tok = context.currentToken();
                logger.atDebug().log("{} entry token=\"{}\"", this, context.currentToken());
                boolean result = expression.test(context);
                if (result) {
                    op.accept(context, tok);
                }
                logger.atDebug().log("{} exit={}", this, result);
                return result;
            }
        };
    }

    /**
     * Returns the expression generating the message error if the expression does not match;
     *
     * @param pattern the error message pattern
     * @param args    the error message arguments
     */
    public NonTerminalExp require(String pattern, Object... args) {
        Expression expression = this;
        return new NonTerminalExp(expression.id) {
            @Override
            public boolean test(ParseContext context) throws Throwable {
                logger.atDebug().log("{} entry token=\"{}\"", this, context.currentToken());
                if (!expression.test(context)) {
                    throw context.currentToken().context().exception(pattern, args);
                }
                logger.atDebug().log("{} exit={}", this, true);
                return true;
            }
        };
    }

    @Override
    public String toString() {
        return id;
    }

    /**
     * Returns the expression that repetitively applys the expression while it matches
     * The resulting expression returns true if the expression matches at least one time
     */
    public NonTerminalExp whileMatch() {
        Expression expression = this;
        return new NonTerminalExp(expression.id + "*") {
            @Override
            public boolean test(ParseContext context) throws Throwable {
                logger.atDebug().log("{} entry token=\"{}\"", this, context.currentToken());
                boolean result = false;
                while (expression.test(context)) {
                    result = true;
                }
                logger.atDebug().log("{} exit={}", this, result);
                return result;
            }
        };
    }

}