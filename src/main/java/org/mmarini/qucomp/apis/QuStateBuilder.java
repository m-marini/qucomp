package org.mmarini.qucomp.apis;

import com.fasterxml.jackson.databind.JsonNode;
import org.mmarini.yaml.Locator;

import java.util.Arrays;

/**
 * Creates or load gates
 */
public interface QuStateBuilder {
    String SCHEMA = "https://mmarini.org/qucomp/qucomp-schema-0.1";

    static QuGate[] loadGates(JsonNode root, Locator locator) {
        JsonSchemas.instance().validateOrThrow(root, SCHEMA);
        return locator.path("gates").elements(root)
                .map(l ->
                        QuGate.fromJson(root, l)
                ).toArray(QuGate[]::new);
    }

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
