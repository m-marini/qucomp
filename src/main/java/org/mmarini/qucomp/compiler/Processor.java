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
import org.mmarini.Tuple2;
import org.mmarini.qucomp.apis.Complex;
import org.mmarini.qucomp.apis.Matrix;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Implements the execution context handling the variable dictionary
 */
public class Processor implements ExecutionContext {

    /**
     * The implemented function definitions
     */
    public static final Map<String, FunctionDef> FUNCTION_BY_ID = Stream.of(
            new FunctionDef("sqrt", 1, Processor::sqrt),
            new FunctionDef("ary", 2, Processor::ary),
            new FunctionDef("sim", 2, Processor::sim),
            new FunctionDef("eps", 2, Processor::eps),
            new FunctionDef("I", 1, Processor::identityGate),
            new FunctionDef("H", 1, Processor::h),
            new FunctionDef("X", 1, Processor::x),
            new FunctionDef("Y", 1, Processor::y),
            new FunctionDef("Z", 1, Processor::z),
            new FunctionDef("S", 1, Processor::s),
            new FunctionDef("T", 1, Processor::t),
            new FunctionDef("SWAP", 2, Processor::swap),
            new FunctionDef("CNOT", 2, Processor::cnot),
            new FunctionDef("CCNOT", 3, Processor::ccnot)
    ).collect(Collectors.toMap(FunctionDef::id, f -> f));

    /**
     * Returns the matrix of CCNOT gate
     *
     * @param context the source context
     * @param args    the arguments
     */
    private static Object ccnot(SourceContext context, Tuple2<Object, SourceContext>[] args) throws QuExecException {
        Tuple2<Object, SourceContext> dataArg = args[0];
        Tuple2<Object, SourceContext> control0Arg = args[1];
        Tuple2<Object, SourceContext> control1Arg = args[2];
        int data = switch (dataArg._1) {
            case null -> throw dataArg._2.execException("Missing argument value");
            case Integer c -> c;
            default -> throw dataArg._2.execException("Argument should be an integer: actual (%s)", dataArg._1);
        };
        int control0 = switch (control0Arg._1) {
            case null -> throw control0Arg._2.execException("Missing argument value");
            case Integer c -> c;
            default -> throw control0Arg._2.execException("Argument should be an integer: actual (%s)", control0Arg._1);
        };
        int control1 = switch (control1Arg._1) {
            case null -> throw control1Arg._2.execException("Missing argument value");
            case Integer c -> c;
            default -> throw control1Arg._2.execException("Argument should be an integer: actual (%s)", control1Arg._1);
        };
        return Matrix.ccnot(data, control0, control1);
    }

    /**
     * Returns the matrix of CNOT gate
     *
     * @param context the source context
     * @param args    the arguments
     */
    private static Object cnot(SourceContext context, Tuple2<Object, SourceContext>[] args) throws QuExecException {
        Tuple2<Object, SourceContext> left = args[0];
        Tuple2<Object, SourceContext> right = args[1];
        int data = switch (left._1) {
            case null -> throw left._2.execException("Missing argument value");
            case Integer c -> c;
            default -> throw left._2.execException("Argument should be an integer: actual (%s)", left._1);
        };
        int control = switch (right._1) {
            case null -> throw right._2.execException("Missing argument value");
            case Integer c -> c;
            default -> throw right._2.execException("Argument should be an integer: actual (%s)", right._1);
        };
        return Matrix.cnot(data, control);
    }

    /**
     * Returns the matrix with all zero elements except the element at(i,j)
     *
     * @param context the source context
     * @param args    the arguments
     */
    private static Object swap(SourceContext context, Tuple2<Object, SourceContext>[] args) throws QuExecException {
        Tuple2<Object, SourceContext> left = args[0];
        Tuple2<Object, SourceContext> right = args[1];
        int b0 = switch (left._1) {
            case null -> throw left._2.execException("Missing argument value");
            case Integer c -> c;
            default -> throw left._2.execException("Argument should be an integer: actual (%s)", left._1);
        };
        int b1 = switch (right._1) {
            case null -> throw right._2.execException("Missing argument value");
            case Integer c -> c;
            default -> throw right._2.execException("Argument should be an integer: actual (%s)", right._1);
        };
        return Matrix.swap(b0, b1);
    }

    /**
     * Returns the matrix with all zero elements except the element at(i,j)
     *
     * @param context the source context
     * @param args    the arguments
     */
    private static Object ary(SourceContext context, Tuple2<Object, SourceContext>[] args) throws QuExecException {
        Tuple2<Object, SourceContext> left = args[0];
        Tuple2<Object, SourceContext> right = args[1];
        int row = switch (left._1) {
            case null -> throw left._2.execException("Missing argument value");
            case Integer c -> c;
            default -> throw left._2.execException("Argument should be an integer: actual (%s)", left._1);
        };
        int col = switch (right._1) {
            case null -> throw right._2.execException("Missing argument value");
            case Integer c -> c;
            default -> throw right._2.execException("Argument should be an integer: actual (%s)", right._1);
        };
        return Matrix.ary(row, col);
    }

    /**
     * Returns the antisymmetric matrix for the element at(i,j)
     *
     * @param context the source context
     * @param args    the arguments
     */
    private static Object eps(SourceContext context, Tuple2<Object, SourceContext>[] args) throws QuExecException {
        Tuple2<Object, SourceContext> left = args[0];
        Tuple2<Object, SourceContext> right = args[1];
        int row = switch (left._1) {
            case null -> throw left._2.execException("Missing argument value");
            case Integer c -> c;
            default -> throw left._2.execException("Argument should be an integer: actual (%s)", left._1);
        };
        int col = switch (right._1) {
            case null -> throw right._2.execException("Missing argument value");
            case Integer c -> c;
            default -> throw right._2.execException("Argument should be an integer: actual (%s)", right._1);
        };
        return Matrix.eps(row, col);
    }

    /**
     * Returns the matrix of H gate for the given qu-bit
     *
     * @param context the source context
     * @param objects the argument
     */
    private static Object h(SourceContext context, Tuple2<Object, SourceContext>[] objects) throws QuExecException {
        Tuple2<Object, SourceContext> arg = objects[0];
        return switch (arg._1) {
            case Integer n -> Matrix.h(n);
            default -> throw arg._2.execException("Argument should be an integer: actual (%s)", arg._1.toString());
        };
    }

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
            case Matrix value -> throw context.execException("Unexpected right argument (%s)", value);
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
            case Matrix value -> throw context.execException("Unexpected right argument (%s)", value);
            default -> throw context.execException("Invalid right operand (%s)", right);
        };
    }

    /**
     * Return the sum of the two operands
     *
     * @param context the source context
     * @param left    the left operand
     * @param right   the right operand
     */
    private static Object add(SourceContext context, Matrix left, Object right) throws QuExecException {
        return switch (right) {
            case Integer value -> throw context.execException("Unexpected right argument integer (%s)", value);
            case Complex value -> throw context.execException("Unexpected right argument complex (%s)", value);
            case Matrix value -> left.add(value);
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
            case Integer ignored -> throw context.execException("Unexpected right argument integer (%s)", right);
            case Complex ignored -> throw context.execException("Unexpected right argument complex (%s)", right);
            case Matrix value -> left.cross(value);
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
            case Integer value -> left.div(value);
            case Complex value -> left.div(value);
            case Matrix value -> throw context.execException("Unexpected right argument (%s)", value);
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
            case Matrix value -> throw context.execException("Unexpected right argument (%s)", value);
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
            case Matrix value -> throw context.execException("Unexpected right argument (%s)", value);
            default -> throw context.execException("Invalid right operand %s", right);
        };
    }

    /**
     * Returns the matrix of identity gate for the given qu-bit
     *
     * @param context the source context
     * @param objects the argument
     */
    private static Object identityGate(SourceContext context, Tuple2<Object, SourceContext>[] objects) throws QuExecException {
        Tuple2<Object, SourceContext> arg = objects[0];
        return switch (arg._1) {
            case Integer n -> Matrix.identity(1 << (n + 1));
            default -> throw arg._2.execException("Argument should be an integer: actual (%s)", arg._1.toString());
        };
    }

    /**
     * Returns the matrix of S gate for the given qu-bit
     *
     * @param context the source context
     * @param objects the argument
     */
    private static Object s(SourceContext context, Tuple2<Object, SourceContext>[] objects) throws QuExecException {
        Tuple2<Object, SourceContext> arg = objects[0];
        return switch (arg._1) {
            case Integer n -> Matrix.s(n);
            default -> throw arg._2.execException("Argument should be an integer: actual (%s)", arg._1.toString());
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
    private static Object multiply(SourceContext context, Matrix left, Object right) throws QuExecException {
        return switch (right) {
            case Integer value -> left.mul(value);
            case Complex value -> left.mul(value);
            case Matrix value -> {
                Matrix result = left.mul(value);
                yield (result.numCols() > 1 || result.numRows() > 1)
                        ? result : result.at(0);
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
    private static Object multiply(SourceContext context, Complex left, Object right) throws QuExecException {
        return switch (right) {
            case Integer value -> left.mul(value);
            case Complex value -> value.mul(left);
            case Matrix value -> value.mul(left);
            default -> throw context.execException("Invalid right operand %s", right);
        };
    }

    /**
     * Returns the matrix with all zero elements except the element at(i,j)
     *
     * @param context the source context
     * @param args    the arguments
     */
    private static Object sim(SourceContext context, Tuple2<Object, SourceContext>[] args) throws QuExecException {
        Tuple2<Object, SourceContext> left = args[0];
        Tuple2<Object, SourceContext> right = args[1];
        int row = switch (left._1) {
            case null -> throw left._2.execException("Missing argument value");
            case Integer c -> c;
            default -> throw left._2.execException("Argument should be an integer: actual (%s)", left._1);
        };
        int col = switch (right._1) {
            case null -> throw right._2.execException("Missing argument value");
            case Integer c -> c;
            default -> throw right._2.execException("Argument should be an integer: actual (%s)", right._1);
        };
        return Matrix.sim(row, col);
    }

    /**
     * Return the square root of the first argument
     *
     * @param context the source context
     * @param args    the arguments
     */
    private static Object sqrt(SourceContext context, Tuple2<Object, SourceContext>[] args) throws QuExecException {
        Tuple2<Object, SourceContext> arg = args[0];
        return switch (arg._1) {
            case null -> throw arg._2.execException("Missing argument value");
            case Integer val -> Complex.create(val).sqrt();
            case Complex val -> val.sqrt();
            case Matrix val -> throw arg._2.execException("Unexpected matrix argument (%s)", val);
            default -> throw arg._2.execException("Unexpected argument (%s)", arg._1);
        };
    }

    /**
     * Return the difference of the two operands left - right
     *
     * @param context the source context
     * @param left    the left operand
     * @param right   the right operand
     */
    private static Object sub(SourceContext context, Matrix left, Object right) throws QuExecException {
        return switch (right) {
            case Integer value -> throw context.execException("Unexpected right argument int (%s)", value);
            case Complex value -> throw context.execException("Unexpected right argument complex (%s)", value);
            case Matrix value -> left.sub(value);
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
            case Matrix value -> throw context.execException("Unexpected right argument (%s)", value);
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
            case Matrix value -> throw context.execException("Unexpected right argument (%s)", value);
            default -> throw context.execException("Invalid right operand %s", right);
        };
    }

    /**
     * Returns the matrix of T gate for the given qu-bit
     *
     * @param context the source context
     * @param objects the argument
     */
    private static Object t(SourceContext context, Tuple2<Object, SourceContext>[] objects) throws QuExecException {
        Tuple2<Object, SourceContext> arg = objects[0];
        return switch (arg._1) {
            case Integer n -> Matrix.t(n);
            default -> throw arg._2.execException("Argument should be an integer: actual (%s)", arg._1.toString());
        };
    }

    /**
     * Returns the matrix of X gate for the given qu-bit
     *
     * @param context the source context
     * @param objects the argument
     */
    private static Object x(SourceContext context, Tuple2<Object, SourceContext>[] objects) throws QuExecException {
        Tuple2<Object, SourceContext> arg = objects[0];
        return switch (arg._1) {
            case Integer n -> Matrix.x(n);
            default -> throw arg._2.execException("Argument should be an integer: actual (%s)", arg._1.toString());
        };
    }

    /**
     * Returns the matrix of Y gate for the given qu-bit
     *
     * @param context the source context
     * @param objects the argument
     */
    private static Object y(SourceContext context, Tuple2<Object, SourceContext>[] objects) throws QuExecException {
        Tuple2<Object, SourceContext> arg = objects[0];
        return switch (arg._1) {
            case Integer n -> Matrix.y(n);
            default -> throw arg._2.execException("Argument should be an integer: actual (%s)", arg._1.toString());
        };
    }

    /**
     * Returns the matrix of Z gate for the given qu-bit
     *
     * @param context the source context
     * @param objects the argument
     */
    private static Object z(SourceContext context, Tuple2<Object, SourceContext>[] objects) throws QuExecException {
        Tuple2<Object, SourceContext> arg = objects[0];
        return switch (arg._1) {
            case Integer n -> Matrix.z(n);
            default -> throw arg._2.execException("Argument should be an integer: actual (%s)", arg._1.toString());
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
        try {
            return switch (left) {
                case Integer value -> add(context, (int) value, right);
                case Complex value -> add(context, value, right);
                case Matrix value -> add(context, value, right);
                default -> throw context.execException("Invalid left operand %s", left);
            };
        } catch (IllegalArgumentException ex) {
            throw context.execException(ex);
        }
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
    public Object cross(SourceContext context, Object left, Object right) throws QuExecException {
        try {
            return switch (left) {
                case Integer ignored -> throw context.execException("Unexpected left argument integer (%s)", left);
                case Complex ignored -> throw context.execException("Unexpected left argument complex (%s)", left);
                case Matrix leftVal -> cross(context, leftVal, right);
                default -> throw context.execException("Invalid left operand %s", left);
            };
        } catch (IllegalArgumentException ex) {
            throw context.execException(ex);
        }
    }

    @Override
    public Object dagger(SourceContext context, Object arg) throws QuExecException {
        return switch (arg) {
            case null -> throw context.execException("Missing value");
            case Integer val -> val;
            case Complex val -> val.conj();
            case Matrix val -> val.dagger();
            default -> throw context.execException("Unexpected value: " + arg);
        };
    }

    @Override
    public Object div(SourceContext context, Object left, Object right) throws QuExecException {
        return switch (left) {
            case Integer value -> divide(context, value, right);
            case Complex value -> divide(context, value, right);
            case Matrix value -> divide(context, value, right);
            default -> throw context.execException("Invalid left operand %s", left);
        };
    }

    @Override
    public Object function(SourceContext context, String id, Tuple2<Object, SourceContext>[] args) throws QuExecException {
        Function2Throws<SourceContext, Tuple2<Object, SourceContext>[], ? super Object, QuExecException> func = Optional.ofNullable(FUNCTION_BY_ID.get(id))
                .map(FunctionDef::function)
                .orElse(null);
        if (func == null) {
            throw context.execException("Unknown function " + id);
        }
        try {
            return func.apply(context, args);
        } catch (IllegalArgumentException ex) {
            throw context.execException(ex);
        }
    }

    @Override
    public Object intToKet(SourceContext context, Object state) throws QuExecException {
        return switch (state) {
            case null -> throw context.execException("Missing value");
            case Integer st -> Matrix.ketBase(st);
            default -> throw context.execException("Expected integer value: (" + state + ")");
        };
    }

    @Override
    public Object mul(SourceContext context, Object left, Object right) throws QuExecException {
        try {
            return switch (left) {
                case Integer value -> multiply(context, value, right);
                case Complex value -> multiply(context, value, right);
                case Matrix value -> multiply(context, value, right);
                default -> throw context.execException("Invalid left operand %s", left);
            };
        } catch (IllegalArgumentException ex) {
            throw context.execException(ex.getMessage());
        }
    }

    @Override
    public Object negate(SourceContext context, Object arg) throws QuExecException {
        return switch (arg) {
            case null -> context.execException("Missing value");
            case Integer val -> -val;
            case Complex val -> val.neg();
            case Matrix val -> val.neg();
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
        try {
            return switch (left) {
                case Integer value -> sub(context, (int) value, right);
                case Complex value -> sub(context, value, right);
                case Matrix value -> sub(context, value, right);
                default -> throw context.execException("Invalid left operand %s", left);
            };
        } catch (IllegalArgumentException ex) {
            throw context.execException(ex);
        }
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
                              Function2Throws<SourceContext, Tuple2<Object, SourceContext>[], ? super Object, QuExecException> function) {

    }

}
