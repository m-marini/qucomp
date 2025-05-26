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

import org.hamcrest.BaseMatcher;
import org.hamcrest.CustomMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.mmarini.qucomp.apis.Bra;
import org.mmarini.qucomp.apis.Complex;
import org.mmarini.qucomp.apis.Ket;
import org.mmarini.qucomp.compiler.CommandNode;
import org.mmarini.qucomp.compiler.SyntaxRule;
import org.mmarini.qucomp.compiler.Token;

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;
import static org.hamcrest.Matchers.*;

public interface Matchers {

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

    static Matcher<CommandNode> isConjCommand(Matcher<CommandNode> arg) {
        return isUnaryCommand(isA(CommandNode.Conj.class), arg);
    }

    static Matcher<CommandNode> isNegateCommmand(Matcher<CommandNode> arg) {
        return isUnaryCommand(isA(CommandNode.Negate.class), arg);
    }

    static Matcher<CommandNode> isFunctionCommand(Matcher<String> id, Matcher<CommandNode> arg) {
        requireNonNull(id);
        requireNonNull(arg);
        return new CustomMatcher<>(format("CallFunction %s of %s",
                id,
                arg)) {

            @Override
            public boolean matches(Object o) {
                if (!(o instanceof CommandNode.CallFunction cmd)) return false;
                return id.matches(cmd.id())
                        && arg.matches(cmd.arg());
            }
        };
    }

    static Matcher<CommandNode> isFunctionCommand(String id, Matcher<CommandNode> arg) {
        return isFunctionCommand(equalTo(id), arg);
    }

    static Matcher<CommandNode> isIntToKet(Matcher<CommandNode> arg) {
        return isUnaryCommand(isA(CommandNode.IntToKet.class), arg);
    }

    static Matcher<CommandNode> isRetrieveVarCommand(Matcher<String> expected) {
        requireNonNull(expected);
        return new CustomMatcher<>(format("RetrieveVar %s",
                expected)) {

            @Override
            public boolean matches(Object o) {
                if (!(o instanceof CommandNode.RetrieveVar cmd)) return false;
                return expected.matches(cmd.id());
            }
        };
    }

    static Matcher<CommandNode> isRetrieveVarCommand(String expected) {
        return isRetrieveVarCommand(equalTo(expected));
    }

    static Matcher<CommandNode> isUnaryCommand(Matcher<Class<? extends CommandNode>> clazz, Matcher<CommandNode> arg) {
        requireNonNull(clazz);
        requireNonNull(arg);


        return new BaseMatcher<>() {

            public void describeMismatch(Object item, Description description) {
                if (item instanceof CommandNode.UnaryNode value) {
                    if (!clazz.matches(value)) {
                        clazz.describeMismatch(value, description);
                    } else if (!arg.matches(value.arg())) {
                        arg.describeMismatch(value.arg(), description);
                    }
                } else {
                    describeMismatch(item, description);
                }
            }

            public void describeTo(Description description) {
                description.appendText("is ")
                        .appendDescriptionOf(clazz)
                        .appendText(" with arg ")
                        .appendDescriptionOf(arg);
            }

            @Override
            public boolean matches(Object o) {
                if (!(o instanceof CommandNode.UnaryNode cmd)) return false;
                return clazz.matches(cmd) && arg.matches(cmd.arg());
            }
        };
    }

    static Matcher<CommandNode> isAddCommand(Matcher<CommandNode> left, Matcher<CommandNode> right) {
        return isBinaryCommand(isA(CommandNode.Add.class), left, right);
    }

    static Matcher<CommandNode> isBinaryCommand(Matcher<Class<? extends CommandNode.BinaryNode>> clazz, Matcher<CommandNode> left, Matcher<CommandNode> right) {
        requireNonNull(clazz);
        requireNonNull(left);
        requireNonNull(right);

        return new BaseMatcher<>() {

            public void describeMismatch(Object item, Description description) {
                if (item instanceof CommandNode.BinaryNode value) {
                    if (!clazz.matches(value)) {
                        clazz.describeMismatch(value, description);
                    }
                    if (!left.matches(value.left())) {
                        description.appendText("left ");
                        left.describeMismatch(value.left(), description);
                    }
                    if (!right.matches(value.right())) {
                        description.appendText("right ");
                        right.describeMismatch(value.right(), description);
                    }
                } else {
                    description.appendValue(item)
                            .appendText(" is not a ")
                            .appendValue(CommandNode.BinaryNode.class);
                }
            }

            public void describeTo(Description description) {
                description.appendText("is ")
                        .appendDescriptionOf(clazz)
                        .appendText(" with arg ")
                        .appendDescriptionOf(left)
                        .appendText(", ")
                        .appendDescriptionOf(left);
            }

            @Override
            public boolean matches(Object o) {
                if (!(o instanceof CommandNode.BinaryNode cmd)) return false;
                return clazz.matches(cmd)
                        && left.matches(cmd.left())
                        && right.matches(cmd.right());
            }
        };
    }

    static Matcher<CommandNode> isCrossCommand(Matcher<CommandNode> left, Matcher<CommandNode> right) {
        return isBinaryCommand(isA(CommandNode.Cross.class), left, right);
    }

    static Matcher<CommandNode> isDivCommand(Matcher<CommandNode> left, Matcher<CommandNode> right) {
        return isBinaryCommand(isA(CommandNode.Div.class), left, right);
    }

    static Matcher<CommandNode> isMulCommand(Matcher<CommandNode> left, Matcher<CommandNode> right) {
        return isBinaryCommand(isA(CommandNode.Mul.class), left, right);
    }

    static Matcher<CommandNode> isSubCommand(Matcher<CommandNode> left, Matcher<CommandNode> right) {
        return isBinaryCommand(isA(CommandNode.Sub.class), left, right);
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

    static <T> Matcher<CommandNode> isValueCommand(Matcher<T> expected) {
        requireNonNull(expected);
        return new CustomMatcher<>(format("ValueCommand of %s",
                expected)) {
            public void describeMismatch(Object item, Description description) {
                if (item instanceof CommandNode.Value value) {
                    description.appendText("value of ")
                            .appendValue(value.toString())
                            .appendText(" ");
                    expected.describeMismatch(value.value(), description);
                } else {
                    super.describeMismatch(item, description);
                }
            }

            @Override
            public boolean matches(Object o) {
                if (!(o instanceof CommandNode.Value cmd)) return false;
                return expected.matches(cmd.value());
            }
        };
    }

    static Matcher<CommandNode> isAssignCommand(String identifier, Matcher<CommandNode> expected) {
        return isAssignCommand(equalTo(identifier), expected);
    }

    static Matcher<CommandNode> isAssignCommand(Matcher<String> identifier, Matcher<CommandNode> expected) {
        requireNonNull(identifier);
        requireNonNull(expected);
        return new BaseMatcher<>() {
            @Override
            public void describeMismatch(Object item, Description description) {
                if (item instanceof CommandNode.Assign value) {
                    if (!identifier.matches(value.id())) {
                        description.appendText("id ");
                        identifier.describeMismatch(value.id(), description);
                    }
                    if (!expected.matches(value.arg())) {
                        description.appendText("arg ");
                        expected.describeMismatch(value.arg(), description);
                    }
                } else {
                    description.appendValue(item)
                            .appendText(" is not an assign command");
                }
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("Assign ");
                identifier.describeTo(description);
                description.appendText(" = ");
                expected.describeTo(description);
            }

            @Override
            public boolean matches(Object o) {
                if (!(o instanceof CommandNode.Assign cmd)) return false;
                return identifier.matches(cmd.id()) && expected.matches(cmd.arg());
            }
        };
    }

    static Matcher<CommandNode> isCodeUnit(Matcher<Iterable<? extends CommandNode>> commands) {
        return new BaseMatcher<>() {
            @Override
            public void describeMismatch(Object item, Description description) {
                if (item instanceof CommandNode.CodeUnit value) {
                    if (!commands.matches(value.commands())) {
                        description.appendText("commands ");
                        commands.describeMismatch(value.commands(), description);
                    }
                } else {
                    description.appendValue(item)
                            .appendText(" is not a code unit command");
                }
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("CodeUnit with ");
                commands.describeTo(description);
            }

            @Override
            public boolean matches(Object o) {
                if (!(o instanceof CommandNode.CodeUnit cmd)) return false;
                return commands.matches(cmd.commands());
            }
        };
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
