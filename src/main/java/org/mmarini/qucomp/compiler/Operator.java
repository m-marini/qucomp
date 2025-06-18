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
import org.mmarini.Function3Throws;
import org.mmarini.qucomp.apis.Complex;
import org.mmarini.qucomp.apis.Matrix;

/**
 * The values operators
 */
public interface Operator {
    /**
     * Generates error for any type of value
     */
    ChainBinaryValueOp binaryOp = new ChainBinaryValueOp(null) {
        @Override
        public Value apply(SourceContext source, Value left, Value right) throws QuExecException {
            throw source.execException("Unexpected %s, %s arguments", left.type(), right.type());
        }
    };

    ChainUnaryValueOp unaryOp = new ChainUnaryValueOp(null) {
        @Override
        public Value apply(SourceContext source, Value value) throws QuExecException {
            throw source.execException("Unexpected %s argument", value.type());
        }
    };

    /**
     * Transforms a single value
     */
    interface UnaryValueOp {
        /**
         * Returns the transformed value
         *
         * @param source the source context
         * @param value  the value
         */
        Value apply(SourceContext source, Value value) throws QuExecException;
    }

    /**
     * Transforms a pair of value
     */
    interface BinaryValueOp {
        /**
         * Returns the transformed value
         *
         * @param source the source context
         * @param left   the left argument
         * @param right  the right argument
         */
        Value apply(SourceContext source, Value left, Value right) throws QuExecException;
    }

    /**
     * Chains the operators and specific type operation
     */
    abstract class ChainUnaryValueOp implements UnaryValueOp {
        protected final UnaryValueOp other;

        /**
         * Creates the chain unary operator
         *
         * @param other the other operator
         */
        protected ChainUnaryValueOp(UnaryValueOp other) {
            this.other = other;
        }

        /**
         * Returns the operator with complex map
         *
         * @param mapper the mapper
         */
        public ChainUnaryValueOp mapComplex(Function2Throws<SourceContext, Complex, Value, QuExecException> mapper) {
            return new ChainUnaryValueOp(this) {
                @Override
                public Value apply(SourceContext source, Value value) throws QuExecException {
                    return value instanceof Value.ComplexValue v
                            ? mapper.apply(source, v.value())
                            : other.apply(source, value);
                }
            };
        }

        /**
         * Returns the operator with integer map
         *
         * @param mapper the mapper
         */
        public ChainUnaryValueOp mapInt(Function2Throws<SourceContext, Integer, Value, QuExecException> mapper) {
            return new ChainUnaryValueOp(this) {
                @Override
                public Value apply(SourceContext source, Value value) throws QuExecException {
                    return value instanceof Value.IntValue v
                            ? mapper.apply(source, v.value())
                            : other.apply(source, value);
                }
            };
        }

        /**
         * Returns the operator with matrix map
         *
         * @param mapper the mapper
         */
        public ChainUnaryValueOp mapMatrix(Function2Throws<SourceContext, Matrix, Value, QuExecException> mapper) {
            return new ChainUnaryValueOp(this) {
                @Override
                public Value apply(SourceContext source, Value value) throws QuExecException {
                    return value instanceof Value.MatrixValue v
                            ? mapper.apply(source, v.value())
                            : other.apply(source, value);
                }
            };
        }
    }

    /**
     * Chains the operators and specific type operation
     */
    abstract class ChainBinaryValueOp implements BinaryValueOp {
        protected final BinaryValueOp other;

        /**
         * Creates the chain unary operator
         *
         * @param other the other operator
         */
        protected ChainBinaryValueOp(BinaryValueOp other) {
            this.other = other;
        }

        /**
         * Returns the operator with (complex, complex) map
         *
         * @param mapper the mapper
         */
        public ChainBinaryValueOp mapComplexComplex(Function3Throws<SourceContext, Complex, Complex, Value, QuExecException> mapper) {
            return new ChainBinaryValueOp(this) {
                @Override
                public Value apply(SourceContext source, Value left, Value right) throws QuExecException {
                    return left instanceof Value.ComplexValue leftV
                            && right instanceof Value.ComplexValue rightV
                            ? mapper.apply(source, leftV.value(), rightV.value())
                            : other.apply(source, left, right);
                }
            };
        }

        /**
         * Returns the operator with (complex, integer) map
         *
         * @param mapper the mapper
         */
        public ChainBinaryValueOp mapComplexInt(Function3Throws<SourceContext, Complex, Integer, Value, QuExecException> mapper) {
            return new ChainBinaryValueOp(this) {
                @Override
                public Value apply(SourceContext source, Value left, Value right) throws QuExecException {
                    return left instanceof Value.ComplexValue leftV
                            && right instanceof Value.IntValue rightV
                            ? mapper.apply(source, leftV.value(), rightV.value())
                            : other.apply(source, left, right);
                }
            };
        }

        /**
         * Returns the operator with (complex, matrix) map
         *
         * @param mapper the mapper
         */
        public ChainBinaryValueOp mapComplexMatrix(Function3Throws<SourceContext, Complex, Matrix, Value, QuExecException> mapper) {
            return new ChainBinaryValueOp(this) {
                @Override
                public Value apply(SourceContext source, Value left, Value right) throws QuExecException {
                    return left instanceof Value.ComplexValue leftV
                            && right instanceof Value.MatrixValue rightV
                            ? mapper.apply(source, leftV.value(), rightV.value())
                            : other.apply(source, left, right);
                }
            };
        }

        /**
         * Returns the operator with (integer, complex) map
         *
         * @param mapper the mapper
         */
        public ChainBinaryValueOp mapIntComplex(Function3Throws<SourceContext, Integer, Complex, Value, QuExecException> mapper) {
            return new ChainBinaryValueOp(this) {
                @Override
                public Value apply(SourceContext source, Value left, Value right) throws QuExecException {
                    return left instanceof Value.IntValue leftV
                            && right instanceof Value.ComplexValue rightV
                            ? mapper.apply(source, leftV.value(), rightV.value())
                            : other.apply(source, left, right);
                }
            };
        }

        /**
         * Returns the operator with (integer, integer) map
         *
         * @param mapper the mapper
         */
        public ChainBinaryValueOp mapIntInt(Function3Throws<SourceContext, Integer, Integer, Value, QuExecException> mapper) {
            return new ChainBinaryValueOp(this) {
                @Override
                public Value apply(SourceContext source, Value left, Value right) throws QuExecException {
                    return left instanceof Value.IntValue leftV
                            && right instanceof Value.IntValue rightV
                            ? mapper.apply(source, leftV.value(), rightV.value())
                            : other.apply(source, left, right);
                }
            };
        }

        /**
         * Returns the operator with (integer, matrix) map
         *
         * @param mapper the mapper
         */
        public ChainBinaryValueOp mapIntMatrix(Function3Throws<SourceContext, Integer, Matrix, Value, QuExecException> mapper) {
            return new ChainBinaryValueOp(this) {
                @Override
                public Value apply(SourceContext source, Value left, Value right) throws QuExecException {
                    return left instanceof Value.IntValue leftV
                            && right instanceof Value.MatrixValue rightV
                            ? mapper.apply(source, leftV.value(), rightV.value())
                            : other.apply(source, left, right);
                }
            };
        }

        /**
         * Returns the operator with (matrix, complex) map
         *
         * @param mapper the mapper
         */
        public ChainBinaryValueOp mapMatrixComplex(Function3Throws<SourceContext, Matrix, Complex, Value, QuExecException> mapper) {
            return new ChainBinaryValueOp(this) {
                @Override
                public Value apply(SourceContext source, Value left, Value right) throws QuExecException {
                    return left instanceof Value.MatrixValue leftV
                            && right instanceof Value.ComplexValue rightV
                            ? mapper.apply(source, leftV.value(), rightV.value())
                            : other.apply(source, left, right);
                }
            };
        }

        /**
         * Returns the operator with (matrix, integer) map
         *
         * @param mapper the mapper
         */
        public ChainBinaryValueOp mapMatrixInt(Function3Throws<SourceContext, Matrix, Integer, Value, QuExecException> mapper) {
            return new ChainBinaryValueOp(this) {
                @Override
                public Value apply(SourceContext source, Value left, Value right) throws QuExecException {
                    return left instanceof Value.MatrixValue leftV
                            && right instanceof Value.IntValue rightV
                            ? mapper.apply(source, leftV.value(), rightV.value())
                            : other.apply(source, left, right);
                }
            };
        }

        /**
         * Returns the operator with (matrix, matrix) map
         *
         * @param mapper the mapper
         */
        public ChainBinaryValueOp mapMatrixMatrix(Function3Throws<SourceContext, Matrix, Matrix, Value, QuExecException> mapper) {
            return new ChainBinaryValueOp(this) {
                @Override
                public Value apply(SourceContext source, Value left, Value right) throws QuExecException {
                    return left instanceof Value.MatrixValue leftV
                            && right instanceof Value.MatrixValue rightV
                            ? mapper.apply(source, leftV.value(), rightV.value())
                            : other.apply(source, left, right);
                }
            };
        }
    }
}
