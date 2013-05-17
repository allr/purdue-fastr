package r.builtins;

import java.util.*;

import r.*;
import r.data.*;
import r.errors.*;
import r.nodes.*;
import r.nodes.tools.*;
import r.nodes.truffle.*;

import java.lang.Integer; // needed because there is a class Integer in this package

/**
 * Parent of functions and operators. The create method is used to create the RNode for a particular call site.
 */
public abstract class CallFactory {

    // LICENSE: Some sub-classes include comments that are copy-pasted from GNU R online manual. GNU R is licensed under GPL.

    /** Declared name of function. */
    final RSymbol name;
    /** Names of the declared parameters. */
    private RSymbol[] parameters;
    /** Names of the required parameters. */
    private RSymbol[] required;
    /** Smallest legal number of parameters. */
    final int maxParameters;
    /** Largest legal number of parameters. */
    final int minParameters;

    public CallFactory(String name) {
        this.name = RSymbol.getSymbol(name);
        maxParameters = -1;
        minParameters = -1;
    }

    /**
     * @param name
     *            operation's name
     * @param parameters
     *            name of the parameters, can be empty but not null.
     * @param required
     *            array of argument names that are required. If null, same as parameters
     */
    CallFactory(String name, String[] parameters, String[] required) {
        this.name = RSymbol.getSymbol(name);
        this.parameters = RSymbol.getSymbols(parameters);
        this.required = required == null ? this.parameters : RSymbol.getSymbols(required);
        boolean dotdot = false;
        if (this.required != this.parameters) {
            for (RSymbol r : this.required) {
                boolean match = false;
                for (RSymbol p : this.parameters) {
                    match |= r == p;
                }
                if (!match) { throw Utils.nyi("Internal error in builtin definition for " + name + "required list has extra values"); }
            }
        }
        RSymbol[] minPs = new RSymbol[parameters.length];
        int pos = 0;
        for (RSymbol p : this.parameters) {
            dotdot |= p == RSymbol.THREE_DOTS_SYMBOL;
            if (!dotdot) minPs[pos++] = p;
        }
        int min = 0;
        for (int i = 0; i < pos; i++) {
            for (RSymbol r : this.required) {
                if (minPs[i] == r) {
                    min++;
                    break;
                }
            }
        }
        maxParameters = dotdot ? Integer.MAX_VALUE : parameters.length;
        minParameters = min;
    }

    /**
     * Create a RNode for a call to a function.
     *
     * @param call
     *            the abstract syntax tree node for this function call
     * @param names
     *            the names of the actual arguments (or null)
     * @param exprs
     *            the values of the actual arguments (not null)
     */
    public abstract RNode create(ASTNode call, RSymbol[] names, RNode[] exprs);

    /**
     * Create a RNode for a binary operation.
     *
     * @param call
     *            the abstract syntax tree node for this function call
     * @param left
     *            the left hand side expression
     * @param right
     *            the right hand side expression
     */
    public RNode create(ASTNode call, RNode left, RNode right) {
        return create(call, null, new RNode[]{left, right});
    }

    /** Description of the arguments passed at a call site. */
    static class ArgumentInfo {
        /** Parameter names in order. */
        RSymbol[] parameters;
        /**
         * For each formal parameter, in order, what is the index of the corresponding actual argument. The value -1
         * indicates that there was no actual to match that formal parameter.
         */
        int[] paramPositions;
        /** Arguments (indexes) that are not matched to one of the function's parameter. */
        ArrayList<Integer> unusedArgs;

        ArgumentInfo(RSymbol[] parameters) {
            this.parameters = parameters;
            int nParams = parameters.length;
            paramPositions = new int[nParams];
            for (int i = 0; i < paramPositions.length; i++) {
                paramPositions[i] = -1;
            }
        }

        /** Return the index in the formal parameter list of one particular formal parameter. */
        private int ix(RSymbol p) {
            for (int i = 0; i < parameters.length; i++) {
                if (parameters[i] == p) { return i; }
            }
            throw new Error(p + " not found in " + this);
        }

        /** Return the index in the formal parameter list of one particular formal parameter. */
        private int ix(String p) {
            return ix(RSymbol.getSymbol(p));
        }

        /** Returns true if an actual was passed for the formal. */
        boolean provided(String name) {
            return paramPositions[ix(name)] != -1;
        }

        /** Returns the position in the actuals of the formal name or -1. */
        int position(String name) {
            return paramPositions[ix(name)];
        }

        /** For debugging. */
        @Override public String toString() {
            String res = "[";
            for (int i = 0; i < parameters.length; i++) {
                res += parameters[i] + "=" + paramPositions[i] + ((i == parameters.length - 1) ? "" : ",");
            }
            return res + "]";
        }
    }

    /**
     * Match the formal (parameters) to the actuals (arguments) at a call site. This is done in three passes, first
     * gather all the argument passed by exact name, then get the arguments passed by partial name as long as they are
     * not ambiguous. Last get the positional arguments.
     *
     * @param argNames
     *            array of the names of arguments (or null)
     * @param argExprs
     *            array of the expressions passed as arguments (not null)
     */
    ArgumentInfo resolveArguments(RSymbol[] argNames, RNode[] argExprs, ASTNode ast) {
        ArgumentInfo a = new ArgumentInfo(parameters);
        int nArgs = argExprs.length;
        int nParams = parameters.length;
        boolean[] argUsed = new boolean[nArgs];

        // Match by name, remember which args are unused.
        if (argNames != null) {
            for (int i = 0; i < nArgs; i++) {
                RSymbol argName = argNames[i];
                if (argName == null) {
                    continue;
                }
                for (int j = 0; j < nParams; j++) {
                    if (argName == parameters[j]) {
                        if (a.paramPositions[j] != -1) {
                            throw RError.getFormalMatchedMultiple(ast, argName.name());
                        }
                        // note: we know that parameter names are unique, so no need to check matching more of them by single argument
                        a.paramPositions[j] = i;
                        argUsed[i] = true;
                    }
                }
            }
        }
        // Match by partial name, ignore arguments already matched and with no name.
        if (argNames != null) {
            boolean[] argUsedForPatternMatching = new boolean[nArgs]; // NOTE: could merge with argUsed if need be
            for (int j = 0; j < nParams; j++) {
                if (a.paramPositions[j] != -1) {
                    continue;
                }
                RSymbol paramName = parameters[j];
                if (paramName == RSymbol.THREE_DOTS_SYMBOL) {
                    // only exact matches after ...
                    // NOTE: GNU-R continues in the search, but I don't see why - exact matching would have established such matches already
                    break;
                }

                boolean paramMatched = false;
                for (int i = 0; i < nArgs; i++) {
                    RSymbol argName = argNames[i];
                    if (argName == null || (argUsed[i] && !argUsedForPatternMatching[i])) {
                        continue;
                    }
                    if (paramName.startsWith(argName)) {
                        if (argUsedForPatternMatching[i]) {
                            throw RError.getArgumentMatchesMultiple(ast, i + 1);
                        }
                        if (paramMatched) {
                            throw RError.getFormalMatchedMultiple(ast, paramName.name());
                        }
                        a.paramPositions[j] = i;
                        argUsed[i] = true;
                        argUsedForPatternMatching[i] = true;
                        paramMatched = true;
                    }
                }
            }
        }

        // Match the remaining arguments by position, taking care of the three dots and of extra arguments.
        int j = 0;
        for (int i = 0; i < nArgs; i++) {
            if (argUsed[i]) {
                continue;
            }
            while (j < nParams && a.paramPositions[j] != -1 && parameters[j] != RSymbol.THREE_DOTS_SYMBOL) {
                j++; // skip params that have been matched already, but if the param is ..., don't advance
            }
            if (j == nParams) { // Garbage params...
                // FIXME: do we still need to record these? currently this seems to be unreachable as we are checking the number of args
                if (a.unusedArgs == null) {
                    a.unusedArgs = new ArrayList<>();
                }
                a.unusedArgs.add(i);
            } else {
                if (parameters[j] == RSymbol.THREE_DOTS_SYMBOL) {
                    if (argExprs[i] == null) {
                        throw RError.getArgumentEmpty(ast, i + 1);
                    }
                    a.paramPositions[j] = i; // Record the last argument that was taken by ...
                    continue;
                } else if (argExprs[i] != null) {
                    if (argNames != null && argNames[i] != null) {
                        throw RError.getUnusedArgument(ast, argNames[i], argExprs[i]);
                    }
                    a.paramPositions[j] = i;
                } else  {
                    // NOTE: yes, this can happen - e.g. rnorm(1,,)
                    j++;
                }
            }
        }
        return a;
    }

    /** Return the index in the formal parameter list of one particular formal parameter. */
    int ix(RSymbol p) {
        for (int i = 0; i < parameters.length; i++) {
            if (parameters[i] == p) { return i; }
        }
        throw Utils.nyi(p + " not found  in  " + this);
    }

    /** Return the index in the formal parameter list of one particular formal parameter. */
    int ix(String p) {
        return ix(RSymbol.getSymbol(p));
    }

    /** Return the name of the function or operation. */
    public RSymbol name() {
        return name;
    }

    ArgumentInfo check(ASTNode call, RSymbol[] names, RNode[] exprs) {
        assert Utils.check(detectRepeatedParameters(call));
        ArgumentInfo ai = resolveArguments(names, exprs, call);
        int provided = 0;
        for (int i = 0; i < ai.paramPositions.length; i++) {
            if (ai.paramPositions[i] != -1) {
                provided++;
            }
        }
        if (provided < minParameters) {
            for (int i = 0; i < required.length; i++) {
                if (ai.paramPositions[ix(required[i])] == -1) { throw RError.getArgumentMissing(call, required[i].name()); }
            }
        }
        if (exprs.length < minParameters) { throw RError.getWrongArity(call, name().name(), minParameters, exprs.length); }
        if (ai.unusedArgs != null) { throw RError.getWrongArity(call, name().name(), maxParameters, exprs.length); }
        return ai;
    }

    boolean detectRepeatedParameters(ASTNode ast) {
        int n = parameters.length;
        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                if (parameters[i] == parameters[j]) {
                    Utils.nyi("Repeated formal argument \"" + parameters[i] + "\" in a builtin: " + PrettyPrinter.prettyPrint(ast));
                }
            }
        }
        return true;
    }

    /** Check that the argument provided has the right name else throw an error. */
    static void ensureArgName(ASTNode ast, String expectedName, RSymbol actualName) {
        if (actualName == null) { return; }
        RSymbol expected = RSymbol.getSymbol(expectedName);
        if (actualName != expected) { throw RError.getGenericError(ast, String.format(RError.ARGUMENT_NOT_MATCH, actualName.pretty(), expectedName)); }
    }

    @Override public String toString() {
        String res = "CallFactory[" + name + "(";
        for (RSymbol r : parameters) {
            res += r + " ";
        }
        return res + ")]";
    }

    // TODO: convert this to something smarter, e.g. an automaton
    static class ArgumentMatch {
        final String[] allowed; // must not include NA and must be unique

        public ArgumentMatch(String[] allowed) {
            this.allowed = allowed;
        }

        public int match(RAny arg, ASTNode ast, String argName) {
            if (arg instanceof RNull) {
                return 0; // default value
            }
            if (!(arg instanceof RString)) {
                throw RError.getMustNullOrString(ast, argName);
            }
            RString m = (RString) arg;
            if (m.size() != 1) {
                throw RError.getMustBeScalar(ast, argName); // in GNU-R, this will appear part of match.arg
            }
            String s = m.getString(0);
            if (s == RString.NA) {
                throw RError.getArgOneOf(ast, argName, allowed);
            }
            int match = -1;
            int nmatches = 0;
            for (int i = 0; i < allowed.length; i++) {
                String a = allowed[i];
                if (a.startsWith(s)) {
                    if (a.length() == s.length()) {  // FIXME: does this check pay off?
                        return i;
                    }
                    nmatches++;
                    match = i;
                }
            }
            if (nmatches == 1) {
                return match;
            }
            throw RError.getArgOneOf(ast, argName, allowed);
        }
    }

    // parses a logical argument to a builtin that is not checked for validity and is used in the GNU-R code
    // of a builtin written in R; this can never exactly match the GNU-R semantics
    // perhaps error messages should be made explicit in a spec, instead
    public static boolean parseUncheckedLogical(RAny arg, ASTNode ast) {
        RLogical l = arg.asLogical();
        int size = l.size();
        if (size >= 1) {
            int v = l.getLogical(0);
            if (v == RLogical.NA) {
                throw RError.getUnexpectedNA(ast);
            }
            if (size > 1) {
                RContext.warning(ast, RError.LENGTH_GT_1);
            }
            return (v == RLogical.TRUE);
        }
        throw RError.getUnexpectedNA(ast);
    }

}
