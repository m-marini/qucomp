package org.mmarini.qucomp.apis;

import java.util.Arrays;
import java.util.stream.IntStream;

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;

/**
 * Defines the gate transformation and the bit indices of the gate
 *
 * @param indices   the indices
 * @param transform the transformation
 */
public record QuGate(int[] indices, Matrix transform) {
    /**
     * Returns the ccnot gate definition
     *
     * @param c0   the control0 bit index
     * @param c1   the control1 bit index
     * @param data the data bit index
     */
    public static QuGate ccnot(int c0, int c1, int data) {
        return new QuGate(new int[]{c0, c1, data}, Matrix.ccnot());
    }

    /**
     * Returns the cnot gate definition
     *
     * @param control the control bit index
     * @param data    the data bit index
     */
    public static QuGate cnot(int control, int data) {
        return new QuGate(new int[]{control, data}, Matrix.ccnot());
    }

    /**
     * Returns the bit permutation from input to internal gate input
     *
     * @param numBits number of qubits
     * @param portMap the bit mapping from internal gate input to input
     */
    static int[] computeMap(int numBits, int... portMap) {
        int[] result = new int[numBits];
        int m = portMap.length;
        boolean[] gateMapped = new boolean[numBits];
        boolean[] inMapped = new boolean[numBits];
        // Map gate input
        for (int i = 0; i < m; i++) {
            result[portMap[i]] = i;
            gateMapped[portMap[i]] = inMapped[i] = true;
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
     * Computes the state permutation
     * <pre>
     *     out[p[i]]=in[i]
     * </pre>
     *
     * @param bitPermutation the bit permutation
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
     * Returns the h gate (Hadamard) definition
     *
     * @param data the data bit index
     */
    public static QuGate h(int data) {
        return new QuGate(new int[]{data}, Matrix.h());
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
     * Returns the s gate definition
     *
     * @param data the data bit index
     */
    public static QuGate s(int data) {
        return new QuGate(new int[]{data}, Matrix.s());
    }

    /**
     * Returns the swap gate definition
     *
     * @param data0 the data0 bit index
     * @param data1 the data1 bit index
     */
    public static QuGate swap(int data0, int data1) {
        return new QuGate(new int[]{data0, data1}, Matrix.swap());
    }

    /**
     * Returns the t gate definition
     *
     * @param data the data bit index
     */
    public static QuGate t(int data) {
        return new QuGate(new int[]{data}, Matrix.t());
    }

    /**
     * Returns the x gate (not) definition
     *
     * @param data the data bit index
     */
    public static QuGate x(int data) {
        return new QuGate(new int[]{data}, Matrix.x());
    }

    /**
     * Returns the y gate definition
     *
     * @param data the data bit index
     */
    public static QuGate y(int data) {
        return new QuGate(new int[]{data}, Matrix.y());
    }

    /**
     * Returns the z gate definition
     *
     * @param data the data bit index
     */
    public static QuGate z(int data) {
        return new QuGate(new int[]{data}, Matrix.z());
    }

    /**
     * Create the gate
     *
     * @param indices   the indices
     * @param transform the transformation
     */
    public QuGate(int[] indices, Matrix transform) {
        this.indices = requireNonNull(indices);
        this.transform = requireNonNull(transform);
        int n = 1 << indices.length;
        if (!transform.hasShape(n, n)) {
            throw new IllegalArgumentException(format("transform shape must be %dx%d (%dx%d)",
                    n, n, transform.numRows(), transform.numCols()));
        }
    }

    /**
     * Returns the state transformation matrix of the gate
     *
     * @param numBits the number of bits
     */
    public Matrix build(int numBits) {
        int m = maxIndices();
        if (m >= numBits) {
            throw new IllegalArgumentException(format("num bits must be greater then %d (%d)",
                    m, numBits));
        }
        // Swap the gate input to align to base transform
        int[] inBits = computeMap(numBits, indices);
        int[] statesPermutation = computeStatePermutation(inBits);
        int[] reverseState = inversePermutation(statesPermutation);
        Matrix inSwapMatrix = Matrix.permute(statesPermutation);
        Matrix outSwapMatrix = Matrix.permute(reverseState);
        // Create the full gate matrix
        int n = indices.length;
        Matrix gateMatrix = transform;
        if (numBits > n) {
            Matrix identities = Matrix.identity(1 << (numBits - n));
            gateMatrix = identities.cross(gateMatrix);
        }
        // Create the full state transformation matrix
        return outSwapMatrix.mul(gateMatrix).mul(inSwapMatrix);
    }

    /**
     * Returns the highest bit index of the gate
     */
    int maxIndices() {
        return Arrays.stream(indices).max().orElse(-1);
    }
}
