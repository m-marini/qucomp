
# Qu Syntax

```
<code-unit> ::= <statement-List>
<statement-list> ::= <code-unit-opt> <statement-list>
<code-unit-opt> ::= <not-end> | <clear-stm> | <assign-stm> | <exp-stm> | ""

<clear-stm> ::= "clear" "(" ")" ";" | ""

<assign-stm> ::= "let" <assign-var-identifier> "=" <exp> ";" | ""

<exp-stm> ::= <exp> ";" | ""

<exp> ::= <add-exp>

<add-exp> ::= <multiply-exp> <add-tail>
<add-tail> ::= <add-tail-opt> <add-tail>
<add-tail-opt> ::= <plus-tail> | <minus-tail>
<plus-tail> ::= "+" <multiply-exp> | ""
<minus-tail> ::= "-" <multiply-exp> | ""

<multiply-exp> ::= <cross-exp> <mul-tail>
<mul-tail> ::= <mul-tail-opt> <mul-tail>
<mul-tail-opt> ::= <multiply-tail> | <divide-tail>
<multiply-tail> ::= "*" <cross-exp> | ""
<divide-tail> ::= "/" <cross-exp> | ""

<cross-exp> ::= <unary-exp> <cross-tail>
<cross-tail> ::= <cross-tail-opt> <cross-tail> 
<cross-tail-opt> ::= "x" <unary-exp> | ""

<unary-exp> ::= <plus-exp> | <negate-exp> | <conj>
<plus-exp> ::= "+" <unary-exp> | ""
<negate-exp> :.= "-" <unary-exp> | ""

<conj> ::= <primary-exp> <conj-tail>
<conj-tail> ::= "^" <conj-tail> | ""   

<primary-exp> ::= <primary-exp-opt>!

<primary-exp-opt> ::= <priority-exp>
                    | <int-literal>
                    | <real-literal>
                    | <bra>
                    | <ket>
                    | <im-unit>
                    | "pi"
                    | "e"
                    | <function>
                    | <var-identifier>

<priority-exp> :== "(" <exp> ")" | ""

<bra> ::= "<" <state-exp> "|" | ""

<ket> ::= "|" <state-exp> ">" | ""

<im-unit> ::= "i" | ""

<function> ::= <function-id> <args-exp>
<args-exp> ::= "(" <exp> ")"

<state-exp> ::= <state-exp-opt>'
<state-exp-opt> ::= <im-state> | <plus-state> | <minus-state-exp> | <int-state>
<im-state> ::= "i" | ""
<plus-state> ::= "+" | ""
<minus-state-exp> ::=  "-" <minus-state-exp-opt> | ""
<minus-state-exp-opt> ::=  "-" <minus-im-state> | <minus-state>
<minus-im-state> ::=  "i" | ""
<minus-state> ::= ""
<int-state> ::= <exp> 

<function-id> ::= in(FUNCTION_KEYWORDS>
<var-identifier> ::= not-in(KEWORDS)
<assign-var-identifier> ::= not-in(KEWORDS)

FUNCTION_KEYWORDS = ("sqrt")

RESERVED_KEYWORDS = ("i", "e", "pi", "x",
                    "I", "H", "X", "Y", "Z", "S", "T", "SWAP", "CNOT", "CCNOT",
                    "exp", "pow",
                    "sin", "cos", "tan", "asin", "acos", "atan", "arg"
                    "sinh", "cosh", "tanh",
                    "anti", "sim", "cell")

STATEMENT_KEYWORDS = ("clear", "let");

KEYWORDS = FUNCTION_KEWORDS + RESERVED_KEYWORDS + STATEMENT_KEYWORDS
```