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

package org.mmarini.qucomp.apis;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static java.lang.Math.sqrt;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.matchesPattern;
import static org.junit.jupiter.api.Assertions.*;
import static org.mmarini.Matchers.complexClose;

class KetTest {

    public static final float HALF_SQRT2 = (float) (sqrt(2) / 2);
    public static final float EPSILON = 1e-6F;

    public static Stream<Arguments> fromText1bitDataSet() {
        return Stream.of(
                Arguments.arguments("|0>", Ket.zero()),
                Arguments.arguments("|1>", Ket.one()),
                Arguments.arguments("|+>", Ket.plus()),
                Arguments.arguments("|->", Ket.minus()),
                Arguments.arguments("|i>", Ket.i()),
                Arguments.arguments("|-i>", Ket.minus_i())
        );
    }

    public static Stream<Arguments> fromText2bitDataSet() {
        return Stream.of(
                Arguments.arguments("|0>|0>", Ket.base(0, 2)),
                Arguments.arguments("|0>|1>", Ket.base(1, 2)),
                Arguments.arguments("|1>|0>", Ket.base(2, 2)),
                Arguments.arguments("|1>|1>", Ket.base(3, 2))
        );
    }

    @Test
    void add() {
        Ket ket0 = Ket.ZERO;
        Ket ket1 = Ket.ONE;
        Ket add = ket0.add(ket1);
        assertThat(add.at(0), complexClose(1, EPSILON));
        assertThat(add.at(1), complexClose(1, EPSILON));
    }

    @ParameterizedTest
    @MethodSource("fromText1bitDataSet")
    void fromText1bit(String text, Ket exp) {
        // Given
        Ket ket = Ket.fromText(text);
        assertEquals(exp, ket);
    }

    @Test
    void conj() {
        Ket ket = Ket.I;
        Bra conj = ket.conj();
        assertEquals(Bra.create(
                Complex.create(HALF_SQRT2).conj(),
                Complex.i(HALF_SQRT2).conj()
        ), conj);
    }

    @Test
    void create() {
        Ket ket = Ket.create(1, 1);
        assertArrayEquals(new Complex[]{
                        Complex.ONE,
                        Complex.ONE},
                ket.values());
    }

    @Test
    void create1() {
        Ket ket = Ket.create(Complex.ONE, Complex.ONE);
        assertArrayEquals(new Complex[]{
                        Complex.ONE,
                        Complex.ONE},
                ket.values());
    }

    @ParameterizedTest
    @CsvSource({
            "0, 1",
            "1, 1",
            "0, 2",
            "1, 2",
            "2, 2",
            "3, 2",
            "0, 3",
            "1, 3",
            "2, 3",
            "3, 3",
            "4, 3",
            "5, 3",
            "6, 3",
            "7, 3",
    })
    void base(int value, int size) {
        Ket b = Ket.base(value, size);
        int n = 1 << size;
        assertEquals(n, b.values().length);
        for (int i = 0; i < size; i++) {
            assertEquals(i == value
                            ? Complex.one() : Complex.zero(),
                    b.values()[i]);
        }
    }

    @ParameterizedTest
    @CsvSource({
            "0,0, 1,0,0,0",
            "0,1, 0,1,0,0",
            "1,0, 0,0,1,0",
            "1,1, 0,0,0,1",
    })
    void cross(int b1, int b0, float exp0, float exp1, float exp2, float exp3) {
        // Given
        Ket ket0 = b0 == 0 ? Ket.zero() : Ket.one();
        Ket ket1 = b1 == 0 ? Ket.zero() : Ket.one();
        Ket ket = ket1.cross(ket0);
        assertEquals(4, ket.values().length);
        assertThat(ket.at(0), complexClose(exp0, EPSILON));
        assertThat(ket.at(1), complexClose(exp1, EPSILON));
        assertThat(ket.at(2), complexClose(exp2, EPSILON));
        assertThat(ket.at(3), complexClose(exp3, EPSILON));
    }

    @ParameterizedTest
    @MethodSource("fromText2bitDataSet")
    void fromText2bit(String text, Ket exp) {
        // Given
        Ket ket = Ket.fromText(text);
        assertEquals(exp, ket);
    }

    @ParameterizedTest
    @CsvSource({
            ", Missing ket expression",
            "|, Missing element after \\|",
            "|>, Unknow ket \\|>",
    })
    void fromTextError(String text, String exp) {
        // Given
        IllegalArgumentException th = assertThrows(IllegalArgumentException.class, () ->
                Ket.fromText(text != null ? text : ""));
        assertThat(th.getMessage(), matchesPattern(exp));
    }

    @Test
    void toString0Test() {
        Ket ket = Ket.create(Complex.zero(), Complex.zero(), Complex.zero(), Complex.zero());
        assertEquals("(0.0) |3>", ket.toString());
    }

    @Test
    void toStringTest() {
        Ket ket = Ket.create(new Complex(0, 0), new Complex(2, 0), new Complex(0, 2), new Complex(2, 2));
        assertEquals("(2.0) |1> + (2.0 i) |2> + (2.0 +2.0 i) |3>", ket.toString());
    }

    @Test
    void minus() {
        Ket ket = Ket.minus();
        assertThat(ket.at(0), complexClose(HALF_SQRT2, EPSILON));
        assertThat(ket.at(1), complexClose(-HALF_SQRT2, EPSILON));
    }

    @Test
    void testMul1() {
        // Given
        Matrix x = Matrix.create(2, 2,
                0, 1,
                1, 0);
        // When
        Ket notZero = Ket.zero().mul(x);
        Ket notOne = Ket.one().mul(x);
        // Then
        assertThat(notZero.values()[0], complexClose(0, EPSILON));
        assertThat(notZero.values()[1], complexClose(1, EPSILON));
        assertThat(notOne.values()[0], complexClose(1, EPSILON));
        assertThat(notOne.values()[1], complexClose(0, EPSILON));
    }

    @Test
    void equals() {
        Ket ket1 = Ket.create(Complex.create(1), Complex.create(2));
        Ket ket2 = Ket.create(Complex.create(1), Complex.create(2));
        assertEquals(ket1, ket2);
        assertEquals(ket2, ket1);
    }

    @Test
    void i() {
        Ket one = Ket.i();
        assertArrayEquals(new Complex[]{
                        Complex.create(HALF_SQRT2),
                        Complex.i(HALF_SQRT2)},
                one.values());
    }

    @Test
    void minus_i() {
        Ket one = Ket.minus_i();
        assertThat(one.at(0), complexClose(HALF_SQRT2, EPSILON));
        assertThat(one.at(1), complexClose(Complex.i(-HALF_SQRT2), EPSILON));
    }

    @Test
    void prob() {
        double[] zero = Ket.zero().prob();
        double[] one = Ket.one().prob();
        double[] plus = Ket.plus().prob();

        assertArrayEquals(new double[]{1, 0}, zero);
        assertArrayEquals(new double[]{0, 1}, one);
        assertThat(plus[0], closeTo(0.5, EPSILON));
        assertThat(plus[1], closeTo(0.5, EPSILON));
    }

    @Test
    void mul() {
        Ket ket0 = new Ket(new Complex[]{Complex.one(), Complex.one()});
        Ket ket = ket0.mul(3);
        assertArrayEquals(new Complex[]{
                        Complex.create(3),
                        Complex.create(3)},
                ket.values());
    }

    @Test
    void neg() {
        Ket ket0 = Ket.ZERO;
        Ket add = ket0.neg();
        assertArrayEquals(new Complex[]{
                        Complex.ONE.neg(),
                        Complex.ZERO.neg()},
                add.values());
    }

    @Test
    void one() {
        Ket one = Ket.one();
        assertArrayEquals(new Complex[]{
                        Complex.ZERO,
                        Complex.ONE},
                one.values());
    }

    @Test
    void plus() {
        Ket one = Ket.plus();
        assertArrayEquals(new Complex[]{
                        Complex.create(HALF_SQRT2),
                        Complex.create(HALF_SQRT2)},
                one.values());
    }

    @Test
    void sub() {
        Ket ket0 = Ket.ZERO;
        Ket ket1 = Ket.ONE;
        Ket add = ket0.sub(ket1);
        assertArrayEquals(new Complex[]{
                        Complex.ONE,
                        Complex.create(-1)},
                add.values());
    }

    @Test
    void testMul() {
        Ket ket0 = new Ket(new Complex[]{Complex.one(), Complex.one()});
        Ket ket = ket0.mul(Complex.i());
        assertArrayEquals(new Complex[]{
                        Complex.i(),
                        Complex.i()},
                ket.values());
    }

    @Test
    void zero() {
        Ket zero = Ket.zero();
        assertArrayEquals(new Complex[]{
                        Complex.ONE,
                        Complex.ZERO},
                zero.values());
    }
}