package org.mmarini.qucomp.apis;

import java.util.Arrays;
import java.util.function.IntUnaryOperator;

import static java.lang.String.format;

/**
 * @param numBits
 * @param mapper
 */
public record BitStateMapper(int numBits, IntUnaryOperator mapper) {

    /**
     * Returns the identity circuit
     *
     * @param numBits the number of bits
     */
    public static BitStateMapper identity(int numBits) {
        IntUnaryOperator op = state -> {
            int n = 1 << numBits;
            if (state < 0 || state >= n) {
                throw new IllegalArgumentException(format("state must be between 0 and %d (%d)",
                        n - 1, state));
            }
            return state;
        };
        return new BitStateMapper(numBits, op);
    }

    /**
     * Return the function that returns the value of a bit in state
     *
     * @param index the bit index
     */
    static IntUnaryOperator value(int index) {
        return value -> (value >> index) & 1;
    }

    /**
     * Returns the output state for the given input state
     *
     * @param state the input state
     */
    public int applyAsInt(int state) {
        return mapper.applyAsInt(state);
    }

    /**
     * Returns the mapper with ccnot operator
     *
     * @param c0   the control0 bit index
     * @param c1   the control1 bit index
     * @param data the data bit index
     */
    public BitStateMapper ccnot(int c0, int c1, int data) {
        validateIndices(c0, c1, data);
        int controlMask = (1 << c0) | (1 << c1);
        int xorMask = 1 << data;
        IntUnaryOperator op = state ->
                (state & controlMask) == controlMask
                        ? state ^ xorMask
                        : state;
        return new BitStateMapper(numBits, mapper.andThen(op));
    }

    /**
     * Returns the mapper with cnot operator
     *
     * @param control the control bit index
     * @param data    the data bit index
     */
    public BitStateMapper cnot(int control, int data) {
        validateIndices(control, data);
        int mask = 1 << control;
        int xorMask = 1 << data;
        IntUnaryOperator op = state ->
                (state & mask) == 0
                        ? state
                        : state ^ xorMask;
        return new BitStateMapper(numBits, mapper.andThen(op));
    }

    /**
     * Returns the circuit with two bits swapped
     *
     * @param index0 bit index 0
     * @param index1 bit index 1
     */
    public BitStateMapper swap(int index0, int index1) {
        validateIndices(index0, index1);
        int mask = ~((1 << index0) | (1 << index1));
        IntUnaryOperator bit0 = value(index0);
        IntUnaryOperator bit1 = value(index1);
        IntUnaryOperator op = state -> {
            int state1 = state & mask; // Clear state bits
            int b0 = bit0.applyAsInt(state);
            int b1 = bit1.applyAsInt(state);
            state1 |= b0 << index1; // Set bit1 to bit0 value
            state1 |= b1 << index0; // Set bit0 to bit1 value
            return state1;
        };
        return new BitStateMapper(numBits, mapper.andThen(op));
    }

    /**
     * Validates the indices values
     *
     * @param indices the indices
     */
    private void validateIndices(int... indices) {
        if (Arrays.stream(indices).anyMatch(i -> i < 0 || i >= numBits)) {
            throw new IllegalArgumentException(format("indices must be between 0 and %d %s",
                    numBits - 1, Arrays.toString(indices)));
        }
    }

    /**
     * Returns the bit not
     *
     * @param index the bit index
     */
    public BitStateMapper x(int index) {
        validateIndices(index);
        return xor(1 << index);
    }

    /**
     * Returns the bit xor
     *
     * @param mask the bit mask
     */
    public BitStateMapper xor(int mask) {
        int mask1 = mask & ((1 << numBits) - 1);
        IntUnaryOperator op = state -> state ^ mask1;
        return new BitStateMapper(numBits, mapper.andThen(op));
    }
}
