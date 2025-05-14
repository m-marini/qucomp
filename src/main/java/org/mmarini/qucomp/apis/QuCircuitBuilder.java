package org.mmarini.qucomp.apis;

import java.util.List;

/**
 * Creates or load gates
 */
public interface QuCircuitBuilder {

    /**
     * Returns the state transformation matrix
     *
     * @param gates the list of gate
     */
    static Matrix build(QuGate... gates) {
        return build(List.of(gates));
    }

    /**
     * Returns the state transformation matrix
     *
     * @param gates the list of gate
     */
    static Matrix build(List<QuGate> gates) {
        int n = numQuBits(gates);
        return gates.stream()
                .map(g -> g.build(n))
                .reduce((a, b) -> b.mul(a))
                .orElseThrow();
    }

    /**
     * Returns the qubit numbers
     *
     * @param gates the list of gates
     */
    static int numQuBits(QuGate... gates) {
        return numQuBits(List.of(gates));
    }

    /**
     * Returns the qubit numbers
     *
     * @param gates the list of gates
     */
    static int numQuBits(List<QuGate> gates) {
        return gates.stream().mapToInt(
                        gate -> gate.maxIndices() + 1)
                .max()
                .orElse(0);
    }
}
