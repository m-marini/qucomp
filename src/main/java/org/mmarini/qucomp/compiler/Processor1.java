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

import io.reactivex.rxjava3.functions.Consumer;

import java.util.HashMap;
import java.util.Map;

import static java.util.Objects.requireNonNull;

public class Processor1 {
    private final Map<String, Object> variables;
    private final Consumer<Object> consumer;

    /**
     * Creates the processor
     *
     * @param consumer the expression result consumer
     */
    public Processor1(Consumer<Object> consumer) {
        this.consumer = requireNonNull(consumer);
        this.variables = new HashMap<>();
    }

    /**
     * Returns the evaluated string of command
     *
     * @param command the command
     */
    private String evalString(CommandNode command) throws Throwable {
        Object value = evaluate(command);
        if (value instanceof String str) {
            return str;
        }
        throw command.context().exception("Value %s is not a string", value);
    }

    public Object evaluate(CommandNode command) throws Throwable {
        return switch (command) {
            case null -> throw new NullPointerException();
            case CommandNode.Consume cmd -> {
                Object value = evaluate(cmd.arg());
                consumer.accept(value);
                yield value;
            }
            case CommandNode.Value cmd -> cmd.value();
            case CommandNode.Assign cmd -> {
                String id = evalString(cmd.left());
                Object value = evaluate(cmd.right());
                variables.put(id, value);
                yield value;
            }
            case CommandNode.CodeUnit cmd -> {
                Object value = 0;
                for (CommandNode commandNode : cmd.commands()) {
                    value = evaluate(commandNode);
                }
                yield value;
            }
            default -> throw command.context().exception("Unknown command %s", command);
        };
    }

    public Map<String, Object> variables() {
        return variables;
    }
}
