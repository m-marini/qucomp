/*
 * Copyright (c) 2022-2023  Marco Marini, marco.marini@mmarini.org
 *
 * Permission is hereby granted, free of charge, to any person
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

package org.mmarini;

import org.hamcrest.CustomMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.mmarini.qucomp.apis.Bra;
import org.mmarini.qucomp.apis.Complex;
import org.mmarini.qucomp.apis.Ket;
import org.mmarini.qucomp.compiler.Command;

import java.util.Optional;

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

    static Matcher<Ket> ketCloseTo(float epsilon, Complex... values) {
        return ketCloseTo(Ket.create(values), epsilon);
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

    static Matcher<Complex> complexClose(float expected, float epsilon) {
        return complexClose(Complex.create(expected), epsilon);
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

    static <T> Matcher<Optional<T>> emptyOptional() {
        return equalTo(Optional.empty());
    }

    static <T> Matcher<Optional<? extends T>> optionalOf(T exp) {
        return optionalOf(equalTo(exp));
    }

    static <T> Matcher<Optional<? extends T>> optionalOf(Matcher<? extends T> exp) {
        requireNonNull(exp);
        return new CustomMatcher<>(format("Optional containing  %s",
                exp)) {
            @Override
            public void describeMismatch(Object item, Description description) {
                if (item instanceof Optional o && o.isPresent()) {
                    super.describeMismatch(item, description);
                } else {
                    exp.describeMismatch(item, description);
                }
            }

            @Override
            public boolean matches(Object o) {
                return o instanceof Optional opt
                        && opt.isPresent()
                        && exp.matches(opt.orElseThrow());
            }
        };
    }

    static <T1, T2> Matcher<Tuple2<? extends T1, ? extends T2>> tupleOf(T1 value1, T2 value2) {
        return tupleOf(equalTo(value1), equalTo(value2));
    }

    static <T1, T2> Matcher<Tuple2<? extends T1, ? extends T2>> tupleOf(Matcher<? extends T1> value1, Matcher<? extends T2> value2) {
        requireNonNull(value1);
        requireNonNull(value2);
        return new CustomMatcher<>(format("Tuple containing  %s, %s",
                value1, value2)) {
            @Override
            public boolean matches(Object o) {
                return o instanceof Tuple2 t
                        && value1.matches(t._1)
                        && value2.matches(t._2);
            }
        };
    }
}