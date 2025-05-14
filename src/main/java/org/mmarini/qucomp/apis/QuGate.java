package org.mmarini.qucomp.apis;

import java.util.Arrays;
import java.util.stream.IntStream;

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;

/**
 * Defines the gate transformation and the bit indices of the gate
 */
public interface QuGate {

    /**
     * Returns the ccnot gate definition
     *
     * @param data the data bit index
     * @param c0   the control0 bit index
     * @param c1   the control1 bit index
     */
    static QuGate ccnot(int data, int c0, int c1) {
        return new QuGateImpl("ccnot", new int[]{data, c0, c1}, Matrix.ccnot());
    }

    /**
     * Returns the cnot gate definition
     *
     * @param data    the data bit index
     * @param control the control bit index
     */
    static QuGate cnot(int data, int control) {
        return new QuGateImpl("cnot", new int[]{data, control}, Matrix.cnot());
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
    static QuGate h(int data) {
        return new QuGateImpl("h", new int[]{data}, Matrix.h());
    }

    /**
     * Returns the ccnot gate definition
     *
     * @param qubit the data bit
     */
    static QuGate i(int qubit) {
        return new QuGateImpl("i", new int[]{qubit}, Matrix.identity());
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
    static QuGate s(int data) {
        return new QuGateImpl("s", new int[]{data}, Matrix.s());
    }

    /**
     * Returns the gate implementing state mapping
     *
     * @param qubits  the qubits
     * @param mapping the mapping
     */
    static QuGate stateMap(int[] qubits, int... mapping) {
        int numStates = qubits.length;
        if (requireNonNull(mapping).length != 1 << numStates) {
            throw new IllegalArgumentException(format("the state mapping should have %d element (%d)",
                    numStates, mapping.length));
        }
        return new QuGateImpl("map", qubits, Matrix.permute(mapping));
    }

    /**
     * Returns the swap gate definition
     *
     * @param data0 the data0 bit index
     * @param data1 the data1 bit index
     */
    static QuGate swap(int data0, int data1) {
        return new QuGateImpl("swap", new int[]{data0, data1}, Matrix.swap());
    }

    /**
     * Returns the t gate definition
     *
     * @param data the data bit index
     */
    static QuGate t(int data) {
        return new QuGateImpl("t", new int[]{data}, Matrix.t());
    }

    /**
     * Returns the x gate (not) definition
     *
     * @param data the data bit index
     */
    static QuGate x(int data) {
        return new QuGateImpl("x", new int[]{data}, Matrix.x());
    }

    /**
     * Returns the y gate definition
     *
     * @param data the data bit index
     */
    static QuGate y(int data) {
        return new QuGateImpl("y", new int[]{data}, Matrix.y());
    }

    /**
     * Returns the z gate definition
     *
     * @param data the data bit index
     */
    static QuGate z(int data) {
        return new QuGateImpl("z", new int[]{data}, Matrix.z());
    }

    /**
     * Returns the state transformation matrix of the gate
     *
     * @param numBits the number of bits
     */
    Matrix build(int numBits);

    /**
     * Returns the indices of bits
     */
    int[] indices();

    /**
     * Returns the highest bit index of the gate
     */
    default int maxIndices() {
        return Arrays.stream(indices()).max().orElse(-1);
    }

    /**
     * Returns the type of gate
     */
    String type();
}
