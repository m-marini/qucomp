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
 * The qu language syntax
 */
public class Syntax1 {
    private static final Logger logger = LoggerFactory.getLogger(Syntax1.class);
    private static final SyntaxBuilder BUILDER = createSyntax();

    /**
     * Returns the qu syntax builder
     */
    private static SyntaxBuilder createSyntax() {
        try {
            SyntaxBuilder builder = new SyntaxBuilder();

            // <clear-stm> ::= "clear" "(" ")" ";" | ""
            builder.opt("<clear-stm>", "clear", "(", ")", ";");

            builder.require("<exp>", "<primary-exp>");

            /*
            <exp-primary> := <priority-exp>
                           | <int-literal>
                           | <real-literal>
             */
            builder.require("<primary-exp>", "<primary-exp-opt>");
            builder.options("<primary-exp-opt>", "<priority-exp>", "<int-literal>", "<real-literal>");

            /*
            <priority-exp> ::= "(" <exp> ")" | ""
             */
            builder.opt("<priority-exp>", "(", "<exp>", ")");

            builder.require("<state-exp>", "<state-exp-opt>");
            /*
            <state-exp-opt> ::= <i-state-literal>
                            | <plus-state-literal>
                            | <minus-state-exp>
                            | <exp>
             */
            builder.options("<state-exp-opt>", "<im-state>", "<plus-state>", "<minus-state-exp>", "<exp>");
            /*
            <minus-state-exp> ::= "-" <minus-state-exp-opt>
             */
            builder.opt("<minus-state-exp>", "-", "<minus-state-exp-opt>");
            /*
            <minus-state-exp-opt> ::= "i" <minus-i-state> | <minus-state>
            */
            builder.options("<minus-state-exp-opt>", "<minus-im-state>", "<minus-state>");

            builder.noPop("<minus-state>");

            // literals
            builder.intLiteral("<int-literal>");
            builder.realLiteral("<real-literal>");
            builder.opt("<im-unit>", "i");
            builder.opt("<im-state>", "i");
            builder.opt("<minus-im-state>", "i");
            builder.opt("<plus-state>", "+");


            // Operators
            builder.oper("+");
            builder.oper("-");
            builder.oper("(");
            builder.oper(")");
            builder.oper(";");

            // Reserved keywords
            builder.id("clear");
            builder.id("i");

            return builder.build();

        } catch (ParseException ex) {
            logger.atError().setCause(ex).log("Error building qu syntax");
            throw new RuntimeException(ex);
        }
    }

    /**
     * Returns the qu syntax rule
     *
     * @param id the rule identifier
     */
    static SyntaxRule rule(String id) {
        return BUILDER.rule(id);
    }
}