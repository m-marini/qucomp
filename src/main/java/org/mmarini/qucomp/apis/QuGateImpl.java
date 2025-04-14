package org.mmarini.qucomp.apis;

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;

/**
 * Defines the gate transformation and the bit indices of the gate
 *
 * @param type      the port type
 * @param indices   the indices
 * @param transform the transformation
 */
public record QuGateImpl(String type, int[] indices, Matrix transform) implements QuGate {
    /**
     * Create the gate
     *
     * @param type      the type
     * @param indices   the indices
     * @param transform the transformation
     */
    public QuGateImpl(String type, int[] indices, Matrix transform) {
        this.type = requireNonNull(type);
        this.indices = requireNonNull(indices);
        this.transform = requireNonNull(transform);
        int n = 1 << indices.length;
        if (!transform.hasShape(n, n)) {
            throw new IllegalArgumentException(format("transform shape must be %dx%d (%dx%d)",
                    n, n, transform.numRows(), transform.numCols()));
        }
    }

    @Override
    public Matrix build(int numBits) {
        int m = maxIndices();
        if (m >= numBits) {
            throw new IllegalArgumentException(format("num bits must be greater then %d (%d)",
                    m, numBits));
        }
        // Swap the gate input to align to base transform
        int[] inBits = QuGate.computeMap(numBits, indices);
        int[] statesPermutation = QuGate.computeStatePermutation(inBits);
        int[] reverseState = QuGate.inversePermutation(statesPermutation);
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
}
