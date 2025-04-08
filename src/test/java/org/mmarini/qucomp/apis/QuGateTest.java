package org.mmarini.qucomp.apis;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mmarini.yaml.Locator;
import org.mmarini.yaml.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mmarini.Matchers.complexClose;

class QuGateTest {

    public static final float EPSILON = 1e-6F;
    public static final java.lang.String S_YAML = """
            ---
            gate: s
            qubit: 1
            """;
    public static final java.lang.String T_YAML = """
            ---
            gate: t
            qubit: 1
            """;
    public static final java.lang.String H_YAML = """
            ---
            gate: h
            qubit: 1
            """;
    public static final java.lang.String X_YAML = """
            ---
            gate: x
            qubit: 1
            """;
    public static final java.lang.String Y_YAML = """
            ---
            gate: y
            qubit: 1
            """;
    public static final java.lang.String Z_YAML = """
            ---
            gate: z
            qubit: 1
            """;
    public static final java.lang.String I_YAML = """
            ---
            gate: i
            qubit: 1
            """;
    public static final java.lang.String SWAP_YAML = """
            ---
            gate: swap
            qubits: [1, 2]
            """;
    public static final java.lang.String CNOT_YAML = """
            ---
            gate: cnot
            control: 1
            data: 2
            """;
    public static final java.lang.String CCNOT_YAML = """
            ---
            gate: ccnot
            controls: [1, 2]
            data: 3
            """;
    private static final Logger logger = LoggerFactory.getLogger(QuGateTest.class);

    @ParameterizedTest
    @CsvSource({
            "0, 0",
            "1, 2",
            "2, 1",
            "3, 3",
    })
    void build20Bit(int in, int exp) {
        // Given
        QuGate gate = QuGate.swap(1, 0);
        // When
        Matrix m = gate.build(2);
        Ket ket0 = Ket.base(in, 2);
        Ket ket = ket0.mul(m);
        // Then
        Ket expKet = Ket.base(exp, 2);
        assertThat(ket.values()[0], complexClose(expKet.values()[0], EPSILON));
        assertThat(ket.values()[1], complexClose(expKet.values()[1], EPSILON));
        assertThat(ket.values()[2], complexClose(expKet.values()[2], EPSILON));
        assertThat(ket.values()[3], complexClose(expKet.values()[3], EPSILON));
    }

    @ParameterizedTest
    @CsvSource({
            "0,1, 0,0",
            "0,1, 1,2",
            "0,1, 2,1",
            "0,1, 3,3",
            "1,0, 0,0",
            "1,0, 1,2",
            "1,0, 2,1",
            "1,0, 3,3",
    })
    void build2Bit(int b0, int b1, int s, int exp) {
        // Given
        QuGate gate = QuGate.swap(b0, b1);
        // When
        Matrix m = gate.build(2);
        Ket ket0 = Ket.base(s, 2);
        Ket ket = ket0.mul(m);
        // Then
        Ket expKet = Ket.base(exp, 2);
        assertThat(ket.values()[0], complexClose(expKet.values()[0], EPSILON));
        assertThat(ket.values()[1], complexClose(expKet.values()[1], EPSILON));
        assertThat(ket.values()[2], complexClose(expKet.values()[2], EPSILON));
        assertThat(ket.values()[3], complexClose(expKet.values()[3], EPSILON));
    }

    @ParameterizedTest
    @CsvSource({
            "0,1, 0,0", // 000 000
            "0,1, 1,2", // 001 010
            "0,1, 2,1", // 010 001
            "0,1, 3,3", // 011 011
            "0,1, 4,4", // 100 100
            "0,1, 5,6", // 101 110
            "0,1, 6,5", // 110 101
            "0,1, 7,7", // 111 111

            "1,0, 0,0", // 000 000
            "1,0, 1,2", // 001 010
            "1,0, 2,1", // 010 001
            "1,0, 3,3", // 011 011
            "1,0, 4,4", // 100 100
            "1,0, 5,6", // 101 110
            "1,0, 6,5", // 110 101
            "1,0, 7,7", // 111 111

            "0,2, 0,0", // 000 000
            "0,2, 1,4", // 001 100
            "0,2, 2,2", // 010 010
            "0,2, 3,6", // 011 110
            "0,2, 4,1", // 100 001
            "0,2, 5,5", // 101 101
            "0,2, 6,3", // 110 011
            "0,2, 7,7", // 111 111

            "2,0, 0,0", // 000 000
            "2,0, 1,4", // 001 100
            "2,0, 2,2", // 010 010
            "2,0, 3,6", // 011 110
            "2,0, 4,1", // 100 001
            "2,0, 5,5", // 101 101
            "2,0, 6,3", // 110 011
            "2,0, 7,7", // 111 111

            "2,1, 0,0", // 000 000
            "2,1, 1,1", // 001 001
            "2,1, 2,4", // 010 100
            "2,1, 3,5", // 011 101
            "2,1, 4,2", // 100 010
            "2,1, 5,3", // 101 011
            "2,1, 6,6", // 110 110
            "2,1, 7,7", // 111 111
    })
    void build3Bit(int bit0, int bit1, int in, int exp) {
        // Given
        QuGate gate = QuGate.swap(bit0, bit1);
        // When
        Matrix m = gate.build(3);
        Ket ket0 = Ket.base(in, 3);
        Ket ket = ket0.mul(m);
        // Then
        logger.atDebug().log("build3Bit m=\n{}", m);
        Ket expKet = Ket.base(exp, 3);
        assertThat(ket.values()[0], complexClose(expKet.values()[0], EPSILON));
        assertThat(ket.values()[1], complexClose(expKet.values()[1], EPSILON));
        assertThat(ket.values()[2], complexClose(expKet.values()[2], EPSILON));
        assertThat(ket.values()[3], complexClose(expKet.values()[3], EPSILON));
        assertThat(ket.values()[4], complexClose(expKet.values()[4], EPSILON));
        assertThat(ket.values()[5], complexClose(expKet.values()[5], EPSILON));
        assertThat(ket.values()[6], complexClose(expKet.values()[6], EPSILON));
        assertThat(ket.values()[7], complexClose(expKet.values()[7], EPSILON));
    }

    @ParameterizedTest
    @CsvSource({
            "1,0, 0,0", // 0000 0000
            "1,0, 1,2", // 0001 0010
            "1,0, 2,1", // 0010 0001
            "1,0, 3,3", // 0011 0011
            "1,0, 4,4", // 0100 0100
            "1,0, 5,6", // 0101 0110
            "1,0, 6,5", // 0110 0101
            "1,0, 7,7", // 0111 0111
            "1,0, 8,8", // 1000 1000
            "1,0, 9,10", // 1001 1010
            "1,0, 10,9", // 1010 1001
            "1,0, 11,11", // 1011 1011
            "1,0, 12,12", // 1100 1100
            "1,0, 13,14", // 1101 1110
            "1,0, 14,13", // 1110 1101
            "1,0, 15,15", // 1111 1111
    })
    void build4Bit(int bit0, int bit1, int in, int exp) {
        // Given
        QuGate gate = QuGate.swap(bit0, bit1);
        // When
        Matrix m = gate.build(4);
        Ket ket0 = Ket.base(in, 4);
        Ket ket = ket0.mul(m);
        // Then
        Ket expKet = Ket.base(exp, 4);
        assertThat(ket.values()[0], complexClose(expKet.values()[0], EPSILON));
        assertThat(ket.values()[1], complexClose(expKet.values()[1], EPSILON));
        assertThat(ket.values()[2], complexClose(expKet.values()[2], EPSILON));
        assertThat(ket.values()[3], complexClose(expKet.values()[3], EPSILON));
        assertThat(ket.values()[4], complexClose(expKet.values()[4], EPSILON));
        assertThat(ket.values()[5], complexClose(expKet.values()[5], EPSILON));
        assertThat(ket.values()[6], complexClose(expKet.values()[6], EPSILON));
        assertThat(ket.values()[7], complexClose(expKet.values()[7], EPSILON));
        assertThat(ket.values()[8], complexClose(expKet.values()[8], EPSILON));
        assertThat(ket.values()[9], complexClose(expKet.values()[9], EPSILON));
        assertThat(ket.values()[10], complexClose(expKet.values()[10], EPSILON));
        assertThat(ket.values()[11], complexClose(expKet.values()[11], EPSILON));
        assertThat(ket.values()[12], complexClose(expKet.values()[12], EPSILON));
        assertThat(ket.values()[13], complexClose(expKet.values()[13], EPSILON));
        assertThat(ket.values()[14], complexClose(expKet.values()[14], EPSILON));
        assertThat(ket.values()[15], complexClose(expKet.values()[15], EPSILON));
    }

    @ParameterizedTest
    @CsvSource({
            "0,1, 0,1,2", // o[0]=i[0], o[1]=i[1], o[2]=i[2], p=(0 1 2)
            "0,2, 0,2,1", // o[0]=i[0], o[1]=i[2], o[2]=i[1], p=(0 2 1)
            "1,0, 1,0,2", // o[0]=i[1], o[1]=i[0], o[2]=i[2], p=(1 0 2)
            "1,2, 2,0,1", // o[0]=i[1], o[1]=i[2], o[2]=i[0], p=(2 0 1)
            "2,0, 1,2,0", // o[0]=i[2], o[1]=i[0], o[2]=i[1], p=(1 2 0)
            "2,1, 2,1,0", // o[0]=i[2], o[1]=i[1], o[2]=i[0], p=(2 1 0)
    })
    void computePortMap3bits(int i0, int i1, int e0, int e1, int e2) {
        // Given
        int[] portMap = new int[]{i0, i1};
        // When
        int[] oitMap = QuGate.computeMap(3, portMap);
        // Then
        assertArrayEquals(new int[]{e0, e1, e2}, oitMap);
    }

    @ParameterizedTest
    @CsvSource({
            "0,1, 0,1,2,3,4", // o[0]=i[0], o[1]=i[1], o[2]=i[2], o[3]=i[3], o[4]=i[4] p=(0 1 2 3 4)
            "0,2, 0,2,1,3,4", // o[0]=i[0], o[1]=i[2], o[2]=i[1], o[3]=i[3], o[4]=i[4] p=(0 2 1 3 4)
            "0,3, 0,3,2,1,4", // o[0]=i[0], o[1]=i[3], o[2]=i[2], o[3]=i[1], o[4]=i[4] p=(0 3 2 1 4)
            "1,0, 1,0,2,3,4", // o[0]=i[1], o[1]=i[0], o[2]=i[2], o[3]=i[3], o[4]=i[4] p=(1 0 2 3 4)
            "1,2, 2,0,1,3,4", // o[0]=i[1], o[1]=i[2], o[2]=i[0], o[3]=i[3], o[4]=i[4] p=(2 0 1 3 4)
            "1,3, 3,0,2,1,4", // o[0]=i[1], o[1]=i[3], o[2]=i[2], o[3]=i[0], o[4]=i[4] p=(3 0 2 1 4)
            "2,0, 1,2,0,3,4", // o[0]=i[2], o[1]=i[0], o[2]=i[1], o[3]=i[3], o[4]=i[4] p=(1 2 0 3 4)
            "2,1, 2,1,0,3,4", // o[0]=i[2], o[1]=i[1], o[2]=i[0], o[3]=i[3], o[4]=i[4]
            "2,3, 2,3,0,1,4", // o[0]=i[2], o[1]=i[3], o[2]=i[0], o[3]=i[3], o[4]=i[4]
            "3,1, 3,1,2,0,4", // o[0]=i[3], o[1]=i[1], o[2]=i[2], o[3]=i[0], o[4]=i[4]
            "3,2, 2,3,1,0,4", // o[0]=i[3], o[1]=i[2], o[2]=i[0], o[3]=i[1], o[4]=i[4] p=(2 3 1 0 4)
            "3,4, 3,4,2,0,1", // o[0]=i[3], o[1]=i[4], o[2]=i[2], o[3]=i[0], o[4]=i[1] p=(3 4 2 0 1)
    })
    void computePortMap5bits(int i0, int i1, int e0, int e1, int e2, int e3, int e4) {
        // Given
        int[] portMap = new int[]{i0, i1};
        // When
        int[] oitMap = QuGate.computeMap(5, portMap);
        // Then
        assertArrayEquals(new int[]{e0, e1, e2, e3, e4}, oitMap);
    }

    @ParameterizedTest(name = "[{index}] p=[{0} {1} {2}]")
    @CsvSource({
            // b[0]=a[0], b[1]=a[1], b[2]=a[2]
            // in  = 000 001 010 011 100 101 110 111
            // out = 000 001 010 011 100 101 110 111
            "0,1,2, 0,1,2,3,4,5,6,7",

            // b[1]=a[0], b[0]=a[1], b[2]=a[2]
            // in  = 000 001 010 011 100 101 110 111
            // out = 000 010 001 011 100 110 101 111
            // iini = 0 1 2 3 4 5 6 7
            // outi = 0 2 1 3 4 6 5 7
            // outstate[s[i]]= instate[i]
            // s=(0, 2, 1, 3, 4 5 6 7)
            "1,0,2, 0,2,1,3,4,6,5,7",

            // b[2]=a[0], b[1]=a[1], b[0]=a[2]
            //  in 000 001 010 011 100 101 110 111
            // out 000 100 010 110 001 101 011 111
            "2,1,0, 0,4,2,6,1,5,3,7",

            // b[1]=a[0], b[2]=a[1], b[0]=a[2]
            //  in 000 001 010 011 100 101 110 111
            // out 000 010 100 110 001 011 101 111
            // iini = 0 1 2 3 4 5 6 7
            // outi = 0 2 4 6 1 3 5 7
            // outstate[s[i]]= instate[i]
            "1,2,0, 0,2,4,6,1,3,5,7"
    })
    void computeStatePermutation3(int b0, int b1, int b2, int s0, int s1, int s2, int s3, int s4, int s5, int s6, int s7) {
        // When
        int[] states = QuGate.computeStatePermutation(b0, b1, b2);
        // Then
        assertArrayEquals(new int[]{s0, s1, s2, s3, s4, s5, s6, s7}, states);
    }

    @Test
    void ccnotFromJson() throws IOException {
        // Given
        JsonNode node = Utils.fromText(CCNOT_YAML);
        // When
        QuGate gate = QuGate.fromJson(node, Locator.root());
        // Then
        assertArrayEquals(new int[]{
                1, 2, 3
        }, gate.indices());
        Matrix m = gate.transform();
        assertSame(Matrix.ccnot(), m);
    }

    @Test
    void cnotFromJson() throws IOException {
        // Given
        JsonNode node = Utils.fromText(CNOT_YAML);
        // When
        QuGate gate = QuGate.fromJson(node, Locator.root());
        // Then
        assertArrayEquals(new int[]{
                1, 2
        }, gate.indices());
        Matrix m = gate.transform();
        assertSame(Matrix.cnot(), m);
    }

    @Test
    void hFromJson() throws IOException {
        // Given
        JsonNode node = Utils.fromText(H_YAML);
        // When
        QuGate gate = QuGate.fromJson(node, Locator.root());
        // Then
        assertArrayEquals(new int[]{
                1
        }, gate.indices());
        Matrix m = gate.transform();
        assertSame(Matrix.h(), m);
    }

    @Test
    void iFromJson() throws IOException {
        // Given
        JsonNode node = Utils.fromText(I_YAML);
        // When
        QuGate gate = QuGate.fromJson(node, Locator.root());
        // Then
        assertArrayEquals(new int[]{
                1
        }, gate.indices());
        Matrix m = gate.transform();
        assertSame(Matrix.identity(), m);
    }

    @ParameterizedTest
    @CsvSource({
            "0,1,2,3, 0,1,2,3",
            "1,0,2,3, 1,0,2,3",
            "0,2,1,3, 0,2,1,3",
            "3,2,1,0, 3,2,1,0",
            "1,2,3,0, 3,0,1,2",
    })
    void inversePermutation(int s0, int s1, int s2, int s3, int e0, int e1, int e2, int e3) {
        // Given
        int[] perm = new int[]{s0, s1, s2, s3};
        // When
        int[] inv = QuGate.inversePermutation(perm);
        // Then
        assertArrayEquals(new int[]{e0, e1, e2, e3}, inv);
    }

    @Test
    void sFromJson() throws IOException {
        // Given
        JsonNode node = Utils.fromText(S_YAML);
        // When
        QuGate gate = QuGate.fromJson(node, Locator.root());
        // Then
        assertArrayEquals(new int[]{
                1
        }, gate.indices());
        Matrix m = gate.transform();
        assertSame(Matrix.s(), m);
    }

    @Test
    void swapFromJson() throws IOException {
        // Given
        JsonNode node = Utils.fromText(SWAP_YAML);
        // When
        QuGate gate = QuGate.fromJson(node, Locator.root());
        // Then
        assertArrayEquals(new int[]{
                1, 2
        }, gate.indices());
        Matrix m = gate.transform();
        assertSame(Matrix.swap(), m);
    }

    @Test
    void tFromJson() throws IOException {
        // Given
        JsonNode node = Utils.fromText(T_YAML);
        // When
        QuGate gate = QuGate.fromJson(node, Locator.root());
        // Then
        assertArrayEquals(new int[]{
                1
        }, gate.indices());
        Matrix m = gate.transform();
        assertSame(Matrix.t(), m);
    }

    @ParameterizedTest(name = "[{index}] bitPerm=[{0} {1}] stateIn={2} stateout={3}")
    @CsvSource({
            // b[0]=a[0], b[1]=a[1], b[2]=a[2]
            // state in  = 000 001 010 011 100 101 110 111
            // State out = 000 001 010 011 100 101 110 111
            "0,1, 0,0",
            "0,1, 1,1",
            "0,1, 2,2",
            "0,1, 3,3",
            "0,1, 4,4",
            "0,1, 5,5",
            "0,1, 6,6",
            "0,1, 7,7",

            // b[0]=a[0], b[1]=a[2], b[2]=a[1];
            // state in  = 000 001 010 011 100 101 110 111
            // State out = 000 001 100 101 010 011 110 111
            "0,2, 0,0",
            "0,2, 1,1",
            "0,2, 2,4",
            "0,2, 3,5",
            "0,2, 4,2",
            "0,2, 5,3",
            "0,2, 6,6",
            "0,2, 7,7",

            // b[0]=a[1], b[1]=a[0], b[2]=a[2];
            // 000 001 010 011 100 101 110 111
            // 000 010 001 011 100 110 101 111
            "1,0, 0,0",
            "1,0, 1,2",
            "1,0, 2,1",
            "1,0, 3,3",
            "1,0, 4,4",
            "1,0, 5,6",
            "1,0, 6,5",
            "1,0, 7,7",

            // b[0]=a[1], b[1]=a[2], b[2]=a[0];
            // bp=(2 0 1)
            // state in  = 000 001 010 011 100 101 110 111
            // State out = 000 100 001 101 010 110 011 111
            // sp(0 4 1 5 2 6 3 7)
            "1,2, 0,0",
            "1,2, 1,4",
            "1,2, 2,1",
            "1,2, 3,5",
            "1,2, 4,2",
            "1,2, 5,6",
            "1,2, 6,3",
            "1,2, 7,7",

            // b[0]=a[2], b[1]=a[0], b[2]=a[1];
            // bp=(1 2 0)
            // state in  = 000 001 010 011 100 101 110 111
            // State out = 000 010 100 110 001 011 101 111
            // sp(0 2 4 6 1 3 5 7)
            "2,0, 0,0",
            "2,0, 1,2",
            "2,0, 2,4",
            "2,0, 3,6",
            "2,0, 4,1",
            "2,0, 5,3",
            "2,0, 6,5",
            "2,0, 7,7",

            // b[0]=a[2], b[1]=a[1], b[2]=a[0];
            // bp=(2 1 0)
            // state in  = 000 001 010 011 100 101 110 111
            // State out = 000 100 010 110 001 101 011 111
            // sp(0 4 2 6 1 5 3 7)
            "2,1, 0,0",
            "2,1, 1,4",
            "2,1, 2,2",
            "2,1, 3,6",
            "2,1, 4,1",
            "2,1, 5,5",
            "2,1, 6,3",
            "2,1, 7,7",
    })
    void testMap3bits(int b0, int b1, int s, int exp) {
        int[] bitPerm = QuGate.computeMap(3, b0, b1);
        int[] statePerm = QuGate.computeStatePermutation(bitPerm);
        Matrix m = Matrix.permute(statePerm);
        Ket ket = Ket.base(s, 3);
        Ket res = ket.mul(m);
        Ket expKet = Ket.base(exp, 3);

        assertEquals(exp, statePerm[s]);

        assertThat(res.at(0), complexClose(expKet.at(0), EPSILON));
        assertThat(res.at(1), complexClose(expKet.at(1), EPSILON));
        assertThat(res.at(2), complexClose(expKet.at(2), EPSILON));
        assertThat(res.at(3), complexClose(expKet.at(3), EPSILON));
        assertThat(res.at(4), complexClose(expKet.at(4), EPSILON));
        assertThat(res.at(5), complexClose(expKet.at(5), EPSILON));
        assertThat(res.at(6), complexClose(expKet.at(6), EPSILON));
        assertThat(res.at(7), complexClose(expKet.at(7), EPSILON));
    }

    @Test
    void xFromJson() throws IOException {
        // Given
        JsonNode node = Utils.fromText(X_YAML);
        // When
        QuGate gate = QuGate.fromJson(node, Locator.root());
        // Then
        assertArrayEquals(new int[]{
                1
        }, gate.indices());
        Matrix m = gate.transform();
        assertSame(Matrix.x(), m);
    }

    @Test
    void yFromJson() throws IOException {
        // Given
        JsonNode node = Utils.fromText(Y_YAML);
        // When
        QuGate gate = QuGate.fromJson(node, Locator.root());
        // Then
        assertArrayEquals(new int[]{
                1
        }, gate.indices());
        Matrix m = gate.transform();
        assertSame(Matrix.y(), m);
    }

    @Test
    void zFromJson() throws IOException {
        // Given
        JsonNode node = Utils.fromText(Z_YAML);
        // When
        QuGate gate = QuGate.fromJson(node, Locator.root());
        // Then
        assertArrayEquals(new int[]{
                1
        }, gate.indices());
        Matrix m = gate.transform();
        assertSame(Matrix.z(), m);
    }
}