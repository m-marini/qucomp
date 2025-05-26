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

import java.io.IOException;
import java.util.List;

import static java.util.Objects.requireNonNull;

/**
 * Parses the parse context.
 * If the rule matches the current token, it joins itself with the token and discards the token from parse context
 */
public interface SyntaxRule {
    String id();

    /**
     * Parses the context applying the rule.
     * Returns true if the rule is applied
     *
     * @param context the parse context
     */
    boolean parse(ParseContext context) throws IOException;

    abstract class AbstractRule implements SyntaxRule {
        private final String id;

        protected AbstractRule(String id) {
            this.id = requireNonNull(id);
        }

        @Override
        public String id() {
            return id;
        }

        @Override
        public String toString() {
            return id;
        }
    }

    abstract class NonTerminalRule extends AbstractRule {
        private List<SyntaxRule> rules;

        protected NonTerminalRule(String id) {
            super(id);
        }

        public List<SyntaxRule> rules() {
            return rules;
        }

        public NonTerminalRule setRules(List<SyntaxRule> rules) {
            this.rules = rules;
            return this;
        }
    }

    abstract class TerminalRule extends AbstractRule {
        protected TerminalRule(String id) {
            super(id);
        }

        protected abstract boolean match(Token token);

        @Override
        public boolean parse(ParseContext context) throws IOException {
            Token token = context.currentToken();
            if (match(token)) {
                context.popToken();
                context.join(token, this);
                return true;
            }
            return false;
        }
    }
}
