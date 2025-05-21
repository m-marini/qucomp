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

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import static org.mmarini.qucomp.compiler.Syntax.codeUnitExp;

/**
 * Parses different sources
 */
public interface Compiler {
    /**
     * Parses the reader for syntax rules in the process context
     *
     * @param reader the reader
     */
    static List<Command> compile(BufferedReader reader) throws Throwable {
        Tokenizer tokenizer = new Tokenizer(reader).open();
        List<Command> result = new ArrayList<>();
        ParseContext parseContext = new ParseContext() {
            @Override
            public void add(Command command) {
                result.add(command);
            }

            @Override
            public Token currentToken() {
                return tokenizer.currentToken();
            }

            @Override
            public void popToken() throws IOException {
                tokenizer.popToken();
            }
        };
        codeUnitExp.test(parseContext);
        return result;
    }

    /**
     * Parses the reader for syntax rules in the process context
     *
     * @param reader the reader
     */
    static List<Command> compile(Reader reader) throws Throwable {
        return compile(new BufferedReader(reader));
    }

    /**
     * Parses the reader for syntax rules in the process context
     *
     * @param file the source file
     */
    static List<Command> compile(File file) throws Throwable {
        return compile(new FileReader(file));
    }

    /**
     * Parses the reader for syntax rules in the process context
     *
     * @param text the source text
     */
    static List<Command> compile(String text) throws Throwable {
        return compile(new StringReader(text));
    }
}