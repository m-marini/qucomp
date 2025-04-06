package org.mmarini.qucomp.apis;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BitStateMapperTest {

    @ParameterizedTest(name = "[{index}] c={0},{1} d={2} {3}->{4}")
    @CsvSource({
            "2,1,0, 0,0", // 0000 -> 0000
            "2,1,0, 1,1", // 0001 -> 0001
            "2,1,0, 2,2", // 0010 -> 0010
            "2,1,0, 3,3", // 0011 -> 0011
            "2,1,0, 4,4", // 0100 -> 0100
            "2,1,0, 5,5", // 0101 -> 0101
            "2,1,0, 6,7", // 0110 -> 0111
            "2,1,0, 7,6", // 0111 -> 0110
            "2,1,0, 8,8", // 1000 -> 1000
            "2,1,0, 9,9", // 1001 -> 1001
            "2,1,0, 10,10", // 1010 -> 1010
            "2,1,0, 11,11", // 1011 -> 1011
            "2,1,0, 12,12", // 1100 -> 1100
            "2,1,0, 13,13", // 1101 -> 1101
            "2,1,0, 14,15", // 1110 -> 1111
            "2,1,0, 15,14", // 1111 -> 1110

            "3,2,1, 0,0", // 0000 -> 0000
            "3,2,1, 1,1", // 0001 -> 0001
            "3,2,1, 2,2", // 0010 -> 0010
            "3,2,1, 3,3", // 0011 -> 0011
            "3,2,1, 4,4", // 0100 -> 0100
            "3,2,1, 5,5", // 0101 -> 0101
            "3,2,1, 6,6", // 0110 -> 0110
            "3,2,1, 7,7", // 0111 -> 0111
            "3,2,1, 8,8", // 1000 -> 1000
            "3,2,1, 9,9", // 1001 -> 1001
            "3,2,1, 10,10", // 1010 -> 1010
            "3,2,1, 11,11", // 1011 -> 1011
            "3,2,1, 12,14", // 1100 -> 1110
            "3,2,1, 13,15", // 1101 -> 1111
            "3,2,1, 14,12", // 1110 -> 1100
            "3,2,1, 15,13", // 1111 -> 1101
    })
    void ccnot(int c0, int c1, int index, int s, int exp) {
        BitStateMapper c = BitStateMapper.identity(4).ccnot(c0, c1, index);
        assertEquals(exp, c.applyAsInt(s));
    }

    @ParameterizedTest(name = "[{index}] c{0} d{1} {2}->{3}")
    @CsvSource({
            "1,0, 0,0", // 000 -> 000
            "1,0, 1,1", // 001 -> 001
            "1,0, 2,3", // 010 -> 011
            "1,0, 3,2", // 011 -> 010
            "1,0, 4,4", // 100 -> 100
            "1,0, 5,5", // 101 -> 101
            "1,0, 6,7", // 110 -> 111
            "1,0, 7,6", // 111 -> 110

            "2,0, 0,0", // 000 -> 000
            "2,0, 1,1", // 001 -> 001
            "2,0, 2,2", // 010 -> 010
            "2,0, 3,3", // 011 -> 011
            "2,0, 4,5", // 100 -> 101
            "2,0, 5,4", // 101 -> 100
            "2,0, 6,7", // 110 -> 111
            "2,0, 7,6", // 111 -> 110

            "2,1, 0,0", // 000 -> 000
            "2,1, 1,1", // 001 -> 001
            "2,1, 2,2", // 010 -> 010
            "2,1, 3,3", // 011 -> 011
            "2,1, 4,6", // 100 -> 110
            "2,1, 5,7", // 101 -> 111
            "2,1, 6,4", // 110 -> 100
            "2,1, 7,5", // 111 -> 101
    })
    void cnot(int control, int index, int s, int exp) {
        BitStateMapper c = BitStateMapper.identity(3).cnot(control, index);
        assertEquals(exp, c.applyAsInt(s));
    }

    @ParameterizedTest()
    @CsvSource({
            "0,0",
            "1,1",
            "2,2",
            "3,3",
            "4,4",
            "5,5",
            "6,6",
            "7,7",
    })
    void identity(int s0, int exp) {
        assertEquals(exp, BitStateMapper.identity(3).applyAsInt(s0));
    }

    @ParameterizedTest(name = "[{index}] bit{0}<->bit{1} {2}->{3}")
    @CsvSource({
            "0,1, 0,0",
            "0,1, 1,2",
            "0,1, 2,1",
            "0,1, 3,3",
            "0,1, 4,4",
            "0,1, 5,6",
            "0,1, 6,5",
            "0,1, 7,7",

            "0,2, 0,0",
            "0,2, 1,4",
            "0,2, 2,2",
            "0,2, 3,6",
            "0,2, 4,1",
            "0,2, 5,5",
            "0,2, 6,3",
            "0,2, 7,7",

            "1,2, 0,0",
            "1,2, 1,1",
            "1,2, 2,4",
            "1,2, 3,5",
            "1,2, 4,2",
            "1,2, 5,3",
            "1,2, 6,6",
            "1,2, 7,7",


    })
    void swap(int i0, int i1, int s, int exp) {
        BitStateMapper c = BitStateMapper.identity(3).swap(i0, i1);
        assertEquals(exp, c.applyAsInt(s));
    }

    @ParameterizedTest(name = "[{index}] bit{0} {1}->{2}")
    @CsvSource({
            "0, 0,1",
            "0, 1,0",
            "0, 2,3",
            "0, 3,2",
            "1, 0,2",
            "1, 1,3",
            "1, 2,0",
            "1, 3,1",
    })
    void x(int index, int s0, int exp) {
        BitStateMapper c = BitStateMapper.identity(2).x(index);
        assertEquals(exp, c.applyAsInt(s0));
    }
}