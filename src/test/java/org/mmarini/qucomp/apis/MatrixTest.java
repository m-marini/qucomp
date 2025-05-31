package org.mmarini.qucomp.apis;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
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

class MatrixTest {
    public static final float EPSILON = 1e-6F;
    public static final float HALF_SQRT2 = (float) (sqrt(2) / 2);
    public static final Matrix MX = Matrix.create(2, 2,
            0, 1,
            1, 0);
    public static final Matrix MZ = Matrix.create(2, 2,
            1, 0,
            0, -1);
    public static final Matrix MZ0 = Matrix.create(4, 4,
            1, 0, 0, 0,
            0, -1, 0, 0,
            0, 0, 1, 0,
            0, 0, 0, -1
    );
    public static final Matrix MZ1 = Matrix.create(4, 4,
            1, 0, 0, 0,
            0, 1, 0, 0,
            0, 0, -1, 0,
            0, 0, 0, -1
    );
    private static final Logger logger = LoggerFactory.getLogger(MatrixTest.class);
    public static Matrix MX0 = Matrix.create(4, 4,
            0, 1, 0, 0,
            1, 0, 0, 0,
            0, 0, 0, 1,
            0, 0, 1, 0);
    public static Matrix MX1 = Matrix.create(4, 4,
            0, 0, 1, 0,
            0, 0, 0, 1,
            1, 0, 0, 0,
            0, 1, 0, 0);

    public static Stream<Arguments> testAddArgs() {
        Matrix x22 = Matrix.create(2, 2,
                0, 1,
                1, 0);
        Matrix x24 = Matrix.create(2, 4,
                0, 1, 0, 1,
                1, 0, 1, 0);
        Matrix x42 = Matrix.create(4, 2,
                0, 1,
                1, 0,
                0, 1,
                1, 0);
        Matrix x44 = Matrix.create(4, 4,
                0, 1, 0, 1,
                1, 0, 1, 0,
                0, 1, 0, 1,
                1, 0, 1, 0);
        Matrix y22 = Matrix.create(2, 2,
                1, 0,
                0, 1);
        Matrix y24 = Matrix.create(2, 4,
                1, 0, 1, 0,
                0, 1, 0, 1);
        Matrix y42 = Matrix.create(4, 2,
                1, 0,
                0, 1,
                1, 0,
                0, 1);
        Matrix y44 = Matrix.create(4, 4,
                1, 0, 1, 0,
                0, 1, 0, 1,
                1, 0, 1, 0,
                0, 1, 0, 1);
        Matrix z22 = Matrix.create(2, 2,
                1, 1,
                1, 1);
        Matrix z2224 = Matrix.create(2, 4,
                1, 1, 1, 0,
                1, 1, 0, 1);
        Matrix z2242 = Matrix.create(4, 2,
                1, 1,
                1, 1,
                1, 0,
                0, 1);
        Matrix z2244 = Matrix.create(4, 4,
                1, 1, 1, 0,
                1, 1, 0, 1,
                1, 0, 1, 0,
                0, 1, 0, 1);
        Matrix z2422 = Matrix.create(2, 4,
                1, 1, 0, 1,
                1, 1, 1, 0);
        Matrix z2424 = Matrix.create(2, 4,
                1, 1, 1, 1,
                1, 1, 1, 1);
        Matrix z2442 = Matrix.create(4, 4,
                1, 1, 0, 1,
                1, 1, 1, 0,
                1, 0, 0, 0,
                0, 1, 0, 0);
        Matrix z2444 = Matrix.create(4, 4,
                1, 1, 1, 1,
                1, 1, 1, 1,
                1, 0, 1, 0,
                0, 1, 0, 1);
        Matrix z4222 = Matrix.create(4, 2,
                1, 1,
                1, 1,
                0, 1,
                1, 0);
        Matrix z4224 = Matrix.create(4, 4,
                1, 1, 1, 0,
                1, 1, 0, 1,
                0, 1, 0, 0,
                1, 0, 0, 0);
        Matrix z4242 = Matrix.create(4, 2,
                1, 1,
                1, 1,
                1, 1,
                1, 1);
        Matrix z4244 = Matrix.create(4, 4,
                1, 1, 1, 0,
                1, 1, 0, 1,
                1, 1, 1, 0,
                1, 1, 0, 1);
        Matrix z4422 = Matrix.create(4, 4,
                1, 1, 0, 1,
                1, 1, 1, 0,
                0, 1, 0, 1,
                1, 0, 1, 0);
        Matrix z4424 = Matrix.create(4, 4,
                1, 1, 1, 1,
                1, 1, 1, 1,
                0, 1, 0, 1,
                1, 0, 1, 0);
        Matrix z4442 = Matrix.create(4, 4,
                1, 1, 0, 1,
                1, 1, 1, 0,
                1, 1, 0, 1,
                1, 1, 1, 0);
        Matrix z4444 = Matrix.create(4, 4,
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
        Matrix m1 = Matrix.create(2, 2,
                1, 2,
                3, 4);
        Matrix m2 = Matrix.create(2, 2,
                5, 6,
                7, 8);
        Matrix m12 = Matrix.create(4, 4,
                5, 6, 10, 12,
                7, 8, 14, 16,
                15, 18, 20, 24,
                21, 24, 28, 32);
        Matrix m21 = Matrix.create(4, 4,
                5, 10, 6, 12,
                15, 20, 18, 24,
                7, 14, 8, 16,
                21, 28, 24, 32);
        return Stream.of(
                Arguments.of(m1, m2, m12),
                Arguments.of(m2, m1, m21),
                Arguments.of(MX, Matrix.identity(), MX1),
                Arguments.of(Matrix.identity(), MX, MX0),
                Arguments.of(MZ, Matrix.identity(), MZ1),
                Arguments.of(Matrix.identity(), MZ, MZ0)
        );
    }

    public static Stream<Arguments> testExtends0Args() {
        Matrix x = Matrix.create(2, 2,
                1, 1,
                1, 1);
        Matrix y24 = Matrix.create(2, 4,
                1, 1, 0, 0,
                1, 1, 0, 0);
        Matrix y42 = Matrix.create(4, 2,
                1, 1,
                1, 1,
                0, 0,
                0, 0);
        Matrix y44 = Matrix.create(4, 4,
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
        Matrix m2 = Matrix.create(2, 2,
                0, 1,
                1, 0);
        Matrix mx0 = Matrix.create(4, 4,
                0, 1, 0, 0,
                1, 0, 0, 0,
                0, 0, 0, 1,
                0, 0, 1, 0);
        Matrix mz0 = Matrix.create(4, 4,
                1, 0, 0, 0,
                0, -1, 0, 0,
                0, 0, 1, 0,
                0, 0, 0, -1);
        return Stream.of(
                Arguments.of(m2, 4, mx0),
                Arguments.of(m2.mul(Complex.i()), 4, mx0.mul(Complex.i())),
                Arguments.of(Matrix.z(), 4, mz0)
        );
    }

    public static Stream<Arguments> testHGateArgs() {
        return Stream.of(
                Arguments.of(0, Matrix.h()),
                Arguments.of(1, Matrix.create(4, 4,
                        HALF_SQRT2, HALF_SQRT2, 0, 0,
                        HALF_SQRT2, -HALF_SQRT2, 0, 0,
                        0, 0, HALF_SQRT2, HALF_SQRT2,
                        0, 0, HALF_SQRT2, -HALF_SQRT2))
        );
    }

    public static Stream<Arguments> testIGateArgs() {
        return Stream.of(
                Arguments.of(0, Matrix.identity(2)),
                Arguments.of(1, Matrix.identity(4)),
                Arguments.of(2, Matrix.identity(8))
        );
    }

    public static Stream<Arguments> testMulMatrixArgs() {
        Matrix m1 = Matrix.create(2, 3,
                IntStream.range(0, 6).mapToObj(Complex::create).toArray(Complex[]::new));
        Matrix m2 = Matrix.create(3, 2,
                IntStream.range(0, 6).mapToObj(Complex::create).toArray(Complex[]::new));
        /*
        | 0 1 2 |   | 0 1 |   | 10 13 |
        | 3 4 5 | x | 2 3 | = | 28 40 |
                    | 4 5 |
         */
        Matrix m12 = Matrix.create(2, 2,
                10, 13,
                28, 40);
                /*
        | 0 1 |   | 0 1 2 |   |  3  4  5 |
        | 2 3 | x | 3 4 5 | = |  9 14 19 |
        | 4 5 |               | 15 24 33 |
         */
        Matrix m21 = Matrix.create(3, 3,
                3, 4, 5,
                9, 14, 19,
                15, 24, 33);
        /*
        | 0 0 1 0 |  | 1  0 0 0  |   | 0  0 1  0 |
        | 0 0 0 1 |  | 0 -1 0 0  | = | 0  0 0 -1 |
        | 1 0 0 0 |  | 0  0 1 0  |   | 1  0 0  0 |
        | 0 1 0 0 |  | 0  0 0 -1 |   | 0 -1 0  0 |
         */
        Matrix mxz = Matrix.create(4, 4,
                0, 0, 1, 0,
                0, 0, 0, -1,
                1, 0, 0, 0,
                0, -1, 0, 0);
        Matrix mzx = Matrix.create(4, 4,
                0, 1, 0, 0,
                1, 0, 0, 0,
                0, 0, 0, -1,
                0, 0, -1, 0);

        return Stream.of(
                Arguments.of(m1, m2, m12),
                Arguments.of(m2, m1, m21),
                Arguments.of(Matrix.xGate(1), Matrix.zGate(0), mxz),
                Arguments.of(Matrix.zGate(1), Matrix.xGate(0), mzx),
                Arguments.of(Matrix.xGate(0), Matrix.zGate(1), mzx),
                Arguments.of(Matrix.zGate(0), Matrix.xGate(1), mxz)
        );
    }

    public static Stream<Arguments> testMulMatrixErrorsArgs() {
        Matrix m22 = Matrix.identity();
        Matrix m33 = Matrix.identity(3);
        Matrix m24 = Matrix.create(2, 4,
                0, 1, 0, 1,
                0, 1, 0, 1);
        Matrix m42 = Matrix.create(4, 2,
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

    public static Stream<Arguments> testSGateArgs() {
        return Stream.of(
                Arguments.of(0, Matrix.s()),
                Arguments.of(1, Matrix.create(4, 4,
                        Complex.one(), Complex.zero(), Complex.zero(), Complex.zero(),
                        Complex.zero(), Complex.one(), Complex.zero(), Complex.zero(),
                        Complex.zero(), Complex.zero(), Complex.i(), Complex.zero(),
                        Complex.zero(), Complex.zero(), Complex.zero(), Complex.i()
                ))
        );
    }

    public static Stream<Arguments> testSubArgs() {
        Matrix x22 = Matrix.create(2, 2,
                0, 1,
                1, 0);
        Matrix x24 = Matrix.create(2, 4,
                0, 1, 0, 1,
                1, 0, 1, 0);
        Matrix x42 = Matrix.create(4, 2,
                0, 1,
                1, 0,
                0, 1,
                1, 0);
        Matrix x44 = Matrix.create(4, 4,
                0, 1, 0, 1,
                1, 0, 1, 0,
                0, 1, 0, 1,
                1, 0, 1, 0);
        Matrix y22 = Matrix.create(2, 2,
                1, 0,
                0, 1);
        Matrix y24 = Matrix.create(2, 4,
                1, 0, 1, 0,
                0, 1, 0, 1);
        Matrix y42 = Matrix.create(4, 2,
                1, 0,
                0, 1,
                1, 0,
                0, 1);
        Matrix y44 = Matrix.create(4, 4,
                1, 0, 1, 0,
                0, 1, 0, 1,
                1, 0, 1, 0,
                0, 1, 0, 1);
        Matrix z22 = Matrix.create(2, 2,
                -1, 1,
                1, -1);
        Matrix z2224 = Matrix.create(2, 4,
                -1, 1, -1, 0,
                1, -1, 0, -1);
        Matrix z2242 = Matrix.create(4, 2,
                -1, 1,
                1, -1,
                -1, 0,
                0, -1);
        Matrix z2244 = Matrix.create(4, 4,
                -1, 1, -1, 0,
                1, -1, 0, -1,
                -1, 0, -1, 0,
                0, -1, 0, -1);
        Matrix z2422 = Matrix.create(2, 4,
                -1, 1, 0, 1,
                1, -1, 1, 0);
        Matrix z2424 = Matrix.create(2, 4,
                -1, 1, -1, 1,
                1, -1, 1, -1);
        Matrix z2442 = Matrix.create(4, 4,
                -1, 1, 0, 1,
                1, -1, 1, 0,
                -1, 0, 0, 0,
                0, -1, 0, 0);
        Matrix z2444 = Matrix.create(4, 4,
                -1, 1, -1, 1,
                1, -1, 1, -1,
                -1, 0, -1, 0,
                0, -1, 0, -1);
        Matrix z4222 = Matrix.create(4, 2,
                -1, 1,
                1, -1,
                0, 1,
                1, 0);
        Matrix z4224 = Matrix.create(4, 4,
                -1, 1, -1, 0,
                1, -1, 0, -1,
                0, 1, 0, 0,
                1, 0, 0, 0);
        Matrix z4242 = Matrix.create(4, 2,
                -1, 1,
                1, -1,
                -1, 1,
                1, -1);
        Matrix z4244 = Matrix.create(4, 4,
                -1, 1, -1, 0,
                1, -1, 0, -1,
                -1, 1, -1, 0,
                1, -1, 0, -1);
        Matrix z4422 = Matrix.create(4, 4,
                -1, 1, 0, 1,
                1, -1, 1, 0,
                0, 1, 0, 1,
                1, 0, 1, 0);
        Matrix z4424 = Matrix.create(4, 4,
                -1, 1, -1, 1,
                1, -1, 1, -1,
                0, 1, 0, 1,
                1, 0, 1, 0);
        Matrix z4442 = Matrix.create(4, 4,
                -1, 1, 0, 1,
                1, -1, 1, 0,
                -1, 1, 0, 1,
                1, -1, 1, 0);
        Matrix z4444 = Matrix.create(4, 4,
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

    public static Stream<Arguments> testTGateArgs() {
        return Stream.of(
                Arguments.of(0, Matrix.t()),
                Arguments.of(1, Matrix.create(4, 4,
                        Complex.one(), Complex.zero(), Complex.zero(), Complex.zero(),
                        Complex.zero(), Complex.one(), Complex.zero(), Complex.zero(),
                        Complex.zero(), Complex.zero(), new Complex(HALF_SQRT2, HALF_SQRT2), Complex.zero(),
                        Complex.zero(), Complex.zero(), Complex.zero(), new Complex(HALF_SQRT2, HALF_SQRT2)
                ))
        );
    }

    public static Stream<Arguments> testXGateArgs() {
        return Stream.of(
                Arguments.of(0, Matrix.x()),
                Arguments.of(1, MX1)
        );
    }

    public static Stream<Arguments> testYGateArgs() {
        return Stream.of(
                Arguments.of(0, Matrix.y()),
                Arguments.of(1, Matrix.create(4, 4,
                        Complex.zero(), Complex.zero(), Complex.i(-1), Complex.zero(),
                        Complex.zero(), Complex.zero(), Complex.zero(), Complex.i(-1),
                        Complex.i(), Complex.zero(), Complex.zero(), Complex.zero(),
                        Complex.zero(), Complex.i(), Complex.zero(), Complex.zero()
                ))
        );
    }

    public static Stream<Arguments> testZGateArgs() {
        return Stream.of(
                Arguments.of(0, Matrix.z()),
                Arguments.of(1, MZ1)
        );
    }

    @ParameterizedTest
    @CsvSource({
            "0, 1,0,0,0,0,0,0,0",
            "1, 0,1,0,0,0,0,0,0",
            "2, 0,0,1,0,0,0,0,0",
            "3, 0,0,0,1,0,0,0,0",
            "4, 0,0,0,0,1,0,0,0",
            "5, 0,0,0,0,0,1,0,0",
            "6, 0,0,0,0,0,0,0,1",
            "7, 0,0,0,0,0,0,1,0"
    })
    void ccnot(int in, float exp0, float exp1, float exp2, float exp3, float exp4, float exp5, float exp6, float exp7) {
        // Given
        Matrix ccnot = Matrix.ccnot();
        // When
        Ket result = Ket.base(in, 3).mul(ccnot);
        // Then
        assertThat(result.values()[0], complexClose(exp0, EPSILON));
        assertThat(result.values()[1], complexClose(exp1, EPSILON));
        assertThat(result.values()[2], complexClose(exp2, EPSILON));
        assertThat(result.values()[3], complexClose(exp3, EPSILON));
        assertThat(result.values()[4], complexClose(exp4, EPSILON));
        assertThat(result.values()[5], complexClose(exp5, EPSILON));
        assertThat(result.values()[6], complexClose(exp6, EPSILON));
        assertThat(result.values()[7], complexClose(exp7, EPSILON));
    }

    @ParameterizedTest
    @CsvSource({
            "0, 1,0,0,0",
            "1, 0,1,0,0",
            "2, 0,0,0,1",
            "3, 0,0,1,0"
    })
    void cnot(int in, float exp0, float exp1, float exp2, float exp3) {
        // Given
        Matrix cnotOp = Matrix.cnot();
        // When
        Ket cnot = Ket.base(in, 2).mul(cnotOp);
        // Then
        assertThat(cnot.values()[0], complexClose(exp0, EPSILON));
        assertThat(cnot.values()[1], complexClose(exp1, EPSILON));
        assertThat(cnot.values()[2], complexClose(exp2, EPSILON));
        assertThat(cnot.values()[3], complexClose(exp3, EPSILON));
    }

    @Test
    void conj() {
        Matrix m = Matrix.create(2, 2,
                IntStream.range(0, 4).mapToObj(Complex::i).toArray(Complex[]::new));
        Matrix c = m.conj();
        assertThat(c.at(0, 0), complexClose(Complex.zero(), EPSILON));
        assertThat(c.at(0, 1), complexClose(Complex.i(-1), EPSILON));
        assertThat(c.at(1, 0), complexClose(Complex.i(-2), EPSILON));
        assertThat(c.at(1, 1), complexClose(Complex.i(-3), EPSILON));
    }

    @Test
    void createComplex() {
        Matrix m = Matrix.create(2, 2,
                Complex.one(), Complex.zero(),
                Complex.zero(), Complex.one());
        assertEquals(2, m.numRows());
        assertEquals(2, m.numCols());
    }

    @Test
    void createReal() {
        Matrix m = Matrix.create(2, 2,
                1, 0,
                0, 1);
        assertEquals(2, m.numRows());
        assertEquals(2, m.numCols());
    }

    @Test
    void index() {
        Matrix m = Matrix.create(2, 2,
                Complex.one(), Complex.zero(),
                Complex.zero(), Complex.one());
        assertEquals(0, m.index(0, 0));
        assertEquals(1, m.index(0, 1));
        assertEquals(2, m.index(1, 0));
        assertEquals(3, m.index(1, 1));
    }

    @Test
    void indexStream() {
        List<int[]> indices = Matrix.indexStream(2, 3).toList();
        assertEquals(6, indices.size());
        assertArrayEquals(new int[]{0, 0}, indices.get(0));
        assertArrayEquals(new int[]{0, 1}, indices.get(1));
        assertArrayEquals(new int[]{0, 2}, indices.get(2));
        assertArrayEquals(new int[]{1, 0}, indices.get(3));
        assertArrayEquals(new int[]{1, 1}, indices.get(4));
        assertArrayEquals(new int[]{1, 2}, indices.get(5));
    }

    @Test
    void mul() {
        Matrix m0 = Matrix.identity(3);
        Matrix m1 = m0.mul(Complex.i(2));

        assertThat(m1.at(0, 0), complexClose(Complex.i(2), EPSILON));
        assertThat(m1.at(0, 1), complexClose(0, EPSILON));
        assertThat(m1.at(0, 2), complexClose(0, EPSILON));
        assertThat(m1.at(1, 0), complexClose(0, EPSILON));
        assertThat(m1.at(1, 1), complexClose(Complex.i(2), EPSILON));
        assertThat(m1.at(1, 2), complexClose(0, EPSILON));
        assertThat(m1.at(2, 0), complexClose(0, EPSILON));
        assertThat(m1.at(2, 1), complexClose(0, EPSILON));
        assertThat(m1.at(2, 2), complexClose(Complex.i(2), EPSILON));
    }

    @Test
    void neg() {
        Matrix m0 = Matrix.identity(3);
        Matrix m1 = m0.neg();
        assertTrue(m1.hasShape(3, 3));
        assertThat(m1.at(0, 0), complexClose(-1, EPSILON));
        assertThat(m1.at(0, 1), complexClose(0, EPSILON));
        assertThat(m1.at(0, 2), complexClose(0, EPSILON));
        assertThat(m1.at(1, 0), complexClose(0, EPSILON));
        assertThat(m1.at(1, 1), complexClose(-1, EPSILON));
        assertThat(m1.at(1, 2), complexClose(0, EPSILON));
        assertThat(m1.at(2, 0), complexClose(0, EPSILON));
        assertThat(m1.at(2, 1), complexClose(0, EPSILON));
        assertThat(m1.at(2, 2), complexClose(-1, EPSILON));
    }

    @ParameterizedTest
    @CsvSource({
            // p=(0 1 2 3)
            // ia=(0 1 2 3) => ib=(0 1 2 3)
            // a=(1 0 0 0) => b=(1 0 0 0) 0->0
            // a=(0 1 0 0) => b=(0 1 0 0) 1->1
            // a=(0 0 1 0) => b=(0 0 1 0) 2->2
            // a=(0 0 0 1) => b=(0 0 0 1) 3->3
            "0,1,2,3, 0,0",
            "0,1,2,3, 1,1",
            "0,1,2,3, 2,2",
            "0,1,2,3, 3,3",

            // p=(0 2 1 3)
            // ia=(0 2 1 3) => ib=(0 1 2 3)
            // a=(1 0 0 0) => b=(1 0 0 0) b[p[0]]=b[0]=1
            // a=(0 1 0 0) => b=(0 0 1 0) b[p[1]]=b[2]=1
            // a=(0 0 1 0) => b=(0 1 0 0) b[p[2]]=b[1]=1
            // a=(0 0 0 1) => b=(0 0 0 1) b[p[3]]=b[3]=1
            "0,2,1,3, 0,0",
            "0,2,1,3, 1,2",
            "0,2,1,3, 2,1",
            "0,2,1,3, 3,3",

            // p=(1 2 3 0)
            // ia=(1 2 3 0) => ib=(0 1 2 3)
            // a=(1 0 0 0) => b=(0 1 0 0) b[p[0]]=b[1]=1
            // a=(0 1 0 0) => b=(0 0 1 0) b[p[1]]=b[2]=1
            // a=(0 0 1 0) => b=(0 0 0 1) b[p[2]]=b[3]=1
            // a=(0 0 0 1) => b=(1 0 0 0) b[p[3]]=b[0]=1
            "1,2,3,0, 0,1",
            "1,2,3,0, 1,2",
            "1,2,3,0, 2,3",
            "1,2,3,0, 3,0",
    })
    void permute2(int s0, int s1, int s2, int s3, int v0, int exp0) {
        // Given
        Matrix m = Matrix.permute(s0, s1, s2, s3);
        Ket ket = Ket.base(v0, 2);
        Ket exp = Ket.base(exp0, 2);
        // When
        Ket res = ket.mul(m);
        // Then

        assertThat(m.at(s0, 0), complexClose(1, EPSILON));
        assertThat(m.at(s1, 1), complexClose(1, EPSILON));
        assertThat(m.at(s2, 2), complexClose(1, EPSILON));
        assertThat(m.at(s3, 3), complexClose(1, EPSILON));

        assertThat(res.at(0), complexClose(exp.at(0), EPSILON));
        assertThat(res.at(1), complexClose(exp.at(1), EPSILON));
        assertThat(res.at(2), complexClose(exp.at(2), EPSILON));
        assertThat(res.at(3), complexClose(exp.at(3), EPSILON));
    }

    @ParameterizedTest
    @CsvSource({
            "0,1,2,3,4,5,6,7, 0,0",
            "0,1,2,3,4,5,6,7, 1,1",
            "0,1,2,3,4,5,6,7, 2,2",
            "0,1,2,3,4,5,6,7, 3,3",
            "0,1,2,3,4,5,6,7, 4,4",
            "0,1,2,3,4,5,6,7, 5,5",
            "0,1,2,3,4,5,6,7, 6,6",
            "0,1,2,3,4,5,6,7, 7,7",

            "0,2,1,3,4,6,5,7, 0,0",
            "0,2,1,3,4,6,5,7, 1,2",
            "0,2,1,3,4,6,5,7, 2,1",
            "0,2,1,3,4,6,5,7, 3,3",
            "0,2,1,3,4,6,5,7, 4,4",
            "0,2,1,3,4,6,5,7, 5,6",
            "0,2,1,3,4,6,5,7, 6,5",
            "0,2,1,3,4,6,5,7, 7,7",
    })
    void permute3(int s0, int s1, int s2, int s3, int s4, int s5, int s6, int s7, int v0, int exp0) {
        // Given
        Matrix m = Matrix.permute(s0, s1, s2, s3, s4, s5, s6, s7);
        Ket ket = Ket.base(v0, 3);
        Ket exp = Ket.base(exp0, 3);
        // When
        Ket res = ket.mul(m);
        assertThat(res.at(0), complexClose(exp.at(0), EPSILON));
        assertThat(res.at(1), complexClose(exp.at(1), EPSILON));
        assertThat(res.at(2), complexClose(exp.at(2), EPSILON));
        assertThat(res.at(3), complexClose(exp.at(3), EPSILON));
    }

    @Test
    void permutei() {
        Matrix m = Matrix.permute(0, 1, 2, 3); //Identity
        assertTrue(m.hasShape(4, 4));
        assertThat(m.at(0, 0), complexClose(1, EPSILON));
        assertThat(m.at(0, 1), complexClose(0, EPSILON));
        assertThat(m.at(0, 2), complexClose(0, EPSILON));
        assertThat(m.at(0, 3), complexClose(0, EPSILON));
        assertThat(m.at(1, 0), complexClose(0, EPSILON));
        assertThat(m.at(1, 1), complexClose(1, EPSILON));
        assertThat(m.at(1, 2), complexClose(0, EPSILON));
        assertThat(m.at(1, 3), complexClose(0, EPSILON));
        assertThat(m.at(2, 0), complexClose(0, EPSILON));
        assertThat(m.at(2, 1), complexClose(0, EPSILON));
        assertThat(m.at(2, 2), complexClose(1, EPSILON));
        assertThat(m.at(2, 3), complexClose(0, EPSILON));
        assertThat(m.at(3, 0), complexClose(0, EPSILON));
        assertThat(m.at(3, 1), complexClose(0, EPSILON));
        assertThat(m.at(3, 2), complexClose(0, EPSILON));
        assertThat(m.at(3, 3), complexClose(1, EPSILON));
    }

    @Test
    void permutei1() {
        Matrix m = Matrix.permute(3, 2, 1, 0); //

        assertThat(m.at(0, 0), complexClose(0, EPSILON));
        assertThat(m.at(0, 1), complexClose(0, EPSILON));
        assertThat(m.at(0, 2), complexClose(0, EPSILON));
        assertThat(m.at(0, 3), complexClose(1, EPSILON));
        assertThat(m.at(1, 0), complexClose(0, EPSILON));
        assertThat(m.at(1, 1), complexClose(0, EPSILON));
        assertThat(m.at(1, 2), complexClose(1, EPSILON));
        assertThat(m.at(1, 3), complexClose(0, EPSILON));
        assertThat(m.at(2, 0), complexClose(0, EPSILON));
        assertThat(m.at(2, 1), complexClose(1, EPSILON));
        assertThat(m.at(2, 2), complexClose(0, EPSILON));
        assertThat(m.at(2, 3), complexClose(0, EPSILON));
        assertThat(m.at(3, 0), complexClose(1, EPSILON));
        assertThat(m.at(3, 1), complexClose(0, EPSILON));
        assertThat(m.at(3, 2), complexClose(0, EPSILON));
        assertThat(m.at(3, 3), complexClose(0, EPSILON));
    }

    @ParameterizedTest
    @CsvSource({
            // 000 001 010 011 100 101 110 111
            // 000 010 001 011 100 110 101 111
            "0,0",
            "1,2",
            "2,1",
            "3,3",
            "4,4",
            "5,6",
            "6,5",
            "7,7",
    })
    void portMerge(int s, int exp) {
        // Given
        Matrix swap = Matrix.swap(); // 2 bit
        Matrix id = Matrix.identity(); // 1 bit
//        Matrix merge = swap.cross(id);
        Matrix merge = id.cross(swap);
        Ket ket = Ket.base(s, 3);
        Ket expKet = Ket.base(exp, 3);
        // When
        Ket outKet = ket.mul(merge);
        // Then
        assertThat(outKet.at(0), complexClose(expKet.at(0), EPSILON));
        assertThat(outKet.at(1), complexClose(expKet.at(1), EPSILON));
        assertThat(outKet.at(2), complexClose(expKet.at(2), EPSILON));
        assertThat(outKet.at(3), complexClose(expKet.at(3), EPSILON));
        assertThat(outKet.at(4), complexClose(expKet.at(4), EPSILON));
        assertThat(outKet.at(5), complexClose(expKet.at(5), EPSILON));
        assertThat(outKet.at(6), complexClose(expKet.at(6), EPSILON));
        assertThat(outKet.at(7), complexClose(expKet.at(7), EPSILON));
    }

    @Test
    void sub() {
        Matrix a = Matrix.create(2, 3, IntStream.range(0, 6)
                .mapToObj(Complex::create)
                .toArray(Complex[]::new));
        Matrix b = Matrix.create(2, 3, IntStream.range(0, 6)
                .mapToObj(i -> Complex.create(i * 2))
                .toArray(Complex[]::new));
        Matrix aa = a.sub(b);
        assertThat(aa.at(0, 0), complexClose(0, EPSILON));
        assertThat(aa.at(0, 1), complexClose(-1, EPSILON));
        assertThat(aa.at(0, 2), complexClose(-2, EPSILON));
        assertThat(aa.at(1, 0), complexClose(-3, EPSILON));
        assertThat(aa.at(1, 1), complexClose(-4, EPSILON));
        assertThat(aa.at(1, 2), complexClose(-5, EPSILON));
    }

    @ParameterizedTest
    @CsvSource({
            "0,0",
            "1,2",
            "2,1",
            "3,3",
    })
    void swap(int instate, int expState) {
        // Given
        Matrix swap = Matrix.swap();
        // When
        Ket result = Ket.base(instate, 2).mul(swap);
        // Then
        Ket exp = Ket.base(expState, 2);
        assertThat(result.values()[0], complexClose(exp.values()[0], EPSILON));
        assertThat(result.values()[1], complexClose(exp.values()[1], EPSILON));
        assertThat(result.values()[2], complexClose(exp.values()[2], EPSILON));
        assertThat(result.values()[3], complexClose(exp.values()[3], EPSILON));
    }

    @Test
    void t() {
        Matrix m = Matrix.t();
        assertThat(m.at(0, 0), complexClose(1, EPSILON));
        assertThat(m.at(0, 1), complexClose(0, EPSILON));
        assertThat(m.at(1, 0), complexClose(0, EPSILON));
        assertThat(m.at(1, 1), complexClose(new Complex(HALF_SQRT2, HALF_SQRT2), EPSILON));
    }

    @ParameterizedTest
    @MethodSource("testAddArgs")
    void testAdd(Matrix left, Matrix right, Matrix exp) {
        Matrix result = left.add(right);
        assertThat(result, matrixCloseTo(exp, EPSILON));
    }

    @Test
    void testAt() {
        Matrix m = Matrix.create(2, 2,
                Complex.zero(), Complex.one(),
                Complex.create(2), Complex.create(3));

        assertThat(m.at(0, 0), complexClose(0, EPSILON));
        assertThat(m.at(0, 1), complexClose(1, EPSILON));
        assertThat(m.at(1, 0), complexClose(2, EPSILON));
        assertThat(m.at(1, 1), complexClose(3, EPSILON));
    }

    @ParameterizedTest
    @MethodSource("testCrossArgs")
    void testCross(Matrix left, Matrix right, Matrix exp) {
        Matrix result = left.cross(right);
        assertThat(result, matrixCloseTo(exp, EPSILON));
    }

    @ParameterizedTest
    @CsvSource({
            // 000 001 010 011 100 101 110 111
            // 000 001 100 101 010 011 110 111
            "0, 0",
            "1, 1",
            "2, 4",
            "3, 5",
            "4, 2",
            "5, 3",
            "6, 6",
            "7, 7",
    })
    void testCross1(int s, int exp) {
        Matrix m0 = Matrix.swap();
        Matrix m1 = Matrix.identity();
        Matrix m = m0.cross(m1);
        Ket ket0 = Ket.base(s, 3);
        Ket expKet = Ket.base(exp, 3);

        Ket ket1 = ket0.mul(m);

        assertThat(ket1.at(0), complexClose(expKet.at(0), EPSILON));
        assertThat(ket1.at(1), complexClose(expKet.at(1), EPSILON));
        assertThat(ket1.at(2), complexClose(expKet.at(2), EPSILON));
        assertThat(ket1.at(3), complexClose(expKet.at(3), EPSILON));
        assertThat(ket1.at(4), complexClose(expKet.at(4), EPSILON));
        assertThat(ket1.at(5), complexClose(expKet.at(5), EPSILON));
        assertThat(ket1.at(6), complexClose(expKet.at(6), EPSILON));
        assertThat(ket1.at(7), complexClose(expKet.at(7), EPSILON));
    }

    @ParameterizedTest
    @CsvSource({
            // 000 001 010 011 100 101 110 111
            // 000 010 001 011 100 110 101 111
            "0, 0",
            "1, 2",
            "2, 1",
            "3, 3",
            "4, 4",
            "5, 6",
            "6, 5",
            "7, 7",
    })
    void testCross2(int s, int exp) {
        Matrix m0 = Matrix.identity();
        Matrix m1 = Matrix.swap();
        Matrix m = m0.cross(m1);
        Ket ket0 = Ket.base(s, 3);
        Ket expKet = Ket.base(exp, 3);

        Ket ket1 = ket0.mul(m);

        assertThat(ket1.at(0), complexClose(expKet.at(0), EPSILON));
        assertThat(ket1.at(1), complexClose(expKet.at(1), EPSILON));
        assertThat(ket1.at(2), complexClose(expKet.at(2), EPSILON));
        assertThat(ket1.at(3), complexClose(expKet.at(3), EPSILON));
        assertThat(ket1.at(4), complexClose(expKet.at(4), EPSILON));
        assertThat(ket1.at(5), complexClose(expKet.at(5), EPSILON));
        assertThat(ket1.at(6), complexClose(expKet.at(6), EPSILON));
        assertThat(ket1.at(7), complexClose(expKet.at(7), EPSILON));
    }

    @Test
    void testExtendRows() {
        Matrix m = Matrix.identity();
        Matrix exp = Matrix.create(4, 2,
                1, 0,
                0, 1,
                0, 0,
                0, 0);
        Matrix result = m.extendsRows(4);
        assertThat(result, matrixCloseTo(exp, EPSILON));
    }

    @ParameterizedTest
    @MethodSource("testExtends0Args")
    void testExtends0(Matrix matrix, int n, int m, Matrix exp) {
        Matrix result = matrix.extends0(n, m);
        assertThat(result, matrixCloseTo(exp, EPSILON));
    }

    @Test
    void testExtendsCols() {
        Matrix m = Matrix.identity();
        Matrix exp = Matrix.create(2, 4,
                1, 0, 0, 0,
                0, 1, 0, 0);
        Matrix result = m.extendsCols(4);
        assertThat(result, matrixCloseTo(exp, EPSILON));
    }

    @ParameterizedTest
    @MethodSource("testExtendsCrossSquareArgs")
    void testExtendsCrossSquare(Matrix matrix, int n, Matrix exp) {
        Matrix result = matrix.extendsCrossSquare(n);
        assertThat(result, matrixCloseTo(exp, EPSILON));
    }

    @Test
    void testH() {
        assertThat(Matrix.h(), matrixCloseTo(Matrix.create(2, 2,
                HALF_SQRT2, HALF_SQRT2,
                HALF_SQRT2, -HALF_SQRT2), EPSILON));
    }

    @ParameterizedTest
    @MethodSource("testHGateArgs")
    void testHGate(int index, Matrix exp) {
        assertThat(Matrix.hGate(index), matrixCloseTo(exp, EPSILON));
    }

    @ParameterizedTest
    @MethodSource("testIGateArgs")
    void testIGate(int index, Matrix exp) {
        assertThat(Matrix.iGate(index), matrixCloseTo(exp, EPSILON));
    }

    @Test
    void testIdentity() {
        assertThat(Matrix.identity(4), matrixCloseTo(Matrix.create(4, 4,
                1, 0, 0, 0,
                0, 1, 0, 0,
                0, 0, 1, 0,
                0, 0, 0, 1), EPSILON));
    }

    @Test
    void testMulConc() {
        // Given
        int n0 = 64;
        int n1 = 64;
        int n2 = 64;
        int noTests = 10;
        Matrix m1 = Matrix.create(n0, n1,
                IntStream.range(0, n0 * n1).mapToObj(Complex::create).toArray(Complex[]::new));
        Matrix m2 = Matrix.create(n1, n2,
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
        Matrix m0 = Matrix.identity(3);
        Matrix m1 = m0.mul(2);

        assertThat(m1.at(0, 0), complexClose(2, EPSILON));
        assertThat(m1.at(0, 1), complexClose(0, EPSILON));
        assertThat(m1.at(0, 2), complexClose(0, EPSILON));
        assertThat(m1.at(1, 0), complexClose(0, EPSILON));
        assertThat(m1.at(1, 1), complexClose(2, EPSILON));
        assertThat(m1.at(1, 2), complexClose(0, EPSILON));
        assertThat(m1.at(2, 0), complexClose(0, EPSILON));
        assertThat(m1.at(2, 1), complexClose(0, EPSILON));
        assertThat(m1.at(2, 2), complexClose(2, EPSILON));
    }

    @ParameterizedTest
    @MethodSource("testMulMatrixArgs")
    void testMulMatrix(Matrix left, Matrix right, Matrix exp) {
        Matrix result = left.mul(right);
        assertThat(result, matrixCloseTo(exp, EPSILON));
    }

    @ParameterizedTest
    @MethodSource("testMulMatrixErrorsArgs")
    void testMulMatrixErrors(Matrix left, Matrix right, String exp) {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> left.mul(right));
        assertEquals(exp, ex.getMessage());
    }

    @ParameterizedTest
    @MethodSource("testSGateArgs")
    void testSGate(int index, Matrix exp) {
        assertThat(Matrix.sGate(index), matrixCloseTo(exp, EPSILON));
    }

    @ParameterizedTest
    @MethodSource("testSubArgs")
    void testSub(Matrix left, Matrix right, Matrix exp) {
        Matrix result = left.sub(right);
        assertThat(result, matrixCloseTo(exp, EPSILON));
    }

    @ParameterizedTest
    @MethodSource("testTGateArgs")
    void testTGate(int index, Matrix exp) {
        assertThat(Matrix.tGate(index), matrixCloseTo(exp, EPSILON));
    }

    @Test
    void testToString() {
        // Given
        Matrix m = Matrix.y();
        // When
        String s = m.toString();
        // Then
        assertEquals("[ 0.0,  -i\n    i, 0.0 ]\n", s);
    }

    @ParameterizedTest
    @MethodSource("testXGateArgs")
    void testXGate(int index, Matrix exp) {
        assertThat(Matrix.xGate(index), matrixCloseTo(exp, EPSILON));
    }

    @Test
    void testY() {
        assertThat(Matrix.y(), matrixCloseTo(Matrix.create(2, 2,
                Complex.zero(), Complex.i().neg(),
                Complex.i(), Complex.zero()), EPSILON));
    }

    @ParameterizedTest
    @MethodSource("testYGateArgs")
    void testYGate(int index, Matrix exp) {
        assertThat(Matrix.yGate(index), matrixCloseTo(exp, EPSILON));
    }

    @ParameterizedTest
    @MethodSource("testZGateArgs")
    void testZGate(int index, Matrix exp) {
        assertThat(Matrix.zGate(index), matrixCloseTo(exp, EPSILON));
    }

    @Test
    void transpose() {
        Matrix m = Matrix.create(3, 4,
                IntStream.range(0, 12)
                        .mapToObj(Complex::create)
                        .toArray(Complex[]::new));
        Matrix t = m.transpose();

        assertThat(t.at(0, 0), complexClose(m.at(0, 0), EPSILON));
        assertThat(t.at(0, 1), complexClose(m.at(1, 0), EPSILON));
        assertThat(t.at(0, 2), complexClose(m.at(2, 0), EPSILON));
        assertThat(t.at(1, 0), complexClose(m.at(0, 1), EPSILON));
        assertThat(t.at(1, 1), complexClose(m.at(1, 1), EPSILON));
        assertThat(t.at(1, 2), complexClose(m.at(2, 1), EPSILON));
        assertThat(t.at(2, 0), complexClose(m.at(0, 2), EPSILON));
        assertThat(t.at(2, 1), complexClose(m.at(1, 2), EPSILON));
        assertThat(t.at(2, 2), complexClose(m.at(2, 2), EPSILON));
        assertThat(t.at(3, 0), complexClose(m.at(0, 3), EPSILON));
        assertThat(t.at(3, 1), complexClose(m.at(1, 3), EPSILON));
        assertThat(t.at(3, 2), complexClose(m.at(2, 3), EPSILON));
    }

    @Test
    void unsafeIndex() {
        // Shape 2x3
        assertEquals(0, Matrix.unsafeIndex(3, 0, 0));
        assertEquals(1, Matrix.unsafeIndex(3, 0, 1));
        assertEquals(2, Matrix.unsafeIndex(3, 0, 2));
        assertEquals(3, Matrix.unsafeIndex(3, 1, 0));
        assertEquals(4, Matrix.unsafeIndex(3, 1, 1));
        assertEquals(5, Matrix.unsafeIndex(3, 1, 2));
    }

    @Test
    void x() {
        Matrix m = Matrix.x();

        assertThat(m.at(0, 0), complexClose(0, EPSILON));
        assertThat(m.at(0, 1), complexClose(1, EPSILON));
        assertThat(m.at(1, 0), complexClose(1, EPSILON));
        assertThat(m.at(1, 1), complexClose(0, EPSILON));
    }
}