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

import io.reactivex.functions.BiConsumer;
import org.mmarini.qucomp.apis.Complex;
import org.mmarini.qucomp.apis.Ket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

/**
 * Defines terminal expression grammars
 */
public abstract class TerminalExp extends Expression {
    private static final Logger logger = LoggerFactory.getLogger(TerminalExp.class);
    /**
     * Pushes the state ket associated to the integer state literal
     * <pre>
     * &lt;state-int-literal> ::= &lt;integer-token> | ""
     * </pre>
     */
    public static final TerminalExp optStateIntLiteral = new TerminalExp("<state-int-literal>") {
        @Override
        public boolean test(ParseContext context) throws Throwable {
            Token token = context.currentToken();
            logger.atDebug().log("{} entry token=\"{}\"", this, token);
            boolean result = false;
            if (token instanceof Token.IntegerToken) {
                context.popToken();
                String stateId = token.token();
                // Decode state
                int nunBits = stateId.length();
                int state = 0;
                for (int i = 0; i < nunBits; i++) {
                    state <<= 1;
                    if (stateId.charAt(i) == '1') {
                        state++;
                    } else if (stateId.charAt(i) != '0') {
                        throw token.context().exception("State must contain only 0 or 1 digits");
                    }
                }
                context.add(new Command.PushKet(token.context(), Ket.base(state, nunBits)));
                result = true;
            }
            logger.atDebug().log("{} exit={}", this, result);
            return result;
        }
    };
    /**
     * Pushes imaginary unit in the stacks
     * <pre>
     * &lt;opt-im-literal> ::= "i" | ""
     * </pre>
     */
    public static final TerminalExp optILiteral = new TerminalExp("<opt-im-literal>") {
        @Override
        public boolean test(ParseContext context) throws Throwable {
            // <imaginary-literal>::= "i" || ""
            Token tok = context.currentToken();
            logger.atDebug().log("{} entry token=\"{}\"", this, tok);
            boolean result = false;
            if (tok instanceof Token.IdentifierToken && tok.token().equals("i")) {
                logger.atDebug().log("{}  match", this);
                context.popToken();
                context.add(new Command.PushComplex(tok.context(), Complex.i()));
                result = true;
            }
            logger.atDebug().log("{} exit={}", this, result);
            return result;
        }
    };
    public static final TerminalExp optNotEmpty = new TerminalExp("<opt-not-empty>") {
        @Override
        public boolean test(ParseContext context) {
            Token token = context.currentToken();
            logger.atDebug().log("{} entry token=\"{}\"", this, token);
            boolean result = !(token instanceof Token.EOFToken);
            logger.atDebug().log("{}  exit={}", this, result);
            return result;
        }
    };
    /**
     * Push real value in the stacks
     * <pre>
     * &lt;opt-real-literal> ::= <real-token> | ""
     * </pre>
     */
    public static final TerminalExp optRealLiteral = new TerminalExp("<opt-real-literal>") {
        @Override
        public boolean test(ParseContext context) throws Throwable {
            Token token = context.currentToken();
            logger.atDebug().log("{} entry token=\"{}\"", this, token);
            boolean result = false;
            if (token instanceof Token.RealToken rToken) {
                logger.atDebug().log("{}  match", this);
                context.popToken();
                context.add(new Command.PushComplex(token.context(), Complex.create(rToken.value())));
                result = true;
            }
            logger.atDebug().log("{}  exit={}", this, result);
            return result;
        }
    };
    /**
     * Push integer value in the stacks
     * <pre>
     * &lt;opt-real-literal> ::= <integer-token> | ""
     * </pre>
     */
    public static final TerminalExp optIntLiteral = new TerminalExp("<opt-int-literal>") {
        @Override
        public boolean test(ParseContext context) throws Throwable {
            Token token = context.currentToken();
            logger.atDebug().log("{} entry token=\"{}\"", this, token);
            boolean result = false;
            if (token instanceof Token.IntegerToken iToken) {
                logger.atDebug().log("{}  match", this);
                context.popToken();
                context.add(new Command.PushInt(token.context(), iToken.value()));
                result = true;
            }
            logger.atDebug().log("{}  exit={}", this, result);
            return result;
        }
    };
    /**
     * Reserved keywords
     */
    private static final Set<String> RESERVED_KEYWORDS = Set.of(
            "i", // imaginary unit
            "x", // cross operator
            // Gates
            "I", "X", "Y", "Z", "H", "S", "T", "SWAP", "CNOT", "CCNOT",
            // Functions
            "sqrt",
            // Statements
            "let"
    );
    /**
     * Push the variable value by identifier
     */
    public static TerminalExp optVarIdentifier = new TerminalExp("<var-identifier>") {
        @Override
        public boolean test(ParseContext context) throws Throwable {
            Token token = context.currentToken();
            logger.atDebug().log("{} entry token=\"{}\"", this, token);
            boolean result = false;
            if (token instanceof Token.IdentifierToken) {
                if (RESERVED_KEYWORDS.contains(token.token())) {
                    throw token.context().exception("%s is a reserved keyword", token);
                }
                logger.atDebug().log("{}  match", this);
                context.popToken();
                context.add(new Command.PushString(token.context(), token.token()));
                result = true;
            }
            logger.atDebug().log("{} exit={}", this, result);
            return result;
        }
    };

    /**
     * Returns the expression requiring the token
     *
     * @param op the required token
     */
    public static Expression op(String op) {
        return optOp(op).require("Missing token %s", op);
    }

    /**
     * Changes the process context
     *
     * @param id the expression identifier
     * @param op the process context operator
     */
    public static TerminalExp operate(String id, BiConsumer<ParseContext, Token> op) {
        return new TerminalExp(id) {
            @Override
            public boolean test(ParseContext context) throws Throwable {
                Token tok = context.currentToken();
                logger.atDebug().log("{} entry token=\"{}\"", this, context.currentToken());
                op.accept(context, tok);
                logger.atDebug().log("{} exit={}", this, true);
                return true;
            }
        };
    }

    /**
     * Skips the identifier token
     *
     * @param identifier the expected identifier
     */
    public static TerminalExp optIdentifier(String identifier) {
        return new TerminalExp("<opt-identifier-" + identifier + ">") {
            @Override
            public boolean test(ParseContext context) throws Throwable {
                Token token = context.currentToken();
                logger.atDebug().log("{} entry token=\"{}\"", this, token);
                boolean result = false;
                if (token instanceof Token.IdentifierToken && token.token().equals(identifier)) {
                    logger.atDebug().log("{}  match", this);
                    context.popToken();
                    result = true;
                }
                logger.atDebug().log("{} exit={}", this, result);
                return result;
            }
        };
    }

    /**
     * Skips the operator token
     *
     * @param op the expected operator
     */
    public static TerminalExp optOp(String op) {
        return new TerminalExp("<opt-operator-" + op + ">") {
            @Override
            public boolean test(ParseContext context) throws Throwable {
                Token token = context.currentToken();
                logger.atDebug().log("{} entry token=\"{}\"", this, token);
                boolean result = false;
                if (token instanceof Token.OperatorToken && token.token().equals(op)) {
                    logger.atDebug().log("{}  match", this);
                    context.popToken();
                    result = true;
                }
                logger.atDebug().log("{} exit={}", this, result);
                return result;
            }
        };
    }

    /**
     * Creates the terminal expression
     *
     * @param id expression identifier
     */
    protected TerminalExp(String id) {
        super(id);
    }
}
