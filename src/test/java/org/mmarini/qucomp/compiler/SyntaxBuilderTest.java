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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.isA;
import static org.junit.jupiter.api.Assertions.*;

class SyntaxBuilderTest {
    private SyntaxBuilder builder;

    @Test
    void buildError() {
        QuException ex = assertThrows(QuException.class, () -> {
            builder.intLiteral("10");
            builder.options("10", "10");
        });
        assertEquals("Rule 10 already defined", ex.getMessage());
    }

    @Test
    void buildRecursive() {
        SyntaxRule rule = assertDoesNotThrow(() -> {
            builder.intLiteral("int-literal");
            builder.options("int-literal-head", "int-literal", "int-literal-head");
            return builder.build().rule("int-literal-head");
        });
        assertNotNull(rule);
        assertEquals("int-literal-head", rule.id());
        assertThat(rule, isA(SyntaxRule.NonTerminalRule.class));
        SyntaxRule.NonTerminalRule r = (SyntaxRule.NonTerminalRule) rule;
        assertThat(r.rules(), hasSize(2));
        assertEquals("int-literal", r.rules().getFirst().id());
        assertSame(rule, r.rules().get(1));
    }

    @BeforeEach
    void setUp() {
        this.builder = new SyntaxBuilder();
    }
}