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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TokenizerTest {

    Tokenizer tokenizer;

    @BeforeEach
    void setUp() {
        this.tokenizer = new Tokenizer(List.of("  ii (12 )  ").iterator());
    }

    @Test
    void testCreate() {
        assertFalse(tokenizer.eof());
        assertEquals(' ', tokenizer.currentChar());
        assertEquals("ii", tokenizer.currentToken());

        tokenizer.popToken();
        assertFalse(tokenizer.eof());
        assertEquals('1', tokenizer.currentChar());
        assertEquals("(", tokenizer.currentToken());

        tokenizer.popToken();
        assertFalse(tokenizer.eof());
        assertEquals(' ', tokenizer.currentChar());
        assertEquals("12", tokenizer.currentToken());

        tokenizer.popToken();
        assertFalse(tokenizer.eof());
        assertEquals(' ', tokenizer.currentChar());
        assertEquals(")", tokenizer.currentToken());

        tokenizer.popToken();
        assertTrue(tokenizer.eof());
    }

    @Test
    void testCreateNull() {
        this.tokenizer = new Tokenizer(Collections.emptyIterator());
        assertTrue(tokenizer.eof());
    }
}