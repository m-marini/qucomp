package org.mmarini.qucomp.apis;

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
        return new QuGateImpl("ccnot", data, c0, c1) {
            @Override
            public Matrix build() {
                return Matrix.ccnot(data, c0, c1);
            }
        };
    }

    /**
     * Returns the cnot gate definition
     *
     * @param data    the data bit index
     * @param control the control bit index
     */
    static QuGate cnot(int data, int control) {
        return new QuGateImpl("cnot", data, control) {
            @Override
            public Matrix build() {
                return Matrix.cnot(data, control);
            }
        };
    }

    /**
     * Returns the bit permutation from input to internal gate input
     *
     * @param numBits number of qubits
     * @param portMap the bits mapping from internal gate input to input
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
     * Returns the h gate (Hadamard) definition
     *
     * @param data the data bit index
     */
    static QuGate h(int data) {
        return new QuGateImpl("h", data) {
            @Override
            public Matrix build() {
                return Matrix.h(data);
            }
        };
    }

    /**
     * Returns the ccnot gate definition
     *
     * @param qubit the data bit
     */
    static QuGate i(int qubit) {
        return new QuGateImpl("i", qubit) {
            @Override
            public Matrix build() {
                return Matrix.identity(2 << qubit);
            }
        };
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
        return new QuGateImpl("s", data) {
            @Override
            public Matrix build() {
                return Matrix.s(data);
            }
        };
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
        return new QuGateImpl("map", qubits) {
            @Override
            public Matrix build() {
                return Matrix.permute(mapping);
            }
        };
    }

    /**
     * Returns the swap gate definition
     *
     * @param data0 the data0 bit index
     * @param data1 the data1 bit index
     */
    static QuGate swap(int data0, int data1) {
        return new QuGateImpl("swap", data0, data1) {
            @Override
            public Matrix build() {
                return Matrix.swap(data0, data1);
            }
        };
    }

    /**
     * Returns the t gate definition
     *
     * @param data the data bit index
     */
    static QuGate t(int data) {
        return new QuGateImpl("t", data) {
            @Override
            public Matrix build() {
                return Matrix.t(data);
            }
        };
    }

    /**
     * Returns the x gate (not) definition
     *
     * @param data the data bit index
     */
    static QuGate x(int data) {
        return new QuGateImpl("x", data) {
            @Override
            public Matrix build() {
                return Matrix.x(data);
            }
        };
    }

    /**
     * Returns the y gate definition
     *
     * @param data the data bit index
     */
    static QuGate y(int data) {
        return new QuGateImpl("y", data) {
            @Override
            public Matrix build() {
                return Matrix.y(data);
            }
        };
    }

    /**
     * Returns the z gate definition
     *
     * @param data the data bit index
     */
    static QuGate z(int data) {
        return new QuGateImpl("z", data) {
            @Override
            public Matrix build() {
                return Matrix.z(data);
            }
        };
    }

    /**
     * Returns the state transformation matrix of the gate
     */
    Matrix build();

    /**
     * Returns the indices of bits
     */
    int[] indices();

    /**
     * Returns the type of gate
     */
    String type();
}
