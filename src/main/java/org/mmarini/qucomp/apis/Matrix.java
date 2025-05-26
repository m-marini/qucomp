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

import static java.lang.Math.min;
import static java.lang.Math.sqrt;
import static java.lang.String.format;
import static java.util.Objects.requireNonNull;
import static org.mmarini.qucomp.apis.VectorUtils.partMul;

/**
 * Complex matrix
 */
public class Matrix {

    public static final Matrix IDENTITY = permute(0, 1);
    public static final Matrix CCNOT = permute(0, 1, 2, 3, 4, 5, 7, 6);
    public static final Matrix SWAP = permute(0, 2, 1, 3);
    public static final Matrix CNOT = permute(0, 1, 3, 2);
    public static final Matrix X = permute(1, 0);

    public static final float HALF_SQRT2 = (float) (sqrt(2) / 2);
    public static final Matrix S = create(2, 2,
            Complex.one(), Complex.zero(),
            Complex.zero(), Complex.i());
    public static final Matrix Y = create(2, 2,
            Complex.zero(), Complex.i(-1),
            Complex.i(), Complex.zero());
    public static final Matrix Z = create(2, 2,
            Complex.one(), Complex.zero(),
            Complex.zero(), Complex.create(-1));
    public static final Matrix H = create(2, 2,
            Complex.one(), Complex.one(),
            Complex.one(), Complex.create(-1)).mul(HALF_SQRT2);
    public static final Matrix T = create(2, 2,
            Complex.one(), Complex.zero(),
            Complex.zero(), new Complex(HALF_SQRT2, HALF_SQRT2));
    private static final long ORDER_BY_CORE_THRESHOLD = 64 * 64 * 64 * 64 / 12;

    /**
     * Returns Toffoli operator
     * <pre>
     *      CCNOT |0> = |0>
     *      CCNOT |1> = |1>
     *      CCNOT |2> = |2>
     *      CCNOT |3> = |3>
     *      CCNOT |4> = |4>
     *      CCNOT |5> = |5>
     *      CCNOT |6> = |7>
     *      CCNOT |7> = |6>
     * </pre>
     */
    public static Matrix ccnot() {
        return CCNOT;
    }

    /**
     * Returns the cnot operator
     * <pre>
     *      CNOT |0> = |0>
     *      CNOT |1> = |1>
     *      CNOT |2> = |3>
     *      CNOT |3> = |2>
     * </pre>
     */
    public static Matrix cnot() {
        return CNOT;
    }

    /**
     * Returns the matrix from cell generator
     *
     * @param numRows the number of rows
     * @param numCols the number of cols
     * @param f       the cell generator by index
     */
    public static Matrix create(int numRows, int numCols, Function<int[], Complex> f) {
        Complex[] cells = new Complex[numRows * numCols];
        indexStream(numRows, numCols).forEach(indices -> {
            Complex cell = f.apply(indices);
            cells[unsafeIndex(numCols, indices)] = cell;
        });
        return new Matrix(numRows, numCols, cells);
    }

    /**
     * Returns the matrix for the given shape and cells
     *
     * @param numRows the number of rows
     * @param numCols the number of cols
     * @param cells   cell values
     */
    public static Matrix create(int numRows, int numCols, Complex... cells) {
        requireNonNull(cells);
        int size = numRows * numCols;
        if (cells.length != size) {
            throw new IllegalArgumentException(format(
                    "expected %d cells (%d)",
                    size, cells.length));
        }
        return new Matrix(numRows, numCols, cells);
    }

    /**
     * Returns the matrix for the given shape and cells
     *
     * @param numRows the number of rows
     * @param numCols the number of cols
     * @param values  cell values
     */
    public static Matrix create(int numRows, int numCols, float... values) {
        return create(numRows, numCols, VectorUtils.create(values));
    }

    /**
     * Returns the H operator (Hadamard)
     */
    public static Matrix h() {
        return H;
    }

    /**
     * Returns the identity square matrix
     *
     * @param size the size of matrix
     */
    public static Matrix identity(int size) {
        return create(size, size, indices ->
                indices[0] == indices[1] ? Complex.one() : Complex.zero());
    }

    /**
     * Returns the identity matrix
     */
    public static Matrix identity() {
        return IDENTITY;
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
    public static Matrix permute(int... permutation) {
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
     * Returns the s matrix
     */
    public static Matrix s() {
        return S;
    }

    /**
     * Returns the swap matrix
     */
    public static Matrix swap() {
        return SWAP;
    }

    /**
     * Returns the T operator
     */
    public static Matrix t() {
        return T;
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

    /**
     * Returns the X operator (Not)
     */
    public static Matrix x() {
        return X;
    }

    /**
     * Returns the Y operator
     */
    public static Matrix y() {
        return Y;
    }

    public static Matrix z() {
        return Z;
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
    protected Matrix(int numRows, int numCols, Complex... cells) {
        this.numRows = numRows;
        this.numCols = numCols;
        this.cells = cells;
    }

    /**
     * Returns the sum matrix (this + other)
     *
     * @param other the other matrix
     */
    public Matrix add(Matrix other) {
        if (!hasShape(other.numRows, other.numCols)) {
            throw new IllegalArgumentException(format("shapes must be congruent %dx%d + %dx%d",
                    numRows, numCols, other.numRows, other.numCols
            ));
        }
        return create(numRows, numCols, indices -> {
            Complex a = at(indices);
            Complex b = other.at(indices);
            return a.add(b);
        });
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
    public Matrix conj() {
        return new Matrix(numRows, numCols, Arrays.stream(cells).map(Complex::conj).toArray(Complex[]::new));
    }

    /**
     * Returns the vector product of two matrices
     *
     * @param other the other matrices
     */
    public Matrix cross(Matrix other) {
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
        return new Matrix(nm, nm, cells);
    }

    /**
     * Returns true if the arrays has the same size
     */
    boolean hasShape(int numRows, int numCols) {
        return numRows == this.numRows && numCols == this.numCols;
    }

    /**
     * Returns the index of element
     *
     * @param indices the indices
     */
    public int index(int... indices) {
        if (indices.length != 2) {
            throw new IllegalArgumentException(format(
                    "indices must be 2 (%d)", indices.length));
        }
        if (indices[0] < 0 || indices[0] >= numRows || indices[1] < 0 || indices[1] >= numCols) {
            throw new IllegalArgumentException(format(
                    "index must have range (0-%d)x(0%d) (%dx %d)",
                    numRows, numCols, indices[0], indices[1]));
        }
        return unsafeIndex(numCols, indices);
    }

    /**
     * Returns the scaled matrix
     *
     * @param scale the scale
     */
    public Matrix mul(float scale) {
        return new Matrix(numRows, numCols, Arrays.stream(cells).map(c -> c.mul(scale)).toArray(Complex[]::new));
    }

    /**
     * Returns the matrix multiplication (this x other)
     *
     * @param other the other matrix
     */
    public Matrix mul(Matrix other) {
        // Validates shapes
        if (numCols != other.numRows) {
            throw new IllegalArgumentException(format("Invalid product operands shapes %dx%d by %dx%d",
                    numRows, numCols,
                    other.numRows, other.numCols));
        }
        long order = (long) numRows * numCols * numCols * other.numCols;
        int cores = Runtime.getRuntime().availableProcessors();
        return (order / cores) > ORDER_BY_CORE_THRESHOLD
                ? mulConc(other)
                : mulSeq(other);
    }

    /**
     * Returns the scaled matrix
     *
     * @param scale the scale
     */
    public Matrix mul(Complex scale) {
        return new Matrix(numRows, numCols, Arrays.stream(cells).map(c -> c.mul(scale)).toArray(Complex[]::new));
    }

    /**
     * Returns the matrix multiplication (this x other) concurrency algorithm
     *
     * @param other the other matrix
     */
    Matrix mulConc(Matrix other) {
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
        return new Matrix(numRows, m, results);
    }

    /**
     * Returns the matrix multiplication (this x other) sequence algorithm
     *
     * @param other the other matrix
     */
    Matrix mulSeq(Matrix other) {
        int n = numRows * other.numCols;
        Complex[] cells = new Complex[n];
        partMul(cells, 0, numRows, other.numCols, this.cells, 0, numCols, other.cells, 0, other.numCols);
        return new Matrix(numRows, other.numCols, cells);
    }

    /**
     * Returns the negated matrix (-this)
     */
    public Matrix neg() {
        return new Matrix(numRows, numCols, Arrays.stream(cells).map(Complex::neg).toArray(Complex[]::new));
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
     * Returns the difference matrix (this - other)
     *
     * @param other the other matrix
     */
    public Matrix sub(Matrix other) {
        if (!hasShape(other.numRows, other.numCols)) {
            throw new IllegalArgumentException(format("shapes must be congruent %dx%d + %dx%d",
                    numRows, numCols, other.numRows, other.numCols
            ));
        }
        return create(numRows, numCols, indices -> {
            Complex a = at(indices);
            Complex b = other.at(indices);
            return a.sub(b);
        });
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
    public Matrix transpose() {
        return create(numCols, numRows, indices ->
                at(indices[1], indices[0]));
    }
}
