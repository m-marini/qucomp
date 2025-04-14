package org.mmarini.qucomp.apis;

import com.fasterxml.jackson.databind.JsonNode;
import org.mmarini.yaml.Locator;

import java.util.Arrays;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.IntFunction;
import java.util.stream.IntStream;

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;

/**
 * Defines the gate transformation and the bit indices of the gate
 */
public interface QuGate {
    Map<java.lang.String, BiFunction<JsonNode, Locator, QuGate>> BUILDERS = Map.ofEntries(
            Map.entry("s", (r, l) -> unaryFromJson(r, l, QuGate::s)),
            Map.entry("t", (r, l) -> unaryFromJson(r, l, QuGate::t)),
            Map.entry("h", (r, l) -> unaryFromJson(r, l, QuGate::h)),
            Map.entry("x", (r, l) -> unaryFromJson(r, l, QuGate::x)),
            Map.entry("y", (r, l) -> unaryFromJson(r, l, QuGate::y)),
            Map.entry("z", (r, l) -> unaryFromJson(r, l, QuGate::z)),
            Map.entry("i", (r, l) -> unaryFromJson(r, l, QuGate::i)),
            Map.entry("swap", QuGate::swapFromJson),
            Map.entry("cnot", QuGate::cnotFromJson),
            Map.entry("ccnot", QuGate::ccnotFromJson),
            Map.entry("map", QuGate::stateMapFromJson)
    );

    /**
     * Returns the ccnot gate definition
     *
     * @param c0   the control0 bit index
     * @param c1   the control1 bit index
     * @param data the data bit index
     */
    static QuGate ccnot(int c0, int c1, int data) {
        return new QuGateImpl("ccnot", new int[]{data, c0, c1}, Matrix.ccnot());
    }

    /**
     * Returns the gate from JSON
     *
     * @param root    the document
     * @param locator the locator
     */
    static QuGate ccnotFromJson(JsonNode root, Locator locator) {
        int[] controls = locator.path("controls")
                .elements(root)
                .mapToInt(l ->
                        l.getNode(root).asInt())
                .toArray();
        int data = locator.path("data").getNode(root).asInt();
        return ccnot(controls[0], controls[1], data);
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
     * Returns the gate from JSON
     *
     * @param root    the document
     * @param locator the locator
     */
    static QuGate cnotFromJson(JsonNode root, Locator locator) {
        int control = locator.path("control").getNode(root).asInt();
        int data = locator.path("data").getNode(root).asInt();
        return cnot(data, control);
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
     * Returns the gate from JSON
     *
     * @param root    the document
     * @param locator the locator
     */
    static QuGate fromJson(JsonNode root, Locator locator) {
        java.lang.String gate = locator.path("gate").getNode(root).asText();
        BiFunction<JsonNode, Locator, QuGate> builder = BUILDERS.get(gate);
        if (builder == null) {
            throw new IllegalArgumentException(format("Unknown gate \"%s\"", gate));
        }
        return builder.apply(root, locator);
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
     * Returns the gate from JSON
     *
     * @param root    the document
     * @param locator the locator
     */
    static QuGate stateMapFromJson(JsonNode root, Locator locator) {
        int[] qubits = locator.path("qubits")
                .elements(root)
                .mapToInt(l ->
                        l.getNode(root).asInt())
                .toArray();
        int[][] changing = locator.path("changes")
                .elements(root)
                .map(l ->
                        l.elements(root)
                                .mapToInt(l1 ->
                                        l1.getNode(root).asInt())
                                .toArray()
                )
                .toArray(int[][]::new);
        int[] mapping = IntStream.range(0, 1 << qubits.length).toArray();
        for (int[] change : changing) {
            if (change[0] < 0 || change[0] >= mapping.length) {
                throw new IllegalArgumentException(format("Source state must be between 0 and %d (%d)",
                        mapping.length, change[0]));
            }
            if (change[1] < 0 || change[1] >= mapping.length) {
                throw new IllegalArgumentException(format("Target state must be between 0 and %d (%d)",
                        mapping.length, change[1]));
            }
            mapping[change[0]] = change[1];
        }
        return stateMap(qubits, mapping);
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
     * Returns the gate from JSON
     *
     * @param root    the document
     * @param locator the locator
     */
    static QuGate swapFromJson(JsonNode root, Locator locator) {
        int[] qubit = locator.path("qubits")
                .elements(root)
                .mapToInt(l ->
                        l.getNode(root).asInt())
                .toArray();
        return swap(qubit[0], qubit[1]);
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
     * Returns the gate from JSON
     *
     * @param root    the document
     * @param locator the locator
     * @param builder the general builder function
     */
    static QuGate unaryFromJson(JsonNode root, Locator locator, IntFunction<QuGate> builder) {
        int qubit = locator.path("qubit").getNode(root).asInt();
        return builder.apply(qubit);
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
