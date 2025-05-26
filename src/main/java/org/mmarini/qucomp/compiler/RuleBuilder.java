package org.mmarini.qucomp.compiler;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import static java.util.Objects.requireNonNull;

/**
 * Builds syntax rule
 */
abstract class RuleBuilder {

    private final String id;

    /**
     * Creates the builder
     *
     * @param id the syntax rule identifier
     */
    protected RuleBuilder(String id) {
        this.id = requireNonNull(id);
    }

    /**
     * Binds the denendant rules id with real syntax rule
     *
     * @param binder the binder that returns the syntax rule by identifier
     */
    void bind(Function<String, SyntaxRule> binder) throws QuException {
    }

    abstract public SyntaxRule build();

    /**
     * Returns the rule identifier
     */
    public String id() {
        return id;
    }

    @Override
    public String toString() {
        return id;
    }

    /**
     * Creates a terminal syntax rule
     */
    public static abstract class TerminalRuleBuilder extends RuleBuilder {
        /**
         * Creates the builder
         *
         * @param id the rule identifier
         */
        protected TerminalRuleBuilder(String id) {
            super(id);
        }
    }

    /**
     * Builds the non-terminal syntax rule defining the dependencies
     */
    public static abstract class NonTerminalRuleBuilder extends RuleBuilder {
        private final String[] depends;

        protected NonTerminalRuleBuilder(String id, String... depends) {
            super(id);
            this.depends = requireNonNull(depends);
        }

        @Override
        void bind(Function<String, SyntaxRule> binder) throws QuException {
            // Finds target rule
            SyntaxRule rule = binder.apply(id());
            if (rule == null) {
                throw new QuException("Missing rule " + this);
            }
            // Checks for missing dependants
            List<String> missing = Arrays.stream(depends)
                    .filter(id -> binder.apply(id) == null)
                    .toList();
            if (!missing.isEmpty()) {
                throw new QuException(rule + " missing dependants "
                        + String.join(", ", missing));
            }
            if (rule instanceof SyntaxRule.NonTerminalRule nonTerm) {
                // binds dependants
                List<SyntaxRule> deps = Arrays.stream(depends).map(binder).toList();
                nonTerm.setRules(deps);
            } else {
                throw new QuException(rule + " is not a non terminal rule");
            }
        }
    }
}
