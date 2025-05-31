package org.mmarini.qucomp.apis;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.lang.Math.sqrt;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mmarini.qucomp.Matchers.complexClose;
import static org.mmarini.qucomp.Matchers.matrixCloseTo;

class Matrix1Test {
    public static final float EPSILON = 1e-6F;
    public static final Matrix1 MX = Matrix1.create(2, 2,
            0, 1,
            1, 0);
    public static final Matrix1 MX0 = Matrix1.create(4, 4,
            0, 1, 0, 0,
            1, 0, 0, 0,
            0, 0, 0, 1,
            0, 0, 1, 0);
    public static final Matrix1 MX1 = Matrix1.create(4, 4,
            0, 0, 1, 0,
            0, 0, 0, 1,
            1, 0, 0, 0,
            0, 1, 0, 0);
    public static final Matrix1 MZ = Matrix1.create(2, 2,
            1, 0,
            0, -1);
    public static final Matrix1 MZ0 = Matrix1.create(4, 4,
            1, 0, 0, 0,
            0, -1, 0, 0,
            0, 0, 1, 0,
            0, 0, 0, -1
    );
    public static final Matrix1 MZ1 = Matrix1.create(4, 4,
            1, 0, 0, 0,
            0, 1, 0, 0,
            0, 0, -1, 0,
            0, 0, 0, -1
    );
    private static final Logger logger = LoggerFactory.getLogger(MatrixTest.class);
    private static final float HALF_SQRT2 = (float) (sqrt(2) / 2);

    public static Stream<Arguments> testAddArgs() {
        Matrix1 x22 = Matrix1.create(2, 2,
                0, 1,
                1, 0);
        Matrix1 x24 = Matrix1.create(2, 4,
                0, 1, 0, 1,
                1, 0, 1, 0);
        Matrix1 x42 = Matrix1.create(4, 2,
                0, 1,
                1, 0,
                0, 1,
                1, 0);
        Matrix1 x44 = Matrix1.create(4, 4,
                0, 1, 0, 1,
                1, 0, 1, 0,
                0, 1, 0, 1,
                1, 0, 1, 0);
        Matrix1 y22 = Matrix1.create(2, 2,
                1, 0,
                0, 1);
        Matrix1 y24 = Matrix1.create(2, 4,
                1, 0, 1, 0,
                0, 1, 0, 1);
        Matrix1 y42 = Matrix1.create(4, 2,
                1, 0,
                0, 1,
                1, 0,
                0, 1);
        Matrix1 y44 = Matrix1.create(4, 4,
                1, 0, 1, 0,
                0, 1, 0, 1,
                1, 0, 1, 0,
                0, 1, 0, 1);
        Matrix1 z22 = Matrix1.create(2, 2,
                1, 1,
                1, 1);
        Matrix1 z2224 = Matrix1.create(2, 4,
                1, 1, 1, 0,
                1, 1, 0, 1);
        Matrix1 z2242 = Matrix1.create(4, 2,
                1, 1,
                1, 1,
                1, 0,
                0, 1);
        Matrix1 z2244 = Matrix1.create(4, 4,
                1, 1, 1, 0,
                1, 1, 0, 1,
                1, 0, 1, 0,
                0, 1, 0, 1);
        Matrix1 z2422 = Matrix1.create(2, 4,
                1, 1, 0, 1,
                1, 1, 1, 0);
        Matrix1 z2424 = Matrix1.create(2, 4,
                1, 1, 1, 1,
                1, 1, 1, 1);
        Matrix1 z2442 = Matrix1.create(4, 4,
                1, 1, 0, 1,
                1, 1, 1, 0,
                1, 0, 0, 0,
                0, 1, 0, 0);
        Matrix1 z2444 = Matrix1.create(4, 4,
                1, 1, 1, 1,
                1, 1, 1, 1,
                1, 0, 1, 0,
                0, 1, 0, 1);
        Matrix1 z4222 = Matrix1.create(4, 2,
                1, 1,
                1, 1,
                0, 1,
                1, 0);
        Matrix1 z4224 = Matrix1.create(4, 4,
                1, 1, 1, 0,
                1, 1, 0, 1,
                0, 1, 0, 0,
                1, 0, 0, 0);
        Matrix1 z4242 = Matrix1.create(4, 2,
                1, 1,
                1, 1,
                1, 1,
                1, 1);
        Matrix1 z4244 = Matrix1.create(4, 4,
                1, 1, 1, 0,
                1, 1, 0, 1,
                1, 1, 1, 0,
                1, 1, 0, 1);
        Matrix1 z4422 = Matrix1.create(4, 4,
                1, 1, 0, 1,
                1, 1, 1, 0,
                0, 1, 0, 1,
                1, 0, 1, 0);
        Matrix1 z4424 = Matrix1.create(4, 4,
                1, 1, 1, 1,
                1, 1, 1, 1,
                0, 1, 0, 1,
                1, 0, 1, 0);
        Matrix1 z4442 = Matrix1.create(4, 4,
                1, 1, 0, 1,
                1, 1, 1, 0,
                1, 1, 0, 1,
                1, 1, 1, 0);
        Matrix1 z4444 = Matrix1.create(4, 4,
                1, 1, 1, 1,
                1, 1, 1, 1,
                1, 1, 1, 1,
                1, 1, 1, 1);

        return Stream.of(
                Arguments.of(x22, y22, z22),
                Arguments.of(x22, y24, z2224),
                Arguments.of(x22, y42, z2242),
                Arguments.of(x22, y44, z2244),

                Arguments.of(x24, y22, z2422),
                Arguments.of(x24, y24, z2424),
                Arguments.of(x24, y42, z2442),
                Arguments.of(x24, y44, z2444),

                Arguments.of(x42, y22, z4222),
                Arguments.of(x42, y24, z4224),
                Arguments.of(x42, y42, z4242),
                Arguments.of(x42, y44, z4244),

                Arguments.of(x44, y22, z4422),
                Arguments.of(x44, y24, z4424),
                Arguments.of(x44, y42, z4442),
                Arguments.of(x44, y44, z4444)
        );
    }

    public static Stream<Arguments> testCrossArgs() {
        Matrix1 m1 = Matrix1.create(2, 2,
                1, 2,
                3, 4);
        Matrix1 m2 = Matrix1.create(2, 2,
                5, 6,
                7, 8);
        Matrix1 m12 = Matrix1.create(4, 4,
                5, 6, 10, 12,
                7, 8, 14, 16,
                15, 18, 20, 24,
                21, 24, 28, 32);
        Matrix1 m21 = Matrix1.create(4, 4,
                5, 10, 6, 12,
                15, 20, 18, 24,
                7, 14, 8, 16,
                21, 28, 24, 32);
        return Stream.of(
                Arguments.of(m1, m2, m12),
                Arguments.of(m2, m1, m21),
                Arguments.of(MX, Matrix1.identity(2), MX1),
                Arguments.of(Matrix1.identity(2), MX, MX0),
                Arguments.of(MZ, Matrix1.identity(2), MZ1),
                Arguments.of(Matrix1.identity(2), MZ, MZ0)
        );
    }

    public static Stream<Arguments> testExtends0Args() {
        Matrix1 x = Matrix1.create(2, 2,
                1, 1,
                1, 1);
        Matrix1 y24 = Matrix1.create(2, 4,
                1, 1, 0, 0,
                1, 1, 0, 0);
        Matrix1 y42 = Matrix1.create(4, 2,
                1, 1,
                1, 1,
                0, 0,
                0, 0);
        Matrix1 y44 = Matrix1.create(4, 4,
                1, 1, 0, 0,
                1, 1, 0, 0,
                0, 0, 0, 0,
                0, 0, 0, 0);
        return Stream.of(
                Arguments.of(x, 2, 2, x),
                Arguments.of(x, 2, 4, y24),
                Arguments.of(x, 4, 2, y42),
                Arguments.of(x, 4, 4, y44)
        );
    }

    public static Stream<Arguments> testExtendsCrossSquareArgs() {
        Matrix1 m2 = Matrix1.create(2, 2,
                0, 1,
                1, 0);
        Matrix1 mx0 = Matrix1.create(4, 4,
                0, 1, 0, 0,
                1, 0, 0, 0,
                0, 0, 0, 1,
                0, 0, 1, 0);
        Matrix1 mz0 = Matrix1.create(4, 4,
                1, 0, 0, 0,
                0, -1, 0, 0,
                0, 0, 1, 0,
                0, 0, 0, -1);
        return Stream.of(
                Arguments.of(m2, 4, mx0),
                Arguments.of(m2.mul(Complex.i()), 4, mx0.mul(Complex.i())),
                Arguments.of(MZ, 4, mz0)
        );
    }

    public static Stream<Arguments> testKetBaseArgs() {
        return Stream.of(
                Arguments.of(0, Matrix1.create(2, 1, 1, 0)),
                Arguments.of(1, Matrix1.create(2, 1, 0, 1)),
                Arguments.of(2, Matrix1.create(4, 1, 0, 0, 1, 0)),
                Arguments.of(3, Matrix1.create(4, 1, 0, 0, 0, 1)),
                Arguments.of(4, Matrix1.create(8, 1, 0, 0, 0, 0, 1, 0, 0, 0)),
                Arguments.of(5, Matrix1.create(8, 1, 0, 0, 0, 0, 0, 1, 0, 0)),
                Arguments.of(6, Matrix1.create(8, 1, 0, 0, 0, 0, 0, 0, 1, 0)),
                Arguments.of(7, Matrix1.create(8, 1, 0, 0, 0, 0, 0, 0, 0, 1))
        );
    }

    public static Stream<Arguments> testMulMatrixArgs() {
        Matrix1 m1 = Matrix1.create(2, 3,
                IntStream.range(0, 6).mapToObj(Complex::create).toArray(Complex[]::new));
        Matrix1 m2 = Matrix1.create(3, 2,
                IntStream.range(0, 6).mapToObj(Complex::create).toArray(Complex[]::new));
        /*
        | 0 1 2 |   | 0 1 |   | 10 13 |
        | 3 4 5 | x | 2 3 | = | 28 40 |
                    | 4 5 |
         */
        Matrix1 m12 = Matrix1.create(2, 2,
                10, 13,
                28, 40);
                /*
        | 0 1 |   | 0 1 2 |   |  3  4  5 |
        | 2 3 | x | 3 4 5 | = |  9 14 19 |
        | 4 5 |               | 15 24 33 |
         */
        Matrix1 m21 = Matrix1.create(3, 3,
                3, 4, 5,
                9, 14, 19,
                15, 24, 33);

        return Stream.of(
                Arguments.of(m1, m2, m12),
                Arguments.of(m2, m1, m21)
        );
    }

    public static Stream<Arguments> testMulMatrixErrorsArgs() {
        Matrix1 m22 = Matrix1.identity(2);
        Matrix1 m33 = Matrix1.identity(3);
        Matrix1 m24 = Matrix1.create(2, 4,
                0, 1, 0, 1,
                0, 1, 0, 1);
        Matrix1 m42 = Matrix1.create(4, 2,
                0, 1,
                0, 1,
                0, 1,
                0, 1);
        return Stream.of(
                Arguments.of(m22, m33, "Expected size multiple of 2x2 (3x3)"),
                Arguments.of(m33, m24, "Expected square matrix (2x4)"),
                Arguments.of(m42, m33, "Expected square matrix (4x2)")
        );
    }

    public static Stream<Arguments> testSubArgs() {
        Matrix1 x22 = Matrix1.create(2, 2,
                0, 1,
                1, 0);
        Matrix1 x24 = Matrix1.create(2, 4,
                0, 1, 0, 1,
                1, 0, 1, 0);
        Matrix1 x42 = Matrix1.create(4, 2,
                0, 1,
                1, 0,
                0, 1,
                1, 0);
        Matrix1 x44 = Matrix1.create(4, 4,
                0, 1, 0, 1,
                1, 0, 1, 0,
                0, 1, 0, 1,
                1, 0, 1, 0);
        Matrix1 y22 = Matrix1.create(2, 2,
                1, 0,
                0, 1);
        Matrix1 y24 = Matrix1.create(2, 4,
                1, 0, 1, 0,
                0, 1, 0, 1);
        Matrix1 y42 = Matrix1.create(4, 2,
                1, 0,
                0, 1,
                1, 0,
                0, 1);
        Matrix1 y44 = Matrix1.create(4, 4,
                1, 0, 1, 0,
                0, 1, 0, 1,
                1, 0, 1, 0,
                0, 1, 0, 1);
        Matrix1 z22 = Matrix1.create(2, 2,
                -1, 1,
                1, -1);
        Matrix1 z2224 = Matrix1.create(2, 4,
                -1, 1, -1, 0,
                1, -1, 0, -1);
        Matrix1 z2242 = Matrix1.create(4, 2,
                -1, 1,
                1, -1,
                -1, 0,
                0, -1);
        Matrix1 z2244 = Matrix1.create(4, 4,
                -1, 1, -1, 0,
                1, -1, 0, -1,
                -1, 0, -1, 0,
                0, -1, 0, -1);
        Matrix1 z2422 = Matrix1.create(2, 4,
                -1, 1, 0, 1,
                1, -1, 1, 0);
        Matrix1 z2424 = Matrix1.create(2, 4,
                -1, 1, -1, 1,
                1, -1, 1, -1);
        Matrix1 z2442 = Matrix1.create(4, 4,
                -1, 1, 0, 1,
                1, -1, 1, 0,
                -1, 0, 0, 0,
                0, -1, 0, 0);
        Matrix1 z2444 = Matrix1.create(4, 4,
                -1, 1, -1, 1,
                1, -1, 1, -1,
                -1, 0, -1, 0,
                0, -1, 0, -1);
        Matrix1 z4222 = Matrix1.create(4, 2,
                -1, 1,
                1, -1,
                0, 1,
                1, 0);
        Matrix1 z4224 = Matrix1.create(4, 4,
                -1, 1, -1, 0,
                1, -1, 0, -1,
                0, 1, 0, 0,
                1, 0, 0, 0);
        Matrix1 z4242 = Matrix1.create(4, 2,
                -1, 1,
                1, -1,
                -1, 1,
                1, -1);
        Matrix1 z4244 = Matrix1.create(4, 4,
                -1, 1, -1, 0,
                1, -1, 0, -1,
                -1, 1, -1, 0,
                1, -1, 0, -1);
        Matrix1 z4422 = Matrix1.create(4, 4,
                -1, 1, 0, 1,
                1, -1, 1, 0,
                0, 1, 0, 1,
                1, 0, 1, 0);
        Matrix1 z4424 = Matrix1.create(4, 4,
                -1, 1, -1, 1,
                1, -1, 1, -1,
                0, 1, 0, 1,
                1, 0, 1, 0);
        Matrix1 z4442 = Matrix1.create(4, 4,
                -1, 1, 0, 1,
                1, -1, 1, 0,
                -1, 1, 0, 1,
                1, -1, 1, 0);
        Matrix1 z4444 = Matrix1.create(4, 4,
                -1, 1, -1, 1,
                1, -1, 1, -1,
                -1, 1, -1, 1,
                1, -1, 1, -1);

        return Stream.of(
                Arguments.of(x22, y22, z22),
                Arguments.of(x22, y24, z2224),
                Arguments.of(x22, y42, z2242),
                Arguments.of(x22, y44, z2244),

                Arguments.of(x24, y22, z2422),
                Arguments.of(x24, y24, z2424),
                Arguments.of(x24, y42, z2442),
                Arguments.of(x24, y44, z2444),

                Arguments.of(x42, y22, z4222),
                Arguments.of(x42, y24, z4224),
                Arguments.of(x42, y42, z4242),
                Arguments.of(x42, y44, z4244),

                Arguments.of(x44, y22, z4422),
                Arguments.of(x44, y24, z4424),
                Arguments.of(x44, y42, z4442),
                Arguments.of(x44, y44, z4444)
        );
    }

    @ParameterizedTest
    @MethodSource("testAddArgs")
    void testAdd(Matrix1 left, Matrix1 right, Matrix1 exp) {
        Matrix1 result = left.add(right);
        assertThat(result, matrixCloseTo(exp, EPSILON));
    }

    @Test
    void testAt() {
        Matrix1 m = Matrix1.create(2, 2,
                Complex.zero(), Complex.one(),
                Complex.create(2), Complex.create(3));

        assertThat(m.at(0, 0), complexClose(0, EPSILON));
        assertThat(m.at(0, 1), complexClose(1, EPSILON));
        assertThat(m.at(1, 0), complexClose(2, EPSILON));
        assertThat(m.at(1, 1), complexClose(3, EPSILON));
    }

    @Test
    void testConj() {
        Matrix1 m = Matrix1.create(2, 2,
                IntStream.range(0, 4).mapToObj(Complex::i).toArray(Complex[]::new));
        Matrix1 c = m.conj();
        assertThat(c, matrixCloseTo(Matrix1.create(2, 2,
                Complex.zero().conj(), Complex.i().conj(),
                Complex.i(2).conj(), Complex.i(3).conj()), EPSILON));
    }

    @Test
    void testCreateComplex() {
        Matrix1 m = Matrix1.create(2, 2,
                Complex.one(), Complex.zero(),
                Complex.zero(), Complex.one());
        assertEquals(2, m.numRows());
        assertEquals(2, m.numCols());
    }

    @Test
    void testCreateReal() {
        Matrix1 m = Matrix1.create(2, 2,
                1, 0,
                0, 1);
        assertEquals(2, m.numRows());
        assertEquals(2, m.numCols());
    }

    @ParameterizedTest
    @MethodSource("testCrossArgs")
    void testCross(Matrix1 left, Matrix1 right, Matrix1 exp) {
        Matrix1 result = left.cross(right);
        assertThat(result, matrixCloseTo(exp, EPSILON));
    }

    @Test
    void testDagger() {
        Matrix1 m = Matrix1.create(2, 2,
                IntStream.range(0, 4).mapToObj(Complex::i).toArray(Complex[]::new));
        Matrix1 c = m.dagger();
        assertThat(c, matrixCloseTo(Matrix1.create(2, 2,
                Complex.zero().conj(), Complex.i(2).conj(),
                Complex.i().conj(), Complex.i(3).conj()), EPSILON));
    }

    @Test
    void testDivComplex() {
        Matrix1 m0 = Matrix1.identity(4);
        Matrix1 m1 = m0.div(Complex.i(2));

        assertThat(m1, matrixCloseTo(Matrix1.create(4, 4,
                Complex.i(-0.5f), Complex.zero(), Complex.zero(), Complex.zero(),
                Complex.zero(), Complex.i(-0.5f), Complex.zero(), Complex.zero(),
                Complex.zero(), Complex.zero(), Complex.i(-0.5f), Complex.zero(),
                Complex.zero(), Complex.zero(), Complex.zero(), Complex.i(-0.5f)
        ), EPSILON));
    }

    @Test
    void testDivFloat() {
        Matrix1 m0 = Matrix1.identity(4);
        Matrix1 m1 = m0.div(2);

        assertThat(m1, matrixCloseTo(Matrix1.create(4, 4,
                0.5f, 0, 0, 0,
                0, 0.5f, 0, 0,
                0, 0, 0.5f, 0,
                0, 0, 0, 0.5f
        ), EPSILON));
    }

    @Test
    void testExtendRows() {
        Matrix1 m = Matrix1.identity(2);
        Matrix1 exp = Matrix1.create(4, 2,
                1, 0,
                0, 1,
                0, 0,
                0, 0);
        Matrix1 result = m.extendsRows(4);
        assertThat(result, matrixCloseTo(exp, EPSILON));
    }

    @ParameterizedTest
    @MethodSource("testExtends0Args")
    void testExtends0(Matrix1 matrix, int n, int m, Matrix1 exp) {
        Matrix1 result = matrix.extends0(n, m);
        assertThat(result, matrixCloseTo(exp, EPSILON));
    }

    @Test
    void testExtendsCols() {
        Matrix1 m = Matrix1.identity(2);
        Matrix1 exp = Matrix1.create(2, 4,
                1, 0, 0, 0,
                0, 1, 0, 0);
        Matrix1 result = m.extendsCols(4);
        assertThat(result, matrixCloseTo(exp, EPSILON));
    }

    @ParameterizedTest
    @MethodSource("testExtendsCrossSquareArgs")
    void testExtendsCrossSquare(Matrix1 matrix, int n, Matrix1 exp) {
        Matrix1 result = matrix.extendsCrossSquare(n);
        assertThat(result, matrixCloseTo(exp, EPSILON));
    }

    @Test
    void testI() {
        assertThat(Matrix1.i(), matrixCloseTo(Matrix1.create(2, 1,
                Complex.create(HALF_SQRT2),
                Complex.i().mul(HALF_SQRT2)), EPSILON
        ));
    }

    @Test
    void testIndex() {
        Matrix1 m = Matrix1.create(2, 2,
                Complex.one(), Complex.zero(),
                Complex.zero(), Complex.one());
        assertEquals(0, m.index(0, 0));
        assertEquals(1, m.index(0, 1));
        assertEquals(2, m.index(1, 0));
        assertEquals(3, m.index(1, 1));
    }

    @Test
    void testIndexStream() {
        List<int[]> indices = Matrix1.indexStream(2, 3).toList();
        assertEquals(6, indices.size());
        assertArrayEquals(new int[]{0, 0}, indices.get(0));
        assertArrayEquals(new int[]{0, 1}, indices.get(1));
        assertArrayEquals(new int[]{0, 2}, indices.get(2));
        assertArrayEquals(new int[]{1, 0}, indices.get(3));
        assertArrayEquals(new int[]{1, 1}, indices.get(4));
        assertArrayEquals(new int[]{1, 2}, indices.get(5));
    }

    @ParameterizedTest
    @MethodSource("testKetBaseArgs")
    void testKetBase(int state, Matrix1 exp) {
        Matrix1 ket = Matrix1.ketBase(state);
        assertThat(ket, matrixCloseTo(exp, EPSILON));
    }

    @Test
    void testKetComplex() {
        assertThat(Matrix1.ket(Complex.zero(), Complex.i()), matrixCloseTo(Matrix1.create(2, 1, Complex.zero(), Complex.i()), EPSILON));
    }

    @Test
    void testKetFloat() {
        assertThat(Matrix1.ket(0, 1), matrixCloseTo(Matrix1.create(2, 1, Complex.zero(), Complex.one()), EPSILON));
    }

    @Test
    void testMinus() {
        assertThat(Matrix1.minus(), matrixCloseTo(Matrix1.create(2, 1,
                HALF_SQRT2, -HALF_SQRT2), EPSILON));
    }

    @Test
    void testMinus_i() {
        assertThat(Matrix1.minus_i(), matrixCloseTo(Matrix1.create(2, 1,
                Complex.create(HALF_SQRT2),
                Complex.i().mul(-HALF_SQRT2)), EPSILON
        ));
    }

    @Test
    void testMulComplex() {
        Matrix1 m0 = Matrix1.identity(4);
        Matrix1 m1 = m0.mul(Complex.i(2));

        assertThat(m1, matrixCloseTo(Matrix1.create(4, 4,
                Complex.i(2), Complex.zero(), Complex.zero(), Complex.zero(),
                Complex.zero(), Complex.i(2), Complex.zero(), Complex.zero(),
                Complex.zero(), Complex.zero(), Complex.i(2), Complex.zero(),
                Complex.zero(), Complex.zero(), Complex.zero(), Complex.i(2)
        ), EPSILON));
    }

    @Test
    void testMulConc() {
        // Given
        int n0 = 64;
        int n1 = 64;
        int n2 = 64;
        int noTests = 10;
        Matrix1 m1 = Matrix1.create(n0, n1,
                IntStream.range(0, n0 * n1).mapToObj(Complex::create).toArray(Complex[]::new));
        Matrix1 m2 = Matrix1.create(n1, n2,
                IntStream.range(0, n1 * n2).mapToObj(Complex::create).toArray(Complex[]::new));
        // When
        long t0 = System.nanoTime();
        for (int i = 0; i < noTests; i++) {
            m1.mulConc(m2);
        }
        long t1 = System.nanoTime();
        logger.atInfo().log("Concurrent multiplication in {} us", (t1 - t0) / 1000L / noTests);
        t0 = System.nanoTime();
        for (int i = 0; i < noTests; i++) {
            m1.mulSeq(m2);
        }
        t1 = System.nanoTime();
        logger.atInfo().log("Sequence multiplication in {} us", (t1 - t0) / 1000L / noTests);
    }

    @Test
    void testMulFloat() {
        Matrix1 m0 = Matrix1.identity(4);
        Matrix1 m1 = m0.mul(2);
        assertThat(m1, matrixCloseTo(Matrix1.create(4, 4,
                2, 0, 0, 0,
                0, 2, 0, 0,
                0, 0, 2, 0,
                0, 0, 0, 2
        ), EPSILON));
    }

    @ParameterizedTest
    @MethodSource("testMulMatrixArgs")
    void testMulMatrix(Matrix1 left, Matrix1 right, Matrix1 exp) {
        Matrix1 result = left.mul(right);
        assertThat(result, matrixCloseTo(exp, EPSILON));
    }

    @ParameterizedTest
    @MethodSource("testMulMatrixErrorsArgs")
    void testMulMatrixErrors(Matrix1 left, Matrix1 right, String exp) {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> left.mul(right));
        assertEquals(exp, ex.getMessage());
    }

    @Test
    void testNeg() {
        Matrix1 m0 = Matrix1.identity(4);
        Matrix1 m1 = m0.neg();
        assertThat(m1, matrixCloseTo(Matrix1.create(4, 4,
                -1, 0, 0, 0,
                0, -1, 0, 0,
                0, 0, -1, 0,
                0, 0, 0, -1), EPSILON));
    }

    @Test
    void testPlus() {
        assertThat(Matrix1.plus(), matrixCloseTo(Matrix1.create(2, 1,
                HALF_SQRT2, HALF_SQRT2), EPSILON));
    }

    @ParameterizedTest
    @MethodSource("testSubArgs")
    void testSub(Matrix1 left, Matrix1 right, Matrix1 exp) {
        Matrix1 result = left.sub(right);
        assertThat(result, matrixCloseTo(exp, EPSILON));
    }

    @Test
    void testToString() {
        // Given
        Matrix1 m = Matrix1.create(2, 2,
                Complex.zero(), Complex.i().neg(),
                Complex.i(), Complex.zero());
        // When
        String s = m.toString();
        // Then
        assertEquals("[ 0.0,  -i\n    i, 0.0 ]\n", s);
    }

    @Test
    void testUnsafeIndex() {
        // Shape 2x3
        assertEquals(0, Matrix1.unsafeIndex(3, 0, 0));
        assertEquals(1, Matrix1.unsafeIndex(3, 0, 1));
        assertEquals(2, Matrix1.unsafeIndex(3, 0, 2));
        assertEquals(3, Matrix1.unsafeIndex(3, 1, 0));
        assertEquals(4, Matrix1.unsafeIndex(3, 1, 1));
        assertEquals(5, Matrix1.unsafeIndex(3, 1, 2));
    }

}