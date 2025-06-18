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

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * The qu language syntax
 */
public class Syntax {
    /**
     * Reserved keywords
     */
    public static final Set<String> RESERVED_KEYWORDS = Set.of(
            "i", // imaginary unit
            "e", // imaginary unit
            "pi", // imaginary unit
            "x", // cross-operator
            // Functions
            "exp",
            "pow",
            "sin",
            "cos",
            "tan",
            "acos",
            "asin",
            "atan",
            "arg",
            "sinh",
            "cosh",
            "tanh"
    );
    /**
     * Statements keywords
     */
    public static final Set<String> STATEMENTS_KEYWORDS = Set.of(
            "let",
            "clear"
    );
    private static final Logger logger = LoggerFactory.getLogger(Syntax.class);

    /**
     * Returns the qu syntax builder
     */
    private static SyntaxBuilder createSyntax() throws QuException {
        logger.atDebug().log("Creating syntax ...");
        SyntaxBuilder builder = new SyntaxBuilder();

        builder.require("<code-unit>", "<code-unit-head>", "<statement-list>", "<eof>");
        builder.repeat("<statement-list>", "<stm>");
        builder.opt("<stm>", "<stm-opt>", ";");
        builder.options("<stm-opt>", "<clear-stm>", "<assign-stm>", "<exp-opt>");
        builder.require("<eof>", "<end>");
        builder.end("<end>");
        builder.empty("<code-unit-head>");

        builder.opt("<clear-stm>", "clear", "(", ")");
        builder.opt("<assign-stm>", "let", "<assign-var-identifier>", "=", "<exp>");

        // <exp-opt> ::= <conj>
        builder.require("<exp>", "<exp-opt>");
        builder.opt("<exp-opt>", "<add-exp>");

        // <add-exp> ::= <multiply-exp> <add-tail>
        // <add-tail> ::= <add-tail-opt> <add-tail>
        // <add-tail-opt> ::= <plus-tail> | <minus-tail>
        // <plus-tail> ::= "+" <multiply-exp> | ""
        // <minus-tail> ::= "-" <multiply-exp> | ""
        builder.opt("<add-exp>", "<multiply-exp>", "<add-tail>");
        builder.repeat("<add-tail>", "<add-tail-opt>");
        builder.options("<add-tail-opt>", "<plus-tail>", "<minus-tail>");
        builder.opt("<plus-tail>", "+", "<multiply-exp>");
        builder.opt("<minus-tail>", "-", "<multiply-exp>");

        // <multiply-exp> ::= <cross-exp> <mul-tail>
        // <mul-tail> ::= <mul-tail-opt> <mul-tail>
        // <mul-tail-opt> ::= <multiply-tail> | <divide-tail>
        // <multiply-tail> ::= "*" <cross-exp> | ""
        // <divide-tail> ::= "/" <cross-exp> | ""
        builder.opt("<multiply-exp>", "<cross-exp>", "<mul-tail>");
        builder.repeat("<mul-tail>", "<mul-tail-opt>");
        builder.options("<mul-tail-opt>", "<multiply-tail>", "<multiply0-tail>", "<divide-tail>");
        builder.opt("<multiply-tail>", "*", "<cross-exp>");
        builder.opt("<multiply0-tail>", ".", "<cross-exp>");
        builder.opt("<divide-tail>", "/", "<cross-exp>");

        // <cross-exp> ::= <unary-exp> <cross-tail>
        // <cross-tail> ::= <cross-tail-opt> <cross-tail>
        // <cross-tail-opt> ::= "x" <unary-exp> | ""
        builder.opt("<cross-exp>", "<unary-exp>", "<cross-tail>");
        builder.repeat("<cross-tail>", "<cross-tail-opt>");
        builder.opt("<cross-tail-opt>", "x", "<unary-exp>");

        // <unary-exp> ::= <plus-exp> | <negate-exp> | <conj>
        // <plus-exp> ::= "+" <unary-exp> | ""
        // <negate-exp> :.= "-" <unary-exp> | ""
        builder.options("<unary-exp>", "<plus-exp>", "<negate-exp>", "<conj>");
        builder.opt("<plus-exp>", "+", "<unary-exp>");
        builder.opt("<negate-exp>", "-", "<unary-exp>");

        // <conj> :: <primary-exp> <conj-tail>
        // <conj-tail> ::= "^" <conj-tail> || ""
        builder.opt("<conj>", "<primary-exp>", "<conj-tail>");
        builder.repeat("<conj-tail>", "^");

        builder.options("<primary-exp>",
                "<priority-exp>",
                "<bra>",
                "<ket>",
                "<im-unit>",
                "pi",
                "e",
                "<function>",
                "<var-identifier>",
                "<int-literal>",
                "<real-literal>");

        // <priority-exp> ::= "(" <exp-opt> ")" | ""
        builder.opt("<priority-exp>", "(", "<exp>", ")");

        // <bra> ::= "|" <state-exp> ">" | ""
        builder.opt("<bra>", "<", "<state-exp>", "|");

        // <ket> ::= "|" <state-exp> ">" | ""
        builder.opt("<ket>", "|", "<state-exp>", ">");

        builder.opt("<function>", "<function-id>", "<args-exp>");
        builder.require("<args-exp>", "(", "<arg-list>", ")");
        builder.options("<arg-list>", "<arg-list-opt>", "<empty-arg>");
        builder.opt("<arg-list-opt>", "<arg>", "<arg-list-tail>");
        builder.repeat("<arg-list-tail>", "<arg-tail>");
        builder.opt("<arg-tail>", ",", "<exp>");
        builder.empty("<empty-arg>");
        builder.opt("<arg>", "<exp-opt>");

            /*
            <state-exp> ::= <i-state-literal>
                          | <plus-state-literal>
                          | <minus-state-exp>
                          | <exp-opt>
             */
        builder.require("<state-exp>", "<state-exp-opt>");
        builder.options("<state-exp-opt>", "<im-state>", "<plus-state>", "<minus-state-exp>", "<int-state>");
        // <minus-state-exp> ::= "-" <minus-state-exp-opt>
        // <minus-state-exp-opt> ::= "i" <minus-i-state> | <minus-state>
        builder.opt("<minus-state-exp>", "-", "<minus-state-exp-opt>");
        builder.options("<minus-state-exp-opt>", "<minus-im-state>", "<minus-state>");
        builder.empty("<minus-state>");
        builder.require("<int-state>", "<exp-opt>");

        // literals
        builder.intLiteral("<int-literal>");
        builder.realLiteral("<real-literal>");
        builder.opt("<im-unit>", "i");
        builder.opt("<im-state>", "i");
        builder.opt("<minus-im-state>", "i");
        builder.opt("<plus-state>", "+");
        builder.idIn("<function-id>", Processor.FUNCTION_BY_ID.keySet());
        builder.idNotIn("<var-identifier>", keywords());
        builder.idNotIn("<assign-var-identifier>", keywords());

        // Operators
        builder.oper("+");
        builder.oper("-");
        builder.oper("(");
        builder.oper(")");
        builder.oper("<");
        builder.oper(">");
        builder.oper("|");
        builder.oper("^");
        builder.oper("*");
        builder.oper("/");
        builder.oper("=");
        builder.oper(";");
        builder.oper(",");
        builder.oper(".");

        // Reserved keywords
        builder.id("clear");
        builder.id("let");
        builder.id("sqrt");
        builder.id("ary");
        builder.id("i");
        builder.id("pi");
        builder.id("e");
        builder.id("x");

        return builder.build();
    }

    public static Set<String> keywords() {
        return Stream.concat(
                        Processor.FUNCTION_BY_ID.keySet().stream(),
                        Stream.concat(
                                STATEMENTS_KEYWORDS.stream(),
                                RESERVED_KEYWORDS.stream()))
                .collect(Collectors.toSet());
    }

    /**
     * Returns the qu syntax rule
     *
     * @param id the rule identifier
     */
    public static SyntaxRule rule(String id) throws QuException {
        return createSyntax().rule(id);
    }
}