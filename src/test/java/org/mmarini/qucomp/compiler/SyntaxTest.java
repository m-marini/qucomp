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
import org.junit.jupiter.params.provider.ValueSource;
import org.mmarini.Tuple2;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mmarini.Matchers.tupleOf;
import static org.mmarini.qucomp.Matchers.*;

class SyntaxTest {

    List<Tuple2<Token, SyntaxRule>> rules;
    private ParseContext parseContext;

    boolean parse(String text, String ruleId) throws IOException {
        Tokenizer tokenizer = Tokenizer.create(text).open();
        SyntaxRule rule = Syntax.rule(ruleId);
        assertNotNull(rule);
        this.parseContext = new ParseContext() {
            @Override
            public Token currentToken() {
                return tokenizer.currentToken();
            }

            @Override
            public void join(Token token, SyntaxRule rule) {
                rules.add(Tuple2.of(token, rule));
            }

            @Override
            public void popToken() throws IOException {
                tokenizer.popToken();
            }

        };
        return rule.parse(parseContext);
    }

    @BeforeEach
    void setUp() {
        this.rules = new ArrayList<>();
    }

    @Test
    void testAddMinus() {
        boolean result = assertDoesNotThrow(() -> parse("1 - 2", "<add-exp>"));
        assertTrue(result);
        assertThat(parseContext.currentToken(), isA(Token.EOFToken.class));
        assertThat(rules, contains(
                tupleOf(intToken(1), rule("<int-literal>")),
                tupleOf(intToken(1), rule("<primary-exp>")),
                tupleOf(intToken(1), rule("<conj>")), // 4
                tupleOf(intToken(1), rule("<unary-exp>")),

                tupleOf(intToken(1), rule("<cross-exp>")),
                tupleOf(intToken(1), rule("<multiply-exp>")),
                tupleOf(opToken("-"), rule("-")), // 8
                tupleOf(intToken(2), rule("<int-literal>")),

                tupleOf(intToken(2), rule("<primary-exp>")),
                tupleOf(intToken(2), rule("<conj>")),
                tupleOf(intToken(2), rule("<unary-exp>")),
                tupleOf(intToken(2), rule("<cross-exp>")),

                tupleOf(intToken(2), rule("<multiply-exp>")), // 16
                tupleOf(opToken("-"), rule("<minus-tail>")),
                tupleOf(opToken("-"), rule("<add-tail-opt>")),
                tupleOf(intToken(1), rule("<add-exp>"))
        ));
    }

    @Test
    void testAddPlus() {
        boolean result = assertDoesNotThrow(() -> parse("1 + 2", "<add-exp>"));
        assertTrue(result);
        assertThat(parseContext.currentToken(), isA(Token.EOFToken.class));
        assertThat(rules, contains(
                tupleOf(intToken(1), rule("<int-literal>")),
                tupleOf(intToken(1), rule("<primary-exp>")),
                tupleOf(intToken(1), rule("<conj>")), // 4
                tupleOf(intToken(1), rule("<unary-exp>")),

                tupleOf(intToken(1), rule("<cross-exp>")),
                tupleOf(intToken(1), rule("<multiply-exp>")),
                tupleOf(opToken("+"), rule("+")), // 8
                tupleOf(intToken(2), rule("<int-literal>")),

                tupleOf(intToken(2), rule("<primary-exp>")),
                tupleOf(intToken(2), rule("<conj>")),
                tupleOf(intToken(2), rule("<unary-exp>")),
                tupleOf(intToken(2), rule("<cross-exp>")),

                tupleOf(intToken(2), rule("<multiply-exp>")), // 16
                tupleOf(opToken("+"), rule("<plus-tail>")),
                tupleOf(opToken("+"), rule("<add-tail-opt>")),
                tupleOf(intToken(1), rule("<add-exp>"))
        ));
    }

    @Test
    void testAddPlusMinus() {
        boolean result = assertDoesNotThrow(() -> parse("1 + 2 - 3", "<add-exp>"));
        assertTrue(result);
        assertThat(parseContext.currentToken(), isA(Token.EOFToken.class));
        assertThat(rules, contains(
                tupleOf(intToken(1), rule("<int-literal>")),
                tupleOf(intToken(1), rule("<primary-exp>")),
                tupleOf(intToken(1), rule("<conj>")), // 4
                tupleOf(intToken(1), rule("<unary-exp>")),

                tupleOf(intToken(1), rule("<cross-exp>")),
                tupleOf(intToken(1), rule("<multiply-exp>")),
                tupleOf(opToken("+"), rule("+")), // 8
                tupleOf(intToken(2), rule("<int-literal>")),

                tupleOf(intToken(2), rule("<primary-exp>")),
                tupleOf(intToken(2), rule("<conj>")), // 12
                tupleOf(intToken(2), rule("<unary-exp>")),
                tupleOf(intToken(2), rule("<cross-exp>")),

                tupleOf(intToken(2), rule("<multiply-exp>")),
                tupleOf(opToken("+"), rule("<plus-tail>")), //16
                tupleOf(opToken("+"), rule("<add-tail-opt>")),
                tupleOf(opToken("-"), rule("-")),

                tupleOf(intToken(3), rule("<int-literal>")),
                tupleOf(intToken(3), rule("<primary-exp>")),
                tupleOf(intToken(3), rule("<conj>")),
                tupleOf(intToken(3), rule("<unary-exp>")),

                tupleOf(intToken(3), rule("<cross-exp>")), // 24
                tupleOf(intToken(3), rule("<multiply-exp>")),
                tupleOf(opToken("-"), rule("<minus-tail>")),
                tupleOf(opToken("-"), rule("<add-tail-opt>")),

                tupleOf(intToken(1), rule("<add-exp>"))
        ));
    }

    @Test
    void testAssign() {
        boolean result = assertDoesNotThrow(() -> parse("let a = 1", "<assign-stm>"));
        assertTrue(result);
        assertThat(parseContext.currentToken(), isA(Token.EOFToken.class));
        assertThat(rules, contains(
                tupleOf(idToken("let"), rule("let")),
                tupleOf(idToken("a"), rule("<assign-var-identifier>")),
                tupleOf(opToken("="), rule("=")),
                tupleOf(intToken(1), rule("<int-literal>")),

                tupleOf(intToken(1), rule("<primary-exp>")),
                tupleOf(intToken(1), rule("<conj>")),
                tupleOf(intToken(1), rule("<unary-exp>")),
                tupleOf(intToken(1), rule("<cross-exp>")),

                tupleOf(intToken(1), rule("<multiply-exp>")),
                tupleOf(intToken(1), rule("<add-exp>")),
                tupleOf(intToken(1), rule("<exp-opt>")),
                tupleOf(intToken(1), rule("<exp>")),

                tupleOf(idToken("let"), rule("<assign-stm>"))
        ));
    }

    @ParameterizedTest
    @CsvSource({
            "let ,Missing <assign-var-identifier>",
            "let a ,Missing =",
            "let a = ,Missing <exp-opt>",
    })
    void testAssignError(String text, String expMsg) {
        QuParseException ex = assertThrows(QuParseException.class, () ->
                parse(text, "<assign-stm>"));
        assertEquals(expMsg, ex.getMessage());
    }

    @Test
    void testBra() {
        boolean result = assertDoesNotThrow(() -> parse("<+|", "<bra>"));
        assertTrue(result);
        assertThat(parseContext.currentToken(), isA(Token.EOFToken.class));
        assertThat(rules, contains(
                tupleOf(opToken("<"), rule("<")),
                tupleOf(opToken("+"), rule("+")),
                tupleOf(opToken("+"), rule("<plus-state>")),
                tupleOf(opToken("+"), rule("<state-exp-opt>")),
                tupleOf(opToken("+"), rule("<state-exp>")),
                tupleOf(opToken("|"), rule("|")),
                tupleOf(opToken("<"), rule("<bra>"))
        ));
    }

    @ParameterizedTest
    @CsvSource({
            "<$|,Missing <exp-opt>",
            "<,Missing <exp-opt>",
            "<0,Missing |",
    })
    void testBraError(String text, String expMsg) {
        QuException ex = assertThrows(QuException.class, () ->
                parse(text, "<bra>"));
        assertEquals(expMsg, ex.getMessage());
    }

    @Test
    void testClear() {
        boolean result = assertDoesNotThrow(() -> parse("clear()", "<clear-stm>"));
        assertTrue(result);
        assertThat(parseContext.currentToken(), isA(Token.EOFToken.class));
        assertThat(rules, contains(
                tupleOf(idToken("clear"), rule("clear")),
                tupleOf(opToken("("), rule("(")),
                tupleOf(opToken(")"), rule(")")),
                tupleOf(idToken("clear"), rule("<clear-stm>"))
        ));
    }

    @ParameterizedTest
    @CsvSource({
            "clear,Missing (",
            "clear (,Missing )",
    })
    void testClearError(String text, String expMsg) {
        QuParseException ex = assertThrows(QuParseException.class, () ->
                parse(text, "<clear-stm>"));
        assertEquals(expMsg, ex.getMessage());
    }

    @Test
    void testCodeUnit() {
        boolean result = assertDoesNotThrow(() -> parse("clear(); clear();", "<code-unit>"));
        assertTrue(result);
        assertThat(parseContext.currentToken(), isA(Token.EOFToken.class));
        assertThat(rules, contains(
                tupleOf(idToken("clear"), rule("<code-unit-head>")),
                tupleOf(idToken("clear"), rule("clear")),
                tupleOf(opToken("("), rule("(")),
                tupleOf(opToken(")"), rule(")")),

                tupleOf(idToken("clear"), rule("<clear-stm>")),
                tupleOf(idToken("clear"), rule("<stm-opt>")),
                tupleOf(opToken(";"), rule(";")),
                tupleOf(idToken("clear"), rule("<stm>")),

                tupleOf(idToken("clear"), rule("clear")),
                tupleOf(opToken("("), rule("(")),
                tupleOf(opToken(")"), rule(")")),
                tupleOf(idToken("clear"), rule("<clear-stm>")),

                tupleOf(idToken("clear"), rule("<stm-opt>")),
                tupleOf(opToken(";"), rule(";")),
                tupleOf(idToken("clear"), rule("<stm>")),
                tupleOf(isA(Token.EOFToken.class), rule("<eof>")),
                tupleOf(idToken("clear"), rule("<code-unit>"))
        ));
    }

    @Test
    void testConj() {
        boolean result = assertDoesNotThrow(() -> parse("1^^", "<conj>"));
        assertTrue(result);
        assertThat(rules, contains(
                tupleOf(intToken(1), rule("<int-literal>")),
                tupleOf(intToken(1), rule("<primary-exp>")),
                tupleOf(opToken("^"), rule("^")),
                tupleOf(opToken("^"), rule("^")),

                tupleOf(intToken(1), rule("<conj>"))
        ));
        assertThat(parseContext.currentToken(), isA(Token.EOFToken.class));
    }

    @Test
    void testCrossxx() {
        boolean result = assertDoesNotThrow(() -> parse("1 x 2 x 3", "<cross-exp>"));
        assertTrue(result);
        assertThat(parseContext.currentToken(), isA(Token.EOFToken.class));
        assertThat(rules, contains(
                tupleOf(intToken(1), rule("<int-literal>")),
                tupleOf(intToken(1), rule("<primary-exp>")),
                tupleOf(intToken(1), rule("<conj>")),
                tupleOf(intToken(1), rule("<unary-exp>")), // 4

                tupleOf(idToken("x"), rule("x")),
                tupleOf(intToken(2), rule("<int-literal>")),
                tupleOf(intToken(2), rule("<primary-exp>")),
                tupleOf(intToken(2), rule("<conj>")),

                tupleOf(intToken(2), rule("<unary-exp>")),
                tupleOf(idToken("x"), rule("<cross-tail-opt>")),
                tupleOf(idToken("x"), rule("x")),
                tupleOf(intToken(3), rule("<int-literal>")), // 16

                tupleOf(intToken(3), rule("<primary-exp>")),
                tupleOf(intToken(3), rule("<conj>")),
                tupleOf(intToken(3), rule("<unary-exp>")), // 20
                tupleOf(idToken("x"), rule("<cross-tail-opt>")),

                tupleOf(intToken(1), rule("<cross-exp>"))
        ));
    }

    @Test
    void testE() {
        boolean result = assertDoesNotThrow(() -> parse("e", "<primary-exp>"));
        assertTrue(result);
        assertThat(parseContext.currentToken(), isA(Token.EOFToken.class));
        assertThat(rules, contains(
                tupleOf(idToken("e"), rule("e")),
                tupleOf(idToken("e"), rule("<primary-exp>"))
        ));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "<primary-exp>",
            "<conj>",
            "<unary-exp>",
            "<cross-exp>",
            "<multiply-exp>",
            "<add-exp>",
            "<exp-opt>",
    })
    void testEmptyExp(String rule) {
        boolean result = assertDoesNotThrow(() -> parse("  ;", rule));
        assertFalse(result);
        assertThat(parseContext.currentToken(), opToken(";"));
        assertThat(rules, empty());
    }

    @ParameterizedTest
    @CsvSource({
            "1,Missing ;",
    })
    void testExpStmError(String text, String expMsg) {
        QuParseException ex = assertThrows(QuParseException.class, () ->
                parse(text, "<stm>"));
        assertEquals(expMsg, ex.getMessage());
    }

    @Test
    void testFunction0() {
        boolean result = assertDoesNotThrow(() -> parse("sqrt()", "<function>"));
        assertTrue(result);
        assertThat(parseContext.currentToken(), isA(Token.EOFToken.class));
        assertThat(rules, contains(
                tupleOf(idToken("sqrt"), rule("<function-id>")),
                tupleOf(opToken("("), rule("(")),
                tupleOf(opToken(")"), rule("<empty-arg>")),
                tupleOf(opToken(")"), rule("<arg-list>")),

                tupleOf(opToken(")"), rule(")")),
                tupleOf(opToken("("), rule("<args-exp>")),
                tupleOf(idToken("sqrt"), rule("<function>"))
        ));
    }

    @Test
    void testFunction1() {
        boolean result = assertDoesNotThrow(() -> parse("sqrt(1)", "<function>"));
        assertTrue(result);
        assertThat(parseContext.currentToken(), isA(Token.EOFToken.class));
        assertThat(rules, contains(
                tupleOf(idToken("sqrt"), rule("<function-id>")),
                tupleOf(opToken("("), rule("(")),
                tupleOf(intToken(1), rule("<int-literal>")),
                tupleOf(intToken(1), rule("<primary-exp>")),

                tupleOf(intToken(1), rule("<conj>")),
                tupleOf(intToken(1), rule("<unary-exp>")),
                tupleOf(intToken(1), rule("<cross-exp>")),
                tupleOf(intToken(1), rule("<multiply-exp>")),

                tupleOf(intToken(1), rule("<add-exp>")),
                tupleOf(intToken(1), rule("<exp-opt>")),
                tupleOf(intToken(1), rule("<arg>")),
                tupleOf(intToken(1), rule("<arg-list-opt>")),
                tupleOf(intToken(1), rule("<arg-list>")),
                tupleOf(opToken(")"), rule(")")),

                tupleOf(opToken("("), rule("<args-exp>")),
                tupleOf(idToken("sqrt"), rule("<function>"))
        ));
    }

    @Test
    void testFunction2() {
        boolean result = assertDoesNotThrow(() -> parse("sqrt(1, 2)", "<function>"));
        assertTrue(result);
        assertThat(parseContext.currentToken(), isA(Token.EOFToken.class));
        assertThat(rules, contains(
                tupleOf(idToken("sqrt"), rule("<function-id>")),
                tupleOf(opToken("("), rule("(")),
                tupleOf(intToken(1), rule("<int-literal>")),
                tupleOf(intToken(1), rule("<primary-exp>")),

                tupleOf(intToken(1), rule("<conj>")),
                tupleOf(intToken(1), rule("<unary-exp>")),
                tupleOf(intToken(1), rule("<cross-exp>")),
                tupleOf(intToken(1), rule("<multiply-exp>")),

                tupleOf(intToken(1), rule("<add-exp>")),
                tupleOf(intToken(1), rule("<exp-opt>")),
                tupleOf(intToken(1), rule("<arg>")),
                tupleOf(opToken(","), rule(",")),

                tupleOf(intToken(2), rule("<int-literal>")),
                tupleOf(intToken(2), rule("<primary-exp>")),
                tupleOf(intToken(2), rule("<conj>")),
                tupleOf(intToken(2), rule("<unary-exp>")),

                tupleOf(intToken(2), rule("<cross-exp>")),
                tupleOf(intToken(2), rule("<multiply-exp>")),
                tupleOf(intToken(2), rule("<add-exp>")),
                tupleOf(intToken(2), rule("<exp-opt>")),

                tupleOf(intToken(2), rule("<exp>")),
                tupleOf(opToken(","), rule("<arg-tail>")),
                tupleOf(intToken(1), rule("<arg-list-opt>")),
                tupleOf(intToken(1), rule("<arg-list>")),

                tupleOf(opToken(")"), rule(")")),
                tupleOf(opToken("("), rule("<args-exp>")),
                tupleOf(idToken("sqrt"), rule("<function>"))
        ));
    }

    @ParameterizedTest
    @CsvSource({
            "sqrt,Missing (",
            "sqrt( ,Missing )",
            "sqrt(1,Missing )",
            "'sqrt(1,' ,Missing <exp-opt>",
    })
    void testFunctionError(String text, String expMsg) {
        QuParseException ex = assertThrows(QuParseException.class, () ->
                parse(text, "<function>"));
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

    @Test
    void testKet() {
        boolean result = assertDoesNotThrow(() -> parse("|+>", "<ket>"));
        assertTrue(result);
        assertThat(parseContext.currentToken(), isA(Token.EOFToken.class));
        assertThat(rules, contains(
                tupleOf(opToken("|"), rule("|")),
                tupleOf(opToken("+"), rule("+")),
                tupleOf(opToken("+"), rule("<plus-state>")),
                tupleOf(opToken("+"), rule("<state-exp-opt>")),
                tupleOf(opToken("+"), rule("<state-exp>")),
                tupleOf(opToken(">"), rule(">")),
                tupleOf(opToken("|"), rule("<ket>"))
        ));
    }

    @ParameterizedTest
    @CsvSource({
            "|/>,Missing <exp-opt>",
            "|,Missing <exp-opt>",
            "|0,Missing >",
    })
    void testKetError(String text, String expMsg) {
        QuException ex = assertThrows(QuException.class, () ->
                parse(text, "<ket>"));
        assertEquals(expMsg, ex.getMessage());
    }

    @Test
    void testMulDiv() {
        boolean result = assertDoesNotThrow(() -> parse("1 / 2", "<multiply-exp>"));
        assertTrue(result);
        assertThat(parseContext.currentToken(), isA(Token.EOFToken.class));
        assertThat(rules, contains(
                tupleOf(intToken(1), rule("<int-literal>")),
                tupleOf(intToken(1), rule("<primary-exp>")),
                tupleOf(intToken(1), rule("<conj>")),
                tupleOf(intToken(1), rule("<unary-exp>")), // 4

                tupleOf(intToken(1), rule("<cross-exp>")),
                tupleOf(opToken("/"), rule("/")),
                tupleOf(intToken(2), rule("<int-literal>")),
                tupleOf(intToken(2), rule("<primary-exp>")),

                tupleOf(intToken(2), rule("<conj>")),
                tupleOf(intToken(2), rule("<unary-exp>")),
                tupleOf(intToken(2), rule("<cross-exp>")), // 12
                tupleOf(opToken("/"), rule("<divide-tail>")),

                tupleOf(opToken("/"), rule("<mul-tail-opt>")),
                tupleOf(intToken(1), rule("<multiply-exp>"))
        ));
    }

    @Test
    void testMulMul() {
        boolean result = assertDoesNotThrow(() -> parse("1 * 2", "<multiply-exp>"));
        assertTrue(result);
        assertThat(parseContext.currentToken(), isA(Token.EOFToken.class));
        assertThat(rules, contains(
                tupleOf(intToken(1), rule("<int-literal>")),
                tupleOf(intToken(1), rule("<primary-exp>")),
                tupleOf(intToken(1), rule("<conj>")),
                tupleOf(intToken(1), rule("<unary-exp>")), // 4

                tupleOf(intToken(1), rule("<cross-exp>")),
                tupleOf(opToken("*"), rule("*")),
                tupleOf(intToken(2), rule("<int-literal>")),
                tupleOf(intToken(2), rule("<primary-exp>")),

                tupleOf(intToken(2), rule("<conj>")),
                tupleOf(intToken(2), rule("<unary-exp>")),
                tupleOf(intToken(2), rule("<cross-exp>")), // 12
                tupleOf(opToken("*"), rule("<multiply-tail>")),

                tupleOf(opToken("*"), rule("<mul-tail-opt>")),
                tupleOf(intToken(1), rule("<multiply-exp>"))
        ));
    }


    @Test
    void testMulMul0() {
        boolean result = assertDoesNotThrow(() -> parse("1 . 2", "<multiply-exp>"));
        assertTrue(result);
        assertThat(parseContext.currentToken(), isA(Token.EOFToken.class));
        assertThat(rules, contains(
                tupleOf(intToken(1), rule("<int-literal>")),
                tupleOf(intToken(1), rule("<primary-exp>")),
                tupleOf(intToken(1), rule("<conj>")),
                tupleOf(intToken(1), rule("<unary-exp>")), // 4

                tupleOf(intToken(1), rule("<cross-exp>")),
                tupleOf(opToken("."), rule(".")),
                tupleOf(intToken(2), rule("<int-literal>")),
                tupleOf(intToken(2), rule("<primary-exp>")),

                tupleOf(intToken(2), rule("<conj>")),
                tupleOf(intToken(2), rule("<unary-exp>")),
                tupleOf(intToken(2), rule("<cross-exp>")), // 12
                tupleOf(opToken("."), rule("<multiply0-tail>")),

                tupleOf(opToken("."), rule("<mul-tail-opt>")),
                tupleOf(intToken(1), rule("<multiply-exp>"))
        ));
    }

    @Test
    void testMulMulDiv() {
        boolean result = assertDoesNotThrow(() -> parse("1 * 2 / 3", "<multiply-exp>"));
        assertTrue(result);
        assertThat(parseContext.currentToken(), isA(Token.EOFToken.class));
        assertThat(rules, contains(
                tupleOf(intToken(1), rule("<int-literal>")),
                tupleOf(intToken(1), rule("<primary-exp>")),
                tupleOf(intToken(1), rule("<conj>")),
                tupleOf(intToken(1), rule("<unary-exp>")), // 4

                tupleOf(intToken(1), rule("<cross-exp>")),
                tupleOf(opToken("*"), rule("*")),
                tupleOf(intToken(2), rule("<int-literal>")),
                tupleOf(intToken(2), rule("<primary-exp>")),

                tupleOf(intToken(2), rule("<conj>")),
                tupleOf(intToken(2), rule("<unary-exp>")),
                tupleOf(intToken(2), rule("<cross-exp>")), // 12
                tupleOf(opToken("*"), rule("<multiply-tail>")),

                tupleOf(opToken("*"), rule("<mul-tail-opt>")),
                tupleOf(opToken("/"), rule("/")),
                tupleOf(intToken(3), rule("<int-literal>")), // 16
                tupleOf(intToken(3), rule("<primary-exp>")),

                tupleOf(intToken(3), rule("<conj>")),
                tupleOf(intToken(3), rule("<unary-exp>")), // 20
                tupleOf(intToken(3), rule("<cross-exp>")),
                tupleOf(opToken("/"), rule("<divide-tail>")),

                tupleOf(opToken("/"), rule("<mul-tail-opt>")),
                tupleOf(intToken(1), rule("<multiply-exp>"))
        ));
    }

    @Test
    void testPi() {
        boolean result = assertDoesNotThrow(() -> parse("pi", "<primary-exp>"));
        assertTrue(result);
        assertThat(parseContext.currentToken(), isA(Token.EOFToken.class));
        assertThat(rules, contains(
                tupleOf(idToken("pi"), rule("pi")),
                tupleOf(idToken("pi"), rule("<primary-exp>"))
        ));
    }

    @ParameterizedTest
    @CsvSource({
            "(,Missing <exp-opt>",
            "(1,Missing )",
    })
    void testPrimaryExpError(String text, String expMsg) {
        QuParseException ex = assertThrows(QuParseException.class, () ->
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
                tupleOf(intToken(1), rule("<primary-exp>")),
                tupleOf(intToken(1), rule("<conj>")),

                tupleOf(intToken(1), rule("<unary-exp>")),
                tupleOf(intToken(1), rule("<cross-exp>")),
                tupleOf(intToken(1), rule("<multiply-exp>")),
                tupleOf(intToken(1), rule("<add-exp>")), // 8

                tupleOf(intToken(1), rule("<exp-opt>")),
                tupleOf(intToken(1), rule("<exp>")),
                tupleOf(opToken(")"), rule(")")),
                tupleOf(opToken("("), rule("<priority-exp>"))
        ));
    }

    @ParameterizedTest
    @CsvSource({
            "(,Missing <exp-opt>",
            "(1,Missing )",
    })
    void testPriorityExpError(String text, String expMsg) {
        QuParseException ex = assertThrows(QuParseException.class, () ->
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
                        tupleOf(intToken(tokenValue), rule("<primary-exp>")),
                        tupleOf(intToken(tokenValue), rule("<conj>")),
                        tupleOf(intToken(tokenValue), rule("<unary-exp>")),

                tupleOf(intToken(tokenValue), rule("<cross-exp>")),
                        tupleOf(intToken(tokenValue), rule("<multiply-exp>")),
                        tupleOf(intToken(tokenValue), rule("<add-exp>")),
                        tupleOf(intToken(tokenValue), rule("<exp-opt>")),

                tupleOf(intToken(tokenValue), rule("<int-state>")),
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
            ">,Missing <exp-opt>",
            "'',Missing <exp-opt>",
    })
    void testStateLiteralError(String text, String expMsg) {
        QuException ex = assertThrows(QuException.class, () ->
                parse(text, "<state-exp>"));
        assertEquals(expMsg, ex.getMessage());
    }

    @Test
    void testUnaryMinusMinus() {
        boolean result = assertDoesNotThrow(() -> parse("--1", "<unary-exp>"));
        assertTrue(result);
        assertThat(parseContext.currentToken(), isA(Token.EOFToken.class));
        assertThat(rules, contains(
                tupleOf(opToken("-"), rule("-")),
                tupleOf(opToken("-"), rule("-")),
                tupleOf(intToken(1), rule("<int-literal>")),
                tupleOf(intToken(1), rule("<primary-exp>")), // 4

                tupleOf(intToken(1), rule("<conj>")),
                tupleOf(intToken(1), rule("<unary-exp>")),
                tupleOf(opToken("-"), rule("<negate-exp>")), // 8
                tupleOf(opToken("-"), rule("<unary-exp>")),

                tupleOf(opToken("-"), rule("<negate-exp>")),
                tupleOf(opToken("-"), rule("<unary-exp>"))
        ));
    }

    @Test
    void testUnaryPlusPlus() {
        boolean result = assertDoesNotThrow(() -> parse("++1", "<unary-exp>"));
        assertTrue(result);
        assertThat(parseContext.currentToken(), isA(Token.EOFToken.class));
        assertThat(rules, contains(
                tupleOf(opToken("+"), rule("+")),
                tupleOf(opToken("+"), rule("+")),
                tupleOf(intToken(1), rule("<int-literal>")),
                tupleOf(intToken(1), rule("<primary-exp>")), // 4

                tupleOf(intToken(1), rule("<conj>")),
                tupleOf(intToken(1), rule("<unary-exp>")),
                tupleOf(opToken("+"), rule("<plus-exp>")), // 8
                tupleOf(opToken("+"), rule("<unary-exp>")),

                tupleOf(opToken("+"), rule("<plus-exp>")),
                tupleOf(opToken("+"), rule("<unary-exp>"))
        ));
    }

    @Test
    void testVarExp() {
        boolean result = assertDoesNotThrow(() -> parse("a", "<var-identifier>"));
        assertTrue(result);
        assertThat(parseContext.currentToken(), isA(Token.EOFToken.class));
        assertThat(rules, contains(
                tupleOf(idToken("a"), rule("<var-identifier>"))
        ));
    }

    @Test
    void textCross() {
        boolean result = assertDoesNotThrow(() -> parse("1 x 2", "<cross-exp>"));
        assertTrue(result);
        assertThat(parseContext.currentToken(), isA(Token.EOFToken.class));
        assertThat(rules, contains(
                tupleOf(intToken(1), rule("<int-literal>")),
                tupleOf(intToken(1), rule("<primary-exp>")),
                tupleOf(intToken(1), rule("<conj>")),
                tupleOf(intToken(1), rule("<unary-exp>")), // 4

                tupleOf(idToken("x"), rule("x")),
                tupleOf(intToken(2), rule("<int-literal>")),
                tupleOf(intToken(2), rule("<primary-exp>")), // 8
                tupleOf(intToken(2), rule("<conj>")),

                tupleOf(intToken(2), rule("<unary-exp>")),
                tupleOf(idToken("x"), rule("<cross-tail-opt>")),
                tupleOf(intToken(1), rule("<cross-exp>"))
        ));
    }
}