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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mmarini.NotImplementedException;
import org.mmarini.Tuple2;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.isA;
import static org.junit.jupiter.api.Assertions.*;
import static org.mmarini.Matchers.tupleOf;
import static org.mmarini.qucomp.Matchers.*;

class Syntax1Test {

    List<Tuple2<Token, SyntaxRule>> rules;
    private ParseContext parseContext;

    boolean parse(String text, String ruleId) throws IOException {
        Tokenizer tokenizer = Tokenizer.create(text).open();
        SyntaxRule rule = Syntax1.rule(ruleId);
        this.parseContext = new ParseContext() {
            @Override
            public void join(Token token, SyntaxRule rule) {
                rules.add(Tuple2.of(token, rule));
            }

            @Override
            public Token currentToken() {
                return tokenizer.currentToken();
            }

            @Override
            public List<CommandNode> popAllReversed() {
                throw new NotImplementedException();
            }

            @Override
            public CommandNode popCommand() {
                throw new NotImplementedException();
            }

            @Override
            public void popToken() throws IOException {
                tokenizer.popToken();
            }

            @Override
            public void push(CommandNode node) {
                throw new NotImplementedException();
            }
        };
        return rule.parse(parseContext);
    }

    @BeforeEach
    void setUp() {
        this.rules = new ArrayList<>();
    }

    @Test
    void testClear() {
        boolean result = assertDoesNotThrow(() -> parse("clear();", "<clear-stm>"));
        assertTrue(result);
        assertThat(parseContext.currentToken(), isA(Token.EOFToken.class));
        assertThat(rules, contains(
                tupleOf(idToken("clear"), rule("clear")),
                tupleOf(opToken("("), rule("(")),
                tupleOf(opToken(")"), rule(")")),
                tupleOf(opToken(";"), rule(";")),
                tupleOf(idToken("clear"), rule("<clear-stm>"))
        ));
    }

    @ParameterizedTest
    @CsvSource({
            "clear,Missing ( (<EOF>)",
            "clear (,Missing ) (<EOF>)",
            "clear (),Missing ; (<EOF>)",
    })
    void testClearError(String text, String expMsg) {
        SourceParseException ex = assertThrows(SourceParseException.class, () ->
                parse(text, "<clear-stm>"));
        assertEquals(expMsg, ex.getMessage());
    }

    @Test
    void testImUnit() {
        boolean result = assertDoesNotThrow(() -> parse("i", "<im-unit>"));
        assertTrue(result);
        assertThat(parseContext.currentToken(), isA(Token.EOFToken.class));
        assertThat(rules, contains(
                tupleOf(idToken("i"), rule("i")),
                tupleOf(idToken("i"), rule("<im-unit>"))
        ));
    }

    @Test
    void testInt() {
        boolean result = assertDoesNotThrow(() -> parse("1234", "<int-literal>"));
        assertTrue(result);
        assertThat(parseContext.currentToken(), isA(Token.EOFToken.class));
        assertThat(rules, contains(tupleOf(intToken(1234), rule("<int-literal>"))
        ));
    }

    @ParameterizedTest
    @CsvSource({
            "(,Missing <primary-exp-opt> (<EOF>)",
            "(1,Missing ) (<EOF>)",
    })
    void testPrimaryExpError(String text, String expMsg) {
        SourceParseException ex = assertThrows(SourceParseException.class, () ->
                parse(text, "<primary-exp>"));
        assertEquals(expMsg, ex.getMessage());
    }

    @Test
    void testPriorityExp() {
        boolean result = assertDoesNotThrow(() -> parse("(1)", "<priority-exp>"));
        assertTrue(result);
        assertThat(parseContext.currentToken(), isA(Token.EOFToken.class));
        assertThat(rules, contains(
                tupleOf(opToken("("), rule("(")),
                tupleOf(intToken(1), rule("<int-literal>")),
                tupleOf(intToken(1), rule("<primary-exp-opt>")),
                tupleOf(intToken(1), rule("<primary-exp>")),
                tupleOf(intToken(1), rule("<exp>")),
                tupleOf(opToken(")"), rule(")")),
                tupleOf(opToken("("), rule("<priority-exp>"))
        ));
    }

    @ParameterizedTest
    @CsvSource({
            "(,Missing <primary-exp-opt> (<EOF>)",
            "(1,Missing ) (<EOF>)",
    })
    void testPriorityExpError(String text, String expMsg) {
        SourceParseException ex = assertThrows(SourceParseException.class, () ->
                parse(text, "<priority-exp>"));
        assertEquals(expMsg, ex.getMessage());
    }

    @Test
    void testRealLiteral() {
        boolean result = assertDoesNotThrow(() -> parse("1.5", "<real-literal>"));
        assertTrue(result);
        assertThat(parseContext.currentToken(), isA(Token.EOFToken.class));
        assertThat(rules, contains(tupleOf(realToken(1.5f), rule("<real-literal>"))));
    }

    @Test
    void testStateExpIm() {
        boolean result = assertDoesNotThrow(() -> parse("i", "<state-exp>"));
        assertTrue(result);
        assertThat(parseContext.currentToken(), isA(Token.EOFToken.class));
        assertThat(rules, contains(
                        tupleOf(idToken("i"), rule("i")),
                        tupleOf(idToken("i"), rule("<im-state>")),
                        tupleOf(idToken("i"), rule("<state-exp-opt>")),
                        tupleOf(idToken("i"), rule("<state-exp>"))
                )
        );
    }

    @ParameterizedTest
    @CsvSource({
            "0,0",
            "1,1",
            "2,2",
            "3,3",
            "4,4",
            "7,7"
    })
    void testStateExpInt(String text, int tokenValue) {
        boolean result = assertDoesNotThrow(() -> parse(text, "<state-exp>"));
        assertTrue(result);
        assertThat(parseContext.currentToken(), isA(Token.EOFToken.class));
        assertThat(rules, contains(
                        tupleOf(intToken(tokenValue), rule("<int-literal>")),
                        tupleOf(intToken(tokenValue), rule("<primary-exp-opt>")),
                        tupleOf(intToken(tokenValue), rule("<primary-exp>")),
                        tupleOf(intToken(tokenValue), rule("<exp>")),
                        tupleOf(intToken(tokenValue), rule("<state-exp-opt>")),
                        tupleOf(intToken(tokenValue), rule("<state-exp>"))
                )
        );
    }

    @Test
    void testStateExpMinus() {
        boolean result = assertDoesNotThrow(() -> parse("-", "<state-exp>"));
        assertTrue(result);
        assertThat(parseContext.currentToken(), isA(Token.EOFToken.class));
        assertThat(rules, contains(
                        tupleOf(opToken("-"), rule("-")),
                        tupleOf(isA(Token.EOFToken.class), rule("<minus-state>")),
                        tupleOf(isA(Token.EOFToken.class), rule("<minus-state-exp-opt>")),
                        tupleOf(opToken("-"), rule("<minus-state-exp>")),
                        tupleOf(opToken("-"), rule("<state-exp-opt>")),
                        tupleOf(opToken("-"), rule("<state-exp>"))
                )
        );
    }

    @Test
    void testStateExpMinusIm() {
        boolean result = assertDoesNotThrow(() -> parse("-i", "<state-exp>"));
        assertTrue(result);
        assertThat(parseContext.currentToken(), isA(Token.EOFToken.class));
        assertThat(rules, contains(
                        tupleOf(opToken("-"), rule("-")),
                        tupleOf(idToken("i"), rule("i")),
                        tupleOf(idToken("i"), rule("<minus-im-state>")),
                        tupleOf(idToken("i"), rule("<minus-state-exp-opt>")),
                        tupleOf(opToken("-"), rule("<minus-state-exp>")),
                        tupleOf(opToken("-"), rule("<state-exp-opt>")),
                        tupleOf(opToken("-"), rule("<state-exp>"))
                )
        );
    }

    @Test
    void testStateExpPlus() {
        boolean result = assertDoesNotThrow(() -> parse("+", "<state-exp>"));
        assertTrue(result);
        assertThat(parseContext.currentToken(), isA(Token.EOFToken.class));
        assertThat(rules, contains(
                        tupleOf(opToken("+"), rule("+")),
                        tupleOf(opToken("+"), rule("<plus-state>")),
                        tupleOf(opToken("+"), rule("<state-exp-opt>")),
                        tupleOf(opToken("+"), rule("<state-exp>"))
                )
        );
    }

    @ParameterizedTest
    @CsvSource({
            "a,Missing <primary-exp-opt> (a)",
            "'',Missing <primary-exp-opt> (<EOF>)",
    })
    void testStateLiteralError(String text, String expMsg) {
        ParseException ex = assertThrows(ParseException.class, () ->
                parse(text, "<state-exp>"));
        assertEquals(expMsg, ex.getMessage());
    }
}