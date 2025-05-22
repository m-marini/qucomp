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
import org.mmarini.qucomp.apis.Bra;
import org.mmarini.qucomp.apis.Complex;
import org.mmarini.qucomp.apis.Ket;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mmarini.Matchers.*;

class ProcessorTest {
    public static final float EPSILON = 1e-3f;
    private Processor processor;
    private List<Command> code;
    private List<Object> consumed;

    void execute(String text) throws Throwable {
        this.code = Compiler.compile(text);
        processor.executeCode(code);
    }

    @BeforeEach
    void setUp() {
        this.consumed = new ArrayList<>();
        this.processor = new Processor(value -> consumed.add(value));
    }

    private SourceContext sourceContext(int line) {
        return new SourceContext(String.valueOf(line), String.valueOf(line), line, 0);
    }

    @Test
    void testAddBraBra1() {
        assertDoesNotThrow(() -> execute("<0| + <1|;"));
        assertThat(consumed, contains(isA(Bra.class)));
        assertThat((Bra) consumed.getFirst(), braCloseTo(EPSILON, 1, 1));
        assertThat(processor.stack, empty());
    }

    @ParameterizedTest
    @CsvSource({
            "<0| + <3|;, 1,0,0,1",
            "<3| + <0|;, 1,0,0,1",
            "<2| + <3|;, 0,0,1,1",
    })
    void testAddBraBra2(String text, float e0, float e1, float e2, float e3) {
        assertDoesNotThrow(() -> execute(text));
        assertEquals(1, consumed.size());
        assertThat((Bra) consumed.getFirst(), braCloseTo(EPSILON, e0, e1, e2, e3));
        assertThat(processor.stack, empty());
    }

    @Test
    void testAddComplexComplex() {
        assertDoesNotThrow(() -> execute("i + i;"));
        assertThat(consumed, contains(new Complex(0, 2)));
        assertThat(processor.stack, empty());
    }

    @Test
    void testAddComplexInt() {
        assertDoesNotThrow(() -> execute("i + 1;"));
        assertThat(consumed, contains(new Complex(1, 1)));
        assertThat(processor.stack, empty());
    }

    @Test
    void testAddIntInt() {
        assertDoesNotThrow(() -> execute("1 + 2;"));
        assertThat(consumed, contains(3));
        assertThat(processor.stack, empty());
    }

    @Test
    void testAddIntIntComplex() {
        assertDoesNotThrow(() -> execute("1 + i;"));
        assertThat(consumed, contains(new Complex(1, 1)));
        assertThat(processor.stack, empty());
    }

    @Test
    void testAddKetKet1() {
        assertDoesNotThrow(() -> execute("|0> + |1>;"));
        assertThat(consumed, contains(Ket.create(1, 1)));
        assertThat(processor.stack, empty());
    }

    @ParameterizedTest
    @CsvSource({
            "|1> + |2>;, 0,1,1,0",
            "|2> + |1>;, 0,1,1,0",
            "|2> + |3>;, 0,0,1,1",
    })
    void testAddKetKet2(String text, float e0, float e1, float e2, float e3) {
        assertDoesNotThrow(() -> execute(text));
        assertThat(consumed, contains(isA(Ket.class)));
        assertThat((Ket) consumed.getFirst(), ketCloseTo(EPSILON, e0, e1, e2, e3));
        assertThat(processor.stack, empty());
    }

    @Test
    void testAssignError() {
        code = List.of(
                new Command.PushInt(sourceContext(0), 1),
                new Command.PushInt(sourceContext(1), 1),
                new Command.Assign(sourceContext(2))
        );
        Throwable ex = assertThrows(Throwable.class, () -> processor.executeCode(code));
        assertEquals("Value 1 is not a string (2)", ex.getMessage());
    }

    @Test
    void testBra() {
        assertDoesNotThrow(() -> execute("<0|;"));
        assertThat(consumed, contains(Bra.zero()));
        assertThat(processor.stack, empty());
        assertTrue(processor.variables.isEmpty());
    }

    @Test
    void testComplex() {
        assertDoesNotThrow(() -> execute("1.;"));
        assertThat(consumed, contains(Complex.create(1)));
        assertThat(processor.stack, empty());
    }

    @ParameterizedTest
    @CsvSource({
            "<0| / <0|;",
            "<00| / <00|;",
            "<0| / <00|;",
            "<00| / <00|;",
    })
    void testDivBraBra(String text) {
        assertDoesNotThrow(() -> execute(text));
        assertThat(consumed, contains(Complex.one()));
        assertThat(processor.stack, empty());
    }

    @Test
    void testDivBraComplex() {
        assertDoesNotThrow(() -> execute("<0| / i;"));
        assertThat(consumed, hasSize(1));
        assertThat((Bra) consumed.getFirst(),
                braCloseTo(Bra.zero().mul(Complex.i().inv()), EPSILON));
        assertThat(processor.stack, empty());
    }

    @Test
    void testDivBraInt() {
        assertDoesNotThrow(() -> execute("<0| / 2;"));
        assertThat(consumed, contains(Bra.zero().mul(1f / 2)));
        assertThat(processor.stack, empty());
    }

    @Test
    void testDivComplexBra() {
        Complex n = Complex.i();
        Bra d = Bra.zero();
        Ket expected = d.conj().mul(d.mul(d.conj()).inv()).mul(n);
        assertDoesNotThrow(() -> execute("i / <0|;"));
        assertThat(consumed, contains(expected));
        assertThat(processor.stack, empty());
    }

    @Test
    void testDivComplexComplex() {
        assertDoesNotThrow(() -> execute("i / i;"));
        assertThat(consumed, contains(new Complex(1, 0)));
        assertThat(processor.stack, empty());
    }

    @Test
    void testDivComplexInt() {
        assertDoesNotThrow(() -> execute("i / 2;"));
        assertThat(consumed, contains(new Complex(0, 0.5f)));
        assertThat(processor.stack, empty());
    }

    @Test
    void testDivComplexKet() {
        Complex n = Complex.i();
        Ket d = Ket.zero();
        Bra expected = d.conj().mul(d.conj().mul(d).inv()).mul(n);
        assertDoesNotThrow(() -> execute("i / |0>;"));
        assertThat(consumed, contains(expected));
        assertThat(processor.stack, empty());
    }

    @Test
    void testDivIntBra() {
        Ket expected = Ket.create(
                Complex.create(2), Complex.zero()
        );
        assertDoesNotThrow(() -> execute("2 / <0|;"));
        assertThat(consumed, contains(expected));
        assertThat(processor.stack, empty());
    }

    @Test
    void testDivIntComplex() {
        assertDoesNotThrow(() -> execute("2 / i;"));
        assertThat(consumed, contains(new Complex(0, -2)));
        assertThat(processor.stack, empty());
    }

    @Test
    void testDivIntIntComplex() {
        assertDoesNotThrow(() -> execute("1 / 2;"));
        assertThat(consumed, contains(Complex.create(1f / 2f)));
        assertThat(processor.stack, empty());
    }

    @Test
    void testDivIntIntInt() {
        assertDoesNotThrow(() -> execute("2 / 2;"));
        assertThat(consumed, contains(1));
        assertThat(processor.stack, empty());
    }

    @Test
    void testDivIntKet() {
        Bra expected = Bra.create(
                Complex.create(2).conj(), Complex.zero().conj()
        );
        assertDoesNotThrow(() -> execute("2 / |0>;"));
        assertThat(consumed, contains(expected));
        assertThat(processor.stack, empty());
    }

    @Test
    void testDivKetComplex() {
        assertDoesNotThrow(() -> execute("|0> / i;"));
        assertThat(consumed, contains(Ket.zero().mul(Complex.i().inv())));
        assertThat(processor.stack, empty());
    }

    @Test
    void testDivKetInt() {
        assertDoesNotThrow(() -> execute("|0> / 2;"));
        assertThat(consumed, contains(Ket.zero().mul(0.5f)));
        assertThat(processor.stack, empty());
    }

    @ParameterizedTest
    @CsvSource({
            "|0> / |0>;",
            "|00> / |00>;",
            "|0> / |00>;",
            "|00> / |00>;",
    })
    void testDivKetKet(String text) {
        assertDoesNotThrow(() -> execute(text));
        assertThat(consumed, contains(Complex.one()));
        assertThat(processor.stack, empty());
    }

    @ParameterizedTest
    @CsvSource({
            "a;,Variable \"a\" not found (a)",
            "|0> * |0>;,Right operand must not be a ket (1.0) |0> (|)",
            "|0> * <0|;,Right operand must not be a bra (1.0) <0| (<)",
            "<0| * <0|;,Right operand must not be a bra (1.0) <0| (<)",
            "<0| / |0>;,Right operand must not be a ket (1.0) |0> (|)",
            "|0> / <0|;,Right operand must not be a bra (1.0) <0| (<)",
            "1 + <0|;,Right operand must not be a bra (1.0) <0| (<)",
            "i + <0|;,Right operand must not be a bra (1.0) <0| (<)",
            "|0> + <0|;,Right operand must not be a bra (1.0) <0| (<)",
            "1 + |0>;,Right operand must not be a ket (1.0) |0> (|)",
            "i + |0>;,Right operand must not be a ket (1.0) |0> (|)",
            "<0| + |0>;,Right operand must not be a ket (1.0) |0> (|)",
            "|0> + 1;,Right operand must not be a complex 1 (1)",
            "|0> + i;,Right operand must not be a complex i (i)",
            "<0| + 1;,Right operand must not be a complex 1 (1)",
            "<0| + i;,Right operand must not be a complex i (i)",

            "1 - <0|;,Right operand must not be a bra (1.0) <0| (<)",
            "i - <0|;,Right operand must not be a bra (1.0) <0| (<)",
            "|0> - <0|;,Right operand must not be a bra (1.0) <0| (<)",
            "1 - |0>;,Right operand must not be a ket (1.0) |0> (|)",
            "i - |0>;,Right operand must not be a ket (1.0) |0> (|)",
            "<0| - |0>;,Right operand must not be a ket (1.0) |0> (|)",
            "|0> - 1;,Right operand must not be a complex 1 (1)",
            "|0> - i;,Right operand must not be a complex i (i)",
            "<0| - 1;,Right operand must not be a complex 1 (1)",
            "<0| - i;,Right operand must not be a complex i (i)",
            "sqrt(|0>);,Argument must not be a ket (1.0) |0> (()",
            "sqrt(<0|);,Argument must not be a bra (1.0) <0| (()",
    })
    void testError(String text, String error) {
        Throwable ex = assertThrows(Throwable.class, () -> execute(text));
        assertEquals(error.replace("$", ","), ex.getMessage());
    }

    @ParameterizedTest
    @CsvSource({
            "sqrt(4);, 2,0",
            "sqrt(4.);, 2,0",
            "sqrt(0.);, 0,0",
            "sqrt(-4.);, 0,2",
            "sqrt(8. * i);, 2,2",
            "sqrt(-8. * i);, 2,-2",
            "sqrt(8. +6* i);, 3,1",
            "sqrt(8. -6* i);, 3,-1",
            "sqrt(-8. +6* i);, 1,3",
            "sqrt(-8. -6* i);, -1,3",
    })
    void testSqrt(String text, float re, float im) {
        assertDoesNotThrow(() -> execute(text));
        assertThat(consumed, contains(isA(Complex.class)));
        assertThat((Complex) consumed.getFirst(), complexClose(re, im, EPSILON));
        assertThat(processor.stack, empty());
    }

    @Test
    void testIm() {
        assertDoesNotThrow(() -> execute("i;"));
        assertThat(consumed, contains(Complex.i()));
        assertThat(processor.stack, empty());
    }

    @Test
    void testInt() {
        assertDoesNotThrow(() -> execute("1;"));
        assertThat(consumed, contains(1));
        assertThat(processor.stack, empty());
        assertTrue(processor.variables.isEmpty());
    }

    @Test
    void testIntComplexComplex() {
        assertDoesNotThrow(() -> execute("1;0.;i;"));
        assertThat(consumed, contains(
                1,
                Complex.zero(),
                Complex.i()
        ));
        assertThat(processor.stack, empty());
        assertTrue(processor.variables.isEmpty());
    }

    @Test
    void testKet() {
        assertDoesNotThrow(() -> execute("|0>;"));
        assertThat(consumed, contains(Ket.zero()));
        assertThat(processor.stack, empty());
        assertTrue(processor.variables.isEmpty());
    }

    @Test
    void testLet() {
        assertDoesNotThrow(() -> execute("let a = 0.;"));
        assertThat(consumed, contains(Complex.zero()));
        assertThat(processor.stack, empty());
        assertThat(processor.variables, hasEntry("a", Complex.zero()));
    }

    @Test
    void testMulBraComplex() {
        assertDoesNotThrow(() -> execute("<0|*i;"));
        assertThat(consumed, contains(Bra.zero().mul(Complex.i())));
        assertThat(processor.stack, empty());
    }

    @Test
    void testMulBraInt() {
        assertDoesNotThrow(() -> execute("<0|*2;"));
        assertThat(consumed, contains(Bra.zero().mul(2)));
        assertThat(processor.stack, empty());
    }

    @ParameterizedTest
    @CsvSource({
            "<0|*|0>;",
            "<00|*|0>;",
            "<0|*|00>;",
            "<00|*|00>;",
    })
    void testMulBraKet(String text) {
        assertDoesNotThrow(() -> execute(text));
        assertThat(consumed, contains(Complex.one()));
        assertThat(processor.stack, empty());
    }

    @Test
    void testMulComplexBra() {
        assertDoesNotThrow(() -> execute("i*<0|;"));
        assertThat(consumed, contains(Bra.zero().mul(Complex.i())));
        assertThat(processor.stack, empty());
    }

    @Test
    void testMulComplexComplex() {
        assertDoesNotThrow(() -> execute("i*i;"));
        assertThat(consumed, contains(new Complex(-1, 0)));
        assertThat(processor.stack, empty());
    }

    @Test
    void testMulComplexInt() {
        assertDoesNotThrow(() -> execute("i*2;"));
        assertThat(consumed, contains(new Complex(0, 2)));
        assertThat(processor.stack, empty());
    }

    @Test
    void testMulComplexKet() {
        assertDoesNotThrow(() -> execute("i*|0>;"));
        assertThat(consumed, contains(Ket.zero().mul(Complex.i())));
        assertThat(processor.stack, empty());
    }

    @Test
    void testMulIntBra() {
        assertDoesNotThrow(() -> execute("2*<0|;"));
        assertThat(consumed, contains(Bra.zero().mul(2)));
        assertThat(processor.stack, empty());
    }

    @Test
    void testMulIntComplex() {
        assertDoesNotThrow(() -> execute("2*i;"));
        assertThat(consumed, contains(new Complex(0, 2)));
        assertThat(processor.stack, empty());
    }

    @Test
    void testMulIntInt() {
        assertDoesNotThrow(() -> execute("2*2;"));
        assertThat(consumed, contains(4));
        assertThat(processor.stack, empty());
    }

    @Test
    void testMulIntKet() {
        assertDoesNotThrow(() -> execute("2*|0>;"));
        assertThat(consumed, contains(Ket.zero().mul(2)));
        assertThat(processor.stack, empty());
    }

    @Test
    void testMulKetComplex() {
        assertDoesNotThrow(() -> execute("|0>*i;"));
        assertThat(consumed, contains(Ket.zero().mul(Complex.i())));
        assertThat(processor.stack, empty());
    }

    @Test
    void testMulKetInt() {
        assertDoesNotThrow(() -> execute("|0>*2;"));
        assertThat(consumed, contains(Ket.zero().mul(2)));
        assertThat(processor.stack, empty());
    }

    @Test
    void testNegBra() {
        assertDoesNotThrow(() -> execute("-<0|;"));
        assertThat(consumed, contains(Bra.zero().neg()));
        assertThat(processor.stack, empty());
        assertTrue(processor.variables.isEmpty());
    }

    @Test
    void testNegateComplex() {
        assertDoesNotThrow(() -> execute("-i;"));
        assertThat(consumed, contains(Complex.i().neg()));
        assertThat(processor.stack, empty());
    }

    @Test
    void testNegateInt() {
        assertDoesNotThrow(() -> execute("-1;"));
        assertThat(consumed, contains(-1));
        assertThat(processor.stack, empty());
    }

    @Test
    void testNegateKet() {
        assertDoesNotThrow(() -> execute("-|1>;"));
        assertThat(consumed, contains(Ket.one().neg()));
        assertThat(processor.stack, empty());
    }

    @Test
    void testRetrieveError() {
        code = List.of(
                new Command.PushInt(sourceContext(0), 1),
                new Command.RetrieveVar(sourceContext(1))
        );
        Throwable ex = assertThrows(Throwable.class, () -> processor.executeCode(code));
        assertEquals("Value 1 is not a string (1)", ex.getMessage());
    }

    @Test
    void testSubBraBra1() {
        assertDoesNotThrow(() -> execute("<0| - <1|;"));
        assertThat(consumed, contains(isA(Bra.class)));
        assertThat((Bra) consumed.getFirst(), braCloseTo(EPSILON, 1, -1));
        assertThat(processor.stack, empty());
    }

    @ParameterizedTest
    @CsvSource({
            "<1| - <2|;, 0,1,-1,0",
            "<2| - <1|;, 0,-1,1,0",
            "<2| - <3|;, 0,0,1,-1",
    })
    void testSubBraBra2(String text, float e0, float e1, float e2, float e3) {
        assertDoesNotThrow(() -> execute(text));
        assertThat(consumed, contains(isA(Bra.class)));
        assertThat((Bra) consumed.getFirst(), braCloseTo(EPSILON, e0, e1, e2, e3));
        assertThat(processor.stack, empty());
    }

    @Test
    void testSubComplexComplex() {
        assertDoesNotThrow(() -> execute("i - i;"));
        assertThat(consumed, contains(Complex.zero()));
        assertThat(processor.stack, empty());
    }

    @Test
    void testSubComplexInt() {
        assertDoesNotThrow(() -> execute("i - 1;"));
        assertThat(consumed, contains(new Complex(-1, 1)));
        assertThat(processor.stack, empty());
    }

    @Test
    void testSubIntComplex() {
        assertDoesNotThrow(() -> execute("1 - i;"));
        assertThat(consumed, contains(new Complex(1, -1)));
        assertThat(processor.stack, empty());
    }

    @Test
    void testSubIntInt() {
        assertDoesNotThrow(() -> execute("1 - 2;"));
        assertThat(consumed, contains(-1));
        assertThat(processor.stack, empty());
    }

    @Test
    void testSubKetKet1() {
        assertDoesNotThrow(() -> execute("|0> - |1>;"));
        assertThat(consumed, contains(isA(Ket.class)));
        assertThat((Ket) consumed.getFirst(), ketCloseTo(EPSILON, 1, -1));
        assertThat(processor.stack, empty());
    }

    @ParameterizedTest
    @CsvSource({
            "|1> - |2>;, 0,1,-1,0",
            "|2> - |1>;, 0,-1,1,0",
            "|2> - |3>;, 0,0,1,-1",
    })
    void testSubKetKet2(String text, float e0, float e1, float e2, float e3) {
        assertDoesNotThrow(() -> execute(text));
        assertThat(consumed, contains(isA(Ket.class)));
        assertThat((Ket) consumed.getFirst(), ketCloseTo(EPSILON, e0, e1, e2, e3));
        assertThat(processor.stack, empty());
    }

    @Test
    void testBraComplex() {
        assertDoesNotThrow(() -> execute("<0|^;"));
        assertThat(consumed, contains(isA(Ket.class)));
        assertThat((Ket) consumed.getFirst(), ketCloseTo(Ket.zero(), EPSILON));
        assertThat(processor.stack, empty());
    }

    @Test
    void testConjComplex() {
        assertDoesNotThrow(() -> execute("i^;"));
        assertThat(consumed, contains(isA(Complex.class)));
        assertThat((Complex) consumed.getFirst(), complexClose(0, -1, EPSILON));
        assertThat(processor.stack, empty());
    }

    @Test
    void testConjInt() {
        assertDoesNotThrow(() -> execute("1^;"));
        assertThat(consumed, contains(1));
        assertThat(processor.stack, empty());
    }

    @Test
    void testKetComplex() {
        assertDoesNotThrow(() -> execute("|0>^;"));
        assertThat(consumed, contains(isA(Bra.class)));
        assertThat((Bra) consumed.getFirst(), braCloseTo(Bra.zero(), EPSILON));
        assertThat(processor.stack, empty());
    }

    @Test
    void testVar() {
        assertDoesNotThrow(() -> execute("let a = 0.;a;"));
        assertThat(consumed, contains(
                Complex.zero(),
                Complex.zero()));
        assertThat(processor.stack, empty());
        assertThat(processor.variables, hasEntry("a", Complex.zero()));
    }
}