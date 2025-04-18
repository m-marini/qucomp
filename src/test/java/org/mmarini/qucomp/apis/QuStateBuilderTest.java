package org.mmarini.qucomp.apis;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mmarini.yaml.Locator;
import org.mmarini.yaml.Utils;

import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.arrayWithSize;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mmarini.Matchers.complexClose;
import static org.mmarini.qucomp.apis.QuGate.swap;
import static org.mmarini.qucomp.apis.QuGate.x;

class QuStateBuilderTest {

    public static final float EPSILON = 1e-6F;
    public static final java.lang.String YAML = """
            ---
            $schema: https://mmarini.org/qucomp/qucomp-schema-0.2
            gates:
              - gate: s
                qubit: 1
              - gate: t
                qubit: 1
              - gate: h
                qubit: 1
              - gate: i
                qubit: 1
              - gate: x
                qubit: 1
              - gate: y
                qubit: 1
              - gate: z
                qubit: 1
              - gate: swap
                qubits: [1, 2]
              - gate: cnot
                control: 1
                data: 2
              - gate: ccnot
                controls: [1, 2]
                data: 3
              - gate: map
                qubits: [1, 2]
                changes:
                  - [0, 1]
                  - [1, 0]
            """;

    @ParameterizedTest
    @CsvSource({
            "0,0", // 000 -> 000
            "1,1", // 001 -> 001
            "2,4", // 010 -> 100
            "3,5", // 011 -> 101
            "4,2", // 100 -> 010
            "5,3", // 101 -> 011
            "6,6", // 110 -> 110
            "7,7", // 111 -> 111
    })
    void build(int in, int exp) {
        // Given
        Matrix m = QuCircuitBuilder.build(swap(1, 2));
        Ket ket0 = Ket.base(in, 3);
        // When
        Ket ket1 = ket0.mul(m);
        // Then
        Ket expKet = Ket.base(exp, 3);
        assertThat(ket1.values()[0], complexClose(expKet.values()[0], EPSILON));
        assertThat(ket1.values()[1], complexClose(expKet.values()[1], EPSILON));
        assertThat(ket1.values()[2], complexClose(expKet.values()[2], EPSILON));
        assertThat(ket1.values()[3], complexClose(expKet.values()[3], EPSILON));
        assertThat(ket1.values()[4], complexClose(expKet.values()[4], EPSILON));
        assertThat(ket1.values()[5], complexClose(expKet.values()[5], EPSILON));
        assertThat(ket1.values()[6], complexClose(expKet.values()[6], EPSILON));
        assertThat(ket1.values()[7], complexClose(expKet.values()[7], EPSILON));

    }

    @ParameterizedTest
    @CsvSource({
            "0,3", // 00 -> 11
            "1,2", // 01 -> 10
            "2,1", // 10 -> 01
            "3,0", // 11 -> 00
    })
    void build1(int in, int exp) {
        // Given
        Matrix m = QuCircuitBuilder.build(x(0), x(1));
        Ket ket0 = Ket.base(in, 2);
        // When
        Ket ket1 = ket0.mul(m);
        // Then
        Ket expKet = Ket.base(exp, 2);
        assertThat(ket1.values()[0], complexClose(expKet.values()[0], EPSILON));
        assertThat(ket1.values()[1], complexClose(expKet.values()[1], EPSILON));
        assertThat(ket1.values()[2], complexClose(expKet.values()[2], EPSILON));
        assertThat(ket1.values()[3], complexClose(expKet.values()[3], EPSILON));

    }

    @Test
    void loadGates() throws IOException {
        JsonNode node = Utils.fromText(YAML);
        QuGate[] gates = QuCircuitBuilder.loadGates(node, Locator.root());
        assertThat(gates, arrayWithSize(11));

        assertArrayEquals(new int[]{1}, gates[0].indices());
        assertEquals(Matrix.s(), ((QuGateImpl) gates[0]).transform());

        assertArrayEquals(new int[]{1}, gates[1].indices());
        assertEquals(Matrix.t(), ((QuGateImpl) gates[1]).transform());

        assertArrayEquals(new int[]{1}, gates[2].indices());
        assertEquals(Matrix.h(), ((QuGateImpl) gates[2]).transform());

        assertArrayEquals(new int[]{1}, gates[3].indices());
        assertEquals(Matrix.identity(), ((QuGateImpl) gates[3]).transform());

        assertArrayEquals(new int[]{1}, gates[4].indices());
        assertEquals(Matrix.x(), ((QuGateImpl) gates[4]).transform());

        assertArrayEquals(new int[]{1}, gates[5].indices());
        assertEquals(Matrix.y(), ((QuGateImpl) gates[5]).transform());

        assertArrayEquals(new int[]{1}, gates[6].indices());
        assertEquals(Matrix.z(), ((QuGateImpl) gates[6]).transform());

        assertArrayEquals(new int[]{1, 2}, gates[7].indices());
        assertEquals(Matrix.swap(), ((QuGateImpl) gates[7]).transform());

        assertArrayEquals(new int[]{2, 1}, gates[8].indices());
        assertEquals(Matrix.cnot(), ((QuGateImpl) gates[8]).transform());

        assertArrayEquals(new int[]{3, 1, 2}, gates[9].indices());
        assertEquals(Matrix.ccnot(), ((QuGateImpl) gates[9]).transform());

        assertArrayEquals(new int[]{1, 2}, gates[10].indices());
        assertThat(((QuGateImpl) gates[10]).transform().at(0, 0), complexClose(0, EPSILON));
        assertThat(((QuGateImpl) gates[10]).transform().at(0, 1), complexClose(1, EPSILON));
        assertThat(((QuGateImpl) gates[10]).transform().at(0, 2), complexClose(0, EPSILON));
        assertThat(((QuGateImpl) gates[10]).transform().at(0, 3), complexClose(0, EPSILON));
        assertThat(((QuGateImpl) gates[10]).transform().at(1, 0), complexClose(1, EPSILON));
        assertThat(((QuGateImpl) gates[10]).transform().at(1, 1), complexClose(0, EPSILON));
        assertThat(((QuGateImpl) gates[10]).transform().at(1, 2), complexClose(0, EPSILON));
        assertThat(((QuGateImpl) gates[10]).transform().at(1, 3), complexClose(0, EPSILON));
        assertThat(((QuGateImpl) gates[10]).transform().at(2, 0), complexClose(0, EPSILON));
        assertThat(((QuGateImpl) gates[10]).transform().at(2, 1), complexClose(0, EPSILON));
        assertThat(((QuGateImpl) gates[10]).transform().at(2, 2), complexClose(1, EPSILON));
        assertThat(((QuGateImpl) gates[10]).transform().at(2, 3), complexClose(0, EPSILON));
        assertThat(((QuGateImpl) gates[10]).transform().at(3, 0), complexClose(0, EPSILON));
        assertThat(((QuGateImpl) gates[10]).transform().at(3, 1), complexClose(0, EPSILON));
        assertThat(((QuGateImpl) gates[10]).transform().at(3, 2), complexClose(0, EPSILON));
        assertThat(((QuGateImpl) gates[10]).transform().at(3, 3), complexClose(1, EPSILON));
    }

    @Test
    void numQuBits() {
        assertEquals(0, QuCircuitBuilder.numQuBits());
        assertEquals(6, QuCircuitBuilder.numQuBits(swap(5, 1), x(3)));
    }
}