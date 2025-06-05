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
import java.util.function.UnaryOperator;

import static java.lang.Math.max;
import static java.lang.Math.sqrt;
import static java.lang.String.format;

/**
 * Vector utilities state
 */
public interface VectorUtils {

    /**
     * Returns the vector added (a + b)
     *
     * @param a the vector a
     * @param b the vector b
     */
    static Complex[] add(Complex[] a, Complex[] b) {
        if (a.length != b.length) {
            throw new IllegalArgumentException(format("Expected %d states (%d)",
                    a.length, b.length));
        }
        Complex[] states = new Complex[a.length];
        for (int i = 0; i < a.length; i++) {
            states[i] = a[i].add(b[i]);
        }
        return states;
    }

    /**
     * Returns a complex list of reals
     *
     * @param values the real values
     */
    static Complex[] create(double... values) {
        Complex[] cells = new Complex[values.length];
        for (int i = 0; i < values.length; i++) {
            cells[i] = Complex.create(values[i]);
        }
        return cells;
    }

    /**
     * Returns the tensor product
     *
     * @param a the vector a
     * @param b the vector b
     */
    static Complex[] cross(Complex[] a, Complex[] b) {
        int n = a.length;
        int m = b.length;
        Complex[] cells = new Complex[n * m];
        int idx = 0;
        for (Complex complex : a) {
            for (Complex value : b) {
                cells[idx] = complex.mul(value);
                idx++;
            }
        }
        return cells;
    }

    /**
     * Returns the ket scaled by real factor
     *
     * @param vector the vector
     * @param alpha  scale
     */
    static Complex[] divScalar(Complex[] vector, double alpha) {
        return Arrays.stream(vector)
                .map(v -> v.div(alpha))
                .toArray(Complex[]::new);
    }

    /**
     * Returns the values mapped by complex operator
     *
     * @param values values
     * @param op     the operator
     */
    static Complex[] map(Complex[] values, UnaryOperator<Complex> op) {
        return Arrays.stream(values).map(op).toArray(Complex[]::new);
    }

    /**
     * Returns the ket scaled by real factor
     *
     * @param vector the vector
     * @param alpha  scale
     */
    static Complex[] divScalar(Complex[] vector, Complex alpha) {
        return Arrays.stream(vector)
                .map(v -> v.div(alpha))
                .toArray(Complex[]::new);
    }

    /**
     * Returns the normalised cells
     *
     * @param cells the cells
     */
    static Complex[] normalise(Complex[] cells) {
        double norm = sqrt(Arrays.stream(cells)
                .mapToDouble(Complex::normSquare)
                .sum());
        return Arrays.stream(cells).map(c -> c.div(norm)).toArray(Complex[]::new);
    }

    /**
     * Returns the number of bits from number of states
     *
     * @param numStates number of states
     */
    static int numBits(int numStates) {
        return numBitsByState(numStates - 1);
    }

    /**
     * Returns the number of bits from state
     *
     * @param state the state
     */
    static int numBitsByState(int state) {
        int n = 0;
        do {
            n++;
        } while ((state >>= 1) != 0);
        return max(n, 1);
    }

    /**
     * Partial matrix multiplication
     *
     * @param d       the destination matrix
     * @param dOffset the destination matrix offset
     * @param numRow  the number of computation rows
     * @param numCols the number of computation columns
     * @param a       the left source matrix
     * @param aOffset the left source matrix offset
     * @param aStride the left source matrix stride (number of columns)
     * @param b       the right source matrix
     * @param bOffset the right source matrix offset
     * @param bStride the right source matrix stride (number of columns)
     */
    static Complex[] partMul(Complex[] d, int dOffset, int numRow, int numCols,
                             Complex[] a, int aOffset, int aStride,
                             Complex[] b, int bOffset, int bStride) {
        int di = dOffset;
        int ai = aOffset;
        for (int i = 0; i < numRow; i++) {
            int dij = di;
            int bj = bOffset;
            for (int j = 0; j < numCols; j++) {
                Complex cell = Complex.zero();
                int aik = ai;
                int bkj = bj;
                for (int k = 0; k < aStride; k++) {
                    cell = cell.add(a[aik].mul(b[bkj]));
                    aik++;
                    bkj += bStride;
                }
                d[dij] = cell;
                dij++;
                bj++;
            }
            di += bStride;
            ai += aStride;
        }
        return d;
    }

    static int qubitValue(int state, int index) {
        return (state >> index) & 1;
    }

    /**
     * Returns the vector subtracted by other (a - b)
     *
     * @param a the vector a
     * @param b the vector b
     */
    static Complex[] sub(Complex[] a, Complex[] b) {
        if (a.length != b.length) {
            throw new IllegalArgumentException(format("Expected %d states (%d)",
                    a.length, b.length));
        }
        Complex[] states = new Complex[a.length];
        for (int i = 0; i < a.length; i++) {
            states[i] = a[i].sub(b[i]);
        }
        return states;
    }

}
