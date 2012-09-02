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
        RFrame fframe = new RFrame(tgt.environment(), func);

        // FIXME: now only eager evaluation (no promises)
        // FIXME: now no support for "..."

        RSymbol[] fargs = func.argNames();
        RNode[] fdefs = func.argExprs();
        Utils.check(fargs.length == fdefs.length);

        int j;
        for (j = 0; j < fargs.length; j++) { // FIXME: get rid of this to improve performance
            fframe.localExtra(j, -1);
        }
        // exact matching on tags (names)
        j = 0;
        for (int i = 0; i < names.length; i++) {
            RSymbol tag = names[i];
            if (tag != null) {
                boolean matched = false;
                for (j = 0; j < fargs.length; j++) {
                    RSymbol ftag = fargs[j];
                    if (tag == ftag) {
                        fframe.localExtra(j, i); // remember the index of supplied argument that matches
                        if (DEBUG_MATCHING) Utils.debug("matched formal at index " + j + " by tag " + tag.pretty() + " to supplied argument at index " + i);
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
                      throw new RuntimeException("Error in " + getAST() + " : unused argument(s) (" + expressions[i].getAST() + ")");
                  }
                  if (fframe.localExtra(j) == -1) {
                      fframe.localExtra(j, i); // remember the index of supplied argument that matches
                      if (DEBUG_MATCHING) Utils.debug("matched formal at index " + j + " by position at formal index " + i);
                      j++;
                      break;
                  }
                  j++;
              }
            }
        }
        // providing values for the arguments
        for (j = 0; j < fargs.length; j++) {
            int i = (int) fframe.localExtra(j);
            if (i != -1) {
                RNode argExp = expressions[i];
                if (argExp != null) {
                  fframe.writeAt(j, (RAny) argExp.execute(context, frame)); // FIXME: premature forcing of a promise
                  if (DEBUG_MATCHING) Utils.debug("supplied formal " + fargs[j].pretty() + " with provided value from supplied index " + i);
                  continue;
                }
             // note that an argument may be matched, but still have a null expression
            }
            RNode defExp = fdefs[j];
            if (defExp != null) {
                fframe.writeAt(j, (RAny) defExp.execute(context, fframe)); // FIXME: premature forcing of a promise
                if (DEBUG_MATCHING) Utils.debug("supplied formal " + fargs[j].pretty() + " with default value");
            } else {
                // throw new RuntimeException("Error in " + getAST() + " : '" + fargs[j].pretty() + "' is missing");
                // This is not an error ! This error will be reported iff some code try to access it. (Which sucks a bit but is the behaviour)
            }
        }
        RNode code = func.body();
        Object res = code.execute(context, fframe);
        return res;
    }
}
