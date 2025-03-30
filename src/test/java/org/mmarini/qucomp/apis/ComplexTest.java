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
import org.mmarini.ArgumentsGenerator;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class ComplexTest {

    public static final long SEED = 1234L;
    public static final double EPSILON = 1e-3;
    public static final double EPSILON_MIN = 0.999e-3;
    public static final double EPSILON_MAX = 1.001e-3;

    public static Stream<Arguments> dataComplex1() {
        return ArgumentsGenerator.createStream(SEED,
                ArgumentsGenerator.uniform(-10, 10),
                ArgumentsGenerator.uniform(-10, 10)
        );
    }

    public static Stream<Arguments> dataComplex2() {
        return ArgumentsGenerator.createStream(SEED,
                ArgumentsGenerator.uniform(-10, 10),
                ArgumentsGenerator.uniform(-10, 10),
                ArgumentsGenerator.uniform(-10, 10),
                ArgumentsGenerator.uniform(-10, 10)
        );
    }

    @ParameterizedTest
    @MethodSource("dataComplex2")
    void add(double a, double ai, double b, double bi) {
        // Given
        Complex ac = new Complex(a, ai);
        Complex bc = new Complex(b, bi);
        // When
        Complex sumab = ac.add(bc);
        Complex sumba = bc.add(ac);
        // Then
        assertEquals(a + b, sumab.real());
        assertEquals(ai + bi, sumab.im());
        assertEquals(sumab, sumba);
        assertEquals(Complex.zero(), ac.add(ac.neg()));
        assertEquals(Complex.zero(), bc.add(bc.neg()));
    }

    @ParameterizedTest
    @MethodSource("dataComplex1")
    void conj(double a, double b) {
        // Given
        Complex c = new Complex(a, b);
        // When
        Complex conj = c.conj();
        // Then
        assertEquals(a, conj.real());
        assertEquals(-b, conj.im());
    }

    @Test
    void create() {
        Complex c = Complex.create(10);
        assertEquals(10D, c.real());
        assertEquals(0D, c.im());
    }

    @ParameterizedTest
    @MethodSource("dataComplex2")
    void div(double a, double ai, double b, double bi) {
        // Given
        Complex ac = new Complex(a, ai);
        Complex bc = new Complex(b, bi);
        // When
        Complex ab = ac.div(bc);
        Complex ba = bc.div(ac);
        // Then
        double ma2 = a * a + ai * ai;
        double mb2 = b * b + bi * bi;
        assertEquals((a * b + ai * bi) / mb2, ab.real());
        assertEquals((-a * bi + ai * b) / mb2, ab.im());
        assertEquals((a * b + ai * bi) / ma2, ba.real());
        assertEquals((a * bi - ai * b) / ma2, ba.im());

        if (ma2 > 0) {
            assertEquals(Complex.one(), ac.div(ac));
        }
        if (mb2 > 0) {
            assertEquals(Complex.one(), bc.div(bc));
        }
    }

    @Test
    void i() {
        Complex c = Complex.i();
        assertEquals(0D, c.real());
        assertEquals(1D, c.im());
    }

    @ParameterizedTest
    @MethodSource("dataComplex1")
    void i1(double a, double b) {
        Complex c = Complex.i(b);
        assertEquals(0D, c.real());
        assertEquals(b, c.im());
    }

    @ParameterizedTest
    @MethodSource("dataComplex1")
    void im(double a, double b) {
        Complex c = new Complex(a, b);
        assertEquals(b, c.im());
    }

    @ParameterizedTest
    @MethodSource("dataComplex1")
    void inv(double a, double b) {
        // Given
        Complex c = new Complex(a, b);
        // When
        Complex inv = c.inv();
        // Then
        assertEquals(a / (a * a + b * b), inv.real());
        assertEquals(-b / (a * a + b * b), inv.im());
    }

    @ParameterizedTest
    @MethodSource("dataComplex1")
    void isClose(double a, double b) {
        // Given
        Complex ac = new Complex(a, b);
        Complex eqPlus = new Complex(a + EPSILON_MIN, b + EPSILON_MIN);
        Complex eqMinus = new Complex(a - EPSILON_MIN, b - EPSILON_MIN);
        Complex neqPlus = new Complex(a + EPSILON_MAX, b);
        Complex neqMinus = new Complex(a - EPSILON_MAX, b);
        Complex neqPlus1 = new Complex(a, b + EPSILON_MAX);
        Complex neqMinus1 = new Complex(a, b - EPSILON_MAX);
        // When
        // Then
        assertTrue(ac.isClose(eqPlus, EPSILON));
        assertTrue(ac.isClose(eqMinus, EPSILON));
        assertFalse(ac.isClose(neqPlus, EPSILON));
        assertFalse(ac.isClose(neqMinus, EPSILON));
        assertFalse(ac.isClose(neqPlus1, EPSILON));
        assertFalse(ac.isClose(neqMinus1, EPSILON));
    }

    @ParameterizedTest
    @MethodSource("dataComplex1")
    void module(double a, double b) {
        // Given
        Complex c = new Complex(a, b);
        // When
        double m2 = c.module();
        // Then
        assertEquals(Math.sqrt(a * a + b * b), m2);
    }

    @ParameterizedTest
    @MethodSource("dataComplex1")
    void moduleSquare(double a, double b) {
        // Given
        Complex c = new Complex(a, b);
        // When
        double m2 = c.moduleSquare();
        // Then
        assertEquals(a * a + b * b, m2);
    }

    @ParameterizedTest
    @MethodSource("dataComplex2")
    void mul(double a, double ai, double b, double bi) {
        // Given
        Complex ac = new Complex(a, ai);
        Complex bc = new Complex(b, bi);
        // When
        Complex ab = ac.mul(bc);
        Complex ba = bc.mul(ac);
        // Then
        assertEquals(a * b - ai * bi, ab.real());
        assertEquals(a * bi + ai * b, ab.im());
        assertEquals(ab, ba);
    }

    @ParameterizedTest
    @MethodSource("dataComplex1")
    void neg(double a, double b) {
        // Given
        Complex c = new Complex(a, b);
        // When
        Complex inv = c.neg();
        // Then
        assertEquals(-a, inv.real());
        assertEquals(-b, inv.im());
    }

    @Test
    void one() {
        Complex c = Complex.one();
        assertEquals(1D, c.real());
        assertEquals(0D, c.im());
    }

    @ParameterizedTest
    @MethodSource("dataComplex1")
    void real(double a, double b) {
        Complex c = new Complex(a, b);
        assertEquals(a, c.real());
    }

    @ParameterizedTest
    @MethodSource("dataComplex2")
    void sub(double a, double ai, double b, double bi) {
        // Given
        Complex ac = new Complex(a, ai);
        Complex bc = new Complex(b, bi);
        // When
        Complex sumab = ac.sub(bc);
        Complex sumba = bc.sub(ac);
        // Then
        assertEquals(a - b, sumab.real());
        assertEquals(ai - bi, sumab.im());
        assertEquals(b - a, sumba.real());
        assertEquals(bi - ai, sumba.im());

        assertEquals(Complex.zero(), ac.sub(ac));
        assertEquals(Complex.zero(), bc.sub(bc));
    }

    @ParameterizedTest
    @CsvSource({
            "1.2345, 6.789,(1.2345 +6.789 i)",
            "1.2345, -6.789,(1.2345 -6.789 i)"
    })
    void testToString(double a, double b, String txt) {
        Complex c = new Complex(a, b);
        assertEquals(txt, c.toString());
    }

    @Test
    void zero() {
        Complex c = Complex.zero();
        assertEquals(0D, c.real());
        assertEquals(0D, c.im());
    }
}