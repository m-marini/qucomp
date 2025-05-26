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

package org.mmarini.qucomp.compiler;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasToString;
import static org.hamcrest.Matchers.isA;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TokenizerTest {

    public static final String TEXT = """
              ii ( 12)
            
              a1 // klsdjalsdjalkjdkal
              b /* kasjdhasjhdaj
                c kajsdhajdhakjd
                asjdadsha */ d
              123.4e-10 /123.4E10
            123.
            123.4
            1e2
            1E2
            """;

    public static Stream<Arguments> testErrorData() {
        return Stream.of(
                Arguments.arguments("1234.0e", "Missing exponent (\\n)"),
                Arguments.arguments("1234.0ea", "Missing exponent (a)")
        );
    }

    Tokenizer tokenizer;

    void createTokenizer(String text) {
        this.tokenizer = new Tokenizer(new BufferedReader(new StringReader(
                text != null ? text : "")));
    }

    @BeforeEach
    void setUp() {
        createTokenizer(TEXT);
    }

    @Test
    void testCreate() throws IOException {
        tokenizer.open();
        Token tok = tokenizer.currentToken();
        assertThat(tok, hasToString("ii"));
        assertThat(tok, isA(Token.IdentifierToken.class));
        assertEquals("  ii ( 12)", tok.context().line());
        assertEquals(1, tok.context().lineNumber());
        assertEquals(2, tok.context().position());

        tok = tokenizer.popToken().currentToken();
        assertThat(tok, hasToString("("));
        assertThat(tok, isA(Token.OperatorToken.class));
        assertEquals("  ii ( 12)", tok.context().line());
        assertEquals(1, tok.context().lineNumber());
        assertEquals(5, tok.context().position());

        tok = tokenizer.popToken().currentToken();
        assertThat(tokenizer.currentToken(), hasToString("12"));
        assertThat(tokenizer.currentToken(), isA(Token.IntegerToken.class));
        assertEquals("  ii ( 12)", tok.context().line());
        assertEquals(1, tok.context().lineNumber());
        assertEquals(7, tok.context().position());

        tok = tokenizer.popToken().currentToken();
        assertThat(tokenizer.currentToken(), hasToString(")"));
        assertThat(tokenizer.currentToken(), isA(Token.OperatorToken.class));
        assertEquals("  ii ( 12)", tok.context().line());
        assertEquals(1, tok.context().lineNumber());
        assertEquals(9, tok.context().position());

        tok = tokenizer.popToken().currentToken();
        assertThat(tokenizer.currentToken(), hasToString("a1"));
        assertThat(tokenizer.currentToken(), isA(Token.IdentifierToken.class));
        assertEquals("  a1 // klsdjalsdjalkjdkal", tok.context().line());
        assertEquals(3, tok.context().lineNumber());
        assertEquals(2, tok.context().position());

        tok = tokenizer.popToken().currentToken();
        assertThat(tokenizer.currentToken(), hasToString("b"));
        assertThat(tokenizer.currentToken(), isA(Token.IdentifierToken.class));
        assertEquals("  b /* kasjdhasjhdaj", tok.context().line());
        assertEquals(4, tok.context().lineNumber());
        assertEquals(2, tok.context().position());

        tok = tokenizer.popToken().currentToken();
        assertThat(tokenizer.currentToken(), hasToString("d"));
        assertThat(tokenizer.currentToken(), isA(Token.IdentifierToken.class));
        assertEquals("    asjdadsha */ d", tok.context().line());
        assertEquals(6, tok.context().lineNumber());
        assertEquals(17, tok.context().position());

        tok = tokenizer.popToken().currentToken();
        assertThat(tokenizer.currentToken(), hasToString("123.4e-10"));
        assertThat(tokenizer.currentToken(), isA(Token.RealToken.class));
        assertEquals(123.4e-10f, ((Token.RealToken) tokenizer.currentToken()).value());
        assertEquals("  123.4e-10 /123.4E10", tok.context().line());
        assertEquals(7, tok.context().lineNumber());
        assertEquals(2, tok.context().position());

        tok = tokenizer.popToken().currentToken();
        assertThat(tokenizer.currentToken(), hasToString("/"));
        assertThat(tokenizer.currentToken(), isA(Token.OperatorToken.class));
        assertEquals("  123.4e-10 /123.4E10", tok.context().line());
        assertEquals(7, tok.context().lineNumber());
        assertEquals(12, tok.context().position());

        tok = tokenizer.popToken().currentToken();
        assertThat(tokenizer.currentToken(), hasToString("123.4E10"));
        assertThat(tokenizer.currentToken(), isA(Token.RealToken.class));
        assertEquals(123.4e10f, ((Token.RealToken) tokenizer.currentToken()).value());
        assertEquals("  123.4e-10 /123.4E10", tok.context().line());
        assertEquals(7, tok.context().lineNumber());
        assertEquals(13, tok.context().position());

        tok = tokenizer.popToken().currentToken();
        assertThat(tokenizer.currentToken(), hasToString("123."));
        assertThat(tokenizer.currentToken(), isA(Token.RealToken.class));
        assertEquals(123f, ((Token.RealToken) tokenizer.currentToken()).value());
        assertEquals("123.", tok.context().line());
        assertEquals(8, tok.context().lineNumber());
        assertEquals(0, tok.context().position());

        tok = tokenizer.popToken().currentToken();
        assertThat(tokenizer.currentToken(), hasToString("123.4"));
        assertThat(tokenizer.currentToken(), isA(Token.RealToken.class));
        assertEquals(123.4f, ((Token.RealToken) tokenizer.currentToken()).value());
        assertEquals("123.4", tok.context().line());
        assertEquals(9, tok.context().lineNumber());
        assertEquals(0, tok.context().position());

        tok = tokenizer.popToken().currentToken();
        assertThat(tokenizer.currentToken(), hasToString("1e2"));
        assertThat(tokenizer.currentToken(), isA(Token.RealToken.class));
        assertEquals(1e2f, ((Token.RealToken) tokenizer.currentToken()).value());
        assertEquals("1e2", tok.context().line());
        assertEquals(10, tok.context().lineNumber());
        assertEquals(0, tok.context().position());

        tok = tokenizer.popToken().currentToken();
        assertThat(tokenizer.currentToken(), hasToString("1E2"));
        assertThat(tokenizer.currentToken(), isA(Token.RealToken.class));
        assertEquals(1e2f, ((Token.RealToken) tokenizer.currentToken()).value());
        assertEquals("1E2", tok.context().line());
        assertEquals(11, tok.context().lineNumber());
        assertEquals(0, tok.context().position());

        assertThat(tokenizer.popToken().currentToken(), isA(Token.EOFToken.class));
    }

    @ParameterizedTest
    @MethodSource("testErrorData")
    void testError(String text, String expMsg) {
        createTokenizer(text);
        QuParseException ex = assertThrows(QuParseException.class, () ->
                tokenizer.open());
        assertEquals(expMsg, ex.getMessage());
    }
}