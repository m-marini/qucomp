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

import org.mmarini.NotImplementedException;

import java.util.Arrays;
import java.util.Objects;

import static java.lang.Math.sqrt;
import static java.lang.String.format;
import static java.util.Objects.requireNonNull;

/**
 * Ket quantum state
 *
 * @param values the values of single state
 */
public record Ket(Complex[] values) {

    public static final Ket ZERO = create(1, 0);
    public static final Ket ONE = create(0, 1);
    public static final Ket PLUS = create(1, 1).mul(sqrt(2) / 2);
    public static final Ket MINUS = create(1, -1).mul(sqrt(2) / 2);
    public static final Ket I = create(Complex.one(), Complex.i()).mul(sqrt(2) / 2);
    public static final Ket MINUS_I = create(Complex.one(), Complex.i(-1)).mul(sqrt(2) / 2);

    /**
     * Create the ket quantum state
     *
     * @param values the single states
     */
    public Ket(Complex[] values) {
        this.values = requireNonNull(values);
        for (Complex value : values) {
            requireNonNull(value);
        }
    }

    /**
     * Returns the ket quantum state
     *
     * @param values the values
     */
    public static Ket create(double... values) {
        return new Ket(Arrays.stream(values)
                .mapToObj(v ->
                        new Complex(v, 0))
                .toArray(Complex[]::new));
    }

    /**
     * Returns tje ket quantum state
     *
     * @param values the values
     */
    public static Ket create(Complex... values) {
        return new Ket(values);
    }

    /**
     * Returns |i>
     */
    public static Ket i() {
        return I;
    }

    /**
     * Returns |-i>
     */
    public static Ket minus() {
        return MINUS;
    }

    /**
     * Returns |->
     */
    public static Ket minus_i() {
        return MINUS_I;
    }

    /**
     * Returns |1>
     */
    public static Ket one() {
        return ONE;
    }

    /**
     * Returns |+>
     */
    public static Ket plus() {
        return PLUS;
    }

    /**
     * Returns |0>
     */
    public static Ket zero() {
        return ZERO;
    }

    /**
     * Returns the ket added to other (this + other)
     *
     * @param other the other ket
     */
    public Ket add(Ket other) {
        if (values.length != other.values.length) {
            throw new IllegalArgumentException(format("Expected %d states (%d)",
                    values.length, other.values.length));
        }
        Complex[] states = new Complex[values.length];
        for (int i = 0; i < values.length; i++) {
            states[i] = values[i].add(other.values[i]);
        }
        return new Ket(states);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Ket ket = (Ket) o;
        return Objects.deepEquals(values, ket.values);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(values);
    }

    /**
     * Returns the conjugated Bra
     */
    public Bra conj() {
        return new Bra(Arrays.stream(values).map(Complex::conj).toArray(Complex[]::new));
    }

    public Ket mul(Matrix alpha) {
        throw new NotImplementedException();
    }

    /**
     * Returns the ket scaled by real factor
     *
     * @param alpha scale
     */
    public Ket mul(double alpha) {
        return new Ket(
                Arrays.stream(values)
                        .map(v -> v.mul(alpha))
                        .toArray(Complex[]::new));
    }

    /**
     * Returns the ket scaled by comlpex factor
     *
     * @param alpha the factor
     */
    public Ket mul(Complex alpha) {
        return new Ket(
                Arrays.stream(values)
                        .map(v -> v.mul(alpha))
                        .toArray(Complex[]::new));
    }

    /**
     * Returns the negated ket
     */
    public Ket neg() {
        return new Ket(Arrays.stream(values).map(Complex::neg).toArray(Complex[]::new));
    }

    /**
     * Returns the ket subtracted by other (this - other)
     *
     * @param other the other ket
     */
    public Ket sub(Ket other) {
        if (values.length != other.values.length) {
            throw new IllegalArgumentException(format("Expected %d states (%d)",
                    values.length, other.values.length));
        }
        Complex[] states = new Complex[values.length];
        for (int i = 0; i < values.length; i++) {
            states[i] = values[i].sub(other.values[i]);
        }
        return new Ket(states);
    }

    @Override
    public String toString() {
        return "Ket" + Arrays.toString(values);
    }
}
