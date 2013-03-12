package r.builtins;

import java.util.*;

import r.*;
import r.data.*;
import r.errors.*;
import r.nodes.*;
import r.nodes.truffle.*;

import java.lang.Integer;// needed because there is a class Integer in this package

/**
 * Parent of functions and operators. The create method is used to create the RNode for a particular call site.
 */
public abstract class CallFactory {

    /** Declared name of function. */
    RSymbol name;
    /** Names of the declared parameters. */
    private RSymbol[] parameters;
    /** Names of the required parameters. */
    private RSymbol[] required;
    /** Smallest legal number of parameters. */
    int maxParameters;
    /** Largest legal number of parameters. */
    int minParameters;

    public CallFactory(String name) {
        this.name = RSymbol.getSymbol(name);
    }

    public CallFactory(RSymbol name) {
        this.name = name;
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
                    dotdot |= p == RSymbol.THREE_DOTS_SYMBOL;
                    match |= r == p;
                }
                if (!match) { throw Utils.nyi("Internal error in builtin definition for " + name + "required list has extra values"); }
            }
        } else {
            for (RSymbol p : this.parameters) {
                dotdot |= p == RSymbol.THREE_DOTS_SYMBOL;
            }
        }
        maxParameters = dotdot ? java.lang.Integer.MAX_VALUE : parameters.length;
        minParameters = this.required.length;
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
            String r = "";
            for (int i = 0; i < parameters.length; i++) {
                if (parameters[i] == p) { return i; }
                r += (p + " != " + parameters[i]);
            }
            throw Utils.nyi(p + " not found in " + this + " " + r);
        }

        /** Return the index in the formal parameter list of one particular formal parameter. */
        private int ix(String p) {
            return ix(RSymbol.getSymbol(p));
        }

        /** Returns true if an actual was passed for the formal. */
        boolean provided(String name) {
            return paramPositions[ix(name)] != -1;
        }

        /** Returns the position in the actuals of the formal name. */
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
     * @param names
     *            array of the names of arguments (or null)
     * @param exprs
     *            array of the expressions passed as arguments (not null)
     */
    ArgumentInfo resolveArguments(RSymbol[] names, RNode[] exprs) {
        ArgumentInfo a = new ArgumentInfo(parameters);
        boolean[] used = new boolean[exprs.length];
        // Match by name, remember which args are unused.
        if (names != null) {
            outer: for (int i = 0; i < exprs.length; i++) {
                if (names[i] == null) continue;
                for (int j = 0; j < parameters.length; j++) {
                    if (names[i] == parameters[j]) {
                        a.paramPositions[j] = i;
                        used[i] = true;
                        continue outer;
                    }
                }
            }
        }
        // Match by partial name, ignore arguments already matched and with no name.
        if (names != null) {
            for (int i = 0; i < parameters.length; i++) {
                if (a.paramPositions[i] != -1) continue;
                boolean match = false;
                for (int j = 0; j < exprs.length; j++) {
                    if (used[j]) continue;
                    if (names[j] == null) continue;
                    if (parameters[i].startsWith(names[j])) {
                        if (match) { throw RError.getGenericError(null, "Argument " + names[j] + " matches multiple formal arguments."); }
                        a.paramPositions[i] = j;
                        match = true;
                    }
                }
            }
        }
        // Match the remaining arguments by position, taking care of the three dots and of extra arguments.
        int nextP = 0;
        for (int i = 0; i < exprs.length; i++) {
            if (used[i]) continue;
            while (nextP < parameters.length && a.paramPositions[nextP] != -1) {
                nextP++; // skip params that have been matched already
            }
            if (nextP == parameters.length) { // FIXME: handle passing of ``...'' objects
                if (a.unusedArgs == null) {
                    a.unusedArgs = new ArrayList<>();
                }
                a.unusedArgs.add(i);
            } else {
                if (parameters[nextP] == RSymbol.THREE_DOTS_SYMBOL) {
                    a.paramPositions[nextP] = i; // so record the last argument that was taken by ...
                    continue;
                } else if (exprs[i] != null) { // positional match
                    if (names != null && names[i] != null) { throw RError.getGenericError(null, "Unknown parameter " + names[i].pretty() + " passed to " + name()); } // FIXME: better error message                        
                    a.paramPositions[nextP] = i;
                } else { // FIXME: JAN asks if this point can ever be reached?
                    nextP++;
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
        ArgumentInfo ai = resolveArguments(names, exprs);
        int provided = 0;
        for (int i = 0; i < ai.paramPositions.length; i++) {
            if (ai.paramPositions[i] != -1) {
                provided++;
            }
        }
        if (provided < minParameters) {
            for (int i = 0; i < required.length; i++) {
                if (ai.paramPositions[ix(required[i])] == -1) { throw RError.getGenericError(call, String.format(RError.ARGUMENT_MISSING, required[i].name())); }
            }
        }
        return ai;
    }

    /** Check that the argument provided has the right name else throw an error. */
    static void ensureArgName(ASTNode ast, String expectedName, RSymbol actualName) {
        if (actualName == null) { return; }
        RSymbol expected = RSymbol.getSymbol(expectedName);
        if (actualName != expected) { throw RError.getGenericError(ast, String.format(RError.ARGUMENT_NOT_MATCH, actualName.pretty(), expectedName)); }
    }

    @Override public String toString() {
        String res = name + "(";
        for (RSymbol r : parameters) {
            res += r + " ";
        }
        return res + ")";
    }
}
