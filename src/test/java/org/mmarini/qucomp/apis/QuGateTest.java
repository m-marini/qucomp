package org.mmarini.qucomp.apis;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mmarini.qucomp.Matchers.complexClose;
import static org.mmarini.qucomp.Matchers.matrixCloseTo;

class QuGateTest {

    public static final double EPSILON = 1e-6F;
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
        Matrix m = gate.build();
        Matrix ket0 = Matrix.ketBase(in);
        Matrix ket = m.mul(ket0);
        // Then
        Matrix expKet = Matrix.ketBase(exp).extendsRows(4);
        assertThat(ket, matrixCloseTo(expKet, EPSILON));
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
        Matrix m = gate.build();
        Matrix ket0 = Matrix.ketBase(s);
        Matrix ket = m.mul(ket0);
        // Then
        Matrix expKet = Matrix.ketBase(exp).extendsRows(4);
        assertThat(ket, matrixCloseTo(expKet, EPSILON));
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
        Matrix m = gate.build();
        Matrix ket0 = Matrix.ketBase(in);
        Matrix ket = m.mul(ket0);
        // Then
        logger.atDebug().log("build3Bit m=\n{}", m);
        Matrix expKet = Matrix.ketBase(exp).extendsRows(8);
        assertThat(ket.extendsRows(8), matrixCloseTo(expKet, EPSILON));
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
        Matrix m = gate.build();
        Matrix ket0 = Matrix.ketBase(in);
        Matrix ket = m.mul(ket0);
        // Then
        Matrix expKet = Matrix.ketBase(exp).extendsRows(4);
        assertThat(ket, matrixCloseTo(expKet, EPSILON));
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

    @ParameterizedTest
    @CsvSource({
            "0, 1, 2, 3, 4, 5, 6, 7",
            "0, 0, 0, 0, 1, 1, 1, 1",
            "0, 0, 1, 1, 2, 2, 3, 3",
    })
    void stateMap(int m0, int m1, int m2, int m3, int m4, int m5, int m6, int m7) {
        // Given
        int[] bits = new int[]{0, 1, 2};
        int[] states = {m0, m1, m2, m3, m4, m5, m6, m7};
        QuGate gate = QuGate.stateMap(bits, states);
        Matrix m = gate.build();

        for (int i = 0; i < 8; i++) {
            // When
            Matrix k = m.mul(Matrix.ketBase(i));
            // Then
            assertThat("on state " + i, k.at(0, 0), complexClose(states[i] == 0 ? 1 : 0, EPSILON));
            assertThat("on state " + i, k.at(1, 0), complexClose(states[i] == 1 ? 1 : 0, EPSILON));
            assertThat("on state " + i, k.at(2, 0), complexClose(states[i] == 2 ? 1 : 0, EPSILON));
            assertThat("on state " + i, k.at(3, 0), complexClose(states[i] == 3 ? 1 : 0, EPSILON));
            assertThat("on state " + i, k.at(4, 0), complexClose(states[i] == 4 ? 1 : 0, EPSILON));
            assertThat("on state " + i, k.at(5, 0), complexClose(states[i] == 5 ? 1 : 0, EPSILON));
            assertThat("on state " + i, k.at(6, 0), complexClose(states[i] == 6 ? 1 : 0, EPSILON));
            assertThat("on state " + i, k.at(7, 0), complexClose(states[i] == 7 ? 1 : 0, EPSILON));
        }
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
        int[] statePerm = Matrix.computeStatePermutation(bitPerm);
        Matrix m = Matrix.permute(statePerm);
        Matrix ket = Matrix.ketBase(s);
        Matrix res = m.mul(ket);
        Matrix expKet = Matrix.ketBase(exp).extendsRows(8);

        assertEquals(exp, statePerm[s]);
        assertThat(res, matrixCloseTo(expKet, EPSILON));
    }
}