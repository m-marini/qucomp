package org.mmarini.qucomp.apis;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.stream.IntStream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mmarini.qucomp.Matchers.complexClose;

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

    @Test
    void partMul() {
        // Given
        //     | 0 1 2 |
        // M = | 3 4 5 |
        //     | 6 7 8 |
        Complex[] m = IntStream.range(0, 9).mapToObj(Complex::create).toArray(Complex[]::new);
        // and empty destination
        Complex[] d = new Complex[9];
        // When
        //         | 0 1 2 |   | 0 1 2 |   |  3+12    4+14     5+16 |   | 15 18  21 |
        // M x M = | 3 4 5 | x | 3 4 5 | = | 12+30 3+16+35  6+20+40 | = | 42 54  66 |
        //         | 6 7 8 |   | 6 7 8 |   | 21+48 6+28+56 12+35+64 |   | 69 90 111 |
        VectorUtils.partMul(d, 0, 3, 3, m, 0, 3, m, 0, 3);
        // Then
        assertThat(d[0], complexClose(15, EPSILON));
        assertThat(d[1], complexClose(18, EPSILON));
        assertThat(d[2], complexClose(21, EPSILON));
        assertThat(d[3], complexClose(42, EPSILON));
        assertThat(d[4], complexClose(54, EPSILON));
        assertThat(d[5], complexClose(66, EPSILON));
        assertThat(d[6], complexClose(69, EPSILON));
        assertThat(d[7], complexClose(90, EPSILON));
        assertThat(d[8], complexClose(111, EPSILON));
    }

    @Test
    void partMul1() {
        // Given
        //     | 0 1 2 |
        // M = | 3 4 5 |
        //     | 6 7 8 |
        Complex[] m = IntStream.range(0, 9).mapToObj(Complex::create).toArray(Complex[]::new);
        // and empty destination
        Complex[] d = new Complex[9];
        // When
        //         | x x x |   | x 1 2 |   | x  x   x |
        // M x M = | 3 4 5 | x | x 4 5 | = | x 54  66 |
        //         | 6 7 8 |   | X 7 8 |   | x 90 111 |
        VectorUtils.partMul(d, 4, 2, 2, m, 3, 3, m, 1, 3);
        // Then
        assertNull(d[0]);
        assertNull(d[1]);
        assertNull(d[2]);
        assertNull(d[3]);
        assertThat(d[4], complexClose(54, EPSILON));
        assertThat(d[5], complexClose(66, EPSILON));
        assertNull(d[6]);
        assertThat(d[7], complexClose(90, EPSILON));
        assertThat(d[8], complexClose(111, EPSILON));
    }

    @Test
    void partMul2() {
        // Given
        //     | 0 1 2 |
        // M = | 3 4 5 |
        //     | 6 7 8 |
        Complex[] m = IntStream.range(0, 9).mapToObj(Complex::create).toArray(Complex[]::new);
        // and empty destination
        Complex[] d = new Complex[9];
        // When
        //         | x x x |   | x 1 x |   | x  x x |
        // M x M = | 3 4 5 | x | x 4 x | = | x 54 x |
        //         | x x x |   | X 7 x |   | x  x x |
        VectorUtils.partMul(d, 4, 1, 1, m, 3, 3, m, 1, 3);
        // Then
        assertNull(d[0]);
        assertNull(d[1]);
        assertNull(d[2]);
        assertNull(d[3]);
        assertThat(d[4], complexClose(54, EPSILON));
        assertNull(d[5]);
        assertNull(d[6]);
        assertNull(d[7]);
        assertNull(d[8]);
    }

    @Test
    void partMul3() {
        // Given
        //
        // M = | 0 1 2 |
        //     | 3 4 5 |
        Complex[] m = IntStream.range(0, 6).mapToObj(Complex::create).toArray(Complex[]::new);
        // and empty destination
        Complex[] d = new Complex[4];
        // When

        // M x T(M) = | 0 1 2 |   | 0 1 |   |  2+8    3+10 |   | 10 13 |
        //            | 3 4 5 | x | 2 3 | = | 8+20 3+12+25 | = | 28 40 |
        //                        | 4 5 |
        VectorUtils.partMul(d, 0, 2, 2, m, 0, 3, m, 0, 2);
        // Then
        assertThat(d[0], complexClose(10, EPSILON));
        assertThat(d[1], complexClose(13, EPSILON));
        assertThat(d[2], complexClose(28, EPSILON));
        assertThat(d[3], complexClose(40, EPSILON));
    }

    @Test
    void partMulBra() {
        // Given
        //
        // A = | 0 1 2 |
        Complex[] a = IntStream.range(0, 3).mapToObj(Complex::create).toArray(Complex[]::new);
        // And
        //     | 0 1 2 |
        // B = | 3 4 5 |
        //     | 6 7 8 |
        Complex[] b = IntStream.range(0, 9).mapToObj(Complex::create).toArray(Complex[]::new);
        // and empty destination
        Complex[] d = new Complex[3];
        // When
        //                     | 0 1 2 |
        // A x B = | 0 1 2 | x | 3 4 5 | = | 3+12 4+14 5+16 | = | 15 18 21 |
        //                     | 6 7 8 |
        VectorUtils.partMul(d, 0, 1, 3, a, 0, 3, b, 0, 3);
        // Then
        assertThat(d[0], complexClose(15, EPSILON));
        assertThat(d[1], complexClose(18, EPSILON));
        assertThat(d[2], complexClose(21, EPSILON));
    }

    @Test
    void partMulKet() {
        // Given
        //     | 0 1 2 |
        // A = | 3 4 5 |
        //     | 6 7 8 |
        Complex[] a = IntStream.range(0, 9).mapToObj(Complex::create).toArray(Complex[]::new);
        // And
        //     | 0 |
        // B = | 1 |
        //     | 2 |
        Complex[] b = IntStream.range(0, 3).mapToObj(Complex::create).toArray(Complex[]::new);
        // and empty destination
        Complex[] d = new Complex[3];
        // When
        //         | 0 1 2 |   | 0 |   |  1+4 |   |  5 |
        // M x M = | 3 4 5 | x | 1 | = | 4+10 | = | 14 |
        //         | 6 7 8 |   | 2 |   | 7+16 |   | 23 |
        VectorUtils.partMul(d, 0, 3, 1, a, 0, 3, b, 0, 1);
        // Then
        assertThat(d[0], complexClose(5, EPSILON));
        assertThat(d[1], complexClose(14, EPSILON));
        assertThat(d[2], complexClose(23, EPSILON));
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