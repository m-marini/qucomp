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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Defines non-terminal expression grammars
 */
public abstract class NonTerminalExp extends Expression {
    private static final Logger logger = LoggerFactory.getLogger(NonTerminalExp.class);

    /**
     * Return the expression that applies all the expression
     *
     * @param id          the expression identifier
     * @param expressions the expression list
     */
    public static NonTerminalExp all(String id, Expression... expressions) {
        return new NonTerminalExp(id) {
            @Override
            public boolean test(ParseContext context) throws Throwable {
                for (Expression expression : expressions) {
                    expression.test(context);
                }
                logger.atDebug().log("{}", this);
                return true;
            }
        };
    }

    /**
     * Returns an expression that matches for the first matching expression among the expressions
     *
     * @param id          the expression identifier
     * @param expressions the list of expressions
     */
    public static NonTerminalExp options(String id, Expression... expressions) {
        return new NonTerminalExp(id) {
            @Override
            public boolean test(ParseContext context) throws Throwable {
                boolean result = false;
                for (Expression expression : expressions) {
                    result = expression.test(context);
                    if (result) {
                        break;
                    }
                }
                if (result) {
                    logger.atDebug().log("{}", this);
                }
                return result;
            }
        };
    }

    /**
     * Creates the non-terminal expression
     *
     * @param id the expression identifier
     */
    protected NonTerminalExp(String id) {
        super(id);
    }
}
