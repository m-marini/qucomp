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

import org.mmarini.NotImplementedException;

import java.io.*;
import java.util.Deque;
import java.util.LinkedList;
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
    static CommandNode compile(BufferedReader reader) throws Throwable {
        Tokenizer tokenizer = new Tokenizer(reader).open();
        Deque<CommandNode> stack = new LinkedList<>();
        ParseContext parseContext = new ParseContext() {

            @Override
            public void join(Token token, SyntaxRule rule) {
                throw new NotImplementedException();
            }

            @Override
            public List<CommandNode> popAllReversed() {
                List<CommandNode> list = stack.stream().toList();
                stack.clear();
                return list;
            }

            @Override
            public CommandNode popCommand() {
                return stack.removeLast();
            }

            @Override
            public void push(CommandNode node) {
                stack.offer(node);
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
        return stack.removeLast();
    }

    /**
     * Parses the reader for syntax rules in the process context
     *
     * @param reader the reader
     */
    static CommandNode compile(Reader reader) throws Throwable {
        return compile(new BufferedReader(reader));
    }

    /**
     * Parses the reader for syntax rules in the process context
     *
     * @param file the source file
     */
    static CommandNode compile(File file) throws Throwable {
        return compile(new FileReader(file));
    }

    /**
     * Parses the reader for syntax rules in the process context
     *
     * @param text the source text
     */
    static CommandNode compile(String text) throws Throwable {
        return compile(new StringReader(text));
    }

}