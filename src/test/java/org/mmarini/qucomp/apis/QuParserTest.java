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

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mmarini.Function3;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.IntFunction;

import static org.junit.jupiter.api.Assertions.*;

class QuParserTest {

    @ParameterizedTest
    @CsvSource({
            "0, 0",
            "12345678, 12345678"
    })
    void parseBit(String source, int expected) {
        QuParser parser = QuParser.create(source);

        int result = parser.parseBit();

        assertEquals(expected, result);
    }

    @ParameterizedTest
    @CsvSource({
            "swap(0;1),swap,1,0",
            "cnot(1;0),cnot,0,1",
    })
    void parseOptBinaryGate(String source, String expId, int expBit0, int expBit1) {
        QuParser parser = QuParser.create(source.replace(";", ","));

        QuGate result = parser.parseOptBinaryGate();

        assertNotNull(result);
        assertEquals(expId, result.type());
        assertArrayEquals(new int[]{expBit0, expBit1}, result.indices());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "cnot",
            "swap",
    })
    void parseOptBinaryId(String source) {
        QuParser parser = QuParser.create(source);

        BiFunction<Integer, Integer, QuGate> result = parser.parseOptBinaryId();

        assertNotNull(result);
    }

    @ParameterizedTest
    @CsvSource({
            "i(0),i",
            "h(1),h",
            "x(2),x",
            "y(3),y",
            "z(4),z",
            "s(5),s",
            "t(6),t",
            "swap(0;1),swap",
            "cnot(2;3),cnot",
            "ccnot(0;1;2),ccnot",
    })
    void parseOptGate(String source, String expId) {
        QuParser parser = QuParser.create(source.replace(";", ","));

        QuGate result = parser.parseOptGate();

        assertNotNull(result);
        assertEquals(expId, result.type());
    }

    @ParameterizedTest
    @CsvSource({
            "ccnot(1;0;3),ccnot,1,0,3",
    })
    void parseOptTrinaryGate(String source, String expId, int expBit0, int expBit1, int expBit2) {
        QuParser parser = QuParser.create(source.replace(";", ","));

        QuGate result = parser.parseOptTrinaryGate();

        assertNotNull(result);
        assertEquals(expId, result.type());
        assertArrayEquals(new int[]{expBit0, expBit1, expBit2}, result.indices());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "ccnot",
    })
    void parseOptTrinaryId(String source) {
        QuParser parser = QuParser.create(source);

        Function3<Integer, Integer, Integer, QuGate> result = parser.parseOptTrinaryId();

        assertNotNull(result);
    }

    @ParameterizedTest
    @CsvSource({
            "i(0),i,0",
            "h(1),h,1",
            "x(2),x,2",
            "y(3),y,3",
            "z(4),z,4",
            "s(5),s,5",
            "t(6),t,6",
    })
    void parseOptUnaryGate(String source, String expId, int expBit) {
        QuParser parser = QuParser.create(source);

        QuGate result = parser.parseOptUnaryGate();

        assertNotNull(result);
        assertEquals(expId, result.type());
        assertArrayEquals(new int[]{expBit}, result.indices());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "i",
            "h",
            "x",
            "y",
            "z",
            "s",
            "t",
    })
    void parseOptUnaryId(String source) {
        QuParser parser = QuParser.create(source);

        IntFunction<QuGate> result = parser.parseOptUnaryId();

        assertNotNull(result);
    }

    @ParameterizedTest
    @CsvSource({
            ",Expected gate (<EOF>)",
            "i(0)  +,Unexpected token (+)",
            "i( ,Expected number (<EOF>)",
            "i( a1),Expected number (a1)",
            "i 11,Expected \"(\" (11)",
            "i ,Expected \"(\" (<EOF>)",
    })
    void testError(String source, String expMsg) {
        QuParser parser = QuParser.create(
                source != null
                        ? source.replace(";;", ",")
                        : "");

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, parser::parse);
        assertEquals(expMsg.replace(";;", ","), ex.getMessage());
    }

    @Test
    void testParse() {
        String source = """
                i(0)
                h(0)
                x(0)
                y(0)
                z(0)
                s(0)
                t(0)
                swap(0,1)
                cnot(0,1)
                ccnot(0,1,2)
                """;
        QuParser parser = QuParser.create(source);

        List<QuGate> result = parser.parse();

        assertNotNull(result);
        assertEquals(10, result.size());
    }
}