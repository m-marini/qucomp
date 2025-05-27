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

/**
 * Computes the result of operations
 * The values can be Integer, Complex, Bra, Ket, Object[]: Array of values
 */
public interface ExecutionContext {
    /**
     * Returns the sum of two operands (left + right)
     *
     * @param context the source position of the operation
     * @param left    the left operand
     * @param right   the right operand
     */
    Object add(SourceContext context, Object left, Object right) throws QuExecException;

    /**
     * Assigns the value to a variable
     *
     * @param context the source position of the operation
     * @param id      the variable identifier
     * @param value   the value
     */
    Object assign(SourceContext context, String id, Object value) throws QuExecException;

    /**
     * Clears all the variables
     *
     * @param context the source position of the operation
     * @return null
     */
    Object clear(SourceContext context) throws QuExecException;

    /**
     * Returns the conjugate
     *
     * @param context the source position of the operation
     * @param arg     the argument
     */
    Object conj(SourceContext context, Object arg) throws QuExecException;

    /**
     * Returns the cross-product of the operands (left x right)
     *
     * @param context the source position of the operation
     * @param left    left operand
     * @param right   right operand
     */
    Object cross(SourceContext context, Object left, Object right) throws QuExecException;

    /**
     * Returns the division of two operands (left / right)
     *
     * @param context the source position of the operation
     * @param left    left operand
     * @param right   right operand
     */
    Object div(SourceContext context, Object left, Object right) throws QuExecException;

    /**
     * Returns the value of function
     *
     * @param context the source position of the operation
     * @param id      the function identifier
     * @param args    the arguments
     */
    Object function(SourceContext context, String id, Object[] args) throws QuExecException;

    /**
     * Returns the ket of integer state
     *
     * @param context the source position of the operation
     * @param state   the state
     */
    Object intToKet(SourceContext context, Object state) throws QuExecException;

    /**
     * Returns the product of two operands
     *
     * @param context the source position of the operation
     * @param left    left operand
     * @param right   right operand
     */
    Object mul(SourceContext context, Object left, Object right) throws QuExecException;

    /**
     * Returns the negation of argument
     *
     * @param context the source position of the operation
     * @param arg     the argument
     */
    Object negate(SourceContext context, Object arg) throws QuExecException;

    /**
     * Returns the value of the variable
     *
     * @param context the source position of the operation
     * @param id      the variable identifier
     */
    Object retrieveVar(SourceContext context, String id) throws QuExecException;

    /**
     * Returns the difference of two operands (left - right)
     *
     * @param context the source position of the operation
     * @param left    left operand
     * @param right   right operand
     */
    Object sub(SourceContext context, Object left, Object right) throws QuExecException;
}
