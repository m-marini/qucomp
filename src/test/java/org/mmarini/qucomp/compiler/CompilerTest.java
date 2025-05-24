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

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.isA;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mmarini.qucomp.Matchers.*;

public class CompilerTest {
    CommandNode code;

    private void compile(String text) throws Throwable {
        code = Compiler.compile(text);
    }

    @Test
    void test1() {
        String text = "1;";
        assertDoesNotThrow(() -> compile(text));
        assertThat(code, isA(CommandNode.CodeUnit.class));
        assertThat(code, containsArgsSize(1));
        assertThat(code, hasArgAt(0, isA(CommandNode.Consume.class)));
    }


    @Test
    void test2() throws Throwable {
        String text = "let a=1;a+i;";
        compile(text);
        assertThat(code, isA(CommandNode.CodeUnit.class));
        CommandNode.CodeUnit cu = (CommandNode.CodeUnit) code;
        assertThat(cu.commands(), contains(
                isA(CommandNode.Assign.class),
                isA(CommandNode.Consume.class)
        ));

        CommandNode.Assign cmd0 = (CommandNode.Assign) cu.commands().getFirst();
        assertThat(cmd0.left(), isValueCommand("a"));
        assertThat(cmd0.right(), isA(CommandNode.Consume.class));


        CommandNode.Consume cmd1 = (CommandNode.Consume) cu.commands().get(1);
    }
}
