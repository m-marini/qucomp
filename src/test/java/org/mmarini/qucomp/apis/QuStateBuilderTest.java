package org.mmarini.qucomp.apis;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mmarini.qucomp.Matchers.matrixCloseTo;
import static org.mmarini.qucomp.apis.QuGate.swap;
import static org.mmarini.qucomp.apis.QuGate.x;

class QuStateBuilderTest {

    public static final double EPSILON = 1e-6;

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
        Matrix ket0 = Matrix.ketBase(in);
        // When
        Matrix ket1 = m.mul(ket0);
        // Then
        Matrix expKet = Matrix.ketBase(exp).extendsRows(4);
        assertThat(ket1, matrixCloseTo(expKet, EPSILON));
    }

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
    void testBuild(int in, int exp) {
        // Given
        Matrix m = QuCircuitBuilder.build(swap(1, 2));
        Matrix ket0 = Matrix.ketBase(in);
        // When
        Matrix ket1 = m.mul(ket0);
        // Then
        Matrix expKet = Matrix.ketBase(exp).extendsRows(8);

        assertThat(ket1, matrixCloseTo(expKet, EPSILON));
    }

    @Test
    void numQuBits() {
        assertEquals(0, QuCircuitBuilder.numQuBits());
        assertEquals(6, QuCircuitBuilder.numQuBits(swap(5, 1), x(3)));
    }
}