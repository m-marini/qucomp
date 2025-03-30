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

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;

/**
 * Bra quantum operator
 */
public record Bra(Complex[] values) {

    public static final Bra ZERO = Ket.zero().conj();
    public static final Bra ONE = Ket.one().conj();
    public static final Bra PLUS = Ket.plus().conj();
    public static final Bra MINUS = Ket.minus().conj();
    public static final Bra I = Ket.i().conj();
    public static final Bra MINUS_I = Ket.minus_i().conj();

    /**
     * Create the bra quantum operator
     *
     * @param values the single states
     */
    public Bra(Complex[] values) {
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
    public static Bra create(double... values) {
        return new Bra(Arrays.stream(values)
                .mapToObj(v ->
                        new Complex(v, 0))
                .toArray(Complex[]::new));
    }

    /**
     * Returns tje ket quantum state
     *
     * @param values the values
     */
    public static Bra create(Complex... values) {
        return new Bra(values);
    }

    /**
     * Returns |i>
     */
    public static Bra i() {
        return I;
    }

    /**
     * Returns |-i>
     */
    public static Bra minus() {
        return MINUS;
    }

    /**
     * Returns |->
     */
    public static Bra minus_i() {
        return MINUS_I;
    }

    /**
     * Returns |1>
     */
    public static Bra one() {
        return ONE;
    }

    /**
     * Returns |+>
     */
    public static Bra plus() {
        return PLUS;
    }

    /**
     * Returns |0>
     */
    public static Bra zero() {
        return ZERO;
    }

    /**
     * Returns the ket added to other (this + other)
     *
     * @param other the other ket
     */
    public Bra add(Bra other) {
        if (values.length != other.values.length) {
            throw new IllegalArgumentException(format("Expected %d states (%d)",
                    values.length, other.values.length));
        }
        Complex[] states = new Complex[values.length];
        for (int i = 0; i < values.length; i++) {
            states[i] = values[i].add(other.values[i]);
        }
        return new Bra(states);
    }

    /**
     * Returns the conjugated Kat
     */
    public Ket conj() {
        return new Ket(Arrays.stream(values).map(Complex::conj).toArray(Complex[]::new));
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Bra bra = (Bra) o;
        return Objects.deepEquals(values, bra.values);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(values);
    }

    /**
     * Returns the scalar product
     *
     * @param ket the ket
     */
    public Complex mul(Ket ket) {
        if (values.length != ket.values().length) {
            throw new IllegalArgumentException(format("Expected %d states (%d)",
                    values.length, ket.values().length));
        }
        Complex result = Complex.zero();
        for (int i = 0; i < values.length; i++) {
            result = result.add(values[i].mul(ket.values()[i]));
        }
        return result;
    }

    public Bra mul(Matrix alpha) {
        throw new NotImplementedException();
    }

    /**
     * Returns the ket scaled by real factor
     *
     * @param alpha scale
     */
    public Bra mul(double alpha) {
        return new Bra(
                Arrays.stream(values)
                        .map(v -> v.mul(alpha))
                        .toArray(Complex[]::new));
    }

    /**
     * Returns the ket scaled by comlpex factor
     *
     * @param alpha the factor
     */
    public Bra mul(Complex alpha) {
        return new Bra(
                Arrays.stream(values)
                        .map(v -> v.mul(alpha))
                        .toArray(Complex[]::new));
    }

    /**
     * Returns the negated ket
     */
    public Bra neg() {
        return new Bra(Arrays.stream(values).map(Complex::neg).toArray(Complex[]::new));
    }

    /**
     * Returns the ket subtracted by other (this - other)
     *
     * @param other the other ket
     */
    public Bra sub(Bra other) {
        if (values.length != other.values.length) {
            throw new IllegalArgumentException(format("Expected %d states (%d)",
                    values.length, other.values.length));
        }
        Complex[] states = new Complex[values.length];
        for (int i = 0; i < values.length; i++) {
            states[i] = values[i].sub(other.values[i]);
        }
        return new Bra(states);
    }

    @Override
    public String toString() {
        return "Bra" + Arrays.toString(values);
    }
}
