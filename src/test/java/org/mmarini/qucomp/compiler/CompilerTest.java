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
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.mmarini.qucomp.apis.Complex;
import org.mmarini.qucomp.apis.Ket;

import java.util.Deque;
import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mmarini.qucomp.Matchers.*;

class CompilerTest {


    public static final float EPSILON = 1e-3f;

    public static Stream<Arguments> argsBra() {
        return Stream.of(
                Arguments.of("<i|;", Ket.i()),
                Arguments.of("<+|;", Ket.plus()),
                Arguments.of("<-|;", Ket.minus()),
                Arguments.of("<-i|;", Ket.minus_i())
        );
    }

    public static Stream<Arguments> argsKet() {
        return Stream.of(
                Arguments.of("|i>;", Ket.i()),
                Arguments.of("|+>;", Ket.plus()),
                Arguments.of("|->;", Ket.minus()),
                Arguments.of("|-i>;", Ket.minus_i())
        );
    }

    private SyntaxRule rule;
    private Compiler compiler;
    private Tokenizer tokenizer;

    private void create(String text) {
        assertDoesNotThrow(() -> {
            this.tokenizer = Tokenizer.create(text).open();
            rule.parse(compiler.createParseContext(tokenizer));
        });
    }

    @BeforeEach
    void setUp() {
        assertDoesNotThrow(() -> {
            this.rule = Syntax.rule("<code-unit>");
            this.compiler = Compiler.create();
        });
    }

    @Test
    void testAddComplex() {
        create("1. + i;");
        Deque<CommandNode> stack = compiler.stack();
        assertThat(stack, hasSize(1));
        CommandNode command = stack.getLast();
        assertThat(command, isCodeUnit(contains(
                isAddCommand(
                        isValueCommand(Complex.create(1), EPSILON),
                        isValueCommand(Complex.i(), EPSILON)
                ))));
    }

    @Test
    void testAddComplexInt() {
        create("1. + i + 2;");
        Deque<CommandNode> stack = compiler.stack();
        assertThat(stack, hasSize(1));
        CommandNode command = stack.getLast();
        assertThat(command, isCodeUnit(contains(
                isAddCommand(
                        isAddCommand(
                                isValueCommand(Complex.create(1), EPSILON),
                                isValueCommand(Complex.i(), EPSILON)),
                        isValueCommand(2)))
        ));
    }

    @Test
    void testAddComplexInt1() {
        create("1. + (i + 2);");
        Deque<CommandNode> stack = compiler.stack();
        assertThat(stack, hasSize(1));
        CommandNode command = stack.getLast();
        assertThat(command, isCodeUnit(contains(
                isAddCommand(
                        isValueCommand(Complex.create(1), EPSILON),
                        isAddCommand(
                                isValueCommand(Complex.i(), EPSILON),
                                isValueCommand(2))
                ))));
    }

    @Test
    void testAddSubInt() {
        create("1 + 2 - 3;");
        Deque<CommandNode> stack = compiler.stack();
        assertThat(stack, hasSize(1));
        CommandNode command = stack.getLast();
        assertThat(command, isCodeUnit(contains(
                isSubCommand(
                        isAddCommand(
                                isValueCommand(1),
                                isValueCommand(2)
                        ),
                        isValueCommand(3))
        )));
    }

    @Test
    void testAddSubInt1() {
        create("1 + (2 - 3);");
        Deque<CommandNode> stack = compiler.stack();
        assertThat(stack, hasSize(1));
        CommandNode command = stack.getLast();
        assertThat(command, isCodeUnit(contains(
                isAddCommand(
                        isValueCommand(1),
                        isSubCommand(
                                isValueCommand(2),
                                isValueCommand(3)
                        )))));
    }

    @Test
    void testAssign() {
        create("let a = 1;");
        Deque<CommandNode> stack = compiler.stack();
        assertThat(stack, hasSize(1));
        CommandNode command = stack.getLast();
        assertThat(command, isCodeUnit(contains(
                isAssignCommand("a", isValueCommand(1)))
        ));
    }

    @ParameterizedTest
    @MethodSource("argsBra")
    void testBra(String text, Ket exp) {
        create(text);
        Deque<CommandNode> stack = compiler.stack();
        assertThat(stack, hasSize(1));
        CommandNode command = stack.getLast();
        assertThat(command, isCodeUnit(contains(
                isConjCommand(
                        isValueCommand(ketCloseTo(exp, EPSILON)))
        )));
    }

    @Test
    void testClear() {
        create("clear();");
        Deque<CommandNode> stack = compiler.stack();
        assertThat(stack, hasSize(1));
        CommandNode command = stack.getLast();
        assertThat(command, isCodeUnit(contains(
                isA(CommandNode.Clear.class))
        ));
    }

    @Test
    void testCodeUnitInt() {
        create("let a = 1; a; clear();");
        Deque<CommandNode> stack = compiler.stack();
        assertThat(stack, contains(isCodeUnit(contains(
                isAssignCommand("a",
                        isValueCommand(1)),
                isRetrieveVarCommand("a"),
                isA(CommandNode.Clear.class)
        ))));
    }

    @Test
    void testConj() {
        create("1^;");
        Deque<CommandNode> stack = compiler.stack();
        assertThat(stack, hasSize(1));
        CommandNode command = stack.getLast();
        assertThat(command, isCodeUnit(contains(
                isConjCommand(
                        isValueCommand(1))
        )));
    }

    @ParameterizedTest
    @CsvSource({
            "1.;, 1,0",
            "i;, 0,1",
            "pi;, 3.1415927,0",
            "e;, 2.7182817,0"
    })
    void testCreateComplex(String text, float expReal, float expIm) {
        create(text);
        Deque<CommandNode> stack = compiler.stack();
        assertThat(stack, hasSize(1));
        CommandNode command = stack.getLast();
        assertThat(command, isCodeUnit(contains(
                isValueCommand(expReal, expIm, EPSILON))
        ));
    }

    @Test
    void testCreateInt() {
        create("1;");
        Deque<CommandNode> stack = compiler.stack();
        assertThat(stack, hasSize(1));
        CommandNode command = stack.getLast();
        assertThat(command, isCodeUnit(contains(
                isValueCommand(1))
        ));
    }

    @Test
    void testCross() {
        create("1 x 2;");
        Deque<CommandNode> stack = compiler.stack();
        assertThat(stack, hasSize(1));
        CommandNode command = stack.getLast();
        assertThat(command, isCodeUnit(contains(
                isCrossCommand(
                        isValueCommand(1),
                        isValueCommand(2)
                ))));
    }

    @Test
    void testCrossIntInt() {
        create("1 x 2 x 3;");
        Deque<CommandNode> stack = compiler.stack();
        assertThat(stack, hasSize(1));
        CommandNode command = stack.getLast();
        assertThat(command, isCodeUnit(contains(
                isCrossCommand(
                        isCrossCommand(
                                isValueCommand(1),
                                isValueCommand(2)),
                        isValueCommand(3)
                ))));
    }

    @Test
    void testCrossIntInt1() {
        create("1 x (2 x 3);");
        Deque<CommandNode> stack = compiler.stack();
        assertThat(stack, hasSize(1));
        CommandNode command = stack.getLast();
        assertThat(command, isCodeUnit(contains(
                isCrossCommand(
                        isValueCommand(1),
                        isCrossCommand(
                                isValueCommand(2),
                                isValueCommand(3)
                        )))));
    }

    @Test
    void testDivInt() {
        create("1 / 2;");
        Deque<CommandNode> stack = compiler.stack();
        assertThat(stack, hasSize(1));
        CommandNode command = stack.getLast();
        assertThat(command, isCodeUnit(contains(
                isDivCommand(
                        isValueCommand(1),
                        isValueCommand(2)
                ))));
    }

    @Test
    void testDivIntInt1() {
        create("1 * (2 / 3);");
        Deque<CommandNode> stack = compiler.stack();
        assertThat(stack, hasSize(1));
        CommandNode command = stack.getLast();
        assertThat(command, isCodeUnit(contains(
                isMulCommand(
                        isValueCommand(1),
                        isDivCommand(
                                isValueCommand(2),
                                isValueCommand(3)
                        )))));
    }

    @ParameterizedTest
    @CsvSource({
            "<0|;, 0",
            "<(+0)|;, 0"
    })
    void testIntBra(String text, int exp) {
        create(text);
        Deque<CommandNode> stack = compiler.stack();
        assertThat(stack, hasSize(1));
        CommandNode command = stack.getLast();
        assertThat(command, isCodeUnit(contains(
                isConjCommand(
                        isIntToKet(
                                isValueCommand(exp))))
        ));
    }

    @ParameterizedTest
    @CsvSource({
            "|0>;, 0",
            "|(+0)>;, 0"
    })
    void testIntKet(String text, int exp) {
        create(text);
        Deque<CommandNode> stack = compiler.stack();
        assertThat(stack, hasSize(1));
        CommandNode command = stack.getLast();
        assertThat(command, isCodeUnit(contains(
                isIntToKet(
                        isValueCommand(exp))
        )));
    }

    @ParameterizedTest
    @MethodSource("argsKet")
    void testKet(String text, Ket exp) {
        create(text);
        Deque<CommandNode> stack = compiler.stack();
        assertThat(stack, hasSize(1));
        CommandNode command = stack.getLast();
        assertThat(command, isCodeUnit(contains(
                isValueCommand(ketCloseTo(exp, EPSILON))
        )));
    }

    @Test
    void testMixed() {
        create("1 + 2 * 3 x 4;");
        Deque<CommandNode> stack = compiler.stack();
        assertThat(stack, hasSize(1));
        CommandNode command = stack.getLast();
        assertThat(command, isCodeUnit(contains(
                isAddCommand(
                        isValueCommand(1),
                        isMulCommand(
                                isValueCommand(2),
                                isCrossCommand(
                                        isValueCommand(3),
                                        isValueCommand(4)
                                ))))));
    }

    @Test
    void testMixed1() {
        create("1 + (2 * 3) x 4;");
        Deque<CommandNode> stack = compiler.stack();
        assertThat(stack, hasSize(1));
        CommandNode command = stack.getLast();
        assertThat(command, isCodeUnit(contains(
                isAddCommand(
                        isValueCommand(1),
                        isCrossCommand(
                                isMulCommand(
                                        isValueCommand(2),
                                        isValueCommand(3)),
                                isValueCommand(4)
                        )))));
    }

    @Test
    void testMulDivInt() {
        create("1 * 2 / 3;");
        Deque<CommandNode> stack = compiler.stack();
        assertThat(stack, hasSize(1));
        CommandNode command = stack.getLast();
        assertThat(command, isCodeUnit(contains(
                isDivCommand(
                        isMulCommand(
                                isValueCommand(1),
                                isValueCommand(2)),
                        isValueCommand(3)
                ))));
    }

    @Test
    void testMulInt() {
        create("1 * 2;");
        Deque<CommandNode> stack = compiler.stack();
        assertThat(stack, hasSize(1));
        CommandNode command = stack.getLast();
        assertThat(command, isCodeUnit(contains(
                isMulCommand(
                        isValueCommand(1),
                        isValueCommand(2)
                ))));
    }

    @Test
    void testMulIntInt() {
        create("1 * 2 * 3;");
        Deque<CommandNode> stack = compiler.stack();
        assertThat(stack, hasSize(1));
        CommandNode command = stack.getLast();
        assertThat(command, isCodeUnit(contains(
                isMulCommand(
                        isMulCommand(
                                isValueCommand(1),
                                isValueCommand(2)),
                        isValueCommand(3)
                ))));
    }

    @Test
    void testMulIntInt1() {
        create("1 * (2 * 3);");
        Deque<CommandNode> stack = compiler.stack();
        assertThat(stack, hasSize(1));
        CommandNode command = stack.getLast();
        assertThat(command, isCodeUnit(contains(
                isMulCommand(
                        isValueCommand(1),
                        isMulCommand(
                                isValueCommand(2),
                                isValueCommand(3)
                        )))));
    }

    @Test
    void testNegate() {
        create("-1;");
        Deque<CommandNode> stack = compiler.stack();
        assertThat(stack, hasSize(1));
        CommandNode command = stack.getLast();
        assertThat(command, isCodeUnit(contains(
                isNegateCommmand(
                        isValueCommand(1)
                )
        )));
    }

    @Test
    void testSqrt() {
        create("sqrt(1);");
        Deque<CommandNode> stack = compiler.stack();
        assertThat(stack, hasSize(1));
        CommandNode command = stack.getLast();
        assertThat(command, isCodeUnit(contains(
                isFunctionCommand("sqrt",
                        isValueCommand(1))
        )));
    }

    @Test
    void testSubInt() {
        create("1 - 2;");
        Deque<CommandNode> stack = compiler.stack();
        assertThat(stack, hasSize(1));
        CommandNode command = stack.getLast();
        assertThat(command, isCodeUnit(contains(
                isSubCommand(
                        isValueCommand(1),
                        isValueCommand(2)
                ))));
    }

    @Test
    void testVarRefComplex() {
        create("a;");
        Deque<CommandNode> stack = compiler.stack();
        assertThat(stack, hasSize(1));
        CommandNode command = stack.getLast();
        assertThat(command, isCodeUnit(contains(
                isRetrieveVarCommand("a"))
        ));
    }
}