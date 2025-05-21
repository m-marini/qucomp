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
import org.mmarini.qucomp.apis.Complex;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.mmarini.Matchers.command;
import static org.mmarini.Matchers.pushCommand;

class CompilerTest {
    List<Command> code;

    private void compile(String text) throws Throwable {
        code = Compiler.compile(text);
    }

    @Test
    void test1() throws Throwable {
        String text = "1;";
        compile(text);
        assertThat(code, contains(
                pushCommand(1),
                command(Command.Consume.class)
        ));
    }

    @Test
    void test2() throws Throwable {
        String text = "let a=1;a+i;";
        compile(text);
        assertThat(code, contains(
                pushCommand("a"),
                pushCommand(1),
                command(Command.Assign.class),
                command(Command.Consume.class),
                pushCommand("a"),
                command(Command.RetrieveVar.class),
                pushCommand(Complex.i(), 1e-3f),
                command(Command.Add.class),
                command(Command.Consume.class)
        ));
    }
}