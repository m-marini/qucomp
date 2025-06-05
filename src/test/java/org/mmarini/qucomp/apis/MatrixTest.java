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

public class MatrixTest {
    public static final double EPSILON = 1e-6F;
    public static final Matrix MX = Matrix.create(2, 2,
            0, 1,
            1, 0);
    public static final Matrix MX0 = Matrix.create(4, 4,
            0, 1, 0, 0,
            1, 0, 0, 0,
            0, 0, 0, 1,
            0, 0, 1, 0);
    public static final Matrix MX1 = Matrix.create(4, 4,
            0, 0, 1, 0,
            0, 0, 0, 1,
            1, 0, 0, 0,
            0, 1, 0, 0);
    public static final Matrix MZ = Matrix.create(2, 2,
            1, 0,
            0, -1);
    public static final Matrix MZ0 = Matrix.create(4, 4,
            1, 0, 0, 0,
            0, -1, 0, 0,
            0, 0, 1, 0,
            0, 0, 0, -1
    );
    public static final Matrix SWAP02 = Matrix.create(8, 8,
            1, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 1, 0, 0, 0,
            0, 0, 1, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 1, 0,
            0, 1, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 1, 0, 0,
            0, 0, 0, 1, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 1
    );
    public static final Matrix SWAP12 = Matrix.create(8, 8,
            1, 0, 0, 0, 0, 0, 0, 0,
            0, 1, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 1, 0, 0, 0,
            0, 0, 0, 0, 0, 1, 0, 0,
            0, 0, 1, 0, 0, 0, 0, 0,
            0, 0, 0, 1, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 1, 0,
            0, 0, 0, 0, 0, 0, 0, 1
    );
    public static final Matrix MZ1 = Matrix.create(4, 4,
            1, 0, 0, 0,
            0, 1, 0, 0,
            0, 0, -1, 0,
            0, 0, 0, -1
    );
    public static final Matrix QUBIT0_01 = Matrix.create(2, 2,
            1, 0,
            0, 0);
    public static final Matrix QUBIT0_02 = Matrix.create(4, 4,
            1, 0, 0, 0,
            0, 0, 0, 0,
            0, 0, 1, 0,
            0, 0, 0, 0);
    public static final Matrix QUBIT0_12 = Matrix.create(4, 4,
            1, 0, 0, 0,
            0, 1, 0, 0,
            0, 0, 0, 0,
            0, 0, 0, 0
    );
    public static final Matrix QUBIT0_03 = Matrix.create(8, 8,
            1, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 1, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 1, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 1, 0,
            0, 0, 0, 0, 0, 0, 0, 0
    );
    public static final Matrix QUBIT0_13 = Matrix.create(8, 8,
            1, 0, 0, 0, 0, 0, 0, 0,
            0, 1, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 1, 0, 0, 0,
            0, 0, 0, 0, 0, 1, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0
    );
    public static final Matrix QUBIT0_23 = Matrix.create(8, 8,
            1, 0, 0, 0, 0, 0, 0, 0,
            0, 1, 0, 0, 0, 0, 0, 0,
            0, 0, 1, 0, 0, 0, 0, 0,
            0, 0, 0, 1, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0
    );
    public static final Matrix QUBIT1_01 = Matrix.create(2, 2,
            0, 0,
            0, 1);
    public static final Matrix QUBIT1_02 = Matrix.create(4, 4,
            0, 0, 0, 0,
            0, 1, 0, 0,
            0, 0, 0, 0,
            0, 0, 0, 1);
    public static final Matrix QUBIT1_12 = Matrix.create(4, 4,
            0, 0, 0, 0,
            0, 0, 0, 0,
            0, 0, 1, 0,
            0, 0, 0, 1
    );
    public static final Matrix QUBIT1_03 = Matrix.create(8, 8,
            0, 0, 0, 0, 0, 0, 0, 0,
            0, 1, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 1, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 1, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 1
    );
    public static final Matrix QUBIT1_13 = Matrix.create(8, 8,
            0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 1, 0, 0, 0, 0, 0,
            0, 0, 0, 1, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 1, 0,
            0, 0, 0, 0, 0, 0, 0, 1
    );
    public static final Matrix QUBIT1_23 = Matrix.create(8, 8,
            0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 1, 0, 0, 0,
            0, 0, 0, 0, 0, 1, 0, 0,
            0, 0, 0, 0, 0, 0, 1, 0,
            0, 0, 0, 0, 0, 0, 0, 1
    );
    static final Matrix S0 = Matrix.create(2, 2,
            Complex.one(), Complex.zero(),
            Complex.zero(), Complex.i()
    );
    static final Matrix S1 = Matrix.create(4, 4,
            Complex.one(), Complex.zero(), Complex.zero(), Complex.zero(),
            Complex.zero(), Complex.one(), Complex.zero(), Complex.zero(),
            Complex.zero(), Complex.zero(), Complex.i(), Complex.zero(),
            Complex.zero(), Complex.zero(), Complex.zero(), Complex.i()
    );
    static final double HALF_SQRT2 = sqrt(2) / 2;
    static final Matrix T0 = Matrix.create(2, 2,
            Complex.one(), Complex.zero(),
            Complex.zero(), new Complex(HALF_SQRT2, HALF_SQRT2)
    );
    static final Matrix T1 = Matrix.create(4, 4,
            Complex.one(), Complex.zero(), Complex.zero(), Complex.zero(),
            Complex.zero(), Complex.one(), Complex.zero(), Complex.zero(),
            Complex.zero(), Complex.zero(), new Complex(HALF_SQRT2, HALF_SQRT2), Complex.zero(),
            Complex.zero(), Complex.zero(), Complex.zero(), new Complex(HALF_SQRT2, HALF_SQRT2)
    );
    static final Matrix X0 = Matrix.create(2, 2,
            0, 1,
            1, 0
    );
    static final Matrix X1 = Matrix.create(4, 4,
            0, 0, 1, 0,
            0, 0, 0, 1,
            1, 0, 0, 0,
            0, 1, 0, 0
    );
    static final Matrix Y0 = Matrix.create(2, 2,
            Complex.zero(), Complex.i(-1),
            Complex.i(), Complex.zero()
    );
    static final Matrix Y1 = Matrix.create(4, 4,
            Complex.zero(), Complex.zero(), Complex.i(-1), Complex.zero(),
            Complex.zero(), Complex.zero(), Complex.zero(), Complex.i(-1),
            Complex.i(), Complex.zero(), Complex.zero(), Complex.zero(),
            Complex.zero(), Complex.i(), Complex.zero(), Complex.zero()
    );
    static final Matrix Z0 = Matrix.create(2, 2,
            1, 0,
            0, -1
    );
    static final Matrix Z1 = Matrix.create(4, 4,
            1, 0, 0, 0,
            0, 1, 0, 0,
            0, 0, -1, 0,
            0, 0, 0, -1
    );
    static final Matrix H0 = Matrix.create(2, 2,
            HALF_SQRT2, HALF_SQRT2,
            HALF_SQRT2, -HALF_SQRT2
    );
    static final Matrix H1 = Matrix.create(4, 4,
            HALF_SQRT2, 0, HALF_SQRT2, 0,
            0, HALF_SQRT2, 0, HALF_SQRT2,
            HALF_SQRT2, 0, -HALF_SQRT2, 0,
            0, HALF_SQRT2, 0, -HALF_SQRT2
    );
    private static final Logger logger = LoggerFactory.getLogger(MatrixTest.class);
    private static final Matrix SWAP01 = Matrix.create(4, 4,
            1, 0, 0, 0,
            0, 0, 1, 0,
            0, 1, 0, 0,
            0, 0, 0, 1
    );

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

    public static Stream<Arguments> testCcnotKetArgs() {
        return Stream.of(
                Arguments.of(0, 1, 2, Matrix.ketBase(0), Matrix.ketBase(0).extendsRows(8)),
                Arguments.of(0, 1, 2, Matrix.ketBase(1), Matrix.ketBase(1).extendsRows(8)),
                Arguments.of(0, 1, 2, Matrix.ketBase(2), Matrix.ketBase(2).extendsRows(8)),
                Arguments.of(0, 1, 2, Matrix.ketBase(3), Matrix.ketBase(3).extendsRows(8)),

                Arguments.of(0, 1, 2, Matrix.ketBase(4), Matrix.ketBase(4).extendsRows(8)),
                Arguments.of(0, 1, 2, Matrix.ketBase(5), Matrix.ketBase(5).extendsRows(8)),
                /**/ Arguments.of(0, 1, 2, Matrix.ketBase(6), Matrix.ketBase(7).extendsRows(8)),
                /**/ Arguments.of(0, 1, 2, Matrix.ketBase(7), Matrix.ketBase(6).extendsRows(8)),

                Arguments.of(0, 2, 1, Matrix.ketBase(0), Matrix.ketBase(0).extendsRows(8)),
                Arguments.of(0, 2, 1, Matrix.ketBase(1), Matrix.ketBase(1).extendsRows(8)),
                Arguments.of(0, 2, 1, Matrix.ketBase(2), Matrix.ketBase(2).extendsRows(8)),
                Arguments.of(0, 2, 1, Matrix.ketBase(3), Matrix.ketBase(3).extendsRows(8)),

                Arguments.of(0, 2, 1, Matrix.ketBase(4), Matrix.ketBase(4).extendsRows(8)),
                Arguments.of(0, 2, 1, Matrix.ketBase(5), Matrix.ketBase(5).extendsRows(8)),
                /**/ Arguments.of(0, 2, 1, Matrix.ketBase(6), Matrix.ketBase(7).extendsRows(8)),
                /**/ Arguments.of(0, 2, 1, Matrix.ketBase(7), Matrix.ketBase(6).extendsRows(8)),

                Arguments.of(1, 0, 2, Matrix.ketBase(0), Matrix.ketBase(0).extendsRows(8)),
                Arguments.of(1, 0, 2, Matrix.ketBase(1), Matrix.ketBase(1).extendsRows(8)),
                Arguments.of(1, 0, 2, Matrix.ketBase(2), Matrix.ketBase(2).extendsRows(8)),
                Arguments.of(1, 0, 2, Matrix.ketBase(3), Matrix.ketBase(3).extendsRows(8)),

                Arguments.of(1, 0, 2, Matrix.ketBase(4), Matrix.ketBase(4).extendsRows(8)),
                /**/ Arguments.of(1, 0, 2, Matrix.ketBase(5), Matrix.ketBase(7).extendsRows(8)),
                Arguments.of(1, 0, 2, Matrix.ketBase(6), Matrix.ketBase(6).extendsRows(8)),
                /**/ Arguments.of(1, 0, 2, Matrix.ketBase(7), Matrix.ketBase(5).extendsRows(8)),

                Arguments.of(1, 2, 0, Matrix.ketBase(0), Matrix.ketBase(0).extendsRows(8)),
                Arguments.of(1, 2, 0, Matrix.ketBase(1), Matrix.ketBase(1).extendsRows(8)),
                Arguments.of(1, 2, 0, Matrix.ketBase(2), Matrix.ketBase(2).extendsRows(8)),
                Arguments.of(1, 2, 0, Matrix.ketBase(3), Matrix.ketBase(3).extendsRows(8)),

                Arguments.of(1, 2, 0, Matrix.ketBase(4), Matrix.ketBase(4).extendsRows(8)),
                /**/ Arguments.of(1, 2, 0, Matrix.ketBase(5), Matrix.ketBase(7).extendsRows(8)),
                Arguments.of(1, 2, 0, Matrix.ketBase(6), Matrix.ketBase(6).extendsRows(8)),
                /**/ Arguments.of(1, 2, 0, Matrix.ketBase(7), Matrix.ketBase(5).extendsRows(8)),

                Arguments.of(2, 0, 1, Matrix.ketBase(0), Matrix.ketBase(0).extendsRows(8)),
                Arguments.of(2, 0, 1, Matrix.ketBase(1), Matrix.ketBase(1).extendsRows(8)),
                Arguments.of(2, 0, 1, Matrix.ketBase(2), Matrix.ketBase(2).extendsRows(8)),
                /**/ Arguments.of(2, 0, 1, Matrix.ketBase(3), Matrix.ketBase(7).extendsRows(8)),

                Arguments.of(2, 0, 1, Matrix.ketBase(4), Matrix.ketBase(4).extendsRows(8)),
                Arguments.of(2, 0, 1, Matrix.ketBase(5), Matrix.ketBase(5).extendsRows(8)),
                Arguments.of(2, 0, 1, Matrix.ketBase(6), Matrix.ketBase(6).extendsRows(8)),
                /**/ Arguments.of(2, 0, 1, Matrix.ketBase(7), Matrix.ketBase(3).extendsRows(8)),

                Arguments.of(2, 1, 0, Matrix.ketBase(0), Matrix.ketBase(0).extendsRows(8)),
                Arguments.of(2, 1, 0, Matrix.ketBase(1), Matrix.ketBase(1).extendsRows(8)),
                Arguments.of(2, 1, 0, Matrix.ketBase(2), Matrix.ketBase(2).extendsRows(8)),
                /**/ Arguments.of(2, 1, 0, Matrix.ketBase(3), Matrix.ketBase(7).extendsRows(8)),

                Arguments.of(2, 1, 0, Matrix.ketBase(4), Matrix.ketBase(4).extendsRows(8)),
                Arguments.of(2, 1, 0, Matrix.ketBase(5), Matrix.ketBase(5).extendsRows(8)),
                Arguments.of(2, 1, 0, Matrix.ketBase(6), Matrix.ketBase(6).extendsRows(8)),
                /**/ Arguments.of(2, 1, 0, Matrix.ketBase(7), Matrix.ketBase(3).extendsRows(8))
        );
    }

    public static Stream<Arguments> testCnotKetArgs() {
        return Stream.of(
                Arguments.of(0, 1, Matrix.ketBase(0), Matrix.ketBase(0).extendsRows(4)),
                Arguments.of(0, 1, Matrix.ketBase(1), Matrix.ketBase(1).extendsRows(4)),
                Arguments.of(0, 1, Matrix.ketBase(2), Matrix.ketBase(3).extendsRows(4)),
                Arguments.of(0, 1, Matrix.ketBase(3), Matrix.ketBase(2).extendsRows(4)),

                Arguments.of(1, 0, Matrix.ketBase(0), Matrix.ketBase(0).extendsRows(4)),
                Arguments.of(1, 0, Matrix.ketBase(1), Matrix.ketBase(3).extendsRows(4)),
                Arguments.of(1, 0, Matrix.ketBase(2), Matrix.ketBase(2).extendsRows(4)),
                Arguments.of(1, 0, Matrix.ketBase(3), Matrix.ketBase(1).extendsRows(4))
        );
    }

    public static Stream<Arguments> testComputeBitsPermutationArgs() {
        // bit map (internal[i] <- input) e.g. [2, 0] := internal[0] = 2,
        //                                               internal[1] = 0
        // expected (input[i] -> internal) e.g. [1, 2, 0] = input[0] = 1
        //                                                  input[1] = 2
        //                                                  input[2] = 0
        return Stream.of(
                Arguments.of(new int[]{0, 1}, new int[]{0, 1}),
                Arguments.of(new int[]{0, 2}, new int[]{0, 2, 1}),
                Arguments.of(new int[]{0, 3}, new int[]{0, 3, 2, 1}),
                Arguments.of(new int[]{1, 0}, new int[]{1, 0}),

                Arguments.of(new int[]{1, 2}, new int[]{2, 0, 1}), //  0->2, 1->0, 2->1
                Arguments.of(new int[]{1, 3}, new int[]{3, 0, 2, 1}), // 0->3, 1->0, 2->2, 3->1
                Arguments.of(new int[]{2, 0}, new int[]{1, 2, 0}),
                Arguments.of(new int[]{2, 1}, new int[]{2, 1, 0}),

                Arguments.of(new int[]{2, 3}, new int[]{2, 3, 0, 1}),
                Arguments.of(new int[]{3, 0}, new int[]{1, 3, 2, 0}), // inp[0] = 1, inp[1] = 3, inp[2] = 2, inp[3] = 0
                Arguments.of(new int[]{3, 1}, new int[]{3, 1, 2, 0}), // inp[0] = 3, inp[1] = 1, inp[2] = 2, inp[3] = 0
                Arguments.of(new int[]{3, 2}, new int[]{2, 3, 1, 0}) // inp[0] = 2, inp[1] = 3, inp[2] = 1, inp[3] = 0
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
        Matrix k1 = Matrix.ketBase(0);
        return Stream.of(
                Arguments.of(k1, k1, Matrix.ket(1, 0, 0, 0)),
                Arguments.of(k1.dagger(), k1.dagger(), Matrix.ket(1, 0, 0, 0).dagger()),
                Arguments.of(m1, m2, m12),
                Arguments.of(m2, m1, m21),
                Arguments.of(MX, Matrix.identity(2), MX1),
                Arguments.of(Matrix.identity(2), MX, MX0),
                Arguments.of(MZ, Matrix.identity(2), MZ1),
                Arguments.of(Matrix.identity(2), MZ, MZ0)
        );
    }

    public static Stream<Arguments> testEpsArgs() {
        return Stream.of(
                Arguments.of(0, 0, Matrix.create(2, 2,
                        0, 0
                        , 0, 0
                )),
                Arguments.of(0, 1, Matrix.create(2, 2,
                        0, -1,
                        1, 0
                )),
                Arguments.of(1, 0, Matrix.create(2, 2,
                        0, -1,
                        1, 0
                )),
                Arguments.of(3, 3, Matrix.create(4, 4,
                        0, 0, 0, 0,
                        0, 0, 0, 0,
                        0, 0, 0, 0,
                        0, 0, 0, 0
                )),
                Arguments.of(1, 3, Matrix.create(4, 4,
                        0, 0, 0, 0,
                        0, 0, 0, 1,
                        0, 0, 0, 0,
                        0, -1, 0, 0
                )),
                Arguments.of(3, 1, Matrix.create(4, 4,
                        0, 0, 0, 0,
                        0, 0, 0, 1,
                        0, 0, 0, 0,
                        0, -1, 0, 0
                ))
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
        Matrix ket2 = Matrix.create(2, 1,
                1, 2);
        Matrix bra2 = Matrix.create(1, 2,
                1, 2);
        Matrix ket4 = Matrix.create(4, 1,
                1, 2, 0, 0);
        Matrix bra4 = Matrix.create(1, 4,
                1, 2, 0, 0);
        return Stream.of(
                Arguments.of(m2, 4, mx0),
                Arguments.of(m2.mul(Complex.i()), 4, mx0.mul(Complex.i())),
                Arguments.of(MZ, 4, mz0),
                Arguments.of(ket2, 4, ket4),
                Arguments.of(bra2, 4, bra4)
        );
    }

    public static Stream<Arguments> testHArgs() {
        return Stream.of(
                Arguments.of(0, H0),
                Arguments.of(1, H1)
        );
    }

    public static Stream<Arguments> testKetBaseArgs() {
        return Stream.of(
                Arguments.of(0, Matrix.create(2, 1, 1, 0)),
                Arguments.of(1, Matrix.create(2, 1, 0, 1)),
                Arguments.of(2, Matrix.create(4, 1, 0, 0, 1, 0)),
                Arguments.of(3, Matrix.create(4, 1, 0, 0, 0, 1)),
                Arguments.of(4, Matrix.create(8, 1, 0, 0, 0, 0, 1, 0, 0, 0)),
                Arguments.of(5, Matrix.create(8, 1, 0, 0, 0, 0, 0, 1, 0, 0)),
                Arguments.of(6, Matrix.create(8, 1, 0, 0, 0, 0, 0, 0, 1, 0)),
                Arguments.of(7, Matrix.create(8, 1, 0, 0, 0, 0, 0, 0, 0, 1))
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
        Matrix m44 = Matrix.create(4, 4,
                1, 0, 0, 0,
                0, 1, 0, 0,
                0, 0, 1, 0,
                0, 0, 0, 1);
        Matrix m22 = Matrix.create(2, 2,
                0, 1,
                1, 0);
        Matrix ket02 = Matrix.create(2, 1, 1, 0);
        Matrix ket04 = Matrix.create(4, 1, 1, 0, 0, 0);
        Matrix ket12 = Matrix.create(2, 1, 0, 1);
        Matrix bra02 = Matrix.create(1, 2, 1, 0);
        Matrix bra12 = Matrix.create(1, 2, 0, 1);
        Matrix bra04 = Matrix.create(1, 4, 1, 0, 0, 0);
        Matrix proj = Matrix.create(2, 4,
                1, 1, 0, 0,
                0, 0, 1, 1);

        return Stream.of(
                Arguments.of(m1, m2, m12),
                Arguments.of(m2, m1, m21),
                Arguments.of(m22, ket02, ket12),
                Arguments.of(bra02, m22, bra12),
                Arguments.of(m44, ket02, ket04),
                Arguments.of(bra02, m44, bra04),
                Arguments.of(proj, Matrix.ket(1, 2, 3, 4), Matrix.ket(3, 7))
        );
    }

    public static Stream<Arguments> testMulMatrixErrorsArgs() {
        Matrix m22 = Matrix.identity(2);
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

    public static Stream<Arguments> testQubit0Args() {
        return Stream.of(
                Arguments.of(0, 1, QUBIT0_01),
                Arguments.of(0, 2, QUBIT0_02),
                Arguments.of(1, 2, QUBIT0_12),
                Arguments.of(0, 3, QUBIT0_03),
                Arguments.of(1, 3, QUBIT0_13),
                Arguments.of(2, 3, QUBIT0_23)
        );
    }

    public static Stream<Arguments> testQubit1Args() {
        return Stream.of(
                Arguments.of(0, 1, QUBIT1_01),
                Arguments.of(0, 2, QUBIT1_02),
                Arguments.of(1, 2, QUBIT1_12),
                Arguments.of(0, 3, QUBIT1_03),
                Arguments.of(1, 3, QUBIT1_13),
                Arguments.of(2, 3, QUBIT1_23)
        );
    }

    public static Stream<Arguments> testSArgs() {
        return Stream.of(
                Arguments.of(0, S0),
                Arguments.of(1, S1)
        );
    }

    public static Stream<Arguments> testSimArgs() {
        return Stream.of(
                Arguments.of(0, 0, Matrix.create(2, 2,
                        1, 0
                        , 0, 0
                )),
                Arguments.of(0, 1, Matrix.create(2, 2,
                        0, 1,
                        1, 0
                )),
                Arguments.of(3, 3, Matrix.create(4, 4,
                        0, 0, 0, 0,
                        0, 0, 0, 0,
                        0, 0, 0, 0,
                        0, 0, 0, 1
                )),
                Arguments.of(3, 1, Matrix.create(4, 4,
                        0, 0, 0, 0,
                        0, 0, 0, 1,
                        0, 0, 0, 0,
                        0, 1, 0, 0
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

    public static Stream<Arguments> testSwapArgs() {
        return Stream.of(
                Arguments.of(0, 0, Matrix.identity(4)),
                Arguments.of(1, 1, Matrix.identity(4)),
                Arguments.of(0, 1, SWAP01),
                Arguments.of(1, 0, SWAP01),
                Arguments.of(0, 2, SWAP02),
                Arguments.of(2, 0, SWAP02),
                Arguments.of(1, 2, SWAP12),
                Arguments.of(2, 1, SWAP12)
        );
    }

    public static Stream<Arguments> testSwapKetArgs() {
        return Stream.of(
                Arguments.of(0, 0, Matrix.ketBase(0), Matrix.ketBase(0).extendsRows(4)),
                Arguments.of(0, 0, Matrix.ketBase(1), Matrix.ketBase(1).extendsRows(4)),
                Arguments.of(0, 0, Matrix.ketBase(2), Matrix.ketBase(2).extendsRows(4)),
                Arguments.of(0, 0, Matrix.ketBase(3), Matrix.ketBase(3).extendsRows(4)),

                Arguments.of(0, 1, Matrix.ketBase(0), Matrix.ketBase(0).extendsRows(4)),
                Arguments.of(0, 1, Matrix.ketBase(1), Matrix.ketBase(2).extendsRows(4)),
                Arguments.of(0, 1, Matrix.ketBase(2), Matrix.ketBase(1).extendsRows(4)),
                Arguments.of(0, 1, Matrix.ketBase(3), Matrix.ketBase(3).extendsRows(4)),

                Arguments.of(1, 2, Matrix.ketBase(0), Matrix.ketBase(0).extendsRows(8)),
                Arguments.of(1, 2, Matrix.ketBase(1), Matrix.ketBase(1).extendsRows(8)),
                Arguments.of(1, 2, Matrix.ketBase(2), Matrix.ketBase(4).extendsRows(8)),
                Arguments.of(1, 2, Matrix.ketBase(3), Matrix.ketBase(5).extendsRows(8)),

                Arguments.of(1, 2, Matrix.ketBase(4), Matrix.ketBase(2).extendsRows(8)),
                Arguments.of(1, 2, Matrix.ketBase(5), Matrix.ketBase(3).extendsRows(8)),
                Arguments.of(1, 2, Matrix.ketBase(6), Matrix.ketBase(6).extendsRows(8)),
                Arguments.of(1, 2, Matrix.ketBase(7), Matrix.ketBase(7).extendsRows(8))
        );
    }

    public static Stream<Arguments> testTArgs() {
        return Stream.of(
                Arguments.of(0, T0),
                Arguments.of(1, T1)
        );
    }

    public static Stream<Arguments> testToStringArgs() {
        return Stream.of(
                Arguments.of(Matrix.create(2, 2,
                                Complex.zero(), Complex.i().neg(),
                                Complex.i(), Complex.zero()),
                        "[ 0.0,  -i\n    i, 0.0 ]\n"),
                Arguments.of(Matrix.create(4, 1,
                                0, 1, 0, 0),
                        "(1.0) |1>"),
                Arguments.of(Matrix.create(4, 1,
                                0, 0, 0, 0),
                        "(0.0) |3>"),
                Arguments.of(Matrix.create(1, 4,
                                0, 1, 0, 0),
                        "(1.0) <1|"),
                Arguments.of(Matrix.create(1, 4,
                                0, 0, 0, 0),
                        "(0.0) <3|")
        );
    }

    public static Stream<Arguments> testXArgs() {
        return Stream.of(
                Arguments.of(0, X0),
                Arguments.of(1, X1)
        );
    }

    public static Stream<Arguments> testYArgs() {
        return Stream.of(
                Arguments.of(0, Y0),
                Arguments.of(1, Y1)
        );
    }

    public static Stream<Arguments> testZArgs() {
        return Stream.of(
                Arguments.of(0, Z0),
                Arguments.of(1, Z1)
        );
    }

    @ParameterizedTest
    @MethodSource("testAddArgs")
    void testAdd(Matrix left, Matrix right, Matrix exp) {
        Matrix result = left.add(right);
        assertThat(result, matrixCloseTo(exp, EPSILON));
    }

    @Test
    void testAry() {
        Matrix result = Matrix.ary(1, 2);
        assertThat(result, matrixCloseTo(Matrix.create(2, 4,
                0, 0, 0, 0,
                0, 0, 1, 0
        ), EPSILON));
    }

    @Test
    void testAtBra() {
        Matrix m = Matrix.create(1, 4,
                0, 1, 2, 3
        );
        assertThat(m.at(0), complexClose(0, EPSILON));
        assertThat(m.at(1), complexClose(1, EPSILON));
        assertThat(m.at(2), complexClose(2, EPSILON));
        assertThat(m.at(3), complexClose(3, EPSILON));
    }

    @Test
    void testAtKet() {
        Matrix m = Matrix.create(4, 1,
                0, 1, 2, 3
        );
        assertThat(m.at(0), complexClose(0, EPSILON));
        assertThat(m.at(1), complexClose(1, EPSILON));
        assertThat(m.at(2), complexClose(2, EPSILON));
        assertThat(m.at(3), complexClose(3, EPSILON));
    }

    @Test
    void testAtMatrix() {
        Matrix m = Matrix.create(2, 2,
                Complex.zero(), Complex.one(),
                Complex.create(2), Complex.create(3));

        assertThat(m.at(0, 0), complexClose(0, EPSILON));
        assertThat(m.at(0, 1), complexClose(1, EPSILON));
        assertThat(m.at(1, 0), complexClose(2, EPSILON));
        assertThat(m.at(1, 1), complexClose(3, EPSILON));
    }

    @Test
    void testBraToString() {
        Matrix ket = Matrix.ket(new Complex(0, 0), new Complex(2, 0), new Complex(0, 2), new Complex(2, 2)).dagger();
        assertEquals("(2.0) <1| + (-2.0 i) <2| + (2.0 -2.0 i) <3|", ket.toString());
    }

    @Test
    void testBraToString0() {
        Matrix ket = Matrix.ket(0, 0, 0, 0).dagger();
        assertEquals("(0.0) <3|", ket.toString());
    }

    @Test
    void testCcnotError() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> Matrix.ccnot(0, 1, 1));
        assertEquals("Expected all different indices [0, 1, 1]", ex.getMessage());
    }

    @ParameterizedTest
    @MethodSource("testCcnotKetArgs")
    void testCcnotKet(int data, int control0, int control1, Matrix ket, Matrix exp) {
        Matrix m = Matrix.ccnot(data, control0, control1);
        Matrix result = m.mul(ket);
        assertThat(result, matrixCloseTo(exp, EPSILON));
    }

    @Test
    void testCnotError() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> Matrix.cnot(2, 2));
        assertEquals("Expected all different indices [2, 2]", ex.getMessage());
    }

    @ParameterizedTest
    @MethodSource("testCnotKetArgs")
    void testCnotKet(int data, int control, Matrix ket, Matrix exp) {
        Matrix m = Matrix.cnot(data, control);
        Matrix result = m.mul(ket);
        assertThat(result, matrixCloseTo(exp, EPSILON));
    }

    @ParameterizedTest
    @MethodSource("testComputeBitsPermutationArgs")
    void testComputeBitsPermutation(int[] in, int[] exp) {
        // When
        int[] result = Matrix.computeBitsPermutation(in);
        // Then
        assertArrayEquals(exp, result);
    }

    @ParameterizedTest(name = "[{index}] sigma({0}, {1}, {2}])")
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
    void testComputeStatePermutation3(int b0, int b1, int b2, int s0, int s1, int s2, int s3, int s4, int s5, int s6, int s7) {
        // When
        int[] states = Matrix.computeStatePermutation(b0, b1, b2);
        // Then
        assertArrayEquals(new int[]{s0, s1, s2, s3, s4, s5, s6, s7}, states);
    }

    @Test
    void testConj() {
        Matrix m = Matrix.create(2, 2,
                IntStream.range(0, 4).mapToObj(Complex::i).toArray(Complex[]::new));
        Matrix c = m.conj();
        assertThat(c, matrixCloseTo(Matrix.create(2, 2,
                Complex.zero().conj(), Complex.i().conj(),
                Complex.i(2).conj(), Complex.i(3).conj()), EPSILON));
    }

    @Test
    void testCreateComplex() {
        Matrix m = Matrix.create(2, 2,
                Complex.one(), Complex.zero(),
                Complex.zero(), Complex.one());
        assertEquals(2, m.numRows());
        assertEquals(2, m.numCols());
    }

    @Test
    void testCreateReal() {
        Matrix m = Matrix.create(2, 2,
                1, 0,
                0, 1);
        assertEquals(2, m.numRows());
        assertEquals(2, m.numCols());
    }

    @ParameterizedTest
    @MethodSource("testCrossArgs")
    void testCross(Matrix left, Matrix right, Matrix exp) {
        Matrix result = left.cross(right);
        assertThat(result, matrixCloseTo(exp, EPSILON));
    }

    @Test
    void testDagger() {
        Matrix m = Matrix.create(2, 2,
                IntStream.range(0, 4).mapToObj(Complex::i).toArray(Complex[]::new));
        Matrix c = m.dagger();
        assertThat(c, matrixCloseTo(Matrix.create(2, 2,
                Complex.zero().conj(), Complex.i(2).conj(),
                Complex.i().conj(), Complex.i(3).conj()), EPSILON));
    }

    @Test
    void testDivComplex() {
        Matrix m0 = Matrix.identity(4);
        Matrix m1 = m0.div(Complex.i(2));

        assertThat(m1, matrixCloseTo(Matrix.create(4, 4,
                Complex.i(-0.5f), Complex.zero(), Complex.zero(), Complex.zero(),
                Complex.zero(), Complex.i(-0.5f), Complex.zero(), Complex.zero(),
                Complex.zero(), Complex.zero(), Complex.i(-0.5f), Complex.zero(),
                Complex.zero(), Complex.zero(), Complex.zero(), Complex.i(-0.5f)
        ), EPSILON));
    }

    @Test
    void testDivDouble() {
        Matrix m0 = Matrix.identity(4);
        Matrix m1 = m0.div(2);

        assertThat(m1, matrixCloseTo(Matrix.create(4, 4,
                0.5f, 0, 0, 0,
                0, 0.5f, 0, 0,
                0, 0, 0.5f, 0,
                0, 0, 0, 0.5f
        ), EPSILON));
    }

    @ParameterizedTest
    @MethodSource("testEpsArgs")
    void testEps(int i, int j, Matrix exp) {
        Matrix result = Matrix.eps(i, j);
        assertThat(result, matrixCloseTo(exp, EPSILON));
    }

    @Test
    void testExtendRows() {
        Matrix m = Matrix.identity(2);
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
        Matrix m = Matrix.identity(2);
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

    @ParameterizedTest
    @MethodSource("testHArgs")
    void testH(int index, Matrix exp) {
        Matrix m = Matrix.h(index);
        assertThat(m, matrixCloseTo(exp, EPSILON));
    }

    @Test
    void testI() {
        assertThat(Matrix.i(), matrixCloseTo(Matrix.create(2, 1,
                Complex.create(HALF_SQRT2),
                Complex.i().mul(HALF_SQRT2)), EPSILON
        ));
    }

    @Test
    void testIndex() {
        Matrix m = Matrix.create(2, 2,
                Complex.one(), Complex.zero(),
                Complex.zero(), Complex.one());
        assertEquals(0, m.index(0, 0));
        assertEquals(1, m.index(0, 1));
        assertEquals(2, m.index(1, 0));
        assertEquals(3, m.index(1, 1));
    }

    @Test
    void testIndexStream() {
        List<int[]> indices = Matrix.indexStream(2, 3).toList();
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
    void testKetBase(int state, Matrix exp) {
        Matrix ket = Matrix.ketBase(state);
        assertThat(ket, matrixCloseTo(exp, EPSILON));
    }

    @Test
    void testKetComplex() {
        assertThat(Matrix.ket(Complex.zero(), Complex.i()), matrixCloseTo(Matrix.create(2, 1, Complex.zero(), Complex.i()), EPSILON));
    }

    @Test
    void testKetDouble() {
        assertThat(Matrix.ket(0, 1), matrixCloseTo(Matrix.create(2, 1, Complex.zero(), Complex.one()), EPSILON));
    }

    @Test
    void testKetToString() {
        Matrix ket = Matrix.ket(new Complex(0, 0), new Complex(2, 0), new Complex(0, 2), new Complex(2, 2));
        assertEquals("(2.0) |1> + (2.0 i) |2> + (2.0 +2.0 i) |3>", ket.toString());
    }

    @Test
    void testKetToString0() {
        Matrix ket = Matrix.ket(0, 0, 0, 0);
        assertEquals("(0.0) |3>", ket.toString());
    }

    @Test
    void testMinus() {
        assertThat(Matrix.minus(), matrixCloseTo(Matrix.create(2, 1,
                HALF_SQRT2, -HALF_SQRT2), EPSILON));
    }

    @Test
    void testMinus_i() {
        assertThat(Matrix.minus_i(), matrixCloseTo(Matrix.create(2, 1,
                Complex.create(HALF_SQRT2),
                Complex.i().mul(-HALF_SQRT2)), EPSILON
        ));
    }

    @Test
    void testMulComplex() {
        Matrix m0 = Matrix.identity(4);
        Matrix m1 = m0.mul(Complex.i(2));

        assertThat(m1, matrixCloseTo(Matrix.create(4, 4,
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
    void testMulDouble() {
        Matrix m0 = Matrix.identity(4);
        Matrix m1 = m0.mul(2);
        assertThat(m1, matrixCloseTo(Matrix.create(4, 4,
                2, 0, 0, 0,
                0, 2, 0, 0,
                0, 0, 2, 0,
                0, 0, 0, 2
        ), EPSILON));
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

    @Test
    void testNeg() {
        Matrix m0 = Matrix.identity(4);
        Matrix m1 = m0.neg();
        assertThat(m1, matrixCloseTo(Matrix.create(4, 4,
                -1, 0, 0, 0,
                0, -1, 0, 0,
                0, 0, -1, 0,
                0, 0, 0, -1), EPSILON));
    }

    @Test
    void testNormalise() {
        Matrix ket = Matrix.create(4, 1, 1, 2, 3, 4);
        double norm = sqrt(1 + 4 + 9 + 16);
        Matrix exp = Matrix.create(4, 1, 1 / norm, 2 / norm, 3 / norm, 4 / norm);
        assertThat(ket.normalise(), matrixCloseTo(exp, EPSILON));
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
    void testPermute4(int s0, int s1, int s2, int s3, int v0, int exp0) {
        // Given
        Matrix m = Matrix.permute(s0, s1, s2, s3);
        Matrix ket = Matrix.ketBase(v0);
        Matrix exp = Matrix.ketBase(exp0).extends0(4, 1);

        // When
        Matrix res = m.mul(ket);
        // Then
        assertThat(res, matrixCloseTo(exp, EPSILON));
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
    void testPermute8(int s0, int s1, int s2, int s3, int s4, int s5, int s6, int s7, int v0, int exp0) {
        // Given
        Matrix m = Matrix.permute(s0, s1, s2, s3, s4, s5, s6, s7);
        Matrix ket = Matrix.ketBase(v0);
        Matrix exp = Matrix.ketBase(exp0).extends0(8, 0);
        // When
        Matrix res = m.mul(ket);
        assertThat(res, matrixCloseTo(exp, EPSILON));
    }

    @Test
    void testPlus() {
        assertThat(Matrix.plus(), matrixCloseTo(Matrix.create(2, 1,
                HALF_SQRT2, HALF_SQRT2), EPSILON));
    }

    @ParameterizedTest
    @MethodSource("testQubit0Args")
    void testQubit0(int index, int size, Matrix expected) {
        assertThat(Matrix.qubit0(index, size), matrixCloseTo(expected, EPSILON));
    }

    @ParameterizedTest
    @MethodSource("testQubit1Args")
    void testQubit1(int index, int size, Matrix expected) {
        assertThat(Matrix.qubit1(index, size), matrixCloseTo(expected, EPSILON));
    }

    @ParameterizedTest
    @MethodSource("testSArgs")
    void testS(int index, Matrix exp) {
        Matrix m = Matrix.s(index);
        assertThat(m, matrixCloseTo(exp, EPSILON));
    }

    @ParameterizedTest
    @MethodSource("testSimArgs")
    void testSim(int i, int j, Matrix exp) {
        Matrix result = Matrix.sim(i, j);
        assertThat(result, matrixCloseTo(exp, EPSILON));
    }

    @ParameterizedTest
    @MethodSource("testSubArgs")
    void testSub(Matrix left, Matrix right, Matrix exp) {
        Matrix result = left.sub(right);
        assertThat(result, matrixCloseTo(exp, EPSILON));
    }

    @ParameterizedTest
    @MethodSource("testSwapArgs")
    void testSwap(int b0, int b1, Matrix exp) {
        Matrix result = Matrix.swap(b0, b1);
        assertThat(result, matrixCloseTo(exp, EPSILON));
    }

    @ParameterizedTest
    @MethodSource("testSwapKetArgs")
    void testSwapKet(int b0, int b1, Matrix ket, Matrix exp) {
        Matrix m = Matrix.swap(b0, b1);
        Matrix result = m.mul(ket);
        assertThat(result, matrixCloseTo(exp, EPSILON));
    }

    @ParameterizedTest
    @MethodSource("testTArgs")
    void testT(int index, Matrix exp) {
        Matrix m = Matrix.t(index);
        assertThat(m, matrixCloseTo(exp, EPSILON));
    }

    @ParameterizedTest
    @MethodSource("testToStringArgs")
    void testToString(Matrix m, String exp) {
        // Given
        // When
        String s = m.toString();
        // Then
        assertEquals(exp, s);
    }

    @Test
    void testUnsafeIndex() {
        // Shape 2x3
        assertEquals(0, Matrix.unsafeIndex(3, 0, 0));
        assertEquals(1, Matrix.unsafeIndex(3, 0, 1));
        assertEquals(2, Matrix.unsafeIndex(3, 0, 2));
        assertEquals(3, Matrix.unsafeIndex(3, 1, 0));
        assertEquals(4, Matrix.unsafeIndex(3, 1, 1));
        assertEquals(5, Matrix.unsafeIndex(3, 1, 2));
    }

    @ParameterizedTest
    @MethodSource("testXArgs")
    void testX(int index, Matrix exp) {
        Matrix m = Matrix.x(index);
        assertThat(m, matrixCloseTo(exp, EPSILON));
    }

    @ParameterizedTest
    @MethodSource("testYArgs")
    void testY(int index, Matrix exp) {
        Matrix m = Matrix.y(index);
        assertThat(m, matrixCloseTo(exp, EPSILON));
    }

    @ParameterizedTest
    @MethodSource("testZArgs")
    void testZ(int index, Matrix exp) {
        Matrix m = Matrix.z(index);
        assertThat(m, matrixCloseTo(exp, EPSILON));
    }

}