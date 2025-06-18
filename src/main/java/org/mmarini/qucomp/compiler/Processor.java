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
import org.mmarini.qucomp.apis.Complex;
import org.mmarini.qucomp.apis.Matrix;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.mmarini.qucomp.compiler.Operator.binaryOp;
import static org.mmarini.qucomp.compiler.Operator.unaryOp;

/**
 * Implements the execution context handling the variable dictionary
 */
public class Processor implements ExecutionContext {

    private static final Operator.BinaryValueOp ADD_OP = binaryOp
            .mapIntInt((ctx, left, right) -> new Value.IntValue(ctx, left + right))
            .mapIntComplex((ctx, left, right) -> new Value.ComplexValue(ctx, Complex.create(left).add(right)))
            .mapComplexInt((ctx, left, right) -> new Value.ComplexValue(ctx, left.add(right)))
            .mapComplexComplex((ctx, left, right) -> new Value.ComplexValue(ctx, left.add(right)))
            .mapMatrixMatrix((ctx, left, right) -> new Value.MatrixValue(ctx, left.add(right)));
    private static final Operator.BinaryValueOp CROSS_OP = binaryOp
            .mapMatrixMatrix((ctx, left, right) -> new Value.MatrixValue(ctx, left.cross(right)));
    private final static Operator.UnaryValueOp DAGGER_OP = unaryOp
            .mapInt(Value.IntValue::new)
            .mapComplex((ctx, value) -> new Value.ComplexValue(ctx, value.conj()))
            .mapMatrix((ctx, value) -> new Value.MatrixValue(ctx, value.dagger()));
    private static final Operator.UnaryValueOp NEGATE_OP = unaryOp
            .mapInt((ctx, value) -> new Value.IntValue(ctx, -value))
            .mapComplex((ctx, value) -> new Value.ComplexValue(ctx, value.neg()))
            .mapMatrix((ctx, value) -> new Value.MatrixValue(ctx, value.neg()));
    private static final Operator.BinaryValueOp SUB_OP = binaryOp
            .mapIntInt((ctx, left, right) -> new Value.IntValue(ctx, left - right))
            .mapIntComplex((ctx, left, right) -> new Value.ComplexValue(ctx, Complex.create(left).sub(right)))
            .mapComplexInt((ctx, left, right) -> new Value.ComplexValue(ctx, left.sub(right)))
            .mapComplexComplex((ctx, left, right) -> new Value.ComplexValue(ctx, left.sub(right)))
            .mapMatrixMatrix((ctx, left, right) -> new Value.MatrixValue(ctx, left.sub(right)));
    private static final Operator.ChainBinaryValueOp MUL_STAR_OP = binaryOp
            .mapIntInt((ctx, left, right) -> new Value.IntValue(ctx, left * right))
            .mapIntComplex((ctx, left, right) -> new Value.ComplexValue(ctx, right.mul(left)))
            .mapIntMatrix((ctx, left, right) -> new Value.MatrixValue(ctx, right.mul(left)))
            .mapComplexInt((ctx, left, right) -> new Value.ComplexValue(ctx, left.mul(right)))
            .mapComplexComplex((ctx, left, right) -> new Value.ComplexValue(ctx, left.mul(right)))
            .mapComplexMatrix((ctx, left, right) -> new Value.MatrixValue(ctx, right.mul(left)))
            .mapMatrixInt((ctx, left, right) -> new Value.MatrixValue(ctx, left.mul(right)))
            .mapMatrixComplex((ctx, left, right) -> new Value.MatrixValue(ctx, left.mul(right)))
            .mapMatrixMatrix((ctx, left, right) -> {
                Matrix result = left.mul(right);
                return result.numRows() == 1 && result.numCols() == 1
                        ? new Value.ComplexValue(ctx, result.at(0, 0))
                        : new Value.MatrixValue(ctx, result);
            });
    private static final Operator.BinaryValueOp MUL_OP = MUL_STAR_OP
            .mapMatrixMatrix((ctx, left, right) -> {
                Matrix result = left.mul0(right);
                return result.numRows() == 1 && result.numCols() == 1
                        ? new Value.ComplexValue(ctx, result.at(0, 0))
                        : new Value.MatrixValue(ctx, result);
            });
    private static final Operator.BinaryValueOp DIV_OP = binaryOp
            .mapIntInt((ctx, left, right) -> left % right == 0
                    ? new Value.IntValue(ctx, left / right)
                    : new Value.ComplexValue(ctx, Complex.create(left).div(right))
            )
            .mapIntComplex((ctx, left, right) -> new Value.ComplexValue(ctx, right.inv().mul(left)))
            .mapComplexInt((ctx, left, right) -> new Value.ComplexValue(ctx, left.div(right)))
            .mapComplexComplex((ctx, left, right) -> new Value.ComplexValue(ctx, left.div(right)))
            .mapMatrixInt((ctx, left, right) -> new Value.MatrixValue(ctx, left.div(right)))
            .mapMatrixComplex((ctx, left, right) -> new Value.MatrixValue(ctx, left.div(right)));
    private static final Operator.UnaryValueOp INT_2_KET_OP = unaryOp
            .mapInt((ctx, value) -> new Value.MatrixValue(ctx, Matrix.ketBase(value)));
    private static final Operator.UnaryValueOp SQRT_OP = unaryOp
            .mapInt((ctx, value) -> new Value.ComplexValue(ctx, Complex.create(value).sqrt()))
            .mapComplex((ctx, value) -> new Value.ComplexValue(ctx, value.sqrt()));
    private static final Operator.UnaryValueOp I_OP = unaryOp
            .mapInt((ctx, value) -> new Value.MatrixValue(ctx, Matrix.identity(1 << (value + 1))));
    private static final Operator.UnaryValueOp H_OP = unaryOp
            .mapInt((ctx, value) -> new Value.MatrixValue(ctx, Matrix.h(value)));
    private static final Operator.UnaryValueOp S_OP = unaryOp
            .mapInt((ctx, value) -> new Value.MatrixValue(ctx, Matrix.s(value)));
    private static final Operator.UnaryValueOp T_OP = unaryOp
            .mapInt((ctx, value) -> new Value.MatrixValue(ctx, Matrix.t(value)));
    private static final Operator.UnaryValueOp X_OP = unaryOp
            .mapInt((ctx, value) -> new Value.MatrixValue(ctx, Matrix.x(value)));
    private static final Operator.UnaryValueOp Y_OP = unaryOp
            .mapInt((ctx, value) -> new Value.MatrixValue(ctx, Matrix.y(value)));
    private static final Operator.UnaryValueOp Z_OP = unaryOp
            .mapInt((ctx, value) -> new Value.MatrixValue(ctx, Matrix.z(value)));
    private static final Operator.UnaryValueOp NORM_OP = unaryOp
            .mapInt((ctx, value) -> new Value.IntValue(ctx, 1))
            .mapComplex((ctx, value) -> new Value.ComplexValue(ctx, Complex.one()))
            .mapMatrix((ctx, value) -> new Value.MatrixValue(ctx, value.normalise()));
    private static final Operator.BinaryValueOp ARY_OP = binaryOp
            .mapIntInt((ctx, left, right) -> new Value.MatrixValue(ctx, Matrix.ary(left, right)));
    private static final Operator.BinaryValueOp SIM_OP = binaryOp
            .mapIntInt((ctx, left, right) -> new Value.MatrixValue(ctx, Matrix.sim(left, right)));
    private static final Operator.BinaryValueOp EPS_OP = binaryOp
            .mapIntInt((ctx, left, right) -> new Value.MatrixValue(ctx, Matrix.eps(left, right)));
    private static final Operator.BinaryValueOp CNOT_OP = binaryOp
            .mapIntInt((ctx, left, right) -> new Value.MatrixValue(ctx, Matrix.cnot(left, right)));
    private static final Operator.BinaryValueOp SWAP_OP = binaryOp
            .mapIntInt((ctx, left, right) -> new Value.MatrixValue(ctx, Matrix.swap(left, right)));
    private static final Operator.BinaryValueOp QUBIT0_OP = binaryOp
            .mapIntInt((ctx, left, right) -> new Value.MatrixValue(ctx, Matrix.qubit0(left, right)));
    private static final Operator.BinaryValueOp QUBIT1_OP = binaryOp
            .mapIntInt((ctx, left, right) -> new Value.MatrixValue(ctx, Matrix.qubit1(left, right)));
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
            new FunctionDef("CCNOT", 3, Processor::ccnot),
            new FunctionDef("qubit0", 2, Processor::qubit0),
            new FunctionDef("qubit1", 2, Processor::qubit1),
            new FunctionDef("normalise", 1, Processor::normalise)
    ).collect(Collectors.toMap(FunctionDef::id, f -> f));

    /**
     * Returns the matrix with all zero elements except the element at(i,j)
     *
     * @param context the source context
     * @param args    the arguments
     */
    private static Value ary(SourceContext context, Value.ListValue args) throws QuExecException {
        return ARY_OP.apply(context, args.value()[0], args.value()[1]);
    }

    /**
     * Returns the matrix of CCNOT gate
     *
     * @param context the source context
     * @param args    the arguments
     */
    private static Value ccnot(SourceContext context, Value.ListValue args) throws QuExecException {
        Value dataArg = args.value()[0];
        Value control0Arg = args.value()[1];
        Value control1Arg = args.value()[2];
        if (dataArg instanceof Value.IntValue data
                && control0Arg instanceof Value.IntValue ctrl0
                && control1Arg instanceof Value.IntValue ctrl1) {
            return new Value.MatrixValue(context, Matrix.ccnot(data.value(), ctrl0.value(), ctrl1.value()));
        } else {
            throw context.execException("Unexpected %s, %s, %s arguments", dataArg.type(), control0Arg.type(), control1Arg.type());
        }
    }

    /**
     * Returns the matrix of CNOT gate
     *
     * @param context the source context
     * @param args    the arguments
     */
    private static Value cnot(SourceContext context, Value.ListValue args) throws QuExecException {
        return CNOT_OP.apply(context, args.value()[0], args.value()[1]);
    }

    /**
     * Returns the antisymmetric matrix for the element at(i,j)
     *
     * @param context the source context
     * @param args    the arguments
     */
    private static Value eps(SourceContext context, Value.ListValue args) throws QuExecException {
        return EPS_OP.apply(context, args.value()[0], args.value()[1]);
    }

    /**
     * Returns the matrix of H gate for the given qu-bit
     *
     * @param context the source context
     * @param args    the arguments
     */
    private static Value h(SourceContext context, Value.ListValue args) throws QuExecException {
        return H_OP.apply(context, args.value()[0]);
    }

    /**
     * Returns the matrix of identity gate for the given qu-bit
     *
     * @param context the source context
     * @param args    the arguments
     */
    private static Value identityGate(SourceContext context, Value.ListValue args) throws QuExecException {
        return I_OP.apply(context, args.value()[0]);
    }

    /**
     * Return the normalised value
     *
     * @param context the source context
     * @param args    the arguments
     */
    private static Value normalise(SourceContext context, Value.ListValue args) throws QuExecException {
        return NORM_OP.apply(context, args.value()[0]);
    }

    /**
     * Returns the matrix for 0-value qubit projection
     *
     * @param context the source context
     * @param args    the arguments
     */
    private static Value qubit0(SourceContext context, Value.ListValue args) throws QuExecException {
        return QUBIT0_OP.apply(context, args.value()[0], args.value()[1]);
    }

    /**
     * Returns the matrix for 1-value qubit projection
     *
     * @param context the source context
     * @param args    the arguments
     */
    private static Value qubit1(SourceContext context, Value.ListValue args) throws QuExecException {
        return QUBIT1_OP.apply(context, args.value()[0], args.value()[1]);
    }

    /**
     * Returns the matrix of S gate for the given qu-bit
     *
     * @param context the source context
     * @param args    the arguments
     */
    private static Value s(SourceContext context, Value.ListValue args) throws QuExecException {
        return S_OP.apply(context, args.value()[0]);
    }

    /**
     * Returns the matrix with all zero elements except the element at(i,j)
     *
     * @param context the source context
     * @param args    the arguments
     */
    private static Value sim(SourceContext context, Value.ListValue args) throws QuExecException {
        return SIM_OP.apply(context, args.value()[0], args.value()[1]);
    }

    /**
     * Return the square root of the first argument
     *
     * @param context the source context
     * @param args    the arguments
     */
    private static Value sqrt(SourceContext context, Value.ListValue args) throws QuExecException {
        return SQRT_OP.apply(context, args.value()[0]);
    }

    /**
     * Returns the matrix with all zero elements except the element at(i,j)
     *
     * @param context the source context
     * @param args    the arguments
     */
    private static Value swap(SourceContext context, Value.ListValue args) throws QuExecException {
        return SWAP_OP.apply(context, args.value()[0], args.value()[1]);
    }

    /**
     * Returns the matrix of T gate for the given qu-bit
     *
     * @param context the source context
     * @param args    the arguments
     */
    private static Value t(SourceContext context, Value.ListValue args) throws QuExecException {
        return T_OP.apply(context, args.value()[0]);
    }

    /**
     * Returns the matrix of X gate for the given qu-bit
     *
     * @param context the source context
     * @param args    the arguments
     */
    private static Value x(SourceContext context, Value.ListValue args) throws QuExecException {
        return X_OP.apply(context, args.value()[0]);
    }

    /**
     * Returns the matrix of Y gate for the given qu-bit
     *
     * @param context the source context
     * @param args    the arguments
     */
    private static Value y(SourceContext context, Value.ListValue args) throws QuExecException {
        return Y_OP.apply(context, args.value()[0]);
    }

    /**
     * Returns the matrix of Z gate for the given qu-bit
     *
     * @param context the source context
     * @param args    the arguments
     */
    private static Value z(SourceContext context, Value.ListValue args) throws QuExecException {
        return Z_OP.apply(context, args.value()[0]);
    }

    private final Map<String, Value> variables;

    /**
     * Creates the processor
     */
    public Processor() {
        this.variables = new HashMap<>();
    }

    @Override
    public Value add(SourceContext context, Value left, Value right) throws QuExecException {
        try {
            return ADD_OP.apply(context, left, right);
        } catch (IllegalArgumentException ex) {
            throw context.execException(ex);
        }
    }

    @Override
    public Value assign(SourceContext context, String id, Value value) throws QuExecException {
        if (value == null) {
            throw context.execException("Missing value");
        }
        Value result = value.source(context);
        variables.put(id, result);
        return result;
    }

    @Override
    public Value clear(SourceContext context) {
        variables.clear();
        return new Value.IntValue(context, 0);
    }

    @Override
    public Value cross(SourceContext context, Value left, Value right) throws QuExecException {
        try {
            return CROSS_OP.apply(context, left, right);
        } catch (IllegalArgumentException ex) {
            throw context.execException(ex);
        }
    }

    @Override
    public Value dagger(SourceContext context, Value arg) throws QuExecException {
        return DAGGER_OP.apply(context, arg);
    }

    @Override
    public Value div(SourceContext context, Value left, Value right) throws QuExecException {
        return DIV_OP.apply(context, left, right);
    }

    @Override
    public Value function(SourceContext context, String id, Value.ListValue args) throws QuExecException {
        Function2Throws<SourceContext, Value.ListValue, Value, QuExecException> func = Optional.ofNullable(FUNCTION_BY_ID.get(id))
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
    public Value intToKet(SourceContext context, Value state) throws QuExecException {
        return INT_2_KET_OP.apply(context, state);
    }

    @Override
    public Value mul(SourceContext context, Value left, Value right) throws QuExecException {
        try {
            return MUL_STAR_OP.apply(context, left, right);
        } catch (IllegalArgumentException ex) {
            throw context.execException(ex.getMessage());
        }
    }

    @Override
    public Value mul0(SourceContext context, Value left, Value right) throws QuExecException {
        try {
            return MUL_OP.apply(context, left, right);
        } catch (IllegalArgumentException ex) {
            throw context.execException(ex.getMessage());
        }
    }

    @Override
    public Value negate(SourceContext context, Value arg) throws QuExecException {
        return NEGATE_OP.apply(context, arg);
    }

    /**
     * Executes the code generated by compiler
     */
    @Override
    public Value retrieveVar(SourceContext context, String id) throws QuExecException {
        Value value = variables.get(id);
        if (value == null) {
            throw context.execException("Undefined variable " + id);
        }
        return value.source(context);
    }

    @Override
    public Value sub(SourceContext context, Value left, Value right) throws QuExecException {
        try {
            return SUB_OP.apply(context, left, right);
        } catch (IllegalArgumentException ex) {
            throw context.execException(ex);
        }
    }

    /**
     * Returns the variable dictionary
     */
    public Map<String, Value> variables() {
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
                              Function2Throws<SourceContext, Value.ListValue, Value, QuExecException> function) {

    }
}
