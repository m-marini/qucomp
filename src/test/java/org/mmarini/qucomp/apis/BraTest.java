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
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static java.lang.Math.sqrt;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mmarini.Matchers.complexClose;

class BraTest {

    public static final double HALF_SQRT2 = sqrt(2) / 2;
    public static final double EPSILON = 1e-3;

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
        Bra one = Bra.minus();
        assertArrayEquals(new Complex[]{
                        Complex.create(HALF_SQRT2).conj(),
                        Complex.create(-HALF_SQRT2).conj()},
                one.values());
    }

    @Test
    void minus_i() {
        Bra one = Bra.minus_i();
        assertArrayEquals(new Complex[]{
                        Complex.create(HALF_SQRT2).conj(),
                        Complex.i(-HALF_SQRT2).conj()},
                one.values());
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
    void zero() {
        Bra zero = Bra.zero();
        assertArrayEquals(new Complex[]{
                        Complex.ONE.conj(),
                        Complex.ZERO.conj()},
                zero.values());
    }
}