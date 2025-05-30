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
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.*;
import static org.mmarini.qucomp.Matchers.braCloseTo;
import static org.mmarini.qucomp.Matchers.complexClose;

class BraTest {

    public static final float HALF_SQRT2 = (float) (sqrt(2) / 2);
    public static final float EPSILON = 1e-3F;

    public static Stream<Arguments> argTestMulKet() {
        return Stream.of(
                Arguments.arguments(Bra.zero(), Ket.zero(), Complex.one()),
                Arguments.arguments(Bra.one(), Ket.one(), Complex.one()),
                Arguments.arguments(Bra.plus(), Ket.plus(), Complex.one()),
                Arguments.arguments(Bra.minus(), Ket.minus(), Complex.one()),
                Arguments.arguments(Bra.i(), Ket.i(), Complex.one()),
                Arguments.arguments(Bra.minus_i(), Ket.minus_i(), Complex.one()),

                Arguments.arguments(Bra.zero(), Ket.zero(), Complex.one()),
                Arguments.arguments(Bra.zero(), Ket.one(), Complex.zero()),
                Arguments.arguments(Bra.zero(), Ket.base(2), Complex.zero()),
                Arguments.arguments(Bra.zero(), Ket.base(3), Complex.zero()),
                Arguments.arguments(Bra.one(), Ket.zero(), Complex.zero()),
                Arguments.arguments(Bra.one(), Ket.one(), Complex.one()),
                Arguments.arguments(Bra.one(), Ket.base(2), Complex.zero()),
                Arguments.arguments(Bra.one(), Ket.base(3), Complex.zero()),
                Arguments.arguments(Bra.base(2), Ket.zero(), Complex.zero()),
                Arguments.arguments(Bra.base(2), Ket.one(), Complex.zero()),
                Arguments.arguments(Bra.base(2), Ket.base(2), Complex.one()),
                Arguments.arguments(Bra.base(2), Ket.base(3), Complex.zero()),
                Arguments.arguments(Bra.base(3), Ket.zero(), Complex.zero()),
                Arguments.arguments(Bra.base(3), Ket.one(), Complex.zero()),
                Arguments.arguments(Bra.base(3), Ket.base(2), Complex.zero()),
                Arguments.arguments(Bra.base(3), Ket.base(3), Complex.one())
        );
    }

    public static Stream<Arguments> argTestMulMatrix() {
        Matrix m22 = Matrix.create(2, 2,
                0, 1,
                1, 0);
        Matrix m42 = Matrix.create(4, 2,
                1, 0,
                0, 1,
                1, 0,
                0, 1
        );
        Matrix m44 = Matrix.create(4, 4,
                0, 1, 0, 0,
                1, 0, 0, 0,
                0, 0, 0, 1,
                0, 0, 1, 0);
        return Stream.of(
                Arguments.of(Bra.zero(), m22, Bra.one()),
                Arguments.of(Bra.one(), m22, Bra.zero()),

                Arguments.of(Bra.zero(), m44, Bra.create(0, 1, 0, 0)),
                Arguments.of(Bra.one(), m44, Bra.create(1, 0, 0, 0)),
                Arguments.of(Bra.base(2), m44, Bra.create(0, 0, 0, 1)),
                Arguments.of(Bra.base(3), m44, Bra.create(0, 0, 1, 0)),
                Arguments.of(Bra.base(2), m44, Bra.create(0, 0, 0, 1)),

                Arguments.of(Bra.zero(), m42, Bra.zero()),
                Arguments.of(Bra.one(), m42, Bra.one()),
                Arguments.of(Bra.base(2), m42, Bra.zero()),
                Arguments.of(Bra.base(3), m42, Bra.one())
        );
    }

    public static Stream<Arguments> argTestMulMatrixError() {
        Matrix m22 = Matrix.create(2, 2,
                0, 1,
                1, 0);
        return Stream.of(
                Arguments.of(m22, Bra.base(2), "Expected matrix with at least 4 rows (2)")
        );
    }

    public static Stream<Arguments> argsTestAdd() {
        return Stream.of(
                Arguments.of(Bra.zero(), Bra.zero(), Bra.create(2, 0)),
                Arguments.of(Bra.zero(), Bra.one(), Bra.create(1, 1)),
                Arguments.of(Bra.one(), Bra.zero(), Bra.create(1, 1)),
                Arguments.of(Bra.zero(), Bra.base(2), Bra.create(1, 0, 1, 0)),
                Arguments.of(Bra.base(2), Bra.zero(), Bra.create(1, 0, 1, 0))
        );
    }

    public static Stream<Arguments> argsTestSub() {
        return Stream.of(
                Arguments.of(Bra.zero(), Bra.zero(), Bra.create(0, 0)),
                Arguments.of(Bra.zero(), Bra.base(2), Bra.create(1, 0, -1, 0)),
                Arguments.of(Bra.base(2), Bra.zero(), Bra.create(-1, 0, 1, 0)),
                Arguments.of(Bra.base(2), Bra.base(3), Bra.create(0, 0, 1, -1))
        );
    }

    @Test
    void conj() {
        Bra bra = Bra.I;
        Ket conj = bra.conj();
        assertEquals(Ket.create(
                Complex.create(HALF_SQRT2),
                Complex.i(HALF_SQRT2)
        ), conj);
    }

    @Test
    void create() {
        Bra ket = Bra.create(1, 1);
        assertArrayEquals(new Complex[]{
                        Complex.ONE,
                        Complex.ONE},
                ket.values());
    }

    @Test
    void create1() {
        Bra ket = Bra.create(Complex.ONE, Complex.ONE);
        assertArrayEquals(new Complex[]{
                        Complex.ONE,
                        Complex.ONE},
                ket.values());
    }

    @Test
    void cross() {
        Bra ket = Bra.zero().cross(Bra.zero());
        Complex[] values = ket.values();
        assertEquals(4, values.length);
        assertThat(values[0], complexClose(1, EPSILON));
        assertThat(values[1], complexClose(0, EPSILON));
        assertThat(values[2], complexClose(0, EPSILON));
        assertThat(values[3], complexClose(0, EPSILON));
    }

    @Test
    void equals() {
        Bra bra1 = Bra.create(Complex.create(1), Complex.create(2));
        Bra bra2 = Bra.create(Complex.create(1), Complex.create(2));
        assertEquals(bra1, bra2);
        assertEquals(bra2, bra1);
    }

    @Test
    void i() {
        Bra one = Bra.i();
        assertArrayEquals(new Complex[]{
                        Complex.create(HALF_SQRT2).conj(),
                        Complex.i(HALF_SQRT2).conj()},
                one.values());
    }

    @Test
    void minus() {
        Bra bra = Bra.minus();
        assertThat(bra.at(0), complexClose(HALF_SQRT2, EPSILON));
        assertThat(bra.at(1), complexClose(-HALF_SQRT2, EPSILON));
    }

    @Test
    void minus_i() {
        Bra bra = Bra.minus_i();
        assertThat(bra.at(0), complexClose(HALF_SQRT2, EPSILON));
        assertThat(bra.at(1), complexClose(Complex.i(HALF_SQRT2), EPSILON));
    }

    @Test
    void neg() {
        Bra ket0 = Bra.ZERO;
        Bra add = ket0.neg();
        assertArrayEquals(new Complex[]{
                        Complex.ONE.neg().conj(),
                        Complex.ZERO.neg().conj()},
                add.values());
    }

    @Test
    void one() {
        Bra one = Bra.one();
        assertArrayEquals(new Complex[]{
                        Complex.ZERO.conj(),
                        Complex.ONE.conj()},
                one.values());
    }

    @Test
    void plus() {
        Bra one = Bra.plus();
        assertArrayEquals(new Complex[]{
                        Complex.create(HALF_SQRT2).conj(),
                        Complex.create(HALF_SQRT2).conj()},
                one.values());
    }

    @ParameterizedTest
    @MethodSource("argsTestAdd")
    void testAdd(Bra left, Bra right, Bra exp) {
        Bra add = left.add(right);
        assertThat(add, braCloseTo(exp, EPSILON));
    }

    @ParameterizedTest
    @CsvSource({
            "0, 2",
            "1, 2",
            "2, 4",
            "3, 4",
            "4, 8",
            "5, 8",
            "6, 8",
            "7, 8",
    })
    void testBase(int value, int size) {
        Bra b = Bra.base(value);
        assertEquals(size, b.numStates());
        for (int i = 0; i < size; i++) {
            assertEquals(i == value
                            ? Complex.one().conj() : Complex.zero().conj(),
                    b.at(i),
                    "value at " + i);
        }
    }

    @Test
    void testMul1() {
        // Given
        Matrix x = Matrix.create(2, 2,
                0, 1,
                1, 0);
        // When
        Bra notZero = Bra.zero().mul(x);
        Bra notOne = Bra.one().mul(x);
        // Then
        assertThat(notZero.values()[0], complexClose(0, EPSILON));
        assertThat(notZero.values()[1], complexClose(1, EPSILON));
        assertThat(notOne.values()[0], complexClose(1, EPSILON));
        assertThat(notOne.values()[1], complexClose(0, EPSILON));
    }

    @Test
    void testMulComplex() {
        Bra ket0 = new Bra(new Complex[]{Complex.one(), Complex.one()});
        Bra ket = ket0.mul(Complex.i());
        assertArrayEquals(new Complex[]{
                        Complex.i(),
                        Complex.i()},
                ket.values());
    }

    @Test
    void testMulFloat() {
        Bra ket0 = new Bra(new Complex[]{Complex.one(), Complex.one()});
        Bra ket = ket0.mul(3);
        assertArrayEquals(new Complex[]{
                        Complex.create(3),
                        Complex.create(3)},
                ket.values());
    }

    @ParameterizedTest
    @MethodSource("argTestMulKet")
    void testMulKet(Bra bra, Ket ket, Complex expected) {
        Complex mul = bra.mul(ket);
        assertThat(mul, complexClose(expected, EPSILON));
    }

    @ParameterizedTest
    @MethodSource("argTestMulMatrix")
    void testMulMatrix(Bra right, Matrix left, Bra exp) {
        Bra ket = right.mul(left);
        assertThat(ket, braCloseTo(exp, EPSILON));
    }

    @ParameterizedTest
    @MethodSource("argTestMulMatrixError")
    void testMulMatrixError(Matrix left, Bra right, String exp) {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> right.mul(left));
        assertThat(ex.getMessage(), equalTo(exp));
    }

    @ParameterizedTest
    @MethodSource("argsTestSub")
    void testSub(Bra left, Bra right, Bra exp) {
        Bra add = left.sub(right);
        assertThat(add, braCloseTo(exp, EPSILON));
    }

    @Test
    void zero() {
        Bra zero = Bra.zero();
        assertArrayEquals(new Complex[]{
                        Complex.ONE.conj(),
                        Complex.ZERO.conj()},
                zero.values());
    }
}