package r.nodes.truffle;

import r.*;
import r.data.*;
import r.nodes.*;

public class FunctionCall extends BaseR {

    RNode closureExpr;
    final RSymbol[] names; // arguments of the call (not of the function), in order
    RNode[] expressions;

    private static final boolean DEBUG_MATCHING = false;

    public FunctionCall(ASTNode ast, RNode closureExpr, RSymbol[] argNames, RNode[] argExprs) {
        super(ast);
        this.closureExpr = updateParent(closureExpr);
        this.names = argNames;
        this.expressions = updateParent(argExprs);
    }

    @Override
    public Object execute(RContext context, RFrame frame) {
        RClosure tgt = (RClosure) closureExpr.execute(context, frame);
        RFunction func = tgt.function();

        RFrame fframe = matchParams2(context, func, tgt.environment());

        RNode code = func.body();
        Object res = code.execute(context, fframe);
        return res;
    }

    private int[] computePositions(final RContext context, final RFunction func, RSymbol[] names) {
        RNode[] arguments = this.expressions;
        RSymbol[] argsNames = this.names;

        RSymbol[] defaultsNames = func.argNames();
        RNode[] defaults = func.argExprs();

        int nbArgs = arguments.length;
        int nbFormals = defaults.length;

        boolean[] used = new boolean[nbFormals]; // Alloc in stack if we are lucky !

        boolean has3dots = false;
        int[] positions = new int[has3dots ? (nbArgs + nbFormals) : nbFormals]; // The right size is unknown in presence of ``...'' !

        for (int i = 0; i < nbArgs; i++) {
            if (argsNames[i] != null) {
                for (int j = 0; j < nbFormals; j++) {
                    if (argsNames[i] == defaultsNames[j]) {
                        names[i] = argsNames[i];
                        positions[i] = j;
                        used[j] = true;
                    }
                }
            }
        }

        int nextParam = 0;
        for (int i = 0; i < nbArgs; i++) {
            if (names[i] == null) {
                while (used[nextParam]) {
                    nextParam++;
                    if (nextParam == nbFormals) {
                        // TODO either error or ``...''
                        context.error(getAST(), "unused argument(s) (" + expressions[i].getAST() + ")");
                    }
                }
                names[i] = defaultsNames[nextParam]; // This is for now useless but needed for ``...''
                positions[i] = nextParam;
                used[nextParam] = true;
            }
        }

        for (int i = nextParam, j = nbArgs; j < nbFormals; i++) {
            if (!used[i]) {
                positions[j++] = i;
            }
        }

        return positions;
    }

    private RFrame matchParams2(RContext context, RFunction func, RFrame parentFrame) {
        RFrame fframe = new RFrame(parentFrame, func);

        RNode[] defaults = func.argExprs();
        RSymbol[] names = new RSymbol[expressions.length];

        int[] positions = computePositions(context, func, names);
        displaceArgs(context, fframe, positions, expressions, names, defaults, parentFrame);
        return fframe;
    }

    private RFrame matchParams(RContext context, RFunction func, RFrame parentFrame) {
        RFrame fframe = new RFrame(parentFrame, func);

        // FIXME: now only eager evaluation (no promises)
        // FIXME: now no support for "..."

        RSymbol[] fargs = func.argNames();
        RNode[] fdefs = func.argExprs();
        Utils.check(fargs.length == fdefs.length);

        int j = 0;
        // exact matching on tags (names)
        for (int i = 0; i < names.length; i++) {
            RSymbol tag = names[i];
            if (tag != null) {
                boolean matched = false;
                for (j = 0; j < fargs.length; j++) {
                    RSymbol ftag = fargs[j];
                    if (tag == ftag) {
                        fframe.localExtra(j, i + 1); // remember the index of supplied argument that matches
                        if (DEBUG_MATCHING)
                            Utils.debug("matched formal at index " + j + " by tag " + tag.pretty() + " to supplied argument at index " + i);
                        matched = true;
                        break;
                    }
                }
                if (!matched) {
                    // FIXME: fix error reporting
                    context.warning(getAST(), "unused argument(s) (" + tag.pretty() + ")"); // FIXME move this string in RError
                }
            }
        }
        // FIXME: add partial matching on tags
        // positional matching of remaining arguments
        j = 0;
        for (int i = 0; i < names.length; i++) {
            RSymbol tag = names[i];
            if (tag == null) {
                for (;;) {
                    if (j == fargs.length) {
                        // FIXME: fix error reporting
                        context.warning(getAST(), "unused argument(s) (" + expressions[i].getAST() + ")");
                    }
                    if (fframe.localExtra(j) == 0) {
                        fframe.localExtra(j, i + 1); // remember the index of supplied argument that matches
                        if (DEBUG_MATCHING)
                            Utils.debug("matched formal at index " + j + " by position at formal index " + i);
                        j++;
                        break;
                    }
                    j++;
                }
            }
        }
        // providing values for the arguments
        for (j = 0; j < fargs.length; j++) {
            int i = (int) fframe.localExtra(j) - 1;
            if (i != -1) {
                RNode argExp = expressions[i];
                if (argExp != null) {
                    fframe.writeAt(j, (RAny) argExp.execute(context, parentFrame)); // FIXME: premature forcing of a promise
                    if (DEBUG_MATCHING)
                        Utils.debug("supplied formal " + fargs[j].pretty() + " with provided value from supplied index " + i);
                    continue;
                }
                // note that an argument may be matched, but still have a null expression
            }
            RNode defExp = fdefs[j];
            if (defExp != null) {
                fframe.writeAt(j, (RAny) defExp.execute(context, fframe)); // FIXME: premature forcing of a promise
                if (DEBUG_MATCHING)
                    Utils.debug("supplied `      " + fargs[j].pretty() + " with default value");
            } else {
                // throw new RuntimeException("Error in " + getAST() + " : '" + fargs[j].pretty() + "' is missing");
                // This is not an error ! This error will be reported iff some code try to access it. (Which sucks a bit but is the behaviour)
            }
        }

        return fframe;
    }

    /**
     * Displace args provided at the good position in the frame.
     *
     * @param context The global context (needed for warning ... and for know for evaluate)
     * @param frame The frame to populate (not the one for evaluate expressions, cf parentFrame)
     * @param positions Where arguments need to be displaced (-1 means ``...'')
     * @param args Arguments provided to this calls
     * @param names Names of extra arguments (...).
     * @param fdefs Defaults values for unprovided parameters.
     * @param parentFrame The frame to evaluate exprs (it's the last argument, since with promises, it should be removed or at least changed)
     *
     *            futureparam 3dotsposition where ... as to be put
     */
    private void displaceArgs(RContext context, RFrame frame, int[] positions, RNode[] args, RSymbol[] names, RNode[] fdefs, RFrame parentFrame) {
        int i;
        int argsGiven = args.length;
        int dfltsArgs = positions.length;

        for (i = 0; i < argsGiven; i++) {
            int p = positions[i];
            if (p >= 0) {
                frame.writeAt(p, (RAny) args[i].execute(context, parentFrame)); // FIXME this is wrong ! We have to build a promise at this point and not evaluate
                // FIXME and it's even worst since it's not the good frame at all !
            } else {
                // TODO add to ``...''
                // Note that names[i] contains a key if needed
                context.warning(args[i].getAST(), "need to be put in ``...'', which is NYI");
            }
        }

        for (; i < dfltsArgs; i++) { // For now we populate frames with prom/value.
            // I'm not found of this, there should be a way to only create/evaluate when needed.
            // Thus there could be a bug if a default values depends on another
            RNode v = fdefs[positions[i]];
            if (v != null) { // TODO insert special value for missing
                frame.writeAt(positions[i], (RAny) fdefs[positions[i]].execute(context, frame));
            }
        }
    }
}
