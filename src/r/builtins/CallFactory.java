package r.builtins;

import java.util.*;

import r.*;
import r.data.*;
import r.errors.*;
import r.nodes.*;
import r.nodes.truffle.*;

import java.lang.Integer; // needed because there is a class Integer in this package

/**
 * Parent of functions and operators. The create method is used to create the RNode for a particular call site.
 */
public abstract class CallFactory {

    /** Declared name of function. */
    RSymbol name;
    /** Names of the declared parameters. */
    RSymbol[] parameters;
    /** Names of the required parameters. */
    RSymbol[] required;
    /** Smallest legal number of parameters. */
    int maxParameters;
    /** Largest legal number of parameters. */
    int minParameters;

    CallFactory() {}

    /**
     * @param name
     * @param parameters
     * @param required
     *            array of argument names that are required. If null, same as parameters
     */
    CallFactory(String name, String[] parameters, String[] required) {
        this.name = RSymbol.getSymbol(name);
        this.parameters = RSymbol.getSymbols(parameters);
        this.required = RSymbol.getSymbols(required == null ? parameters : required);
        boolean dotdot = false;
        for (RSymbol p : this.parameters) {
            dotdot |= p == RSymbol.THREE_DOTS_SYMBOL;
        }
        maxParameters = dotdot ? Integer.MAX_VALUE : parameters.length;
        minParameters = required.length;
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
        /** Which formal parameters were provided an actual argument at this call. */
        boolean[] providedParams;
        /** Used??? */
        private int[] argPositions;
        /**
         * For each formal parameter, in order, what is the index of the corresponding actual argument. The value -1
         * indicates that there was no actual to match that formal parameter.
         */
        int[] paramPositions;
        /** Arguments (indexes) that are not matched to one of the function's parameter. */
        ArrayList<Integer> unusedArgs;

        ArgumentInfo(int nArgs, RSymbol[] parameters) {
            this.parameters = parameters;
            int nParams = parameters.length;
            providedParams = new boolean[nParams]; // FIXME: could merge with paramPositions
            argPositions = new int[nArgs];
            paramPositions = new int[nParams];
            for (int i = 0; i < paramPositions.length; i++) {
                paramPositions[i] = -1;
            }
        }

        /** Return the index in the formal parameter list of one particular formal parameter. */
        int ix(RSymbol p) {
            for (int i = 0; i < parameters.length; i++) {
                if (parameters[i] == p) { return i; }
            }
            throw Utils.nyi();
        }

        /** Return the index in the formal parameter list of one particular formal parameter. */
        int ix(String p) {
            return ix(RSymbol.getSymbol(p));
        }

        /** Returns true if an actual was passed for the formal. */
        boolean provided(String name) {
            return paramPositions[ix(name)] == -1;
        }

        /** Returns the position in the actuals of the formal name. */
        int position(String name) {
            return paramPositions[ix(name)];
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
        int nArgs = exprs.length;
        ArgumentInfo a = new ArgumentInfo(nArgs, parameters);
        // Match by name, remember which args are unused.
        outer: for (int i = 0; i < nArgs; i++) {
            if (names[i] == null) continue;
            for (int j = 0; j < parameters.length; j++) {
                if (names[i] == parameters[j]) {
                    a.argPositions[i] = j;
                    a.paramPositions[j] = i;
                    a.providedParams[j] = true;
                    continue outer;
                }
            }
        }
        // Match by partial name, ignore arguments already matched and with no name.
        for (int i = 0; i < nArgs; i++) {
            if (a.paramPositions[i] != -1 || names[i] == null) continue;
            boolean match = false;
            for (int j = 0; j < parameters.length; j++) {
                if (a.providedParams[j]) continue;
                if (parameters[j].name().startsWith(names[i].name())) {
                    if (match) { throw RError.getGenericError(null, "Argument " + i + " matches multiple formal arguments."); }
                    a.argPositions[i] = j;
                    a.paramPositions[j] = i;
                    a.providedParams[j] = true;
                    match = true;
                }
            }
        }
        // Match the remaining arguments by position, taking care of the three dots and of extra arguments.
        int nextP = 0;
        for (int i = 0; i < nArgs; i++) {
            if (a.paramPositions[i] != -1) continue;
            while (nextP < parameters.length && a.providedParams[nextP]) {
                nextP++; // skip params that have been matched already
            }
            if (nextP == parameters.length) { // FIXME: handle passing of ``...'' objects
                if (a.unusedArgs == null) {
                    a.unusedArgs = new ArrayList<>();
                }
                a.unusedArgs.add(i);
            } else {
                if (parameters[nextP] == RSymbol.THREE_DOTS_SYMBOL) {
                    a.argPositions[i] = nextP;
                    a.paramPositions[nextP] = i; // so record the last argument that was taken by ...
                    continue;
                }
                if (exprs[i] != null) { // positional match
                    if (names[i] != null) { throw RError.getGenericError(null, "Unknown parameter " + names[i].pretty() + " passed to " + name()); } // FIXME: better error message                        
                    a.argPositions[i] = nextP;
                    a.paramPositions[nextP] = i;
                    a.providedParams[nextP] = true;
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
        throw Utils.nyi();
    }

    /** Return the index in the formal parameter list of one particular formal parameter. */
    int ix(String p) {
        return ix(RSymbol.getSymbol(p));
    }

    /** Return the name of the function or operation. */
    public RSymbol name() {
        return name;
    }

    /**
     * Verify that the function argument at index was passed into the call, else throw an exception.
     */
    public static void checkArgumentIsPresent(ASTNode call, boolean[] provided, String[] names, int index) {
        if (provided[index]) { return; }
        BuiltIn.missingArg(call, names[index]);
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
}
