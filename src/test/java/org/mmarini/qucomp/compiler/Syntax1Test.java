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
import org.mmarini.NotImplementedException;
import org.mmarini.Tuple2;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.isA;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mmarini.Matchers.tokenWithRule;

class Syntax1Test {

    List<Tuple2<Token, SyntaxRule>> rules;
    private ParseContext parseContext;

    boolean parse(String text, String ruleId) throws IOException {
        Tokenizer tokenizer = Tokenizer.create(text).open();
        SyntaxRule rule = Syntax1.rule(ruleId);
        this.parseContext = new ParseContext() {
            @Override
            public void add(Tuple2<Token, SyntaxRule> tokenWithRule) {
                rules.add(tokenWithRule);
            }

            @Override
            public Token currentToken() {
                return tokenizer.currentToken();
            }

            @Override
            public List<CommandNode> popAllReversed() {
                throw new NotImplementedException();
            }

            @Override
            public CommandNode popCommand() {
                throw new NotImplementedException();
            }

            @Override
            public void popToken() throws IOException {
                tokenizer.popToken();
            }

            @Override
            public void push(CommandNode node) {
                throw new NotImplementedException();
            }
        };
        return rule.parse(parseContext);
    }

    @BeforeEach
    void setUp() {
        this.rules = new ArrayList<>();
    }

    @Test
    void testClear() {
        boolean result = assertDoesNotThrow(() -> parse("clear();", "clear-stm"));
        assertTrue(result);
        assertThat(parseContext.currentToken(), isA(Token.EOFToken.class));
        assertThat(rules, contains(
                tokenWithRule("clear", "<clear>"),
                tokenWithRule("(", "<(>"),
                tokenWithRule(")", "<)>"),
                tokenWithRule(";", "<;>"),
                tokenWithRule("clear", "<clear-stm>")
        ));
    }
}