package org.mmarini.qucomp.apis;

import java.util.Arrays;
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
        return gates.stream()
                .map(QuGate::build)
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
                        gate -> Arrays.stream(gate.indices()).max().orElse(0) + 1)
                .max()
                .orElse(0);
    }
}
