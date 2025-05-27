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

import org.mmarini.Function2Throws;
import org.mmarini.qucomp.apis.Bra;
import org.mmarini.qucomp.apis.Complex;
import org.mmarini.qucomp.apis.Ket;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.lang.Math.abs;

/**
 * Implements the execution context handling the variable dictionary
 */
public class Processor implements ExecutionContext {

    /**
     * The implemented function definitions
     */
    public static final Map<String, FunctionDef> FUNCTION_BY_ID = Stream.of(
            new FunctionDef("sqrt", 1, Processor::sqrt)).collect(Collectors.toMap(
            FunctionDef::id, f -> f
    ));

    /**
     * Return the sum of the two operands
     *
     * @param context the command
     * @param left    the left operand
     * @param right   the right operand
     */
    private static Object add(SourceContext context, int left, Object right) throws QuExecException {
        return switch (right) {
            case Integer value -> left + value;
            case Complex value -> Complex.create(left).add(value);
            case Ket value -> throw context.execException("Unexpected right argument ket (%s)", value);
            case Bra value -> throw context.execException("Unexpected right argument bra (%s)", value);
            default -> throw context.execException("Invalid right operand %s", right);
        };
    }

    /**
     * Return the sum of the two operands
     *
     * @param context the source context
     * @param left    the left operand
     * @param right   the right operand
     */
    private static Object add(SourceContext context, Complex left, Object right) throws QuExecException {
        return switch (right) {
            case Integer value -> left.add(value);
            case Complex value -> left.add(value);
            case Ket value -> throw context.execException("Unexpected right argument ket (%s)", value);
            case Bra value -> throw context.execException("Unexpected right argument bra (%s)", value);
            default -> throw context.execException("Invalid right operand (%s)", right);
        };
    }

    /**
     * Return the sum of the two operands
     *
     * @param context the command
     * @param left    the left operand
     * @param right   the right operand
     */
    private static Object add(SourceContext context, Ket left, Object right) throws QuExecException {
        return switch (right) {
            case Integer value -> throw context.execException("Unexpected right argument integer (%s)", value);
            case Complex value -> throw context.execException("Unexpected right argument complex (%s)", value);
            case Ket value -> left.extend(value.values().length)
                    .add(value.extend(left.values().length));
            case Bra value -> throw context.execException("Unexpected right argument bra (%s)", value);
            default -> throw context.execException("Unexpected right argument %s", right);
        };
    }

    /**
     * Return the sum of the two operands
     *
     * @param context the source context
     * @param left    the left operand
     * @param right   the right operand
     */
    private static Object add(SourceContext context, Bra left, Object right) throws QuExecException {
        return switch (right) {
            case Integer value -> throw context.execException("Unexpected right argument integer (%s)", value);
            case Complex value -> throw context.execException("Unexpected right argument complex (%s)", value);
            case Ket value -> throw context.execException("Unexpected right argument ket (%s)", value);
            case Bra value -> left.extend(value.values().length)
                    .add(value.extend(left.values().length));
            default -> throw context.execException("Invalid right operand %s", right);
        };
    }

    /**
     * Return the cross product of the two operands
     *
     * @param context the source context
     * @param left    the left operand
     * @param right   the right operand
     */
    private static Object cross(SourceContext context, Ket left, Object right) throws QuExecException {
        return switch (right) {
            case Integer ignored -> throw context.execException("Unexpected right argument integer (%s)", right);
            case Complex ignored -> throw context.execException("Unexpected right argument complex (%s)", right);
            case Ket value -> left.cross(value);
            case Bra ignored -> throw context.execException("Unexpected right argument bra (%s)", right);
            default -> throw context.execException("Invalid right operand %s", right);
        };
    }

    /**
     * Return the cross product of the two operands
     *
     * @param context the source context
     * @param left    the left operand
     * @param right   the right operand
     */
    private static Object cross(SourceContext context, Bra left, Object right) throws QuExecException {
        return switch (right) {
            case Integer ignored -> throw context.execException("Unexpected right argument integer (%s)", right);
            case Complex ignored -> throw context.execException("Unexpected right argument complex (%s)", right);
            case Bra value -> left.cross(value);
            case Ket ignored -> throw context.execException("Unexpected right argument ket (%s)", right);
            default -> throw context.execException("Invalid right operand %s", right);
        };
    }

    /**
     * Return the division of the two operands left / right
     *
     * @param context the source context
     * @param left    the left operand
     * @param right   the right operand
     */
    private static Object divide(SourceContext context, Bra left, Object right) throws QuExecException {
        return switch (right) {
            case Integer value -> left.mul(1f / value);
            case Complex value -> left.mul(value.inv());
            case Ket value -> throw context.execException("Unexpected right argument ket (%s)", value);
            case Bra value -> {
                left = left.extend(value.values().length);
                value = value.extend(left.values().length);
                Ket ket = value.conj();
                yield left.mul(ket).mul(value.mul(ket).inv());
            }
            default -> throw context.execException("Invalid right operand %s", right);
        };
    }

    /**
     * Return the division of the two operands left / right
     *
     * @param context the source context
     * @param left    the left operand
     * @param right   the right operand
     */
    private static Object divide(SourceContext context, Ket left, Object right) throws QuExecException {
        return switch (right) {
            case Integer value -> left.mul(1f / value);
            case Complex value -> left.mul(value.inv());
            case Bra value -> throw context.execException("Unexpected right argument bra (%s)", value);
            case Ket value -> throw context.execException("Unexpected right argument ket (%s)", value);
            default -> throw context.execException("Invalid right operand %s", right);
        };
    }

    /**
     * Return the division of the two operands left / right
     *
     * @param context the source context
     * @param left    the left operand
     * @param right   the right operand
     */
    private static Object divide(SourceContext context, int left, Object right) throws QuExecException {
        return switch (right) {
            case Integer value -> left % value == 0
                    ? left / value
                    : Complex.create(left).div(Complex.create(value));
            case Complex value -> Complex.create(left).div(value);
            case Ket value -> {
                Bra bra = value.conj();
                yield bra.mul(bra.mul(value).inv().mul(left));
            }
            case Bra value -> {
                Ket ket = value.conj();
                yield ket.mul(value.mul(ket).inv().mul(left));
            }
            default -> throw context.execException("Invalid right operand %s", right);
        };
    }

    /**
     * Return the division of the two operands left / right
     *
     * @param context the source context
     * @param left    the left operand
     * @param right   the right operand
     */
    private static Object divide(SourceContext context, Complex left, Object right) throws QuExecException {
        return switch (right) {
            case Integer value -> left.div(Complex.create(value));
            case Complex value -> left.div(value);
            case Ket value -> {
                Bra bra = value.conj();
                yield bra.mul(bra.mul(value).inv().mul(left));
            }
            case Bra value -> {
                Ket ket = value.conj();
                yield ket.mul(value.mul(ket).inv().mul(left));
            }
            default -> throw context.execException("Invalid right operand %s", right);
        };
    }

    /**
     * Return the product of the two operands left / right
     *
     * @param context the source context
     * @param left    the left operand
     * @param right   the right operand
     */
    private static Object multiply(SourceContext context, int left, Object right) throws QuExecException {
        return switch (right) {
            case Integer value -> left * value;
            case Complex value -> value.mul(left);
            case Ket value -> value.mul(left);
            case Bra value -> value.mul(left);
            default -> throw context.execException("Invalid right operand %s", right);
        };
    }

    /**
     * Return the product of the two operands left / right
     *
     * @param context the command
     * @param left    the left operand
     * @param right   the right operand
     */
    private static Object multiply(SourceContext context, Bra left, Object right) throws QuExecException {
        return switch (right) {
            case Integer value -> left.mul(value);
            case Complex value -> left.mul(value);
            case Ket value -> left.extend(value.values().length)
                    .mul(value.extend(left.values().length));
            case Bra value -> throw context.execException("Unexpected right argument bra (%s)", value);
            default -> throw context.execException("Invalid right operand (%s)", right);
        };
    }

    /**
     * Return the product of the two operands left / right
     *
     * @param context the source context
     * @param left    the left operand
     * @param right   the right operand
     */
    private static Object multiply(SourceContext context, Ket left, Object right) throws QuExecException {
        return switch (right) {
            case Integer value -> left.mul(value);
            case Complex value -> left.mul(value);
            case Ket value -> throw context.execException("Unexpected right argument ket (%s)", value);
            case Bra value -> throw context.execException("Unexpected right argument bra (%s)", value);
            default -> throw context.execException("Invalid right operand (%s)", right);
        };
    }

    /**
     * Return the product of the two operands left / right
     *
     * @param context the source context
     * @param left    the left operand
     * @param right   the right operand
     */
    private static Object multiply(SourceContext context, Complex left, Object right) throws QuExecException {
        return switch (right) {
            case Integer value -> left.mul(value);
            case Complex value -> value.mul(left);
            case Ket value -> value.mul(left);
            case Bra value -> value.mul(left);
            default -> throw context.execException("Invalid right operand %s", right);
        };
    }

    /**
     * Return the square root of the first argument
     *
     * @param context the source context
     * @param args    the arguments
     */
    private static Object sqrt(SourceContext context, Object... args) throws QuExecException {
        Object value = args[0];
        return switch (value) {
            case null -> throw context.execException("Missing argument value");
            case Integer val -> Complex.create((float) Math.sqrt(val));
            case Complex val when abs(val.module()) == 0 -> Complex.zero();
            case Complex val when val.real() >= 0 -> {
                float reDelta = val.real() + val.module();
                float re = (float) Math.sqrt(reDelta / 2);
                float im = (float) (val.im() / Math.sqrt(2 * reDelta));
                yield new Complex(re, im);
            }
            case Complex val -> {
                float reDelta = -val.real() + val.module();
                float re = (float) (val.im() / Math.sqrt(2 * reDelta));
                float im = (float) Math.sqrt(reDelta / 2);
                yield new Complex(re, im);
            }
            case Bra val -> throw context.execException("Unexpected argument bra (%s)", val);
            case Ket val -> throw context.execException("Unexpected argument ket (%s)", val);
            default -> throw context.execException("Unexpected argument (%s)", value);
        };
    }

    /**
     * Return the difference of the two operands left - right
     *
     * @param context the source context
     * @param left    the left operand
     * @param right   the right operand
     */
    private static Object sub(SourceContext context, Bra left, Object right) throws QuExecException {
        return switch (right) {
            case Integer value -> throw context.execException("Unexpected right argument int (%s)", value);
            case Complex value -> throw context.execException("Unexpected right argument complex (%s)", value);
            case Ket value -> throw context.execException("Unexpected right argument ket (%s)", value);
            case Bra value -> left.extend(value.values().length)
                    .sub(value.extend(left.values().length));
            default -> throw context.execException("Invalid right operand %s", right);
        };
    }

    /**
     * Return the difference of the two operands left - right
     *
     * @param context the source context
     * @param left    the left operand
     * @param right   the right operand
     */
    private static Object sub(SourceContext context, Complex left, Object right) throws QuExecException {
        return switch (right) {
            case Integer value -> left.sub(value);
            case Complex value -> left.sub(value);
            case Ket value -> throw context.execException("Unexpected right argument ket (%s)", value);
            case Bra value -> throw context.execException("Unexpected right argument bra (%s)", value);
            default -> throw context.execException("Invalid right operand %s", right);
        };
    }

    /**
     * Return the difference of the two operands left - right
     *
     * @param context the command
     * @param left    the left operand
     * @param right   the right operand
     */
    private static Object sub(SourceContext context, Ket left, Object right) throws QuExecException {
        return switch (right) {
            case Integer value -> throw context.execException("Unexpected right argument int (%s)", value);
            case Complex value -> throw context.execException("Unexpected right argument complex (%s)", value);
            case Ket value -> left.extend(value.values().length)
                    .sub(value.extend(left.values().length));
            case Bra value -> throw context.execException("Unexpected right argument bra (%s)", value);
            default -> throw context.execException("Invalid right operand %s", right);
        };
    }

    /**
     * Return the difference of the two operands left - right
     *
     * @param context the command
     * @param left    the left operand
     * @param right   the right operand
     */
    private static Object sub(SourceContext context, int left, Object right) throws QuExecException {
        return switch (right) {
            case Integer value -> left - value;
            case Complex value -> Complex.create(left).sub(value);
            case Ket value -> throw context.execException("Unexpected right argument ket (%s)", value);
            case Bra value -> throw context.execException("Unexpected right argument bra (%s)", value);
            default -> throw context.execException("Invalid right operand %s", right);
        };
    }

    private final Map<String, Object> variables;

    /**
     * Creates the processor
     *
     */
    public Processor() {
        this.variables = new HashMap<>();
    }

    @Override
    public Object add(SourceContext context, Object left, Object right) throws QuExecException {
        return switch (left) {
            case Integer value -> add(context, (int) value, right);
            case Complex value -> add(context, value, right);
            case Ket value -> add(context, value, right);
            case Bra value -> add(context, value, right);
            default -> throw context.execException("Invalid left operand %s", left);
        };
    }

    @Override
    public Object assign(SourceContext context, String id, Object value) throws QuExecException {
        if (value == null) {
            throw context.execException("Missing value");
        }
        variables.put(id, value);
        return value;
    }

    @Override
    public Object clear(SourceContext context) {
        variables.clear();
        return null;
    }

    @Override
    public Object conj(SourceContext context, Object arg) throws QuExecException {
        return switch (arg) {
            case null -> throw context.execException("Missing value");
            case Integer val -> val;
            case Complex val -> val.conj();
            case Ket val -> val.conj();
            case Bra val -> val.conj();
            default -> throw context.execException("Unexpected value: " + arg);
        };
    }

    @Override
    public Object cross(SourceContext context, Object left, Object right) throws QuExecException {
        return switch (left) {
            case Integer ignored -> throw context.execException("Unexpected left argument integer (%s)", left);
            case Complex ignored -> throw context.execException("Unexpected left argument complex (%s)", left);
            case Ket leftVal -> cross(context, leftVal, right);
            case Bra leftVal -> cross(context, leftVal, right);
            default -> throw context.execException("Invalid left operand %s", left);
        };
    }

    @Override
    public Object div(SourceContext context, Object left, Object right) throws QuExecException {
        return switch (left) {
            case Integer value -> divide(context, value, right);
            case Complex value -> divide(context, value, right);
            case Ket value -> divide(context, value, right);
            case Bra value -> divide(context, value, right);
            default -> throw context.execException("Invalid left operand %s", left);
        };
    }

    @Override
    public Object function(SourceContext context, String id, Object[] args) throws QuExecException {
        Function2Throws<SourceContext, Object[], ? super Object, QuExecException> func = Optional.ofNullable(FUNCTION_BY_ID.get(id))
                .map(FunctionDef::function)
                .orElse(null);
        if (func == null) {
            throw context.execException("Unknown function " + id);
        }
        return func.apply(context, args);
    }

    @Override
    public Object intToKet(SourceContext context, Object state) throws QuExecException {
        return switch (state) {
            case null -> throw context.execException("Missing value");
            case Integer st -> Ket.base(st);
            default -> throw context.execException("Expected integer value: (" + state + ")");
        };
    }

    @Override
    public Object mul(SourceContext context, Object left, Object right) throws QuExecException {
        return switch (left) {
            case Integer value -> multiply(context, value, right);
            case Complex value -> multiply(context, value, right);
            case Ket value -> multiply(context, value, right);
            case Bra value -> multiply(context, value, right);
            default -> throw context.execException("Invalid left operand %s", left);
        };
    }

    @Override
    public Object negate(SourceContext context, Object arg) throws QuExecException {
        return switch (arg) {
            case null -> context.execException("Missing value");
            case Integer val -> -val;
            case Complex val -> val.neg();
            case Ket val -> val.neg();
            case Bra val -> val.neg();
            default -> throw context.execException("Unexpected value: " + arg);
        };
    }

    /**
     * Executes the code generated by compiler
     */
    @Override
    public Object retrieveVar(SourceContext context, String id) throws QuExecException {
        Object value = variables.get(id);
        if (value == null) {
            throw context.execException("Undefined variable " + id);
        }
        return value;
    }

    @Override
    public Object sub(SourceContext context, Object left, Object right) throws QuExecException {
        return switch (left) {
            case Integer value -> sub(context, (int) value, right);
            case Complex value -> sub(context, value, right);
            case Ket value -> sub(context, value, right);
            case Bra value -> sub(context, value, right);
            default -> throw context.execException("Invalid left operand %s", left);
        };
    }

    /**
     * Returns the variable dictionary
     */
    public Map<String, Object> variables() {
        return variables;
    }

    /**
     * Stores the definition of function
     *
     * @param id       the function identifier
     * @param numArgs  the number of required arguments
     * @param function the function
     */
    public record FunctionDef(String id, int numArgs,
                              Function2Throws<SourceContext, Object[], ? super Object, QuExecException> function) {

    }

}
