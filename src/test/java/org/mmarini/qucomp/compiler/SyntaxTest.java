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

import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.mmarini.qucomp.apis.Complex;
import org.mmarini.qucomp.apis.Ket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.isA;
import static org.junit.jupiter.api.Assertions.*;
import static org.mmarini.qucomp.compiler.Syntax.*;
import static org.mmarini.qucomp.compiler.TerminalExp.*;

class SyntaxTest {

    public static Stream<Arguments> argsOptBra() {
        return argsStateLiteral().map(
                args -> {
                    Object[] argsAry = args.get();
                    String text = argsAry[0].toString();
                    Object[] newArgs = Arrays.copyOf(argsAry, argsAry.length);
                    newArgs[0] = "<" + text + "|";
                    return Arguments.arguments(newArgs);
                }
        );
    }

    public static Stream<Arguments> argsOptKet() {
        return argsStateLiteral().map(
                args -> {
                    Object[] argsAry = args.get();
                    String text = argsAry[0].toString();
                    Object[] newArgs = Arrays.copyOf(argsAry, argsAry.length);
                    newArgs[0] = "|" + text + ">";
                    return Arguments.arguments(newArgs);
                }
        );
    }

    public static Stream<Arguments> argsStateLiteral() {
        return Stream.of(
                Arguments.of("+", Ket.plus()),
                Arguments.of("-", Ket.minus()),
                Arguments.of("i", Ket.i()),
                Arguments.of("-i", Ket.minus_i()),
                Arguments.of("0", Ket.zero()),
                Arguments.of("1", Ket.one()),
                Arguments.of("2", Ket.base(2, 2)),
                Arguments.of("3", Ket.base(3, 2)),
                Arguments.of("4", Ket.base(4, 3)),
                Arguments.of("7", Ket.base(7, 3))
        );
    }

    ParseContext parseContext;
    List<Command> code;

    private boolean parse(String text, Expression syntax) throws Throwable {
        BufferedReader reader = new BufferedReader(new StringReader(text != null ? text : ""));
        Tokenizer tokenizer = new Tokenizer(reader).open();
        this.parseContext = new ParseContext() {
            @Override
            public void add(Command command) {
                code.add(command);
            }

            @Override
            public Token currentToken() {
                return tokenizer.currentToken();
            }

            @Override
            public void popToken() throws IOException {
                tokenizer.popToken();
            }
        };
        return syntax.test(parseContext);
    }

    @BeforeEach
    void setUp() {
        this.code = new ArrayList<>();
    }

    @ParameterizedTest
    @CsvSource({
            "1. + 2. + 3., 1,0, 2,0, 3,0",
            "1. + 2. + i, 1,0, 2,0, 0,1",
            "1. + i + 3., 1,0, 0,1, 3,0",
            "i + 2. + 3., 0,1, 2,0, 3,0",
    })
    void testAddComplexComplexComplex(String text, float expReal1, float expIm1, float expReal2, float expIm2, float expReal3, float expIm3) throws Throwable {
        Complex exp1 = new Complex(expReal1, expIm1);
        Complex exp2 = new Complex(expReal2, expIm2);
        Complex exp3 = new Complex(expReal3, expIm3);
        parse(text, sumExp);
        assertThat(parseContext.currentToken(), Matchers.isA(Token.EOFToken.class));
        assertThat(code, contains(
                isA(Command.PushComplex.class),
                isA(Command.PushComplex.class),
                isA(Command.Add.class),
                isA(Command.PushComplex.class),
                isA(Command.Add.class)
        ));
        assertEquals(exp1, ((Command.PushComplex) code.getFirst()).value());
        assertEquals(exp2, ((Command.PushComplex) code.get(1)).value());
        assertEquals(exp3, ((Command.PushComplex) code.get(3)).value());
    }

    @ParameterizedTest
    @CsvSource({
            "1. + (2. + 3.), 1,0, 2,0, 3,0",
            "1. + (2. + i), 1,0, 2,0, 0,1",
            "1. + (i + 3.), 1,0, 0,1, 3,0",
            "i + (2. + 3.), 0,1, 2,0, 3,0",
    })
    void testAddComplexComplexComplex2(String text, float expReal1, float expIm1, float expReal2, float expIm2, float expReal3, float expIm3) throws Throwable {
        Complex exp1 = new Complex(expReal1, expIm1);
        Complex exp2 = new Complex(expReal2, expIm2);
        Complex exp3 = new Complex(expReal3, expIm3);
        parse(text, sumExp);
        assertThat(parseContext.currentToken(), Matchers.isA(Token.EOFToken.class));
        assertThat(code, contains(
                isA(Command.PushComplex.class),
                isA(Command.PushComplex.class),
                isA(Command.PushComplex.class),
                isA(Command.Add.class),
                isA(Command.Add.class)
        ));
        assertEquals(exp1, ((Command.PushComplex) code.getFirst()).value());
        assertEquals(exp2, ((Command.PushComplex) code.get(1)).value());
        assertEquals(exp3, ((Command.PushComplex) code.get(2)).value());
    }

    @ParameterizedTest
    @CsvSource({
            "1., 1,0",
            "i, 0,1",
            "+1., 1,0",
            "+i, 0,1",
    })
    void testAddExpComplex(String text, float expReal1, float expIm1) throws Throwable {
        parse(text, sumExp);
        assertThat(parseContext.currentToken(), Matchers.isA(Token.EOFToken.class));
        assertThat(code, contains(
                isA(Command.PushComplex.class)
        ));
        assertEquals(new Complex(expReal1, expIm1), ((Command.PushComplex) code.getFirst()).value());
    }

    @ParameterizedTest
    @CsvSource({
            "1. + 2., 1,0, 2,0",
            "2. + 1., 2,0, 1,0",
            "1. + i, 1,0, 0,1",
            "i + 1., 0,1, 1,0",
            "i + i, 0,1, 0,1",
            "+1. + +2., 1,0, 2,0",
            "+2. + +1., 2,0, 1,0",
            "+1. + +i, 1,0, 0,1",
            "+i + +1., 0,1, 1,0",
            "+i + +i, 0,1, 0,1"
    })
    void testAddExpComplexComplex(String text, float expReal1, float expIm1, float expReal2, float expIm2) throws Throwable {
        Complex exp1 = new Complex(expReal1, expIm1);
        Complex exp2 = new Complex(expReal2, expIm2);
        parse(text, sumExp);
        assertThat(parseContext.currentToken(), Matchers.isA(Token.EOFToken.class));
        assertThat(code, contains(
                isA(Command.PushComplex.class),
                isA(Command.PushComplex.class),
                isA(Command.Add.class)
        ));
        assertEquals(exp1, ((Command.PushComplex) code.getFirst()).value());
        assertEquals(exp2, ((Command.PushComplex) code.get(1)).value());
    }

    @ParameterizedTest
    @CsvSource({
            "let a=1.;,a,1",
    })
    void testAssignVar(String text, String expId, float expValue) throws Throwable {
        assertTrue(parse(text, optAssignExp));
        assertThat(parseContext.currentToken(), Matchers.isA(Token.EOFToken.class));
        assertThat(code, contains(
                isA(Command.PushString.class),
                isA(Command.PushComplex.class),
                isA(Command.Assign.class),
                isA(Command.Consume.class)
        ));
        assertEquals(expId, ((Command.PushString) code.getFirst()).value());
        assertEquals(Complex.create(expValue), ((Command.PushComplex) code.get(1)).value());
    }

    @ParameterizedTest
    @CsvSource({
            "let x=1;,x is a reserved keyword (x)",
            "let 3=3;,Missing variable identifier (3)",
            "let a 1,Missing token = (1)",
            "let a=1,Missing token ; (<EOF>)"
    })
    void testAssignVarError(String text, String expMsg) {
        ParseException ex = assertThrows(ParseException.class, () ->
                parse(text, optAssignExp));
        assertEquals(expMsg, ex.getMessage());
    }

    @ParameterizedTest
    @CsvSource({
            "<a|,Missing state literal (a)",
            "<,Missing state literal (<EOF>)",
            "<0,Missing token | (<EOF>)",
    })
    void testBraError(String text, String expMsg) {
        ParseException ex = assertThrows(ParseException.class, () ->
                parse(text, optBra));
        assertEquals(expMsg, ex.getMessage());
    }

    @ParameterizedTest
    @CsvSource({
            "let a = 1; a;, a, 1",
    })
    void testCodeUnit(String text, String expId, int expInt) throws Throwable {
        assertTrue(parse(text, codeUnitExp));
        assertThat(parseContext.currentToken(), Matchers.isA(Token.EOFToken.class));
        assertThat(code, contains(
                isA(Command.PushString.class),
                isA(Command.PushInt.class),
                isA(Command.Assign.class),
                isA(Command.Consume.class),
                isA(Command.PushString.class),
                isA(Command.RetrieveVar.class),
                isA(Command.Consume.class)
        ));
        assertEquals(expId, ((Command.PushString) code.getFirst()).value());
        assertEquals(expInt, ((Command.PushInt) code.get(1)).value());
        assertEquals(expId, ((Command.PushString) code.get(4)).value());
    }

    @ParameterizedTest
    @CsvSource({
            "1. / 2., 1,0, 2,0",
            "2. / 1., 2,0, 1,0",
            "1. / i, 1,0, 0,1",
            "i / 1., 0,1, 1,0",
            "i / i, 0,1, 0,1",
            "+1. / +2., 1,0, 2,0",
            "+2. / +1., 2,0, 1,0",
            "+1. / +i, 1,0, 0,1",
            "+i / +1., 0,1, 1,0",
            "+i / +i, 0,1, 0,1"
    })
    void testDivExpComplexComplex(String text, float expReal1, float expIm1, float expReal2, float expIm2) throws Throwable {
        Complex exp1 = new Complex(expReal1, expIm1);
        Complex exp2 = new Complex(expReal2, expIm2);
        parse(text, prodExp);
        assertThat(parseContext.currentToken(), Matchers.isA(Token.EOFToken.class));
        assertThat(parseContext.currentToken(), Matchers.isA(Token.EOFToken.class));
        assertThat(code, contains(
                isA(Command.PushComplex.class),
                isA(Command.PushComplex.class),
                isA(Command.Divide.class)
        ));
        assertEquals(exp1, ((Command.PushComplex) code.getFirst()).value());
        assertEquals(exp2, ((Command.PushComplex) code.get(1)).value());
    }

    @ParameterizedTest
    @CsvSource({
            "1. / 2. / 3., 1,0, 2,0, 3,0",
            "1. / 2. / i, 1,0, 2,0, 0,1",
            "1. / i / 3., 1,0, 0,1, 3,0",
            "i / 2. / 3., 0,1, 2,0, 3,0",
    })
    void testDivExpComplexComplexComplex(String text, float expReal1, float expIm1, float expReal2, float expIm2, float expReal3, float expIm3) throws Throwable {
        Complex exp1 = new Complex(expReal1, expIm1);
        Complex exp2 = new Complex(expReal2, expIm2);
        Complex exp3 = new Complex(expReal3, expIm3);
        parse(text, prodExp);
        assertThat(parseContext.currentToken(), Matchers.isA(Token.EOFToken.class));
        assertThat(code, contains(
                isA(Command.PushComplex.class),
                isA(Command.PushComplex.class),
                isA(Command.Divide.class),
                isA(Command.PushComplex.class),
                isA(Command.Divide.class)
        ));
        assertEquals(exp1, ((Command.PushComplex) code.getFirst()).value());
        assertEquals(exp2, ((Command.PushComplex) code.get(1)).value());
        assertEquals(exp3, ((Command.PushComplex) code.get(3)).value());
    }

    @ParameterizedTest
    @CsvSource({
            "1. / (2. / 3.), 1,0, 2,0, 3,0",
    })
    void testDivExpComplexComplexComplex1(String text, float expReal1, float expIm1, float expReal2, float expIm2, float expReal3, float expIm3) throws Throwable {
        Complex exp1 = new Complex(expReal1, expIm1);
        Complex exp2 = new Complex(expReal2, expIm2);
        Complex exp3 = new Complex(expReal3, expIm3);
        parse(text, prodExp);
        assertThat(parseContext.currentToken(), Matchers.isA(Token.EOFToken.class));
        assertThat(code, contains(
                isA(Command.PushComplex.class),
                isA(Command.PushComplex.class),
                isA(Command.PushComplex.class),
                isA(Command.Divide.class),
                isA(Command.Divide.class)
        ));
        assertEquals(exp1, ((Command.PushComplex) code.getFirst()).value());
        assertEquals(exp2, ((Command.PushComplex) code.get(1)).value());
        assertEquals(exp3, ((Command.PushComplex) code.get(2)).value());
    }

    @ParameterizedTest
    @CsvSource({
            "|a>,Missing state literal (a)",
            "|,Missing state literal (<EOF>)",
            "|0,Missing token > (<EOF>)",
    })
    void testKetError(String text, String expMsg) {
        ParseException ex = assertThrows(ParseException.class, () ->
                parse(text, optKet));
        assertEquals(expMsg, ex.getMessage());
    }

    @ParameterizedTest
    @CsvSource({
            "1., 1,0",
            "i, 0,1",
            "+1., 1,0",
            "+i, 0,1",
    })
    void testMulExpComplex(String text, float expReal1, float expIm1) throws Throwable {
        parse(text, prodExp);
        assertThat(parseContext.currentToken(), Matchers.isA(Token.EOFToken.class));
        assertThat(code, contains(
                isA(Command.PushComplex.class)
        ));
        assertEquals(new Complex(expReal1, expIm1), ((Command.PushComplex) code.getFirst()).value());
    }

    @ParameterizedTest
    @CsvSource({
            "1. * 2., 1,0, 2,0",
            "2. * 1., 2,0, 1,0",
            "1. * i, 1,0, 0,1",
            "i * 1., 0,1, 1,0",
            "i * i, 0,1, 0,1",
            "+1. * +2., 1,0, 2,0",
            "+2. * +1., 2,0, 1,0",
            "+1. * +i, 1,0, 0,1",
            "+i * +1., 0,1, 1,0",
            "+i * +i, 0,1, 0,1"
    })
    void testMulExpComplexComplex(String text, float expReal1, float expIm1, float expReal2, float expIm2) throws Throwable {
        Complex exp1 = new Complex(expReal1, expIm1);
        Complex exp2 = new Complex(expReal2, expIm2);
        parse(text, prodExp);
        assertThat(parseContext.currentToken(), Matchers.isA(Token.EOFToken.class));
        assertThat(code, contains(
                isA(Command.PushComplex.class),
                isA(Command.PushComplex.class),
                isA(Command.Multiply.class)
        ));
        assertEquals(exp1, ((Command.PushComplex) code.getFirst()).value());
        assertEquals(exp2, ((Command.PushComplex) code.get(1)).value());
    }

    @ParameterizedTest
    @CsvSource({
            "1. * 2. * 3., 1,0, 2,0, 3,0",
            "1. * 2. * i, 1,0, 2,0, 0,1",
            "1. * i * 3., 1,0, 0,1, 3,0",
            "i * 2. * 3., 0,1, 2,0, 3,0",
    })
    void testMulExpComplexComplexComplex(String text, float expReal1, float expIm1, float expReal2, float expIm2, float expReal3, float expIm3) throws Throwable {
        Complex exp1 = new Complex(expReal1, expIm1);
        Complex exp2 = new Complex(expReal2, expIm2);
        Complex exp3 = new Complex(expReal3, expIm3);
        parse(text, prodExp);
        assertThat(parseContext.currentToken(), Matchers.isA(Token.EOFToken.class));
        assertThat(code, contains(
                isA(Command.PushComplex.class),
                isA(Command.PushComplex.class),
                isA(Command.Multiply.class),
                isA(Command.PushComplex.class),
                isA(Command.Multiply.class)
        ));
        assertEquals(exp1, ((Command.PushComplex) code.getFirst()).value());
        assertEquals(exp2, ((Command.PushComplex) code.get(1)).value());
        assertEquals(exp3, ((Command.PushComplex) code.get(3)).value());
    }

    @ParameterizedTest
    @CsvSource({
            "1. * (2. * 3.), 1,0, 2,0, 3,0",
    })
    void testMulExpComplexComplexComplex1(String text, float expReal1, float expIm1, float expReal2, float expIm2, float expReal3, float expIm3) throws Throwable {
        Complex exp1 = new Complex(expReal1, expIm1);
        Complex exp2 = new Complex(expReal2, expIm2);
        Complex exp3 = new Complex(expReal3, expIm3);
        parse(text, prodExp);
        assertThat(parseContext.currentToken(), Matchers.isA(Token.EOFToken.class));
        assertThat(code, contains(
                isA(Command.PushComplex.class),
                isA(Command.PushComplex.class),
                isA(Command.PushComplex.class),
                isA(Command.Multiply.class),
                isA(Command.Multiply.class)
        ));
        assertEquals(exp1, ((Command.PushComplex) code.getFirst()).value());
        assertEquals(exp2, ((Command.PushComplex) code.get(1)).value());
        assertEquals(exp3, ((Command.PushComplex) code.get(2)).value());
    }

    @ParameterizedTest
    @MethodSource("argsOptBra")
    void testOptBra(String text, Ket ket) {
        boolean result = assertDoesNotThrow(() -> parse(text, optBra));
        assertTrue(result);
        assertThat(parseContext.currentToken(), isA(Token.EOFToken.class));
        assertThat(code, contains(
                isA(Command.PushKet.class),
                isA(Command.Conj.class)));
        assertEquals(ket, ((Command.PushKet) code.getFirst()).value());
    }

    @ParameterizedTest
    @CsvSource({
            "i,true",
            ",false",
            "*,false",
            "a,false",
            "1,false",
            "1.2,false",
    })
    void testOptIdentifier(String text, boolean exp) throws Throwable {
        TerminalExp expr = optIdentifier("i");
        assertEquals(exp, parse(text, expr));

    }

    @ParameterizedTest
    @CsvSource({
            "1234,1234",
    })
    void testOptIntLiteral(String text, int expValue) throws Throwable {
        assertTrue(parse(text, optIntLiteral));
        assertThat(parseContext.currentToken(), Matchers.isA(Token.EOFToken.class));
        assertThat(code, contains(isA(Command.PushInt.class)));
        assertEquals(expValue, ((Command.PushInt) code.getFirst()).value());
    }

    @ParameterizedTest
    @MethodSource("argsOptKet")
    void testOptKet(String text, Ket ket) {
        boolean result = assertDoesNotThrow(() -> parse(text, optKet));
        assertTrue(result);
        assertThat(parseContext.currentToken(), Matchers.isA(Token.EOFToken.class));
        assertThat(code, contains(isA(Command.PushKet.class)));
        assertEquals(ket, ((Command.PushKet) code.getFirst()).value());
    }

    @ParameterizedTest
    @CsvSource({
            "i,false",
            ",false",
            "*,true",
            "1,false",
            "1.2,false",
    })
    void testOptOperator(String text, boolean exp) throws Throwable {
        TerminalExp expr = optOp("*");
        assertEquals(exp, parse(text, expr));
    }

    @ParameterizedTest
    @CsvSource({
            "1234.5,1234.5",
            "1234.5e10,1234.5e10",
            "1234.5E10,1234.5E10",
            "1234.5e-10,1234.5e-10",
            "1234.5E-10,1234.5E-10",
            "1234.5e+10,1234.5e+10",
            "1234.5E+10,1234.5E+10",
    })
    void testOptRealLiteral(String text, float expValue) throws Throwable {
        assertTrue(parse(text, optRealLiteral));
        assertThat(parseContext.currentToken(), Matchers.isA(Token.EOFToken.class));
        assertThat(code, contains(isA(Command.PushComplex.class)));
        assertEquals(Complex.create(expValue), ((Command.PushComplex) code.getFirst()).value());
    }

    @ParameterizedTest
    @MethodSource("argsOptBra")
    void testPrimaryExpBra(String text, Ket ket) {
        boolean result = assertDoesNotThrow(() -> parse(text, primaryExp));
        assertTrue(result);
        assertThat(parseContext.currentToken(), Matchers.isA(Token.EOFToken.class));
        assertThat(code, contains(
                isA(Command.PushKet.class),
                isA(Command.Conj.class)));
        assertEquals(ket, ((Command.PushKet) code.getFirst()).value());
    }

    @ParameterizedTest
    @CsvSource({
            "i, 0,1",
            "3., 3,0",
            "( 3. ), 3,0",
    })
    void testPrimaryExpComplex(String text, float real, float im) throws Throwable {
        assertTrue(parse(text, primaryExp));
        assertThat(parseContext.currentToken(), Matchers.isA(Token.EOFToken.class));
        assertThat(code, contains(isA(Command.PushComplex.class)));
        assertEquals(new Complex(real, im), ((Command.PushComplex) code.getFirst()).value());
    }

    @Test
    void testConjExp() {
        boolean result = assertDoesNotThrow(() -> parse("1^", conjExp));
        assertTrue(result);
        assertThat(parseContext.currentToken(), Matchers.isA(Token.EOFToken.class));
        assertThat(code, contains(
                isA(Command.PushInt.class),
                isA(Command.Conj.class)));
        assertEquals(1, ((Command.PushInt) code.getFirst()).value());
    }

    @Test
    void testConjExp2() {
        boolean result = assertDoesNotThrow(() -> parse("1^^", conjExp));
        assertTrue(result);
        assertThat(parseContext.currentToken(), Matchers.isA(Token.EOFToken.class));
        assertThat(code, contains(
                isA(Command.PushInt.class),
                isA(Command.Conj.class),
                isA(Command.Conj.class)));
        assertEquals(1, ((Command.PushInt) code.getFirst()).value());
    }

    @ParameterizedTest
    @CsvSource({
            "$,Missing primary expression ($)",
            ",Missing primary expression (<EOF>)",
            "x,x is a reserved keyword (x)"
    })
    void testPrimaryExpError(String text, String expMsg) {
        ParseException ex = assertThrows(ParseException.class, () ->
                parse(text, primaryExp));
        assertEquals(expMsg, ex.getMessage());
    }

    @ParameterizedTest
    @MethodSource("argsOptKet")
    void testPrimaryExpKet(String text, Ket ket) {
        boolean result = assertDoesNotThrow(() -> parse(text, primaryExp));
        assertTrue(result);
        assertThat(parseContext.currentToken(), Matchers.isA(Token.EOFToken.class));
        assertThat(code, contains(isA(Command.PushKet.class)));
        assertEquals(ket, ((Command.PushKet) code.getFirst()).value());
    }

    @ParameterizedTest
    @CsvSource({
            "a,a",
            "a2,a2",
    })
    void testPrimaryExpVar(String text, String expId) throws Throwable {
        assertTrue(parse(text, primaryExp));
        assertThat(parseContext.currentToken(), Matchers.isA(Token.EOFToken.class));
        assertThat(code, contains(
                isA(Command.PushString.class),
                isA(Command.RetrieveVar.class)
        ));
        assertEquals(expId, ((Command.PushString) code.getFirst()).value());
    }

    @ParameterizedTest
    @CsvSource({
            "/,",
            ",Missing token / (<EOF>)",
            "*,Missing token / (*)",
            "a,Missing token / (a)",
    })
    void testSlashToken(String text, String expMsg) {
        Expression exp = op("/");
        if (expMsg != null) {
            ParseException ex = assertThrows(ParseException.class, () ->
                    parse(text, exp));
            assertEquals(expMsg, ex.getMessage());
        } else {
            boolean result = assertDoesNotThrow(() -> parse(text, exp));
            assertTrue(result);
            assertThat(parseContext.currentToken(), Matchers.isA(Token.EOFToken.class));
        }
    }

    @ParameterizedTest
    @MethodSource("argsStateLiteral")
    void testStateLiteral(String text, Ket ket) {
        boolean result = assertDoesNotThrow(() -> parse(text, stateLiteralExp));
        assertTrue(result);
        assertThat(parseContext.currentToken(), Matchers.isA(Token.EOFToken.class));
        assertThat(code, Matchers.contains(isA(Command.PushKet.class)));
        assertEquals(ket, ((Command.PushKet) code.getFirst()).value());

    }

    @ParameterizedTest
    @CsvSource({
            "a,Missing state literal (a)",
            ",Missing state literal (<EOF>)",
    })
    void testStateLiteralError(String text, String expMsg) {
        ParseException ex = assertThrows(ParseException.class, () ->
                parse(text, stateLiteralExp));
        assertEquals(expMsg, ex.getMessage());
    }

    @ParameterizedTest
    @CsvSource({
            "1. - 2. - 3., 1,0, 2,0, 3,0",
            "1. - 2. - i, 1,0, 2,0, 0,1",
            "1. - i - 3., 1,0, 0,1, 3,0",
            "i - 2. - 3., 0,1, 2,0, 3,0",
    })
    void testSubComplexComplexComplex(String text, float expReal1, float expIm1, float expReal2, float expIm2, float expReal3, float expIm3) throws Throwable {
        Complex exp1 = new Complex(expReal1, expIm1);
        Complex exp2 = new Complex(expReal2, expIm2);
        Complex exp3 = new Complex(expReal3, expIm3);
        parse(text, sumExp);
        assertThat(parseContext.currentToken(), Matchers.isA(Token.EOFToken.class));
        assertThat(code, contains(
                isA(Command.PushComplex.class),
                isA(Command.PushComplex.class),
                isA(Command.Sub.class),
                isA(Command.PushComplex.class),
                isA(Command.Sub.class)
        ));
        assertEquals(exp1, ((Command.PushComplex) code.getFirst()).value());
        assertEquals(exp2, ((Command.PushComplex) code.get(1)).value());
        assertEquals(exp3, ((Command.PushComplex) code.get(3)).value());
    }

    @ParameterizedTest
    @CsvSource({
            "1. - 2., 1,0, 2,0",
            "2. - 1., 2,0, 1,0",
            "1. - i, 1,0, 0,1",
            "i - 1., 0,1, 1,0",
            "i - i, 0,1, 0,1",
            "+1. - +2., 1,0, 2,0",
            "+2. - +1., 2,0, 1,0",
            "+1. - +i, 1,0, 0,1",
            "+i - +1., 0,1, 1,0",
            "+i - +i, 0,1, 0,1"
    })
    void testSubExpComplexComplex(String text, float expReal1, float expIm1, float expReal2, float expIm2) throws Throwable {
        Complex exp1 = new Complex(expReal1, expIm1);
        Complex exp2 = new Complex(expReal2, expIm2);
        parse(text, sumExp);
        assertThat(parseContext.currentToken(), Matchers.isA(Token.EOFToken.class));
        assertThat(code, Matchers.contains(
                isA(Command.PushComplex.class),
                isA(Command.PushComplex.class),
                isA(Command.Sub.class)
        ));
        assertEquals(exp1, ((Command.PushComplex) code.getFirst()).value());
        assertEquals(exp2, ((Command.PushComplex) code.get(1)).value());
    }

    @ParameterizedTest
    @CsvSource({
            "i, 0,1, 0",
            "+ + + i, 0,1, 0",
            "3., 3,0, 0",
            "- + + 3., 3,0, 1",
            "+ - + 3., 3,0, 1",
            "+ + - 3., 3,0, 1",
            "- - + 3., 3,0, 2",
            "- + - 3., 3,0, 2",
            "+ - - 3., 3,0, 2",
            "- - - 3., 3,0, 3",
            "- - - ( 3. ), 3,0, 3",
    })
    void testUnaryExpComplex(String text, float real, float im, int expNumNegation) throws Throwable {
        assertTrue(parse(text, unaryExp));
        assertThat(parseContext.currentToken(), Matchers.isA(Token.EOFToken.class));
        assertEquals(1 + expNumNegation, code.size());
        assertThat(code.getFirst(), Matchers.isA(Command.PushComplex.class));
        assertEquals(new Complex(real, im), ((Command.PushComplex) code.getFirst()).value());
        for (int i = 0; i < expNumNegation; i++) {
            assertThat(code.get(i + 1), isA(Command.Negate.class));
        }
    }

}