package org.mmarini.qucomp.apis;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mmarini.Matchers.complexClose;

class VectorUtilsTest {

    public static final float EPSILON = 1e-6F;

    @Test
    void cross() {
        // Given
        Complex[] v0 = new Complex[]{
                Complex.one(), Complex.zero()
        };
        Complex[] v1 = new Complex[]{
                Complex.zero(), Complex.one()
        };
        // When
        Complex[] v00 = VectorUtils.cross(v0, v0);
        Complex[] v01 = VectorUtils.cross(v0, v1);
        Complex[] v10 = VectorUtils.cross(v1, v0);
        Complex[] v11 = VectorUtils.cross(v1, v1);
        // Then
        assertEquals(4, v00.length);
        assertThat(v00[0], complexClose(1, EPSILON));
        assertThat(v00[1], complexClose(0, EPSILON));
        assertThat(v00[2], complexClose(0, EPSILON));
        assertThat(v00[3], complexClose(0, EPSILON));
        assertEquals(4, v01.length);
        assertThat(v01[0], complexClose(0, EPSILON));
        assertThat(v01[1], complexClose(1, EPSILON));
        assertThat(v01[2], complexClose(0, EPSILON));
        assertThat(v01[3], complexClose(0, EPSILON));
        assertEquals(4, v10.length);
        assertThat(v10[0], complexClose(0, EPSILON));
        assertThat(v10[1], complexClose(0, EPSILON));
        assertThat(v10[2], complexClose(1, EPSILON));
        assertThat(v10[3], complexClose(0, EPSILON));
        assertEquals(4, v11.length);
        assertThat(v11[0], complexClose(0, EPSILON));
        assertThat(v11[1], complexClose(0, EPSILON));
        assertThat(v11[2], complexClose(0, EPSILON));
        assertThat(v11[3], complexClose(1, EPSILON));
    }

    @ParameterizedTest
    @CsvSource({
            "0, 0, 0",
            "0, 1, 1",
            "0, 2, 0",
            "0, 3, 1",
            "1, 0, 0",
            "1, 1, 0",
            "1, 2, 1",
            "1, 3, 1"
    })
    void matchesQubit(int index, int state, int expected) {
        assertEquals(expected, VectorUtils.qubitValue(state, index));
    }
}