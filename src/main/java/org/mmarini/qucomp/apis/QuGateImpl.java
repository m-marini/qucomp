package org.mmarini.qucomp.apis;

import java.util.Arrays;
import java.util.Objects;
import java.util.StringJoiner;

import static java.util.Objects.requireNonNull;

/**
 * Defines the gate transformation and the bit indices of the gate
 */
public abstract class QuGateImpl implements QuGate {
    private final String type;
    private final int[] indices;

    /**
     * Create the gate
     *
     * @param type      the type
     * @param indices the indices
     */
    public QuGateImpl(String type, int... indices) {
        this.type = requireNonNull(type);
        this.indices = requireNonNull(indices);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        QuGateImpl quGate = (QuGateImpl) o;
        return Objects.equals(type, quGate.type) && Objects.deepEquals(indices, quGate.indices);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, Arrays.hashCode(indices));
    }

    @Override
    public int[] indices() {
        return indices;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", QuGateImpl.class.getSimpleName() + "[", "]")
                .add("type='" + type + "'")
                .add("indices=" + Arrays.toString(indices))
                .toString();
    }

    @Override
    public String type() {
        return type;
    }
}
