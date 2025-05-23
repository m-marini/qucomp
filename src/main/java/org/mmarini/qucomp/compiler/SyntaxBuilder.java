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

import org.mmarini.MapStream;
import org.mmarini.Tuple2;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static java.lang.String.format;

/**
 * The syntax rule builder decouples the definition of syntax rules from the building
 * allowing recursive rule references
 */
public class SyntaxBuilder {
    private final Map<String, RuleBuilder> builders;
    private Map<String, SyntaxRule> ruleMap;

    /**
     * Creates the syntax rule builder
     */
    public SyntaxBuilder() {
        this.builders = new HashMap<>();
    }

    /**
     * Returns the syntax rule by building all referenced rules and binding them together
     */
    public SyntaxBuilder build() throws ParseException {
        ruleMap = MapStream.of(builders).mapValues(RuleBuilder::build).toMap();
        for (RuleBuilder builder : builders.values()) {
            builder.bind(ruleMap::get);
        }
        return this;
    }

    /**
     * Returns the syntax rule that matches for the given identifier
     *
     * @param id the identifier
     */
    public RuleBuilder.TerminalRuleBuilder id(String id) throws ParseException {
        return put(new RuleBuilder.TerminalRuleBuilder(id) {
            @Override
            public SyntaxRule build() {
                return new SyntaxRule.TerminalRule(id) {

                    @Override
                    protected boolean match(Token token) {
                        return token instanceof Token.IdentifierToken && token.token().equals(id);
                    }
                };
            }
        });
    }

    /**
     * Returns the int literal builder
     *
     * @param ruleId the syntax rule identifier
     */
    public RuleBuilder.TerminalRuleBuilder intLiteral(String ruleId) throws ParseException {
        return put(new RuleBuilder.TerminalRuleBuilder(ruleId) {
            @Override
            public SyntaxRule build() {
                return new SyntaxRule.TerminalRule(ruleId) {
                    @Override
                    protected boolean match(Token token) {
                        return token instanceof Token.IntegerToken;
                    }
                };
            }
        });
    }


    /**
     * Returns the syntax rule that matches for the given operator
     *
     * @param op the operator
     */
    public RuleBuilder.TerminalRuleBuilder oper(String op) throws ParseException {
        return put(new RuleBuilder.TerminalRuleBuilder(op) {
            @Override
            public SyntaxRule build() {
                return new SyntaxRule.TerminalRule(op) {
                    @Override
                    protected boolean match(Token token) {
                        return token instanceof Token.OperatorToken && token.token().equals(op);
                    }
                };
            }
        });
    }

    public RuleBuilder.NonTerminalRuleBuilder opt(String id, String condition, String... rules) throws ParseException {
        String[] deps = Stream.concat(
                Stream.of(condition),
                Stream.of(rules)
        ).toArray(String[]::new);
        return put(new RuleBuilder.NonTerminalRuleBuilder(id, deps) {
            @Override
            public SyntaxRule build() {
                return new SyntaxRule.NonTerminalRule(id) {
                    @Override
                    public boolean parse(ParseContext context) throws IOException {
                        Token token = context.currentToken();
                        List<SyntaxRule> rules1 = rules();
                        if (!rules1.getFirst().parse(context)) {
                            return false;
                        }
                        for (int i = 1; i < rules1.size(); i++) {
                            rules1.get(i).parse(context);
                        }
                        context.add(Tuple2.of(token, this));
                        return true;
                    }
                };
            }
        });
    }

    /**
     * Returns the builder of the option rule list
     *
     * @param id      the identifier
     * @param depends the dependant rules
     */
    public RuleBuilder.NonTerminalRuleBuilder options(String id, String... depends) throws ParseException {
        return put(new RuleBuilder.NonTerminalRuleBuilder(id, depends) {
            @Override
            public SyntaxRule build() {
                return new SyntaxRule.NonTerminalRule(id) {
                };
            }
        });
    }

    /**
     * Stores the builder in the builder dictionary.
     *
     * @param builder the builder
     * @param <T>     the builder type
     * @return the builder
     */
    private <T extends RuleBuilder> T put(T builder) throws ParseException {
        String id = builder.id();
        if (builders.containsKey(id)) {
            throw new ParseException(format("Rule <%s> already defined", id));
        }
        builders.put(id, builder);
        return builder;
    }

    /**
     * Returns the rule by identifier
     *
     * @param id the rule identifier
     */
    public SyntaxRule rule(String id) {
        return ruleMap != null ? ruleMap.get(id) : null;
    }
}
