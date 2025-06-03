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
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.mmarini.qucomp.apis.Complex;
import org.mmarini.qucomp.apis.Matrix;

import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mmarini.qucomp.Matchers.complexClose;
import static org.mmarini.qucomp.Matchers.matrixCloseTo;
import static org.mmarini.qucomp.apis.MatrixTest.*;

class ProcessorTest {
    public static final float EPSILON = 1e-5f;
    private static final Matrix NORM02 = Matrix.create(4, 1,
            0.5f, 0.5f, 0.5f, 0.5f
    );

    public static Stream<Arguments> testMatrixArgs() {
        return Stream.of(
                Arguments.of("<+|;", Matrix.plus().dagger()),
                Arguments.of("<-|;", Matrix.minus().dagger()),
                Arguments.of("<-i|;", Matrix.minus_i().dagger()),
                Arguments.of("<i|;", Matrix.i().dagger()),
                // 5
                Arguments.of("<0|;", Matrix.ketBase(0).dagger()),
                Arguments.of("<1|;", Matrix.ketBase(1).dagger()),
                Arguments.of("<2|;", Matrix.ketBase(2).dagger()),
                Arguments.of("<9|;", Matrix.ketBase(9).dagger()),
                Arguments.of("- <+|;", Matrix.plus().neg().dagger()),
                // 10
                Arguments.of("|+>^;", Matrix.plus().dagger()),
                Arguments.of("<0| + <1|;", Matrix.ket(1, 1).dagger()),
                Arguments.of("<1| + <0|;", Matrix.ket(1, 1).dagger()),
                Arguments.of("<0| + <3|;", Matrix.ket(1, 0, 0, 1).dagger()),
                Arguments.of("<3| + <0|;", Matrix.ket(1, 0, 0, 1).dagger()),
                // 15
                Arguments.of("<2| + <3|;", Matrix.ket(0, 0, 1, 1).dagger()),
                Arguments.of("<3| + <2|;", Matrix.ket(0, 0, 1, 1).dagger()),
                Arguments.of("<0| - <1|;", Matrix.ket(1, -1).dagger()),
                Arguments.of("<1| - <0|;", Matrix.ket(-1, 1).dagger()),
                Arguments.of("<0| - <3|;", Matrix.ket(1, 0, 0, -1).dagger()),
                // 20
                Arguments.of("<3| - <0|;", Matrix.ket(-1, 0, 0, 1).dagger()),
                Arguments.of("<2| - <3|;", Matrix.ket(0, 0, 1, -1).dagger()),
                Arguments.of("<3| - <2|;", Matrix.ket(0, 0, -1, 1).dagger()),
                Arguments.of("<0| * i;", Matrix.ket(1, 0).dagger().mul(Complex.i())),
                Arguments.of("i * <0|;", Matrix.ket(1, 0).dagger().mul(Complex.i())),
                // 25
                Arguments.of("<0| * 2;", Matrix.ket(1, 0).dagger().mul(2)),
                Arguments.of("2 * <0|;", Matrix.ket(1, 0).dagger().mul(2)),
                Arguments.of("<0| / 2;", Matrix.ketBase(0).dagger().div(2)),
                Arguments.of("<0| / i;", Matrix.ketBase(0).dagger().div(Complex.i())),
                Arguments.of("<0| x <1|;", Matrix.ketBase(1).dagger().extendsCols(4)),
                // 30
                Arguments.of("<1| x <0|;", Matrix.ketBase(2).dagger().extendsCols(4)),
                Arguments.of("<0| x <2|;", Matrix.ketBase(2).dagger().extendsCols(8)),
                Arguments.of("<2| x <0|;", Matrix.ketBase(4).dagger().extendsCols(8)),
                Arguments.of("<2| x <2|;", Matrix.ketBase(10).dagger().extendsCols(8)),
                Arguments.of("|+>;", Matrix.plus()),
                // 35
                Arguments.of("|->;", Matrix.minus()),
                Arguments.of("|-i>;", Matrix.minus_i()),
                Arguments.of("|i>;", Matrix.i()),
                Arguments.of("|0>;", Matrix.ketBase(0)),
                Arguments.of("|1>;", Matrix.ketBase(1)),
                // 40
                Arguments.of("|2>;", Matrix.ketBase(2)),
                Arguments.of("- |+>;", Matrix.plus().neg()),
                Arguments.of("<+|^;", Matrix.plus()),
                Arguments.of("|0> + |1>;", Matrix.ketBase(0).add(Matrix.ketBase(1))),
                Arguments.of("|1> + |0>;", Matrix.ketBase(0).add(Matrix.ketBase(1))),
                // 45
                Arguments.of("|0> + |3>;", Matrix.ketBase(0).add(Matrix.ketBase(3))),
                Arguments.of("|3> + |0>;", Matrix.ketBase(0).add(Matrix.ketBase(3))),
                Arguments.of("|2> + |3>;", Matrix.ketBase(2).add(Matrix.ketBase(3))),
                Arguments.of("|3> + |2>;", Matrix.ketBase(2).add(Matrix.ketBase(3))),
                Arguments.of("|0> * <0|;", Matrix.create(2, 2,
                        1, 0,
                        0, 0)),
                // 50
                Arguments.of("|0> - <0|;", Matrix.create(2, 2,
                        0, 0,
                        0, 0)),
                Arguments.of("<0| - |0>;", Matrix.create(2, 2,
                        0, 0,
                        0, 0)),
                Arguments.of("|0> - |1>;", Matrix.ketBase(0).sub(Matrix.ketBase(1))),
                Arguments.of("|1> - |0>;", Matrix.ketBase(1).sub(Matrix.ketBase(0))),
                Arguments.of("|0> - |3>;", Matrix.ketBase(0).sub(Matrix.ketBase(3))),
                // 55
                Arguments.of("|3> - |0>;", Matrix.ketBase(3).sub(Matrix.ketBase(0))),
                Arguments.of("|2> - |3>;", Matrix.ketBase(2).sub(Matrix.ketBase(3))),
                Arguments.of("|3> - |2>;", Matrix.ketBase(3).sub(Matrix.ketBase(2))),
                Arguments.of("<0| + |0>;", Matrix.create(2, 2,
                        2, 0,
                        0, 0)),

                Arguments.of("i * |0>;", Matrix.ketBase(0).mul(Complex.i())),
                // 60
                Arguments.of("|0> * i;", Matrix.ketBase(0).mul(Complex.i())),
                Arguments.of("2 * |0>;", Matrix.ketBase(0).mul(2)),
                Arguments.of("|0> * 2;", Matrix.ketBase(0).mul(2)),
                Arguments.of("|0> / 2;", Matrix.ketBase(0).mul(0.5f)),
                Arguments.of("|0> / i;", Matrix.ketBase(0).mul(Complex.i().inv())),
                // 65
                Arguments.of("|0> x |1>;", Matrix.ketBase(1).extendsRows(4)),
                Arguments.of("|1> x |0>;", Matrix.ketBase(2).extendsRows(4)),
                Arguments.of("|0> x |2>;", Matrix.ketBase(2).extendsRows(8)),
                Arguments.of("|2> x |0>;", Matrix.ketBase(4).extendsRows(8)),
                Arguments.of("|2> x |2>;", Matrix.ketBase(10).extendsRows(16)),

                // 70
                Arguments.of("I(0);", Matrix.identity(2)),
                Arguments.of("I(1);", Matrix.identity(4)),
                Arguments.of("H(0);", Matrix.h(0)),
                Arguments.of("H(1);", Matrix.h(1)),
                Arguments.of("X(0);", Matrix.x(0)),
                // 75
                Arguments.of("X(1);", Matrix.x(1)),
                Arguments.of("Y(0);", Matrix.y(0)),
                Arguments.of("Y(1);", Matrix.y(1)),
                Arguments.of("Z(0);", Matrix.z(0)),
                Arguments.of("Z(1);", Matrix.z(1)),
                // 80
                Arguments.of("S(0);", Matrix.s(0)),
                Arguments.of("S(1);", Matrix.s(1)),
                Arguments.of("T(0);", Matrix.t(0)),
                Arguments.of("T(1);", Matrix.t(1)),
                Arguments.of("|0> x <0|;", Matrix.ary(0, 0)),
                // 85)
                Arguments.of("<0| x |0>;", Matrix.ary(0, 0)),
                Arguments.of("ary(2,3);", Matrix.ary(2, 3)),
                Arguments.of("sim(2,3);", Matrix.sim(2, 3)),
                Arguments.of("eps(2,3);", Matrix.eps(2, 3)),
                Arguments.of("CNOT(1,2);", Matrix.cnot(1, 2)),
                Arguments.of("CCNOT(1,2,3);", Matrix.ccnot(1, 2, 3)),
                Arguments.of("SWAP(0,1);", Matrix.swap(0, 1)),
                Arguments.of("qubit0(0,1);", QUBIT0_01),
                Arguments.of("qubit0(0,2);", QUBIT0_02),
                Arguments.of("qubit0(1,2);", QUBIT0_12),
                Arguments.of("qubit0(0,3);", QUBIT0_03),
                Arguments.of("qubit0(1,3);", QUBIT0_13),
                Arguments.of("qubit0(2,3);", QUBIT0_23),
                Arguments.of("qubit1(0,1);", QUBIT1_01),
                Arguments.of("qubit1(0,2);", QUBIT1_02),
                Arguments.of("qubit1(1,2);", QUBIT1_12),
                Arguments.of("qubit1(0,3);", QUBIT1_03),
                Arguments.of("qubit1(1,3);", QUBIT1_13),
                Arguments.of("qubit1(2,3);", QUBIT1_23),
                Arguments.of("normalise(|0>+|1>+|2>+|3>);", NORM02)
        );
    }

    private Processor processor;
    private Compiler compiler;
    private SyntaxRule syntax;

    <T> T execute(String text) throws QuException {
        CommandNode code = assertDoesNotThrow(() -> {
            Tokenizer tokenizer = Tokenizer.create(text).open();
            syntax.parse(compiler.createParseContext(tokenizer));
            return compiler.pop();
        });
        return (T) code.evaluate(processor);
    }

    @BeforeEach
    void setUp() {
        this.processor = new Processor();
        assertDoesNotThrow(() -> {
            this.compiler = Compiler.create();
            this.syntax = Syntax.rule("<code-unit>");
        });
    }

    @Test
    void testAssign() {
        Object[] result = assertDoesNotThrow(() -> execute("let a = 1;"));
        assertThat(result[0], equalTo(1));
        assertThat(processor.variables(), hasEntry(equalTo("a"), equalTo(1)));
    }

    @Test
    void testClear() {
        processor.variables().put("a", 1);
        Object[] results = assertDoesNotThrow(() -> execute("clear();"));
        assertNull(results[0]);
        assertThat(processor.variables(), anEmptyMap());
    }

    @ParameterizedTest
    @CsvSource({
            "1.; , 1,0",
            "i; , 0,1",
            "pi; , 3.1415927,0",
            "e; , 2.7182817,0",
            "-1.; , -1,0",
            "-i; , 0,-1",
            "1.^; , 1,0",
            "i^; , 0,-1",

            "i + i;, 0,2",
            "1 + i;, 1,1",
            "i + 1;, 1,1",

            "i - i;, 0,0",
            "1 - i;, 1,-1",
            "i - 1;, -1,1",

            "sqrt(4);, 2,0",
            "sqrt(4.);, 2,0",
            "sqrt(0.);, 0,0",
            "sqrt(-4.);, 0,2",
            "sqrt(-4);, 0,2",
            "sqrt(8. * i);, 2,2",
            "sqrt(-8. * i);, 2,-2",
            "sqrt(8. +6* i);, 3,1",
            "sqrt(-8. +6* i);, 1,3",
            "sqrt(8. -6* i);, 3,-1",
            "sqrt(-8. -6* i);, -1,3",

            "<0| * |0>;, 1,0",
            "<2| * |0>;, 0,0",
            "<0| * |2>;, 0,0",
            "<2| * |3>;, 0,0",
            "<3| * |3>;, 1,0",
            "i * i;, -1,0",
            "i * 2;, 0,2",
            "2 * i;, 0,2",

            "1 / 2;, 0.5, 0",
            "1 / i;, 0, -1",
            "i / 2;, 0, 0.5",
            "i / i;, 1, 0",
            "normalise(i);, 1, 0",
    })
    void testComplex(String text, float re, float im) {
        Complex expected = new Complex(re, im);
        Object[] result = assertDoesNotThrow(() -> execute(text));
        assertThat((Complex) result[0], complexClose(expected, EPSILON));
        assertThat(processor.variables(), anEmptyMap());
    }

    @ParameterizedTest
    @CsvSource({
            "|1.>; , Expected integer value: (1.0) token(\"1.\")",
            "a; , Undefined variable a token(\"a\")",
            "sqrt(|0>);,Unexpected matrix argument ((1.0) |0>) token(\"0\")",
            "sqrt(<0|);,Unexpected matrix argument ((1.0) <0|) token(\"<\")",
            // 5
            "|0> * |0>;,Invalid product operands shapes 2x1 by 2x1 token(\"*\")",
            "<0| * <0|;,Invalid product operands shapes 1x2 by 1x2 token(\"*\")",
            "1 + <0|;,Unexpected right argument ((1.0) <0|) token(\"+\")",
            "i + <0|;,Unexpected right argument ((1.0) <0|) token(\"+\")",
            "1 + |0>;,Unexpected right argument ((1.0) |0>) token(\"+\")",
            // 10
            "i + |0>;,Unexpected right argument ((1.0) |0>) token(\"+\")",
            "|0> + 1;,Unexpected right argument integer (1) token(\"+\")",
            "|0> + i;,Unexpected right argument complex (i) token(\"+\")",
            "<0| + 1;,Unexpected right argument integer (1) token(\"+\")",
            "<0| + i;,Unexpected right argument complex (i) token(\"+\")",
            // 15
            "1 - <0|;,Unexpected right argument ((1.0) <0|) token(\"-\")",
            "i - <0|;,Unexpected right argument ((1.0) <0|) token(\"-\")",
            "1 - |0>;,Unexpected right argument ((1.0) |0>) token(\"-\")",
            "i - |0>;,Unexpected right argument ((1.0) |0>) token(\"-\")",
            "|0> - 1;,Unexpected right argument int (1) token(\"-\")",
            // 20
            "|0> - i;,Unexpected right argument complex (i) token(\"-\")",
            "<0| - 1;,Unexpected right argument int (1) token(\"-\")",
            "<0| - i;,Unexpected right argument complex (i) token(\"-\")",
            "<0| / |0>;,Unexpected right argument ((1.0) |0>) token(\"/\")",
            "|0> / |0>;,Unexpected right argument ((1.0) |0>) token(\"/\")",
            // 25
            "|0> / <0|;,Unexpected right argument ((1.0) <0|) token(\"/\")",
            "1 x 1;, Unexpected left argument integer (1) token(\"x\")",
            "i x 1;, Unexpected left argument complex (i) token(\"x\")",
            "|0> x 1;, Unexpected right argument integer (1) token(\"x\")",
            "|0> x i;, Unexpected right argument complex (i) token(\"x\")",
            // 30
            "<0| x 1;, Unexpected right argument integer (1) token(\"x\")",
            "<0| x i;, Unexpected right argument complex (i) token(\"x\")",
            "I(i);, Argument should be an integer: actual (i) token(\"i\")",
            "I(|0>);, Argument should be an integer: actual ((1.0) |0>) token(\"0\")",
            "I(<0|);, Argument should be an integer: actual ((1.0) <0|) token(\"<\")",
            // 35
            "H(i);, Argument should be an integer: actual (i) token(\"i\")",
            "H(|0>);, Argument should be an integer: actual ((1.0) |0>) token(\"0\")",
            "H(<0|);, Argument should be an integer: actual ((1.0) <0|) token(\"<\")",
            "X(i);, Argument should be an integer: actual (i) token(\"i\")",
            "X(|0>);, Argument should be an integer: actual ((1.0) |0>) token(\"0\")",
            // 40
            "X(<0|);, Argument should be an integer: actual ((1.0) <0|) token(\"<\")",
            "Y(i);, Argument should be an integer: actual (i) token(\"i\")",
            "Y(|0>);, Argument should be an integer: actual ((1.0) |0>) token(\"0\")",
            "Y(<0|);, Argument should be an integer: actual ((1.0) <0|) token(\"<\")",
            "Z(i);, Argument should be an integer: actual (i) token(\"i\")",
            // 45
            "Z(|0>);, Argument should be an integer: actual ((1.0) |0>) token(\"0\")",
            "Z(<0|);, Argument should be an integer: actual ((1.0) <0|) token(\"<\")",
            "S(i);, Argument should be an integer: actual (i) token(\"i\")",
            "S(|0>);, Argument should be an integer: actual ((1.0) |0>) token(\"0\")",
            "S(<0|);, Argument should be an integer: actual ((1.0) <0|) token(\"<\")",
            // 50
            "T(i);, Argument should be an integer: actual (i) token(\"i\")",
            "T(|0>);, Argument should be an integer: actual ((1.0) |0>) token(\"0\")",
            "T(<0|);, Argument should be an integer: actual ((1.0) <0|) token(\"<\")",
            "<0| / <0|;, Unexpected right argument ((1.0) <0|) token(\"/\")",
            "<2| / <2|;, Unexpected right argument ((1.0) <2|) token(\"/\")",
            // 55
            "'ary(1,i);', Argument should be an integer: actual (i) token(\"i\")",
            "'ary(i,1);', Argument should be an integer: actual (i) token(\"i\")",
            "'sim(1,i);', Argument should be an integer: actual (i) token(\"i\")",
            "'sim(i,1);', Argument should be an integer: actual (i) token(\"i\")",
            "'eps(1,i);', Argument should be an integer: actual (i) token(\"i\")",
            "'eps(i,1);', Argument should be an integer: actual (i) token(\"i\")",
            "'SWAP(1,i);', Argument should be an integer: actual (i) token(\"i\")",
            "'SWAP(i,1);', Argument should be an integer: actual (i) token(\"i\")",
            "'CNOT(1,i);', Control qubit should be an integer: actual (i) token(\"i\")",
            "'CNOT(i,1);', Data qubit should be an integer: actual (i) token(\"i\")",
            "'CNOT(0,0);', 'Expected all different indices [0, 0] token(\"CNOT\")'",
            "'CCNOT(1,i,1);', Control0 qubit should be an integer: actual (i) token(\"i\")",
            "'CCNOT(i,1,1);', Data qubit should be an integer: actual (i) token(\"i\")",
            "'CCNOT(1,1,i);', Control1 qubit should be an integer: actual (i) token(\"i\")",
            "'CCNOT(0,0,1);', 'Expected all different indices [0, 0, 1] token(\"CCNOT\")'",
            "'CCNOT(0,1,0);', 'Expected all different indices [0, 1, 0] token(\"CCNOT\")'",
            "'CCNOT(1,0,0);', 'Expected all different indices [1, 0, 0] token(\"CCNOT\")'",
            "'qubit0(1,i);', Number of qubits should be an integer: actual (i) token(\"i\")",
            "'qubit0(i,1);', Qubit index should be an integer: actual (i) token(\"i\")",
            "'qubit1(1,i);', Number of qubits should be an integer: actual (i) token(\"i\")",
            "'qubit1(i,1);', Qubit index should be an integer: actual (i) token(\"i\")",
    })
    void testError(String text, String msg) {
        QuException ex = assertThrows(QuException.class, () -> execute(text));
        assertEquals(msg, ex.getMessage());
    }

    @ParameterizedTest
    @CsvSource({
            "1; , 1",
            "-1; , -1",
            "1^; , 1",

            "1 + 2;, 3",
            "2 + 1;, 3",
            "1 + 2 + 3;, 6",

            "1 - 2;, -1",
            "2 - 1;, 1",
            "1 - 2 + 3;, 2",

            "2 * 3;, 6",
            "3 * 2;, 6",
            "2 * 3 * 4;, 24",

            "4 / 2;, 2",
            "90 / 3;, 30",
            "normalise(10);, 1",
    })
    void testInt(String text, int expected) {
        Object[] result = assertDoesNotThrow(() -> execute(text));
        assertEquals(expected, result[0]);
        assertThat(processor.variables(), anEmptyMap());
    }

    @ParameterizedTest
    @MethodSource("testMatrixArgs")
    void testMatrix(String text, Matrix exp) {
        Object[] result = assertDoesNotThrow(() -> execute(text));
        assertThat((Matrix) result[0], matrixCloseTo(exp, EPSILON));
        assertThat(processor.variables(), anEmptyMap());
    }

    @Test
    void testVar() {
        Object[] result = assertDoesNotThrow(() -> execute("let a = 1; -a;"));
        assertThat(result[0], equalTo(1));
        assertThat(result[1], equalTo(-1));
        assertThat(processor.variables(), hasEntry(equalTo("a"), equalTo(1)));
    }
}