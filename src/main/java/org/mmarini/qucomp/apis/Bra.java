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

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.IntStream;

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
     * Returns a ket base for the given value and size
     *
     * @param value the value
     * @param size  the number of qubit
     */
    public static Bra base(int value, int size) {
        int n = 1 << size;
        return new Bra(IntStream.range(0, n)
                .mapToObj(i -> value == i ? Complex.one() : Complex.zero())
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
     * Returns the ket quantum state
     *
     * @param values the values
     */
    public static Bra create(float... values) {
        return new Bra(VectorUtils.create(values));
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
     * Returns the ket added to other (this + other)
     *
     * @param other the other ket
     */
    public Bra add(Bra other) {
        return new Bra(VectorUtils.add(values, other.values));
    }

    /***
     * Returns value
     * @param i the index
     */
    public Complex at(int i) {
        return values[i];
    }

    /**
     * Returns the conjugated Kat
     */
    public Ket conj() {
        return new Ket(VectorUtils.conj(values));
    }

    /**
     * Returns the tensor product (this x other)
     *
     * @param other the other ket
     */
    public Bra cross(Bra other) {
        return new Bra(VectorUtils.cross(values, other.values));
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
        return VectorUtils.mul(values, ket.values());
    }

    /**
     * Returns the transformed ket
     *
     * @param matrix the matrix operator
     */
    public Bra mul(Matrix matrix) {
        int n = values.length;
        if (matrix.numRows() != n) {
            throw new IllegalArgumentException(format("Matrix operator must have shape ? x %d (%d x %d)",
                    n,
                    matrix.numRows(), matrix.numCols()));
        }
        int m = matrix.numCols();
        Complex[] cells = new Complex[m];
        for (int i = 0; i < m; i++) {
            Complex cell = Complex.zero();
            for (int j = 0; j < n; j++) {
                Complex c = matrix.at(j, i);
                cell = cell.add(c.mul(values[j]));
            }
            cells[i] = cell;
        }
        return create(cells);
    }

    /**
     * Returns the ket scaled by real factor
     *
     * @param alpha scale
     */
    public Bra mul(float alpha) {
        return new Bra(VectorUtils.mul(values, alpha));
    }

    /**
     * Returns the ket scaled by complex factor
     *
     * @param alpha the factor
     */
    public Bra mul(Complex alpha) {
        return new Bra(VectorUtils.mul(values, alpha));
    }

    /**
     * Returns the negated ket
     */
    public Bra neg() {
        return new Bra(VectorUtils.neg(values));
    }

    /**
     * Returns the ket subtracted by other (this - other)
     *
     * @param other the other ket
     */
    public Bra sub(Bra other) {
        return new Bra(VectorUtils.sub(values, other.values));
    }

    @Override
    public String toString() {
        return "Bra" + Arrays.toString(values);
    }
}
