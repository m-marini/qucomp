/*
 * Copyright (c) 2025 Marco Marini, marco.marini@mmarini.org
 *
 *  Permission is hereby granted, free of charge, to any person
 * obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without
 * restriction, including without limitation the rights to use,
 * copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following
 * conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 *
 *    END OF TERMS AND CONDITIONS
 *
 */

package org.mmarini.qucomp.apis;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.lang.Math.max;
import static java.lang.Math.sqrt;
import static java.lang.String.format;
import static java.util.Objects.requireNonNull;
import static org.mmarini.qucomp.apis.VectorUtils.partMul;

/**
 * Ket quantum state
 *
 * @param values the values of single states
 */
public record Ket(Complex[] values) {

    public static final Ket ZERO = create(1, 0);
    public static final Ket ONE = create(0, 1);
    public static final Complex HALF_SQRT2 = Complex.create((float) (sqrt(2) / 2));
    public static final Complex I_HALF_SQRT2 = new Complex(0f, (float) (sqrt(2) / 2));
    public static final Ket PLUS = create(HALF_SQRT2, HALF_SQRT2);
    public static final Ket MINUS = create(HALF_SQRT2, HALF_SQRT2.neg());
    public static final Ket I = create(HALF_SQRT2, I_HALF_SQRT2);
    public static final Ket MINUS_I = create(HALF_SQRT2, I_HALF_SQRT2.neg());
    private static final Map<String, Ket> KET_DICTIONARY = Map.of(
            "|0>", zero(),
            "|1>", one(),
            "|+>", plus(),
            "|->", minus(),
            "|i>", i(),
            "|-i>", minus_i()
    );

    /**
     * Returns a ket base for the given value and size
     *
     * @param value the value
     * @param size  the number of qubit
     */
    public static Ket base(int value, int size) {
        int n = 1 << size;
        return new Ket(IntStream.range(0, n)
                .mapToObj(i -> value == i ? Complex.one() : Complex.zero())
                .toArray(Complex[]::new));
    }

    /**
     * Returns the ket quantum state
     *
     * @param values the values
     */
    public static Ket create(float... values) {
        return new Ket(VectorUtils.create(values));
    }

    /**
     * Returns tje ket quantum state
     *
     * @param values the values
     */
    public static Ket create(Complex... values) {
        return new Ket(values);
    }

    /**
     * Returns the ket by parsing a text
     * <p>
     * The syntax of text is:
     * <pre>
     *  &lt;ket> ::= &lt>ket1> &lt;ket>
     *  &lt;ket1> ::= "|0>" | "|1>" | "|+>" | "|->" | "|i>" | "|-i>"
     * </pre>
     * </p>
     *
     * @param text the text
     */
    public static Ket fromText(String text) {
        StringTokenizer tokenizer = new StringTokenizer(requireNonNull(text), "| ", true);
        Ket result = null;
        while (tokenizer.hasMoreElements()) {
            String tok = tokenizer.nextToken();
            if (tok.equals("|")) {
                if (!tokenizer.hasMoreElements()) {
                    throw new IllegalArgumentException(format("Missing element after %s", tok));
                }
                String tok1 = tokenizer.nextToken();
                Ket keti = KET_DICTIONARY.get("|" + tok1);
                if (keti == null) {
                    throw new IllegalArgumentException(format("Unknown ket |%s", tok1));
                }
                result = result == null ? keti : result.cross(keti);
            } else if (!tok.isBlank()) {
                throw new IllegalArgumentException(format("Unknown token \"%s\"", tok));
            }
        }
        if (result == null) {
            throw new IllegalArgumentException("Missing ket expression");
        }
        return result;
    }

    /**
     * Returns |i>
     */
    public static Ket i() {
        return I;
    }

    /**
     * Returns |-i>
     */
    public static Ket minus() {
        return MINUS;
    }

    /**
     * Returns |->
     */
    public static Ket minus_i() {
        return MINUS_I;
    }

    /**
     * Returns |1>
     */
    public static Ket one() {
        return ONE;
    }

    /**
     * Returns |+>
     */
    public static Ket plus() {
        return PLUS;
    }

    /**
     * Returns |0>
     */
    public static Ket zero() {
        return ZERO;
    }

    /**
     * Create the ket quantum state
     *
     * @param values the single states
     */
    public Ket(Complex[] values) {
        this.values = requireNonNull(values);
        for (Complex value : values) {
            requireNonNull(value);
        }
    }

    /**
     * Returns the ket added to other (this + other)
     *
     * @param other the other ket
     */
    public Ket add(Ket other) {
        return new Ket(VectorUtils.add(values, other.values));
    }

    /***
     * Returns value
     * @param i the index
     */
    public Complex at(int i) {
        return values[i];
    }

    /**
     * Returns the probabilities per bit
     */
    public double[] bitProbs() {
        int n = numBits();
        double[] stateProbs = prob();
        double[] result = new double[n];
        int mask = 1;
        for (int i = 0; i < n; i++) {
            double prob = 0;
            for (int s = 0; s < stateProbs.length; s++) {
                if ((s & mask) != 0) {
                    double stateProb = stateProbs[s];
                    prob += stateProb;
                }
            }
            result[i] = prob;
            mask <<= 1;
        }
        return result;
    }

    /**
     * Returns the conjugated Bra
     */
    public Bra conj() {
        return new Bra(VectorUtils.conj(values));
    }

    /**
     * Returns the tensor product (this x other)
     *
     * @param other the other ket
     */
    public Ket cross(Ket other) {
        return new Ket(VectorUtils.cross(values, other.values));
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Ket ket = (Ket) o;
        return Objects.deepEquals(values, ket.values);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(values);
    }

    /**
     * Returns the module square
     */
    public double moduleSquare() {
        return Arrays.stream(values()).mapToDouble(Complex::moduleSquare).sum();
    }

    /**
     * Returns the transformed ket
     *
     * @param matrix the matrix operator
     */
    public Ket mul(Matrix matrix) {
        int n = values.length;
        if (matrix.numCols() != n) {
            throw new IllegalArgumentException(format("Matrix operator must have shape ? x %d (%dx%d)",
                    n,
                    matrix.numRows(), matrix.numCols()));
        }
        int m = matrix.numRows();
        Complex[] cells = new Complex[m];
        partMul(cells, 0, matrix.numRows(), 1, matrix.cells(), 0, matrix.numCols(), this.values, 0, 1);
        return create(cells);
    }

    /**
     * Returns the ket scaled by real factor
     *
     * @param alpha scale
     */
    public Ket mul(float alpha) {
        return new Ket(VectorUtils.mulScalar(values, alpha));
    }

    /**
     * Returns the ket scaled by complex factor
     *
     * @param alpha the factor
     */
    public Ket mul(Complex alpha) {
        return new Ket(VectorUtils.mulScalar(values, alpha));
    }

    /**
     * Returns the negated ket
     */
    public Ket neg() {
        return new Ket(VectorUtils.neg(values));
    }

    /**
     * Returns the number of bits
     */
    public int numBits() {
        int numStates = values.length;
        int numBits = 0;
        for (int i = numStates; i > 0; i >>= 1) {
            numBits++;
        }
        return max(numBits - 1, 0);
    }

    /**
     * Returns the probability of each state
     */
    public double[] prob() {
        return Arrays.stream(values).mapToDouble(Complex::moduleSquare)
                .toArray();
    }

    /**
     * Returns the two complementary kets by states
     * result[0] is the not matching states values
     * result[1] is the matching states values
     *
     * @param states the matching states
     */
    public Ket[] split(int... states) {
        Set<Integer> stateSet = Arrays.stream(states).boxed().collect(Collectors.toSet());
        Complex[] match = new Complex[values.length];
        Complex[] notMatch = new Complex[values.length];
        for (int i = 0; i < values.length; i++) {
            if (stateSet.contains(i)) {
                match[i] = values[i];
                notMatch[i] = Complex.zero();
            } else {
                notMatch[i] = values[i];
                match[i] = Complex.zero();
            }
        }
        return new Ket[]{Ket.create(notMatch), Ket.create(match)};
    }

    /**
     * Returns the ket subtracted by other (this - other)
     *
     * @param other the other ket
     */
    public Ket sub(Ket other) {
        return new Ket(VectorUtils.sub(values, other.values));
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        boolean isZero = true;
        for (int i = 0; i < values.length; i++) {
            if (values[i].module() != 0) {
                if (!isZero) {
                    builder.append(" + ");
                }
                isZero = false;
                builder.append("(");
                builder.append(values[i]);
                builder.append(") |");
                builder.append(i);
                builder.append(">");
            }
        }
        return isZero ? "(0.0) |" + (values.length - 1) + ">" : builder.toString();
    }
}
