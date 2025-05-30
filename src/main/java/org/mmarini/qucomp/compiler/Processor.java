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
import org.mmarini.NotImplementedException;
import org.mmarini.qucomp.apis.*;

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
            new FunctionDef("sqrt", 1, Processor::sqrt),
            new FunctionDef("I", 1, Processor::identityGate),
            new FunctionDef("H", 1, Processor::h),
            new FunctionDef("X", 1, Processor::x),
            new FunctionDef("Y", 1, Processor::y),
            new FunctionDef("Z", 1, Processor::z),
            new FunctionDef("S", 1, Processor::s),
            new FunctionDef("T", 1, Processor::t)
    ).collect(Collectors.toMap(FunctionDef::id, f -> f));

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
            case Ket value -> throw context.execException("Invalid right ket argument (%s)", value);
            case Bra value -> throw context.execException("Invalid right bra argument (%s)", value);
            case Matrix ignored -> throw context.execException("Invalid right matrix argument (%s)", right);
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
            case Ket value -> throw context.execException("Invalid right ket argument (%s)", value);
            case Bra value -> throw context.execException("Invalid right bra argument (%s)", value);
            case Matrix ignored -> throw context.execException("Invalid right matrix argument (%s)", right);
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
            case Integer value -> throw context.execException("Invalid right integer argument (%s)", value);
            case Complex value -> throw context.execException("Invalid right complex argument (%s)", value);
            case Ket value -> left.extend(value.values().length)
                    .add(value.extend(left.values().length));
            case Bra value -> throw context.execException("Invalid right bra argument (%s)", value);
            case Matrix ignored -> throw context.execException("Invalid right matrix argument (%s)", right);
            default -> throw context.execException("Invalid right argument %s", right);
        };
    }

    /**
     * Return the sum of the two operands
     *
     * @param context the command
     * @param left    the left operand
     * @param right   the right operand
     */
    private static Object add(SourceContext context, Matrix left, Object right) throws QuExecException {
        return switch (right) {
            case null -> null;
            case Integer value -> throw context.execException("Invalid right integer argument (%s)", value);
            case Complex value -> throw context.execException("Invalid right complex argument (%s)", value);
            case Ket value -> throw context.execException("Invalid right ket argument (%s)", value);
            case Bra value -> throw context.execException("Invalid right bra argument (%s)", value);
            case Matrix value -> throw new NotImplementedException();
            default -> throw context.execException("Invalid right argument %s", right);
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
            case Integer value -> throw context.execException("Invalid right integer argument (%s)", value);
            case Complex value -> throw context.execException("Invalid right complex argument (%s)", value);
            case Bra value -> left.extend(value.values().length)
                    .add(value.extend(left.values().length));
            case Ket value -> throw context.execException("Invalid right ket argument (%s)", value);
            case Matrix ignored -> throw context.execException("Invalid right matrix argument (%s)", right);
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
            case Integer ignored -> throw context.execException("Invalid right integer argument (%s)", right);
            case Complex ignored -> throw context.execException("Invalid right complex argument (%s)", right);
            case Bra ignored -> throw context.execException("Invalid right bra argument (%s)", right);
            case Ket value -> left.cross(value);
            case Matrix ignored -> throw context.execException("Invalid right matrix argument (%s)", right);
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
    private static Object cross(SourceContext context, Matrix left, Object right) throws QuExecException {
        return switch (right) {
            case Integer ignored -> throw context.execException("Invalid right integer argument (%s)", right);
            case Complex ignored -> throw context.execException("Invalid right complex argument (%s)", right);
            case Bra ignored -> throw context.execException("Invalid right bra argument (%s)", right);
            case Ket ignored -> throw context.execException("Invalid right ket argument (%s)", right);
            case Matrix value -> left.cross(value);
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
            case Integer ignored -> throw context.execException("Invalid right integer argument (%s)", right);
            case Complex ignored -> throw context.execException("Invalid right complex argument (%s)", right);
            case Bra value -> left.cross(value);
            case Ket ignored -> throw context.execException("Invalid right ket argument (%s)", right);
            case Matrix ignored -> throw context.execException("Invalid right matrix argument (%s)", right);
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
            case Ket value -> throw context.execException("Invalid right ket argument (%s)", value);
            case Bra value -> {
                left = left.extend(value.values().length);
                value = value.extend(left.values().length);
                Ket ket = value.conj();
                yield left.mul(ket).mul(value.mul(ket).inv());
            }
            case Matrix value -> throw context.execException("Invalid right matrix argument (%s)", value);
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
            case Bra value -> throw context.execException("Invalid right bra argument (%s)", value);
            case Ket value -> throw context.execException("Invalid right ket argument (%s)", value);
            case Matrix value -> throw context.execException("Invalid right matrix argument (%s)", value);
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
    private static Object divide(SourceContext context, Matrix left, Object right) throws QuExecException {
        return switch (right) {
            case Integer value -> left.mul(1f / value);
            case Complex value -> left.mul(value.inv());
            case Bra value -> throw new NotImplementedException();
            case Ket value -> throw context.execException("Invalid right ket argument (%s)", value);
            case Matrix value -> throw context.execException("Invalid right matrix argument (%s)", value);
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
            case Matrix value -> throw context.execException("Invalid right matrix argument (%s)", value);
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
            case Matrix value -> throw context.execException("Invalid right matrix argument (%s)", value);
            default -> throw context.execException("Invalid right operand %s", right);
        };
    }

    /**
     * Returns the matrix of H gate for the given qu-bit
     *
     * @param context the source context
     * @param objects the argument
     */
    private static Object h(SourceContext context, Object[] objects) throws QuExecException {
        Object arg = objects[0];
        return switch (arg) {
            case Integer n -> QuGate.h(n).build(n + 1);
            default -> throw context.execException("Argument should be an integer: actual (%s)", arg.toString());
        };
    }

    /**
     * Returns the matrix of identity gate for the given qu-bit
     *
     * @param context the source context
     * @param objects the argument
     */
    private static Object identityGate(SourceContext context, Object[] objects) throws QuExecException {
        Object arg = objects[0];
        return switch (arg) {
            case Integer n -> Matrix.identity(1 << (n + 1));
            default -> throw context.execException("Argument should be an integer: actual (%s)", arg.toString());
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
            case Bra value -> throw context.execException("Invalid right bra argument (%s)", value);
            case Matrix matrix -> {
                if (left.numStates() > matrix.numRows()) {
                    matrix = promote(matrix, left.numStates(), matrix.numCols());
                }
                if (matrix.numRows() > left.numStates()) {
                    left = left.extend(matrix.numRows());
                }
                yield left.mul(matrix);
            }
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
    private static Object multiply(SourceContext context, int left, Object right) throws QuExecException {
        return switch (right) {
            case Integer value -> left * value;
            case Complex value -> value.mul(left);
            case Ket value -> value.mul(left);
            case Bra value -> value.mul(left);
            case Matrix value -> value.mul(left);
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
    private static Object multiply(SourceContext context, Ket left, Object right) throws QuExecException {
        return switch (right) {
            case Integer value -> left.mul(value);
            case Complex value -> left.mul(value);
            case Ket value -> throw context.execException("Invalid right ket argument (%s)", value);
            case Bra value -> throw context.execException("Invalid right bra argument (%s)", value);
            case Matrix value -> throw context.execException("Invalid right matrix argument (%s)", value);
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
    private static Object multiply(SourceContext context, Matrix left, Object right) throws QuExecException {
        return switch (right) {
            case Integer value -> left.mul(value);
            case Complex value -> left.mul(value);
            case Bra value -> throw context.execException("Invalid right bra argument (%s)", value);
            case Ket ket -> {
                if (left.numCols() > ket.numStates()) {
                    ket = ket.extend(left.numCols());
                }
                if (left.numCols() < ket.numStates()) {
                    left = promote(left, left.numRows(), ket.numStates());
                }
                yield ket.mul(left);
            }
            case Matrix value -> promote(left, value.numRows(), value.numCols())
                    .mul(promote(value, left.numRows(), left.numCols()));
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
            case Matrix value -> value.mul(left);
            default -> throw context.execException("Invalid right operand %s", right);
        };
    }

    /**
     *
     * @param x
     * @param n
     * @param m
     * @return
     */
    private static Matrix promote(Matrix x, int n, int m) {
        if (n > x.numRows()) {
            // Promotes rows
            int nn = n / x.numRows();
            x = Matrix.identity(nn).cross(x);
        }
        if (m > x.numCols()) {
            int nn = m / x.numCols();
            x = Matrix.identity(nn).cross(x);
        }
        return x;
    }

    /**
     * Returns the matrix of S gate for the given qu-bit
     *
     * @param context the source context
     * @param objects the argument
     */
    private static Object s(SourceContext context, Object[] objects) throws QuExecException {
        Object arg = objects[0];
        return switch (arg) {
            case Integer n -> QuGate.s(n).build(n + 1);
            default -> throw context.execException("Argument should be an integer: actual (%s)", arg.toString());
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
            case Bra val -> throw context.execException("Invalid bra argument (%s)", val);
            case Ket val -> throw context.execException("Invalid ket argument (%s)", val);
            default -> throw context.execException("Invalid argument (%s)", value);
        };
    }

    /**
     * Return the sum of the two operands
     *
     * @param context the command
     * @param left    the left operand
     * @param right   the right operand
     */
    private static Object sub(SourceContext context, Matrix left, Object right) throws QuExecException {
        return switch (right) {
            case Integer value -> throw context.execException("Invalid right integer argument (%s)", value);
            case Complex value -> throw context.execException("Invalid right complex argument (%s)", value);
            case Ket value -> throw context.execException("Invalid right ket argument (%s)", value);
            case Bra value -> throw context.execException("Invalid right bra argument (%s)", value);
            case Matrix value -> null;
            default -> throw context.execException("Invalid right argument %s", right);
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
            case Integer value -> throw context.execException("Invalid right argument int (%s)", value);
            case Complex value -> throw context.execException("Invalid right complex argument (%s)", value);
            case Ket value -> throw context.execException("Invalid right ket argument (%s)", value);
            case Bra value -> left.extend(value.values().length)
                    .sub(value.extend(left.values().length));
            case Matrix ignored -> throw context.execException("Invalid right matrix argument (%s)", right);
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
            case Ket value -> throw context.execException("Invalid right ket argument (%s)", value);
            case Bra value -> throw context.execException("Invalid right bra argument (%s)", value);
            case Matrix ignored -> throw context.execException("Invalid right matrix argument (%s)", right);
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
            case Integer value -> throw context.execException("Invalid right argument int (%s)", value);
            case Complex value -> throw context.execException("Invalid right complex argument (%s)", value);
            case Ket value -> left.extend(value.values().length)
                    .sub(value.extend(left.values().length));
            case Bra value -> throw context.execException("Invalid right bra argument (%s)", value);
            case Matrix ignored -> throw context.execException("Invalid right matrix argument (%s)", right);
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
            case Ket value -> throw context.execException("Invalid right ket argument (%s)", value);
            case Bra value -> throw context.execException("Invalid right bra argument (%s)", value);
            case Matrix ignored -> throw context.execException("Invalid right matrix argument (%s)", right);
            default -> throw context.execException("Invalid right operand %s", right);
        };
    }

    /**
     * Returns the matrix of T gate for the given qu-bit
     *
     * @param context the source context
     * @param objects the argument
     */
    private static Object t(SourceContext context, Object[] objects) throws QuExecException {
        Object arg = objects[0];
        return switch (arg) {
            case Integer n -> QuGate.t(n).build(n + 1);
            default -> throw context.execException("Argument should be an integer: actual (%s)", arg.toString());
        };
    }

    /**
     * Returns the matrix of X gate for the given qu-bit
     *
     * @param context the source context
     * @param objects the argument
     */
    private static Object x(SourceContext context, Object[] objects) throws QuExecException {
        Object arg = objects[0];
        return switch (arg) {
            case Integer n -> QuGate.x(n).build(n + 1);
            default -> throw context.execException("Argument should be an integer: actual (%s)", arg.toString());
        };
    }

    /**
     * Returns the matrix of Y gate for the given qu-bit
     *
     * @param context the source context
     * @param objects the argument
     */
    private static Object y(SourceContext context, Object[] objects) throws QuExecException {
        Object arg = objects[0];
        return switch (arg) {
            case Integer n -> QuGate.y(n).build(n + 1);
            default -> throw context.execException("Argument should be an integer: actual (%s)", arg.toString());
        };
    }

    /**
     * Returns the matrix of Z gate for the given qu-bit
     *
     * @param context the source context
     * @param objects the argument
     */
    private static Object z(SourceContext context, Object[] objects) throws QuExecException {
        Object arg = objects[0];
        return switch (arg) {
            case Integer n -> QuGate.z(n).build(n + 1);
            default -> throw context.execException("Argument should be an integer: actual (%s)", arg.toString());
        };
    }
    private final Map<String, Object> variables;

    /**
     * Creates the processor
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
            case Matrix value -> add(context, value, right);
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
            case Matrix val -> val.conj();
            default -> throw context.execException("Invalid value: " + arg);
        };
    }

    @Override
    public Object cross(SourceContext context, Object left, Object right) throws QuExecException {
        return switch (left) {
            case Integer ignored -> throw context.execException("Invalid left integer argument (%s)", left);
            case Complex ignored -> throw context.execException("Invalid left complex argument (%s)", left);
            case Ket leftVal -> cross(context, leftVal, right);
            case Bra leftVal -> cross(context, leftVal, right);
            case Matrix leftVal -> cross(context, leftVal, right);
            default -> throw context.execException("Invalid left operand %s", left);
        };
    }

    @Override
    public Object div(SourceContext context, Object left, Object right) throws QuExecException {
        return switch (left) {
            case Integer value -> divide(context, value, right);
            case Complex value -> divide(context, value, right);
            case Bra value -> divide(context, value, right);
            case Ket value -> divide(context, value, right);
            case Matrix value -> divide(context, value, right);
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
            case Matrix value -> multiply(context, value, right);
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
            case Matrix val -> val.neg();
            default -> throw context.execException("Invalid value: " + arg);
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
            case Matrix value -> sub(context, value, right);
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
