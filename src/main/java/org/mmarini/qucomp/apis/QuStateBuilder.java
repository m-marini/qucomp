package org.mmarini.qucomp.apis;

import java.util.Arrays;

public interface QuStateBuilder {
    /**
     * Returns the state transformation matrix
     *
     * @param gates the list of gate
     */
    static Matrix build(QuGate... gates) {
        int n = numQuBits(gates);
        return Arrays.stream(gates)
                .map(g -> g.build(n))
                .reduce(Matrix::mul)
                .orElseThrow();
    }

    /**
     * Returns the qubit numbers
     *
     * @param gates the list of gates
     */
    static int numQuBits(QuGate... gates) {
        return Arrays.stream(gates).mapToInt(
                        gate -> gate.maxIndices() + 1)
                .max()
                .orElse(0);
    }
}
