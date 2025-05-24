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

package org.mmarini.qucomp;

import org.hamcrest.CustomMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.mmarini.qucomp.apis.Bra;
import org.mmarini.qucomp.apis.Complex;
import org.mmarini.qucomp.apis.Ket;
import org.mmarini.qucomp.compiler.Command;
import org.mmarini.qucomp.compiler.CommandNode;
import org.mmarini.qucomp.compiler.SyntaxRule;
import org.mmarini.qucomp.compiler.Token;

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;
import static org.hamcrest.Matchers.equalTo;

public interface Matchers {

    static Matcher<Bra> braCloseTo(float epsilon, Complex... values) {
        return braCloseTo(Bra.create(values), epsilon);
    }

    static Matcher<Bra> braCloseTo(float epsilon, float... values) {
        return braCloseTo(Bra.create(values), epsilon);
    }

    static Matcher<Bra> braCloseTo(Bra expected, float epsilon) {
        requireNonNull(expected);
        return new CustomMatcher<>(format("Bra close to %s within +- %f",
                expected,
                epsilon)) {
            @Override
            public void describeMismatch(Object item, Description description) {
                if (item instanceof Bra bra) {
                    description.appendText("bra ")
                            .appendValue(bra.toString())
                            .appendText(" differs from ")
                            .appendValue(expected.toString())
                            .appendText(" (more then ")
                            .appendValue(epsilon)
                            .appendText(")");
                } else {
                    super.describeMismatch(item, description);
                }
            }

            @Override
            public boolean matches(Object o) {
                if (!(o instanceof Bra bra)) return false;
                Complex[] v1 = bra.values();
                Complex[] v2 = expected.values();
                if (v1.length != v2.length) return false;
                for (int i = 0; i < v1.length; i++) {
                    if (!v1[i].isClose(v2[i], epsilon)) {
                        return false;
                    }
                }
                return true;
            }
        };
    }

    static Matcher<Command> command(Class<? extends Command> expected) {
        return new CustomMatcher<>(format("Command %s",
                expected)) {

            @Override
            public boolean matches(Object o) {
                return expected.isInstance(o);
            }
        };
    }

    static Matcher<Complex> complexClose(float expectedReal, float expectedIm, float epsilon) {
        return complexClose(new Complex(expectedReal, expectedIm), epsilon);
    }

    static Matcher<Complex> complexClose(Complex expected, float epsilon) {
        requireNonNull(expected);
        return new CustomMatcher<>(format("Complex close to %s within +- %f",
                expected,
                epsilon)) {
            @Override
            public void describeMismatch(Object item, Description description) {
                if (item instanceof Complex complex) {
                    description.appendText("complex ")
                            .appendValue(complex.toString())
                            .appendText(" differs from ")
                            .appendValue(expected.toString())
                            .appendText(" (more then ")
                            .appendValue(epsilon)
                            .appendText(")");
                } else {
                    super.describeMismatch(item, description);
                }
            }

            @Override
            public boolean matches(Object o) {
                if (!(o instanceof Complex complex)) return false;
                return complex.isClose(expected, epsilon);
            }
        };
    }

    static Matcher<Complex> complexClose(float expected, float epsilon) {
        return complexClose(Complex.create(expected), epsilon);
    }

    static Matcher<CommandNode> containsArgsSize(Matcher<Integer> expected) {
        return new CustomMatcher<>(format("CodeUnit with %s",
                expected)) {

            @Override
            public boolean matches(Object o) {
                if (!(o instanceof CommandNode.CodeUnit cmd)) return false;
                return expected.matches(cmd.commands().size());
            }
        };
    }

    static Matcher<CommandNode> containsArgsSize(int expected) {
        return containsArgsSize(equalTo(expected));
    }

    static Matcher<CommandNode> hasArgAt(int index, Matcher<CommandNode> expected) {
        return new CustomMatcher<>(format("CodeUnit with argument at %d that %s",
                index, expected)) {

            @Override
            public boolean matches(Object o) {
                if (!(o instanceof CommandNode.CodeUnit cmd)) return false;
                if (index >= cmd.commands().size()) return false;
                CommandNode arg = cmd.commands().get(index);
                return expected.matches(arg);
            }
        };
    }

    static Matcher<Token> idToken(String expValue) {
        return idToken(equalTo(expValue));
    }

    static Matcher<Token> idToken(Matcher<String> expValue) {
        requireNonNull(expValue);
        return new CustomMatcher<>(format("IdentifierToken(%s)",
                expValue)) {
            @Override
            public void describeMismatch(Object item, Description description) {
                if (item instanceof Token.IdentifierToken) {
                    super.describeMismatch(item, description);
                } else {
                    expValue.describeMismatch(item, description);
                }
            }

            @Override
            public boolean matches(Object o) {
                return o instanceof Token.IdentifierToken opt && expValue.matches(opt.token());
            }
        };
    }

    static Matcher<Token> intToken(int expValue) {
        return intToken(equalTo(expValue));
    }

    static Matcher<Token> intToken(Matcher<Integer> expValue) {
        requireNonNull(expValue);
        return new CustomMatcher<>(format("IntegerToken(%s)",
                expValue)) {
            @Override
            public void describeMismatch(Object item, Description description) {
                if (item instanceof Token.IntegerToken) {
                    super.describeMismatch(item, description);
                } else {
                    expValue.describeMismatch(item, description);
                }
            }

            @Override
            public boolean matches(Object o) {
                return o instanceof Token.IntegerToken opt && expValue.matches(opt.value());
            }
        };
    }

    static Matcher<CommandNode> isAddCommand(Matcher<CommandNode> leftExpected, Matcher<CommandNode> rightExpected) {
        return new CustomMatcher<>(format("Add %s, %s",
                leftExpected, rightExpected)) {

            @Override
            public boolean matches(Object o) {
                if (!(o instanceof CommandNode.Add cmd)) return false;
                return leftExpected.matches(cmd.left()) && rightExpected.matches(cmd.right());
            }
        };
    }

    static Matcher<CommandNode> isAssignCommand(Matcher<CommandNode> leftExpected, Matcher<CommandNode> rightExpected) {
        return new CustomMatcher<>(format("Assign %s, %s",
                leftExpected, rightExpected)) {

            @Override
            public boolean matches(Object o) {
                if (!(o instanceof CommandNode.Assign cmd)) return false;
                return leftExpected.matches(cmd.left()) && rightExpected.matches(cmd.right());
            }
        };
    }

    static <T> Matcher<CommandNode> isConsumeCommand(Matcher<T> expected) {
        return new CustomMatcher<>(format("Consume %s",
                expected)) {

            @Override
            public boolean matches(Object o) {
                if (!(o instanceof CommandNode.Consume cmd)) return false;
                return expected.matches(cmd.arg());
            }
        };
    }

    static <T> Matcher<CommandNode> isRetrieveVarCommand(Matcher<T> expected) {
        return new CustomMatcher<>(format("RetrieveVar %s",
                expected)) {

            @Override
            public boolean matches(Object o) {
                if (!(o instanceof CommandNode.RetrieveVar cmd)) return false;
                return expected.matches(cmd.arg());
            }
        };
    }

    static <T> Matcher<CommandNode> isValueCommand(Matcher<T> expected) {
        return new CustomMatcher<>(format("Value %s",
                expected)) {

            @Override
            public boolean matches(Object o) {
                if (!(o instanceof CommandNode.Value cmd)) return false;
                return expected.matches(cmd.value());
            }
        };
    }

    static Matcher<CommandNode> isValueCommand(int expected) {
        return isValueCommand(equalTo(expected));
    }

    static Matcher<CommandNode> isValueCommand(Complex expected, float epsilon) {
        return isValueCommand(complexClose(expected, epsilon));
    }

    static Matcher<CommandNode> isValueCommand(float re, float im, float epsilon) {
        return isValueCommand(complexClose(re, im, epsilon));
    }

    static Matcher<CommandNode> isValueCommand(float re, float epsilon) {
        return isValueCommand(complexClose(re, epsilon));
    }

    static Matcher<CommandNode> isValueCommand(String expected) {
        return isValueCommand(equalTo(expected));
    }

    static Matcher<CommandNode> isValueCommand(Ket expected, float epsilon) {
        return isValueCommand(ketCloseTo(expected, epsilon));
    }

    static Matcher<Ket> ketCloseTo(float epsilon, Complex... values) {
        return ketCloseTo(Ket.create(values), epsilon);
    }

    static Matcher<Ket> ketCloseTo(float epsilon, float... values) {
        return ketCloseTo(Ket.create(values), epsilon);
    }

    static Matcher<Ket> ketCloseTo(Ket expected, float epsilon) {
        requireNonNull(expected);
        return new CustomMatcher<>(format("Ket close to %s within +- %f",
                expected,
                epsilon)) {
            @Override
            public void describeMismatch(Object item, Description description) {
                if (item instanceof Bra bra) {
                    description.appendText("ket ")
                            .appendValue(bra.toString())
                            .appendText(" differs from ")
                            .appendValue(expected.toString())
                            .appendText(" (more then ")
                            .appendValue(epsilon)
                            .appendText(")");
                } else {
                    super.describeMismatch(item, description);
                }
            }

            @Override
            public boolean matches(Object o) {
                if (!(o instanceof Ket value)) return false;
                Complex[] v1 = value.values();
                Complex[] v2 = expected.values();
                if (v1.length != v2.length) return false;
                for (int i = 0; i < v1.length; i++) {
                    if (!v1[i].isClose(v2[i], epsilon)) {
                        return false;
                    }
                }
                return true;
            }
        };
    }

    static Matcher<Token> opToken(String expValue) {
        return opToken(equalTo(expValue));
    }

    static Matcher<Token> opToken(Matcher<String> expValue) {
        requireNonNull(expValue);
        return new CustomMatcher<>(format("OperatorToken(%s)",
                expValue)) {
            @Override
            public void describeMismatch(Object item, Description description) {
                if (item instanceof Token.OperatorToken) {
                    super.describeMismatch(item, description);
                } else {
                    expValue.describeMismatch(item, description);
                }
            }

            @Override
            public boolean matches(Object o) {
                return o instanceof Token.OperatorToken opt && expValue.matches(opt.token());
            }
        };
    }

    static Matcher<Command> pushCommand(Complex expected, float epsilon) {
        return new CustomMatcher<>(format("PushComplex(%s) command",
                expected)) {

            @Override
            public boolean matches(Object o) {
                if (!(o instanceof Command.PushComplex cmd)) return false;
                return cmd.value().isClose(expected, epsilon);
            }
        };
    }

    static Matcher<Command> pushCommand(float expected, float epsilon) {
        return pushCommand(Complex.create(expected), epsilon);
    }

    static Matcher<Command> pushCommand(int expected) {
        return new CustomMatcher<>(format("PushInt(%d) command",
                expected)) {

            @Override
            public boolean matches(Object o) {
                if (!(o instanceof Command.PushInt cmd)) return false;
                return cmd.value() == expected;
            }
        };
    }

    static Matcher<Command> pushCommand(String expected) {
        return new CustomMatcher<>(format("PushInt(%s) command",
                expected)) {

            @Override
            public boolean matches(Object o) {
                if (!(o instanceof Command.PushString cmd)) return false;
                return expected.equals(cmd.value());
            }
        };
    }

    static Matcher<Token> realToken(float expValue) {
        return realToken(equalTo(expValue));
    }

    static Matcher<Token> realToken(Matcher<Float> expValue) {
        requireNonNull(expValue);
        return new CustomMatcher<>(format("RealToken(%s)",
                expValue)) {
            @Override
            public void describeMismatch(Object item, Description description) {
                if (item instanceof Token.RealToken) {
                    super.describeMismatch(item, description);
                } else {
                    expValue.describeMismatch(item, description);
                }
            }

            @Override
            public boolean matches(Object o) {
                return o instanceof Token.RealToken opt && expValue.matches(opt.value());
            }
        };
    }

    static Matcher<SyntaxRule> rule(String expId) {
        return rule(equalTo(expId));
    }

    static Matcher<SyntaxRule> rule(Matcher<String> expId) {
        requireNonNull(expId);
        return new CustomMatcher<>(format("SyntaxRule %s",
                expId)) {
            @Override
            public void describeMismatch(Object item, Description description) {
                if (item instanceof SyntaxRule) {
                    super.describeMismatch(item, description);
                } else {
                    expId.describeMismatch(item, description);
                }
            }

            @Override
            public boolean matches(Object o) {
                return o instanceof SyntaxRule opt && expId.matches(opt.id());
            }
        };
    }

}
