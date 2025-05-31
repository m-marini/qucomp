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

import io.reactivex.rxjava3.functions.Action;
import io.reactivex.rxjava3.schedulers.Schedulers;
import org.mmarini.ParallelProcess;
import org.mmarini.Tuple2;

import java.util.Arrays;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.lang.Math.*;
import static java.lang.String.format;
import static java.util.Objects.requireNonNull;
import static org.mmarini.qucomp.apis.VectorUtils.numBitsByState;
import static org.mmarini.qucomp.apis.VectorUtils.partMul;

/**
 * Complex matrix
 */
public class Matrix1 {

    private static final long ORDER_BY_CORE_THRESHOLD = 64 * 64 * 64 * 64 / 12;
    private static final Complex HALF_SQRT2 = Complex.create((float) (sqrt(2) / 2));
    private static final Complex I_HALF_SQRT2 = new Complex(0, (float) (sqrt(2) / 2));
    private static final Matrix1 I_KET = ket(HALF_SQRT2, I_HALF_SQRT2);
    private static final Matrix1 MINUS_I_KET = ket(HALF_SQRT2, I_HALF_SQRT2.neg());
    private static final Matrix1 PLUS_KET = ket(HALF_SQRT2, HALF_SQRT2);
    private static final Matrix1 MINUS_KET = ket(HALF_SQRT2, HALF_SQRT2.neg());

    /**
     * Returns the state permutation given the input bit permutation
     * <pre>
     *     out[p[i]]=in[i]
     * </pre>
     *
     * @param bitPermutation the bit permutations in[i] = the bit index of the resulting bit for the i-th input bit
     */
    static int[] computeStatePermutation(int... bitPermutation) {
        return IntStream.range(0, 1 << bitPermutation.length)
                .map(s -> {
                    int s1 = 0;
                    int mask = 1;
                    for (int i = 0; i < bitPermutation.length; i++) {
                        int b = s & mask;
                        if (b != 0) {
                            int sh = bitPermutation[i] - i;
                            if (sh < 0) {
                                b >>>= -sh;
                            } else if (sh > 0) {
                                b <<= sh;
                            }
                            s1 |= b;
                        }
                        mask <<= 1;
                    }
                    return s1;
                })
                .toArray();
    }

    /**
     * Returns the matrix for the given shape and cells
     *
     * @param numRows the number of rows
     * @param numCols the number of cols
     * @param cells   cell values
     */
    public static Matrix1 create(int numRows, int numCols, Complex... cells) {
        requireNonNull(cells);
        int size = numRows * numCols;
        if (cells.length != size) {
            throw new IllegalArgumentException(format(
                    "expected %d cells (%d)",
                    size, cells.length));
        }
        return new Matrix1(numRows, numCols, cells);
    }

    /**
     * Returns the matrix for the given shape and cells
     *
     * @param numRows the number of rows
     * @param numCols the number of cols
     * @param values  cell values
     */
    public static Matrix1 create(int numRows, int numCols, float... values) {
        return create(numRows, numCols, VectorUtils.create(values));
    }

    /**
     * Returns the matrix from cell generator
     *
     * @param numRows the number of rows
     * @param numCols the number of cols
     * @param f       the cell generator by index
     */
    public static Matrix1 create(int numRows, int numCols, Function<int[], Complex> f) {
        Complex[] cells = new Complex[numRows * numCols];
        indexStream(numRows, numCols).forEach(indices -> {
            Complex cell = f.apply(indices);
            cells[unsafeIndex(numCols, indices)] = cell;
        });
        return new Matrix1(numRows, numCols, cells);
    }

    /**
     * Returns the |i> ket
     */
    public static Matrix1 i() {
        return I_KET;
    }

    /**
     * Returns the identity square matrix
     *
     * @param size the size of matrix
     */
    public static Matrix1 identity(int size) {
        return create(size, size, indices ->
                indices[0] == indices[1] ? Complex.one() : Complex.zero());
    }

    /**
     * Returns the stream of indices
     */
    static Stream<int[]> indexStream(int numRows, int numCols) {
        return IntStream.range(0, numRows)
                .boxed()
                .flatMap(i ->
                        IntStream.range(0, numCols)
                                .mapToObj(j -> new int[]{i, j})
                );
    }

    /**
     * Returns the inverse permutation
     *
     * @param s the permutation
     */
    static int[] inversePermutation(int[] s) {
        int[] reverse = new int[s.length];
        for (int i = 0; i < s.length; i++) {
            reverse[s[i]] = i;
        }
        return reverse;
    }

    /**
     * Returns the ket
     *
     * @param values the state values
     */
    public static Matrix1 ket(Complex... values) {
        return new Matrix1(values.length, 1, values);
    }

    /**
     * Returns the ket
     *
     * @param values the state values
     */
    public static Matrix1 ket(float... values) {
        Complex[] cells = new Complex[values.length];
        for (int i = 0; i < cells.length; i++) {
            cells[i] = Complex.create(values[i]);
        }
        return ket(cells);
    }

    /**
     * Returns the ket base of the given state
     *
     * @param state the state
     */
    public static Matrix1 ketBase(int state) {
        int n = 1 << numBitsByState(state);
        Complex[] cells = new Complex[n];
        Arrays.fill(cells, Complex.zero());
        cells[state] = Complex.one();
        return new Matrix1(n, 1, cells);
    }

    /**
     * Returns |->
     */
    public static Matrix1 minus() {
        return MINUS_KET;
    }

    /**
     * Return |-i>
     */
    public static Matrix1 minus_i() {
        return MINUS_I_KET;
    }

    /**
     * Returns the matrix that permutes the values of column vector.
     * <p>
     * The value of the i-th element of the permutation array is the target position
     * of source element at i-th position so
     * </p>
     * <pre>
     *     B = M x A
     *
     *     b(p[i]) := a[i]
     * </pre>
     * E.g.
     * <pre>
     *     p = [ 2 3 4 0 1 ]
     *
     *     b(2) = a(0)
     *     b(3) = a(1)
     *     b(4) = a(2)
     *     b(0) = a(3)
     *     b(1) = a(4)
     *
     *         | 0 0 0 1 0 |
     *         | 0 0 0 0 1 |
     *     M = | 1 0 0 0 0 | m[p[j],j] = 1
     *         | 0 1 0 0 0 |
     *         | 0 0 1 0 0 |
     *
     *     a=(2 3 4 0 1) => b=(0 1 2 3 4)
     *     a=(0 1 2 3 4) => b=(3 4 0 1 2)
     *
     * </pre>
     *
     * @param permutation the target mapping
     */
    public static Matrix1 permute(int... permutation) {
        int n = permutation.length;
        return create(n, permutation.length, indices -> {
            int i = indices[0];
            int j = indices[1];
            return i == permutation[j]
                    ? Complex.one()
                    : Complex.zero();
        });
    }

    /**
     * Returns |+>
     */
    public static Matrix1 plus() {
        return PLUS_KET;
    }

    /**
     * Returns the index of element
     *
     * @param stride  the stride
     * @param indices the indices
     */
    static int unsafeIndex(int stride, int... indices) {
        return indices[0] * stride + indices[1];
    }

    private final int numRows;
    private final int numCols;
    private final Complex[] cells;

    /**
     * Creates the matrix
     *
     * @param numRows the number of rows
     * @param numCols the number of columns
     * @param cells   the cells values
     */
    protected Matrix1(int numRows, int numCols, Complex... cells) {
        this.numRows = numRows;
        this.numCols = numCols;
        this.cells = cells;
    }

    /**
     * Returns the sum matrix (this + other)
     *
     * @param other the other matrix
     */
    public Matrix1 add(Matrix1 other) {
        int n = max(numRows, other.numRows);
        int m = max(numCols, other.numCols);
        Matrix1 left = extends0(n, m);
        Matrix1 right = other.extends0(n, m);
        Complex[] cells = VectorUtils.add(left.cells, right.cells);
        return new Matrix1(n, m, cells);
    }

    /**
     * Returns the element at index
     *
     * @param indices the indices
     */
    public Complex at(int... indices) {
        return cells[index(indices)];
    }

    /**
     * Returns the cells
     */
    public Complex[] cells() {
        return cells;
    }

    /**
     * Returns the conjugated matrix
     */
    public Matrix1 conj() {
        return new Matrix1(numRows, numCols, Arrays.stream(cells).map(Complex::conj).toArray(Complex[]::new));
    }

    /**
     * Returns the vector product of two matrices
     *
     * @param other the other matrices
     */
    public Matrix1 cross(Matrix1 other) {
        int n = numRows;
        int m = other.numRows;
        if (n != numCols || m != other.numCols) {
            throw new IllegalArgumentException(format("matrices must be square matrix (%dx%d) x (%d, %d)",
                    n, numCols, m, other.numCols));
        }
        int nm = n * m;
        Complex[] cells = new Complex[nm * nm];
        indexStream(n, numCols)
                .flatMap(ik ->
                        indexStream(m, other.numCols).map(jl -> Tuple2.of(ik, jl))
                ).forEach(t -> {
                    int[] ik = t._1;
                    int[] jl = t._2;
                    int i = ik[0];
                    int k = ik[1];
                    int j = jl[0];
                    int l = jl[1];
                    // Computes the result indices
                    int r = i * m + j;
                    int q = k * m + l;
                    Complex cell = at(ik).mul(other.at(jl));
                    int idx = unsafeIndex(nm, r, q);
                    cells[idx] = cell;
                });
        return new Matrix1(nm, nm, cells);
    }

    /**
     * Returns the conjugate transpose matrix
     */
    public Matrix1 dagger() {
        return conj().transpose();
    }

    /**
     * Returns the division by divisor
     *
     * @param value the divisor
     */
    public Matrix1 div(float value) {
        Complex[] cells = VectorUtils.divScalar(this.cells, value);
        return new Matrix1(numRows, numCols, cells);
    }

    /**
     * Returns the division by divisor
     *
     * @param value the divisor
     */
    public Matrix1 div(Complex value) {
        Complex[] cells = VectorUtils.divScalar(this.cells, value);
        return new Matrix1(numRows, numCols, cells);
    }

    /**
     * Returns the extended matrix by appending zero filled cell
     *
     * @param numRows the number of resulting rows
     * @param numCols the number of resulting columns
     */
    public Matrix1 extends0(int numRows, int numCols) {
        return extendsCols(numCols)
                .extendsRows(numRows);
    }

    /**
     * Returns the extended matrix by appending zero filled cols
     *
     * @param numCols the number resulting of rows
     */
    public Matrix1 extendsCols(int numCols) {
        if (this.numCols >= numCols) {
            return this;
        }
        Complex[] cells = new Complex[numRows * numCols];

        for (int i = 0; i < numRows; i++) {
            System.arraycopy(this.cells, i * this.numCols, cells, i * numCols, this.numCols);
            Arrays.fill(cells, i * numCols + this.numCols, i * numCols + numCols, Complex.zero());
        }
        return new Matrix1(numRows, numCols, cells);
    }

    /**
     * Returns the extended matrix by cross-product of square matrices
     *
     * @param n the size of the resulting matrix
     */
    public Matrix1 extendsCrossSquare(int n) {
        if (numCols != numRows) {
            throw new IllegalArgumentException(format("Expected square matrix (%dx%d)", numRows, numCols));
        }
        if (n == numRows) {
            return this;
        }
        if ((n % numRows) > 0) {
            throw new IllegalArgumentException(format("Expected size multiple of %dx%d (%dx%d)", numRows, numRows, n, n));
        }
        int q = n / numRows;
        return identity(q).cross(this);
    }

    /**
     * Returns the extended matrix by appending zer filled rows
     *
     * @param numRows the number resulting of rows
     */
    public Matrix1 extendsRows(int numRows) {
        if (this.numRows >= numRows) {
            return this;
        }
        Complex[] cells = new Complex[numRows * numCols];
        System.arraycopy(this.cells, 0, cells, 0, this.cells.length);
        Arrays.fill(cells, this.cells.length, cells.length, Complex.zero());
        return new Matrix1(numRows, numCols, cells);
    }

    /**
     * Returns true if the arrays has the same size
     */
    boolean hasShape(int numRows, int numCols) {
        return numRows == this.numRows && numCols == this.numCols;
    }

    /**
     * Returns true if the arrays has the same size
     */
    boolean hasShape(Matrix1 other) {
        return hasShape(other.numRows, other.numCols);
    }

    /**
     * Returns the index of element
     *
     * @param indices the indices
     */
    public int index(int... indices) {
        if (indices.length != 2) {
            throw new IllegalArgumentException(format(
                    "Expected 2 indices (%d)", indices.length));
        }
        if (indices[0] < 0 || indices[0] >= numRows || indices[1] < 0 || indices[1] >= numCols) {
            throw new IllegalArgumentException(format(
                    "index must have range (0-%d)x(0-%d) (%dx%d)",
                    numRows, numCols, indices[0], indices[1]));
        }
        return unsafeIndex(numCols, indices);
    }

    /**
     * Returns the scaled matrix
     *
     * @param scale the scale
     */
    public Matrix1 mul(float scale) {
        return new Matrix1(numRows, numCols, Arrays.stream(cells).map(c -> c.mul(scale)).toArray(Complex[]::new));
    }

    /**
     * Returns the matrix multiplication (this x right) with extensions
     *
     * @param right the right matrix
     */
    public Matrix1 mul(Matrix1 right) {
        // Check for extensions
        Matrix1 left = this;
        if (left.numCols > right.numRows) {
            right = right.extendsCrossSquare(left.numCols);
        } else if (left.numCols < right.numRows) {
            left = left.extendsCrossSquare(right.numRows);
        }
        return left.safeMul(right);
    }

    /**
     * Returns the scaled matrix
     *
     * @param scale the scale
     */
    public Matrix1 mul(Complex scale) {
        return new Matrix1(numRows, numCols, Arrays.stream(cells).map(c -> c.mul(scale)).toArray(Complex[]::new));
    }

    /**
     * Returns the matrix multiplication (this x other) concurrency algorithm
     *
     * @param other the other matrix
     */
    Matrix1 mulConc(Matrix1 other) {
        int cores = Runtime.getRuntime().availableProcessors();
        int m = other.numCols;
        int numCells = numRows * m;
        int numCellsPerThread = (2 * numCells + cores) / cores / 2;
        // no partition
        int rowsPerThread;
        int colsPerThread;

        if (m > numCellsPerThread) {
            // horizontal partition
            colsPerThread = numCellsPerThread;
            rowsPerThread = 1;
        } else {
            // vertical partition
            rowsPerThread = (numCellsPerThread + m - 1) / m;
            colsPerThread = m;
        }
        Complex[] results = new Complex[numRows * m];
        ParallelProcess.TaskScheduler tasks = ParallelProcess.scheduler(Schedulers.computation());

        for (int i = 0; i < numRows; i += rowsPerThread) {
            int dOffset = i * m;
            int aOffset = i * numCols;
            int numTaskRow = min(numRows - i, rowsPerThread);
            for (int j = 0; j < m; j += colsPerThread) {
                int bOffset = j;
                int numTaskCols = min(m - j, colsPerThread);
                Action task = () ->
                        partMul(results, dOffset,
                                numTaskRow, numTaskCols,
                                cells, aOffset, numCols,
                                other.cells, bOffset, m);
                tasks.add(task);
            }
        }
        // Execute
        tasks.run();
        return new Matrix1(numRows, m, results);
    }

    /**
     * Returns the matrix multiplication (this x other) sequence algorithm
     *
     * @param other the other matrix
     */
    Matrix1 mulSeq(Matrix1 other) {
        int n = numRows * other.numCols;
        Complex[] cells = new Complex[n];
        partMul(cells, 0, numRows, other.numCols, this.cells, 0, numCols, other.cells, 0, other.numCols);
        return new Matrix1(numRows, other.numCols, cells);
    }

    /**
     * Returns the negated matrix (-this)
     */
    public Matrix1 neg() {
        return new Matrix1(numRows, numCols, Arrays.stream(cells).map(Complex::neg).toArray(Complex[]::new));
    }

    /**
     * Returns the number of columns
     */
    public int numCols() {
        return numCols;
    }

    /**
     * Returns the number of rows
     */
    public int numRows() {
        return numRows;
    }

    /**
     * Returns the matrix multiplication (this x right)
     *
     * @param right the right matrix
     */
    private Matrix1 safeMul(Matrix1 right) {
        // Validates shapes
        if (numCols != right.numRows) {
            // Check for extensions
            throw new IllegalArgumentException(format("Invalid product operands shapes %dx%d by %dx%d",
                    numRows, numCols,
                    right.numRows, right.numCols));
        }
        long order = (long) numRows * numCols * numCols * right.numCols;
        int cores = Runtime.getRuntime().availableProcessors();
        return (order / cores) > ORDER_BY_CORE_THRESHOLD
                ? mulConc(right)
                : mulSeq(right);
    }

    /**
     * Returns the difference matrix (this - other)
     *
     * @param other the other matrix
     */
    public Matrix1 sub(Matrix1 other) {
        int n = max(numRows, other.numRows);
        int m = max(numCols, other.numCols);
        Matrix1 left = extends0(n, m);
        Matrix1 right = other.extends0(n, m);
        Complex[] cells = VectorUtils.sub(left.cells, right.cells);
        return new Matrix1(n, m, cells);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        String[] cols = Arrays.stream(cells).map(Complex::toString).toArray(String[]::new);
        int[] colSize = IntStream.range(0, numCols)
                .map(j ->
                        IntStream.range(0, numRows)
                                .map(i ->
                                        cols[unsafeIndex(numCols, i, j)].length()
                                ).max()
                                .orElse(0))
                .toArray();
        builder.append("[");
        for (int i = 0; i < numRows; i++) {
            if (i != 0) {
                builder.append("\n ");
            }
            for (int j = 0; j < numCols; j++) {
                builder.append(j == 0 ? " " : ", ");
                String fmt = "%" + colSize[j] + "s";
                builder.append(format(fmt, cols[unsafeIndex(numCols, i, j)]));
            }
        }
        builder.append(" ]\n");
        return builder.toString();
    }

    /**
     * Returns the transpose matrix
     */
    public Matrix1 transpose() {
        return create(numCols, numRows, indices ->
                at(indices[1], indices[0]));
    }
}
