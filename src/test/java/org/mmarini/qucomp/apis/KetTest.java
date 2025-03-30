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

import static java.lang.Math.sqrt;
import static org.junit.jupiter.api.Assertions.*;

class KetTest {

    public static final double HALF_SQRT2 = sqrt(2) / 2;

    @Test
    void add() {
        Ket ket0 = Ket.ZERO;
        Ket ket1 = Ket.ONE;
        Ket add = ket0.add(ket1);
        assertArrayEquals(new Complex[]{
                        Complex.ONE,
                        Complex.ONE},
                add.values());
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
    void minus() {
        Ket one = Ket.minus();
        assertArrayEquals(new Complex[]{
                        Complex.create(HALF_SQRT2),
                        Complex.create(-HALF_SQRT2)},
                one.values());
    }

    @Test
    void minus_i() {
        Ket one = Ket.minus_i();
        assertArrayEquals(new Complex[]{
                        Complex.create(HALF_SQRT2),
                        Complex.i(-HALF_SQRT2)},
                one.values());
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