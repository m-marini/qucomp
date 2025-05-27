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
import org.mmarini.qucomp.apis.Bra;
import org.mmarini.qucomp.apis.Complex;
import org.mmarini.qucomp.apis.Ket;

import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mmarini.qucomp.Matchers.*;

class ProcessorTest {
    public static final float EPSILON = 1e-5f;

    public static Stream<Arguments> argsBra() {
        return Stream.of(
                Arguments.of("<+|;", Bra.plus()),
                Arguments.of("<-|;", Bra.minus()),
                Arguments.of("<-i|;", Bra.minus_i()),
                Arguments.of("<i|;", Bra.i()),
                Arguments.of("<0|;", Bra.zero()),
                Arguments.of("<1|;", Bra.one()),
                Arguments.of("<2|;", Ket.base(2, 2).conj()),
                Arguments.of("<9|;", Ket.base(9, 4).conj()),

                Arguments.of("- <+|;", Bra.plus().neg()),
                Arguments.of("|+>^;", Ket.plus().conj()),

                Arguments.of("<0| + <1|;", Bra.zero().add(Bra.one())),
                Arguments.of("<1| + <0|;", Bra.one().add(Bra.zero())),
                Arguments.of("<0| + <3|;", Bra.base(0, 2).add(Bra.base(3, 2))),
                Arguments.of("<3| + <0|;", Bra.base(0, 2).add(Bra.base(3, 2))),
                Arguments.of("<2| + <3|;", Bra.base(2, 2).add(Bra.base(3, 2))),
                Arguments.of("<3| + <2|;", Bra.base(2, 2).add(Bra.base(3, 2))),

                Arguments.of("<0| - <1|;", Bra.zero().sub(Bra.one())),
                Arguments.of("<1| - <0|;", Bra.one().sub(Bra.zero())),
                Arguments.of("<0| - <3|;", Bra.base(0, 2).sub(Bra.base(3, 2))),
                Arguments.of("<3| - <0|;", Bra.base(3, 2).sub(Bra.base(0, 2))),
                Arguments.of("<2| - <3|;", Bra.base(2, 2).sub(Bra.base(3, 2))),
                Arguments.of("<3| - <2|;", Bra.base(3, 2).sub(Bra.base(2, 2))),

                Arguments.of("<0| * i;", Bra.zero().mul(Complex.i())),
                Arguments.of("i * <0|;", Bra.zero().mul(Complex.i())),
                Arguments.of("<0| * 2;", Bra.zero().mul(Complex.create(2))),
                Arguments.of("2 * <0|;", Bra.zero().mul(Complex.create(2))),

                Arguments.of("<0| / 2;", Bra.zero().mul(0.5f)),
                Arguments.of("<0| / i;", Bra.zero().mul(Complex.i().inv())),
                Arguments.of("i / |0>;", Bra.zero().mul(Complex.i())),
                Arguments.of("2 / |0>;", Bra.zero().mul(2)),

                Arguments.of("<0| x <1|;", Ket.base(1, 2).conj()),
                Arguments.of("<1| x <0|;", Ket.base(2, 2).conj()),
                Arguments.of("<0| x <2|;", Ket.base(2, 3).conj()),
                Arguments.of("<2| x <0|;", Ket.base(4, 3).conj()),
                Arguments.of("<2| x <2|;", Ket.base(10, 4).conj())
        );
    }

    public static Stream<Arguments> argsKet() {
        return Stream.of(
                Arguments.of("|+>;", Ket.plus()),
                Arguments.of("|->;", Ket.minus()),
                Arguments.of("|-i>;", Ket.minus_i()),
                Arguments.of("|i>;", Ket.i()),
                Arguments.of("|0>;", Ket.zero()),
                Arguments.of("|1>;", Ket.one()),
                Arguments.of("|2>;", Ket.base(2, 2)),

                Arguments.of("- |+>;", Ket.plus().neg()),
                Arguments.of("<+|^;", Bra.plus().conj()),

                Arguments.of("|0> + |1>;", Ket.zero().add(Ket.one())),
                Arguments.of("|1> + |0>;", Ket.zero().add(Ket.one())),
                Arguments.of("|0> + |3>;", Ket.base(0, 2).add(Ket.base(3, 2))),
                Arguments.of("|3> + |0>;", Ket.base(0, 2).add(Ket.base(3, 2))),
                Arguments.of("|2> + |3>;", Ket.base(2, 2).add(Ket.base(3, 2))),
                Arguments.of("|3> + |2>;", Ket.base(2, 2).add(Ket.base(3, 2))),

                Arguments.of("|0> - |1>;", Ket.zero().sub(Ket.one())),
                Arguments.of("|1> - |0>;", Ket.one().sub(Ket.zero())),
                Arguments.of("|0> - |3>;", Ket.base(0, 2).sub(Ket.base(3, 2))),
                Arguments.of("|3> - |0>;", Ket.base(3, 2).sub(Ket.base(0, 2))),
                Arguments.of("|2> - |3>;", Ket.base(2, 2).sub(Ket.base(3, 2))),
                Arguments.of("|3> - |2>;", Ket.base(3, 2).sub(Ket.base(2, 2))),

                Arguments.of("i * |0>;", Ket.zero().mul(Complex.i())),
                Arguments.of("|0> * i;", Ket.zero().mul(Complex.i())),
                Arguments.of("2 * |0>;", Ket.zero().mul(2)),
                Arguments.of("|0> * 2;", Ket.zero().mul(2)),

                Arguments.of("|0> / 2;", Ket.zero().mul(0.5f)),
                Arguments.of("|0> / i;", Ket.zero().mul(Complex.i().inv())),
                Arguments.of("2 / <0|;", Ket.zero().mul(2)),
                Arguments.of("i / <0|;", Ket.zero().mul(Complex.i())),

                Arguments.of("|0> x |1>;", Ket.base(1, 2)),
                Arguments.of("|1> x |0>;", Ket.base(2, 2)),
                Arguments.of("|0> x |2>;", Ket.base(2, 3)),
                Arguments.of("|2> x |0>;", Ket.base(4, 3)),
                Arguments.of("|2> x |2>;", Ket.base(10, 4))
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

    @ParameterizedTest
    @MethodSource("argsBra")
    void testBra(String text, Bra exp) {
        Object[] results = assertDoesNotThrow(() -> execute(text));
        Bra result = (Bra) results[0];
        assertThat(result, braCloseTo(exp, EPSILON));
        assertThat(processor.variables(), anEmptyMap());
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

            "<0| / <0|;, 1,0",
            "<2| / <2|;, 1,0",
            "<0| / <2|;, 0,0",
            "<2| / <0|;, 0,0",
            "<2| / <2|;, 1,0",
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
            "sqrt(|0>);,Unexpected argument ket ((1.0) |0>) token(\"sqrt\")",
            "sqrt(<0|);,Unexpected argument bra ((1.0) <0|) token(\"sqrt\")",

            "|0> * |0>;,Unexpected right argument ket ((1.0) |0>) token(\"*\")",
            "|0> * <0|;,Unexpected right argument bra ((1.0) <0|) token(\"*\")",
            "<0| * <0|;,Unexpected right argument bra ((1.0) <0|) token(\"*\")",

            "1 + <0|;,Unexpected right argument bra ((1.0) <0|) token(\"+\")",
            "i + <0|;,Unexpected right argument bra ((1.0) <0|) token(\"+\")",
            "|0> + <0|;,Unexpected right argument bra ((1.0) <0|) token(\"+\")",
            "1 + |0>;,Unexpected right argument ket ((1.0) |0>) token(\"+\")",
            "i + |0>;,Unexpected right argument ket ((1.0) |0>) token(\"+\")",
            "<0| + |0>;,Unexpected right argument ket ((1.0) |0>) token(\"+\")",
            "|0> + 1;,Unexpected right argument integer (1) token(\"+\")",
            "|0> + i;,Unexpected right argument complex (i) token(\"+\")",
            "<0| + 1;,Unexpected right argument integer (1) token(\"+\")",
            "<0| + i;,Unexpected right argument complex (i) token(\"+\")",

            "1 - <0|;,Unexpected right argument bra ((1.0) <0|) token(\"-\")",
            "i - <0|;,Unexpected right argument bra ((1.0) <0|) token(\"-\")",
            "|0> - <0|;,Unexpected right argument bra ((1.0) <0|) token(\"-\")",
            "1 - |0>;,Unexpected right argument ket ((1.0) |0>) token(\"-\")",
            "i - |0>;,Unexpected right argument ket ((1.0) |0>) token(\"-\")",
            "<0| - |0>;,Unexpected right argument ket ((1.0) |0>) token(\"-\")",
            "|0> - 1;,Unexpected right argument int (1) token(\"-\")",
            "|0> - i;,Unexpected right argument complex (i) token(\"-\")",
            "<0| - 1;,Unexpected right argument int (1) token(\"-\")",
            "<0| - i;,Unexpected right argument complex (i) token(\"-\")",

            "<0| / |0>;,Unexpected right argument ket ((1.0) |0>) token(\"/\")",
            "|0> / |0>;,Unexpected right argument ket ((1.0) |0>) token(\"/\")",
            "|0> / <0|;,Unexpected right argument bra ((1.0) <0|) token(\"/\")",

            "1 x 1;, Unexpected left argument integer (1) token(\"x\")",
            "i x 1;, Unexpected left argument complex (i) token(\"x\")",
            "|0> x 1;, Unexpected right argument integer (1) token(\"x\")",
            "|0> x i;, Unexpected right argument complex (i) token(\"x\")",
            "|0> x <0|;, Unexpected right argument bra ((1.0) <0|) token(\"x\")",
            "<0| x 1;, Unexpected right argument integer (1) token(\"x\")",
            "<0| x i;, Unexpected right argument complex (i) token(\"x\")",
            "<0| x |0>;, Unexpected right argument ket ((1.0) |0>) token(\"x\")",
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
            "90 / 3;, 30"
    })
    void testInt(String text, int expected) {
        Object[] result = assertDoesNotThrow(() -> execute(text));
        assertEquals(expected, result[0]);
        assertThat(processor.variables(), anEmptyMap());
    }

    @ParameterizedTest
    @MethodSource("argsKet")
    void testKet(String text, Ket exp) {
        Object[] result = assertDoesNotThrow(() -> execute(text));
        assertThat((Ket) result[0], ketCloseTo(exp, EPSILON));
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