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
public class Matrix {

    private static final long ORDER_BY_CORE_THRESHOLD = 64 * 64 * 64 * 64 / 12;
    private static final float HALF_SQRT2 = (float) (sqrt(2) / 2);
    private static final Matrix I_KET = ket(Complex.create(HALF_SQRT2), Complex.i(HALF_SQRT2));
    private static final Matrix MINUS_I_KET = ket(Complex.create(HALF_SQRT2), Complex.i(-HALF_SQRT2));
    private static final Matrix PLUS_KET = ket(HALF_SQRT2, HALF_SQRT2);
    private static final Matrix MINUS_KET = ket(HALF_SQRT2, -HALF_SQRT2);
    private static final Matrix CNOT_GATE = permute(0, 1, 3, 2);
    private static final Matrix CCNOT_GATE = permute(0, 1, 2, 3, 4, 5, 7, 6);
    private static final Matrix H_GATE = create(2, 2,
            HALF_SQRT2, HALF_SQRT2,
            HALF_SQRT2, -HALF_SQRT2);
    private static final Matrix S_GATE = create(2, 2,
            Complex.one(), Complex.zero(),
            Complex.zero(), Complex.i());
    private static final Matrix T_GATE = create(2, 2,
            Complex.one(), Complex.zero(),
            Complex.zero(), new Complex(HALF_SQRT2, HALF_SQRT2));
    private static final Matrix X_GATE = create(2, 2,
            0, 1,
            1, 0);
    private static final Matrix Y_GATE = create(2, 2,
            Complex.zero(), Complex.i(-1),
            Complex.i(), Complex.zero());
    private static final Matrix Z_GATE = create(2, 2,
            1, 0,
            0, -1);

    /**
     * Returns the matrix with all zero elements except the element at(row, col)
     *
     * @param row the row index
     * @param col the column index
     */
    public static Matrix ary(int row, int col) {
        int rows = 1 << numBitsByState(row);
        int cols = 1 << numBitsByState(col);
        Complex[] cells = new Complex[rows * cols];
        Arrays.fill(cells, Complex.zero());
        cells[unsafeIndex(cols, row, col)] = Complex.one();
        return new Matrix(rows, cols, cells);
    }

    /**
     * Returns the matrix of ccnot gate (Toffoli) applied to the given bits
     *
     * @param data     the data bit
     * @param control0 the first control bit
     * @param control1 the second control bit
     */
    public static Matrix ccnot(int data, int control0, int control1) {
        return createGate(CCNOT_GATE, data, control0, control1);
    }

    /**
     * Returns the matrix of cnot gate applied to the given bits
     *
     * @param data    the data bit
     * @param control the control bit
     */
    public static Matrix cnot(int data, int control) {
        return createGate(CNOT_GATE, data, control);
    }

    /**
     * Returns the bit permutation from input to internal gate input.
     * <p>
     * The input bits map is the values of internal gate bits for each input gate input (internal[i]<-bitMap[i])
     * E.g.
     * <pre>
     *     [2, 0] = internals[0] <- input[2],
     *              internals[1] <- input[0]
     * </pre>
     * </p>
     * <p>
     * The results is the map of the values of input gate bits for each internal gate input (internal[i]->bitMap[i])
     * E.g. the result of [2, 1] is
     * <pre>
     *     [2, 1, 0] = internals[0] -> input[2],
     *                 internals[1] -> input[1],
     *                 internals[2] -> input[0]
     * </pre>
     * </p>
     *
     * @param bitMap the bits map
     */
    static int[] computeBitsPermutation(int... bitMap) {
        validateBitMap(bitMap);
        int numBits = max(bitMap.length, Arrays.stream(bitMap).max().orElse(0) + 1);
        int[] result = new int[numBits];
        int m = bitMap.length;
        boolean[] gateMapped = new boolean[numBits];
        boolean[] inMapped = new boolean[numBits];
        // Map gate input
        for (int i = 0; i < m; i++) {
            result[bitMap[i]] = i;
            gateMapped[bitMap[i]] = inMapped[i] = true;
        }
        // Mapped unchanged
        for (int i = m; i < numBits; i++) {
            if (!gateMapped[i]) {
                gateMapped[i] = inMapped[i] = true;
                result[i] = i;
            }
        }
        // Map remaining
        int free = 0;
        for (int i = m; i < numBits; i++) {
            if (!inMapped[i]) {
                while (gateMapped[free]) {
                    free++;
                }
                result[free] = i;
                gateMapped[free] = inMapped[i] = true;
            }

        }
        return result;
    }

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
            cells[unsafeIndex(numCols, indices[0], indices[1])] = cell;
        });
        return new Matrix(numRows, numCols, cells);
    }

    /**
     * Returns the matrix of cnot gate applied to the given bits
     *
     * @param baseGate the base gate matrix
     * @param bitMap   the bit map
     */
    private static Matrix createGate(Matrix baseGate, int... bitMap) {
        int[] statePermuteIn = computeStatePermutation(computeBitsPermutation(bitMap));
        int[] statePermuteOut = inversePermutation(statePermuteIn);
        return permute(statePermuteOut).mul(baseGate).mul(permute(statePermuteIn));
    }


    /**
     * Returns the antisymmetric matrix with the (row, col) element equal (-1)^(row+col) if row < col
     *
     * @param row row index
     * @param col columns index
     */
    public static Matrix eps(int row, int col) {
        int size = 1 << numBitsByState(max(row, col));
        Complex[] cells = new Complex[size * size];
        Arrays.fill(cells, Complex.zero());
        if (row != col) {
            float val = (row + col) % 2 == 0
                    ? 1 : -1;
            val = row < col ? val : -val;
            cells[unsafeIndex(size, row, col)] = Complex.create(val);
            cells[unsafeIndex(size, col, row)] = Complex.create(-val);
        }
        return new Matrix(size, size, cells);
    }

    /**
     * Returns the matrix of H (Hadamard) gate applied to i-th bit
     *
     * @param index the bit index
     */
    public static Matrix h(int index) {
        return createGate(H_GATE, index);
    }

    /**
     * Returns the |i> ket
     */
    public static Matrix i() {
        return I_KET;
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
    public static Matrix ket(Complex... values) {
        return new Matrix(values.length, 1, values);
    }

    /**
     * Returns the ket
     *
     * @param values the state values
     */
    public static Matrix ket(float... values) {
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
    public static Matrix ketBase(int state) {
        int n = 1 << numBitsByState(state);
        Complex[] cells = new Complex[n];
        Arrays.fill(cells, Complex.zero());
        cells[state] = Complex.one();
        return new Matrix(n, 1, cells);
    }

    /**
     * Returns |->
     */
    public static Matrix minus() {
        return MINUS_KET;
    }

    /**
     * Return |-i>
     */
    public static Matrix minus_i() {
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
     * Returns |+>
     */
    public static Matrix plus() {
        return PLUS_KET;
    }

    /**
     * Returns the qubit 0 value projection matrix
     *
     * @param index     the qubit index
     * @param numQubits the number of qubits
     */
    public static Matrix qubit0(int index, int numQubits) {
        int nBits = max(index + 1, numQubits);
        int nStates = 1 << nBits;
        int mask = 1 << index;
        Complex[] cells = new Complex[nStates * nStates];
        Arrays.fill(cells, Complex.zero());
        for (int i = 0; i < nStates; i++) {
            if ((i & mask) == 0) {
                cells[unsafeIndex(nStates, i, i)] = Complex.one();
            }
        }
        return new Matrix(nStates, nStates, cells);
    }

    /**
     * Returns the qubit 0 value projection matrix
     *
     * @param index     the qubit index
     * @param numQubits the number of qubits
     */
    public static Matrix qubit1(int index, int numQubits) {
        int nBits = max(index + 1, numQubits);
        int nStates = 1 << nBits;
        int mask = 1 << index;
        Complex[] cells = new Complex[nStates * nStates];
        Arrays.fill(cells, Complex.zero());
        for (int i = 0; i < nStates; i++) {
            if ((i & mask) != 0) {
                cells[unsafeIndex(nStates, i, i)] = Complex.one();
            }
        }
        return new Matrix(nStates, nStates, cells);
    }

    /**
     * Returns the symmetric matrix with the (row, col) element equal one
     *
     * @param row row index
     * @param col columns index
     */
    public static Matrix sim(int row, int col) {
        int size = 1 << numBitsByState(max(row, col));
        Complex[] cells = new Complex[size * size];
        Arrays.fill(cells, Complex.zero());
        cells[unsafeIndex(size, row, col)] = Complex.one();
        cells[unsafeIndex(size, col, row)] = Complex.one();
        return new Matrix(size, size, cells);
    }

    /**
     * Returns the index of element
     *
     * @param stride the stride
     * @param row    the row index
     * @param col    the column index
     */
    static int unsafeIndex(int stride, int row, int col) {
        return row * stride + col;
    }

    /**
     * Returns the matrix of S gate applied to i-th bit
     *
     * @param index the bit index
     */
    public static Matrix s(int index) {
        return createGate(S_GATE, index);
    }

    /**
     * Returns the matrix that transforms the states by swapping two bits
     *
     * @param b0 the first bit index
     * @param b1 the second bit index
     */
    public static Matrix swap(int b0, int b1) {
        int nBits = max(max(b0, b1), 1) + 1;
        int[] bitPerm = IntStream.range(0, nBits).toArray();
        bitPerm[b0] = b1;
        bitPerm[b1] = b0;
        int[] statePerm = computeStatePermutation(bitPerm);
        statePerm = inversePermutation(statePerm);
        return permute(statePerm);
    }

    /**
     * Returns the matrix of T gate applied to i-th bit
     *
     * @param index the bit index
     */
    public static Matrix t(int index) {
        return createGate(T_GATE, index);
    }

    /**
     * Returns the vector product of two matrices
     * left[i, k] * right[j, l]
     *
     * @param right the right matrices
     */
    public Matrix cross(Matrix right) {
        int rows = numRows * right.numRows;
        int cols = numCols * right.numCols;
        Complex[] cells = new Complex[rows * cols];
        int idx = 0;
        for (int i = 0; i < numRows; i++) {
            for (int j = 0; j < right.numRows; j++) {
                for (int k = 0; k < numCols; k++) {
                    for (int l = 0; l < right.numCols; l++) {
                        cells[idx] = at(i, k).mul(right.at(j, l));
                        idx++;
                    }
                }
            }
        }
        return new Matrix(rows, cols, cells);
    }

    /**
     * Checks for the valid bit map with different values each other
     *
     * @param bitMap the bit map
     */
    private static void validateBitMap(int... bitMap) {
        for (int i = 0; i < bitMap.length; i++) {
            for (int j = i + 1; j < bitMap.length; j++) {
                if (bitMap[i] == bitMap[j]) {
                    throw new IllegalArgumentException(format("Expected all different indices %s", Arrays.toString(bitMap)));
                }
            }
        }
    }

    /**
     * Returns the matrix of x (not) gate applied to i-th bit
     *
     * @param index the bit index
     */
    public static Matrix x(int index) {
        return createGate(X_GATE, index);
    }

    /**
     * Returns the matrix of Y gate applied to i-th bit
     *
     * @param index the bit index
     */
    public static Matrix y(int index) {
        return createGate(Y_GATE, index);
    }

    /**
     * Returns the matrix of Z gate applied to i-th bit
     *
     * @param index the bit index
     */
    public static Matrix z(int index) {
        return createGate(Z_GATE, index);
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
        int n = max(numRows, other.numRows);
        int m = max(numCols, other.numCols);
        Matrix left = extends0(n, m);
        Matrix right = other.extends0(n, m);
        Complex[] cells = VectorUtils.add(left.cells, right.cells);
        return new Matrix(n, m, cells);
    }

    /**
     * Returns the element at index
     *
     * @param row the row index
     * @param col the column index
     */
    public Complex at(int row, int col) {
        return cells[index(row, col)];
    }

    /**
     * Returns the element at index (only for ket or bra)
     *
     * @param index index
     */
    public Complex at(int index) {
        return cells[index(index)];
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
     * Returns the conjugate transpose matrix
     */
    public Matrix dagger() {
        return conj().transpose();
    }

    /**
     * Returns the division by divisor
     *
     * @param value the divisor
     */
    public Matrix div(float value) {
        Complex[] cells = VectorUtils.divScalar(this.cells, value);
        return new Matrix(numRows, numCols, cells);
    }

    /**
     * Returns the division by divisor
     *
     * @param value the divisor
     */
    public Matrix div(Complex value) {
        Complex[] cells = VectorUtils.divScalar(this.cells, value);
        return new Matrix(numRows, numCols, cells);
    }

    /**
     * Returns the extended matrix by appending zero filled cell
     *
     * @param numRows the number of resulting rows
     * @param numCols the number of resulting columns
     */
    public Matrix extends0(int numRows, int numCols) {
        return extendsCols(numCols)
                .extendsRows(numRows);
    }

    /**
     * Returns the extended matrix by appending zero filled cols
     *
     * @param numCols the number resulting of rows
     */
    public Matrix extendsCols(int numCols) {
        if (this.numCols >= numCols) {
            return this;
        }
        Complex[] cells = new Complex[numRows * numCols];

        for (int i = 0; i < numRows; i++) {
            System.arraycopy(this.cells, i * this.numCols, cells, i * numCols, this.numCols);
            Arrays.fill(cells, i * numCols + this.numCols, i * numCols + numCols, Complex.zero());
        }
        return new Matrix(numRows, numCols, cells);
    }

    /**
     * Returns the extended matrix by cross-product of square matrices
     *
     * @param n the size of the resulting matrix
     */
    public Matrix extendsCrossSquare(int n) {
        if (numCols == 1) {
            // Extend ket
            return extendsRows(n);
        }
        if (numRows == 1) {
            // Extend Bra
            return extendsCols(n);
        }
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
    public Matrix extendsRows(int numRows) {
        if (this.numRows >= numRows) {
            return this;
        }
        Complex[] cells = new Complex[numRows * numCols];
        System.arraycopy(this.cells, 0, cells, 0, this.cells.length);
        Arrays.fill(cells, this.cells.length, cells.length, Complex.zero());
        return new Matrix(numRows, numCols, cells);
    }

    /**
     * Returns the index of element
     *
     * @param row the row index
     * @param col the column index
     */
    public int index(int row, int col) {
        if (row < 0 || row >= numRows || col < 0 || col >= numCols) {
            throw new IllegalArgumentException(format(
                    "index must have range (0-%d) x (0-%d) [%d, %d]",
                    numRows, numCols, row, col));
        }
        return unsafeIndex(numCols, row, col);
    }

    /**
     * Returns the index of element
     *
     * @param index the index
     */
    public int index(int index) {
        if (numCols == 1) {
            if (index < 0 || index >= numRows) {
                throw new IllegalArgumentException(format(
                        "index must have range (0-%d) [%d]",
                        numRows, index));
            }
        } else if (numRows == 1) {
            if (index < 0 || index >= numCols) {
                throw new IllegalArgumentException(format(
                        "index must have range (0-%d) [%d]",
                        numCols, index));
            }
        } else {
            throw new IllegalArgumentException(format(
                    "Expected 2 indices [%d]", index));
        }
        return index;
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
     * Returns the matrix multiplication (this x right) with extensions
     *
     * @param right the right matrix
     */
    public Matrix mul(Matrix right) {
        // Check for extensions
        Matrix left = this;
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
     * Returns the matrix multiplication (this x right)
     *
     * @param right the right matrix
     */
    private Matrix safeMul(Matrix right) {
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
    public Matrix sub(Matrix other) {
        int n = max(numRows, other.numRows);
        int m = max(numCols, other.numCols);
        Matrix left = extends0(n, m);
        Matrix right = other.extends0(n, m);
        Complex[] cells = VectorUtils.sub(left.cells, right.cells);
        return new Matrix(n, m, cells);
    }

    /**
     * Returns the bra string
     */
    private String toBraString() {
        StringBuilder builder = new StringBuilder();
        boolean isZero = true;
        for (int i = 0; i < cells.length; i++) {
            if (cells[i].norm() != 0) {
                if (!isZero) {
                    builder.append(" + ");
                }
                isZero = false;
                builder.append("(");
                builder.append(cells[i]);
                builder.append(") <");
                builder.append(i);
                builder.append("|");
            }
        }
        return isZero ? "(0.0) <" + (cells.length - 1) + "|" : builder.toString();
    }

    /**
     * Returns the ket string
     */
    private String toKetString() {
        StringBuilder builder = new StringBuilder();
        boolean isZero = true;
        for (int i = 0; i < cells.length; i++) {
            if (cells[i].norm() != 0) {
                if (!isZero) {
                    builder.append(" + ");
                }
                isZero = false;
                builder.append("(");
                builder.append(cells[i]);
                builder.append(") |");
                builder.append(i);
                builder.append(">");
            }
        }
        return isZero ? "(0.0) |" + (cells.length - 1) + ">" : builder.toString();
    }

    @Override
    public String toString() {
        if (numCols == 1) {
            if (numRows == 1) {
                // Scalar value
                return String.valueOf(cells[0]);
            } else {
                return toKetString();
            }
        } else if (numRows == 1) {
            return toBraString();
        }
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
