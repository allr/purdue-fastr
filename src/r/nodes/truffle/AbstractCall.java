package r.nodes.truffle;

import r.Truffle.*;
import r.*;
import r.data.*;
import r.errors.*;
import r.nodes.*;

public abstract class AbstractCall extends BaseR {

    protected final RSymbol[] argNames;
    @Children protected final RNode[] argExprs;

    public AbstractCall(ASTNode orig, RSymbol[] argNames, RNode[] argsExprs) {
        super(orig);
        this.argNames = argNames;
        this.argExprs = adoptChildren(argsExprs);
    }

    @Override public void replace0(RNode o, RNode n) {
        replace(argExprs, o, n);
    }

    @Override public String toString() {
        String args = "";
        for (int i = 0; i < argExprs.length; i++) {
            if (argNames != null && argNames[i] != null) {
                args += argNames[i] + "=";
            }
            args += argExprs[i] + ",";
        }
        return this.getClass() + "[" + args + "]";
    }

    protected final int[] computePositions(RSymbol[] paramNames, RSymbol[] usedArgNames) {

        int nArgs = argExprs.length;
        int nParams = paramNames.length;

        boolean[] provided = new boolean[nParams]; // Alloc in stack if the VM is not a shitty one (i.e. trivial escape analysis)

        boolean has3dots = false;
        int[] positions = new int[has3dots ? (nArgs + nParams) : nParams]; // The right size is unknown in presence of ``...'' !

        for (int i = 0; i < nArgs; i++) { // matching by name
            if (argNames[i] != null) {
                for (int j = 0; j < nParams; j++) {
                    if (argNames[i] == paramNames[j]) {
                        usedArgNames[i] = argNames[i];
                        positions[i] = j;
                        provided[j] = true;
                    }
                }
            }
        }

        int nextParam = 0;
        for (int i = 0; i < nArgs; i++) { // matching by position
            if (usedArgNames[i] == null) {
                while (nextParam < nParams && provided[nextParam]) {
                    nextParam++;
                }
                if (nextParam == nParams) {
                    // TODO support ``...''
                    throw RError.getUnusedArgument(ast, argNames[i], argExprs[i]);
                }
                if (argExprs[i] != null) {
                    usedArgNames[i] = paramNames[nextParam]; // This is for now useless but needed for ``...''
                    positions[i] = nextParam;
                    provided[nextParam] = true;
                } else {
                    nextParam++;
                }
            }
        }

        // FIXME T: ??? - what is this? - why more positions than nArgs?
        // FIXME F: answer there may be missing.
        int j = nArgs;
        while (j < nParams) {
            if (!provided[nextParam]) {
                positions[j++] = nextParam;
            }
            nextParam++;
        }

        return positions;
    }

    protected final int[] computePositions(final RFunction func, RSymbol[] usedArgNames) {
        return computePositions(func.paramNames(), usedArgNames);
    }

    /**
     * Displace args provided at the good position in the frame.
     * 
     * @param callerFrame
     *            The frame to evaluate exprs (it's the last argument, since with promises, it should be removed or at
     *            least changed)
     * @param positions
     *            Where arguments need to be displaced (-1 means ``...'')
     * @param names
     *            Names of extra arguments (...).
     */
    @ExplodeLoop protected final Object[] placeArgs(Frame callerFrame, int[] positions, RSymbol[] names, int nparams) {

        Object[] argValues = new Object[nparams];
        int i;
        for (i = 0; i < argExprs.length; i++) {
            int p = positions[i];
            if (p >= 0) {
                RNode v = argExprs[i];
                if (v != null) {
                    argValues[p] = argExprs[i].execute(callerFrame);
                    // FIXME this is wrong ! We have to build a promise at this point and not evaluate
                }
            } else {
                // TODO support ``...''
                // Note that names[i] contains a key if needed
                RContext.warning(argExprs[i].getAST(), "need to be put in ``...'', which is NYI");
            }
        }
        return argValues;
    }
}
