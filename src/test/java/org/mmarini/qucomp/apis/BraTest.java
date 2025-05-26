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
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mmarini.qucomp.Matchers.complexClose;

class BraTest {

    public static final float HALF_SQRT2 = (float) (sqrt(2) / 2);
    public static final float EPSILON = 1e-3F;

    public static Stream<Arguments> dataBraket() {
        return Stream.of(
                Arguments.arguments(
                        Bra.zero(), Ket.zero(), Complex.one()
                ),
                Arguments.arguments(
                        Bra.one(), Ket.one(), Complex.one()
                ),
                Arguments.arguments(
                        Bra.plus(), Ket.plus(), Complex.one()
                ),
                Arguments.arguments(
                        Bra.minus(), Ket.minus(), Complex.one()
                ),
                Arguments.arguments(
                        Bra.i(), Ket.i(), Complex.one()
                ),
                Arguments.arguments(
                        Bra.minus_i(), Ket.minus_i(), Complex.one()
                ),
                Arguments.arguments(
                        Bra.zero(), Ket.one(), Complex.zero()
                ),
                Arguments.arguments(
                        Bra.one(), Ket.zero(), Complex.zero()
                ),
                Arguments.arguments(
                        Bra.plus(), Ket.minus(), Complex.zero()
                ),
                Arguments.arguments(
                        Bra.minus(), Ket.plus(), Complex.zero()
                ),
                Arguments.arguments(
                        Bra.i(), Ket.minus_i(), Complex.zero()
                ),
                Arguments.arguments(
                        Bra.minus_i(), Ket.i(), Complex.zero()
                )
        );
    }

    @Test
    void add() {
        Bra bra0 = Bra.ZERO;
        Bra bra1 = Bra.ONE;
        Bra add = bra0.add(bra1);
        assertArrayEquals(new Complex[]{
                        Complex.ONE.conj(),
                        Complex.ONE.conj()},
                add.values());
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
        Bra b = Bra.base(value, size);
        int n = 1 << size;
        assertEquals(n, b.values().length);
        for (int i = 0; i < size; i++) {
            assertEquals(i == value
                            ? Complex.one().conj() : Complex.zero().conj(),
                    b.values()[i]);
        }
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
    void mul() {
        Bra ket0 = new Bra(new Complex[]{Complex.one(), Complex.one()});
        Bra ket = ket0.mul(3);
        assertArrayEquals(new Complex[]{
                        Complex.create(3),
                        Complex.create(3)},
                ket.values());
    }

    @ParameterizedTest
    @MethodSource("dataBraket")
    void mulScalar(Bra bra, Ket ket, Complex expected) {
        Complex mul = bra.mul(ket);
        assertThat(mul, complexClose(expected, EPSILON));
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

    @Test
    void sub() {
        Bra ket0 = Bra.ZERO;
        Bra ket1 = Bra.ONE;
        Bra add = ket0.sub(ket1);
        assertArrayEquals(new Complex[]{
                        Complex.ONE,
                        Complex.create(-1)},
                add.values());
    }

    @Test
    void testMul() {
        Bra ket0 = new Bra(new Complex[]{Complex.one(), Complex.one()});
        Bra ket = ket0.mul(Complex.i());
        assertArrayEquals(new Complex[]{
                        Complex.i(),
                        Complex.i()},
                ket.values());
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
    void zero() {
        Bra zero = Bra.zero();
        assertArrayEquals(new Complex[]{
                        Complex.ONE.conj(),
                        Complex.ZERO.conj()},
                zero.values());
    }
}