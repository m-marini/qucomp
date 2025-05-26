package org.mmarini.qucomp.apis;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mmarini.qucomp.Matchers.complexClose;
import static org.mmarini.qucomp.apis.QuGate.swap;
import static org.mmarini.qucomp.apis.QuGate.x;

class QuStateBuilderTest {

    public static final float EPSILON = 1e-6F;

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
    void numQuBits() {
        assertEquals(0, QuCircuitBuilder.numQuBits());
        assertEquals(6, QuCircuitBuilder.numQuBits(swap(5, 1), x(3)));
    }
}