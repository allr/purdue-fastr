package r.builtins;

import com.oracle.truffle.api.frame.*;

import r.*;
import r.builtins.BuiltIn.NamedArgsBuiltIn.*;
import r.data.*;
import r.errors.*;
import r.nodes.*;
import r.nodes.truffle.*;

// note: in GNU-R, this is implemented in R
public class TriangularPart {
    private static final String[] paramNames = new String[]{"x", "diag"};

    private static final int IX = 0;
    private static final int IDIAG = 1;

    public abstract static class Triangular {

        public abstract RArray triangular(int m, int n, boolean diag);

        public RAny triangular(ASTNode ast, RAny argx, boolean diag) {
            if (!(argx instanceof RArray)) {    // FIXME: this could be faster using node rewriting
                Utils.nyi("unsupported (invalid) argument type");
                return null;
            }
            RArray a = (RArray) argx;
            int[] dim = a.dimensions();
            if (dim == null) {
                int m = a.size();
                return triangular(m, 1, diag).setDimensions(new int[] {m, 1});
            }
            if (dim.length == 2) {
                return triangular(dim[0], dim[1], diag).setDimensions(dim);
            }
            Utils.nyi("unsupported case");
            return null;
        }

        public RAny triangular(ASTNode ast, RAny argx, RAny argdiag) {
            if (argdiag instanceof RLogical || argdiag instanceof RDouble || argdiag instanceof RInt) { // FIXME: this could be faster using node rewriting
                RArray diag = (RArray) argdiag;
                int size = diag.size();
                if (size == 0) {
                    throw RError.getInvalidArgument(ast, "diag");
                }
                if (size > 1) {
                    RContext.warning(ast, RError.LENGTH_GT_1);
                }
                int l = diag.asLogical().getLogical(0);
                if (l != RLogical.NA) {
                    return triangular(ast, argx, l == RLogical.TRUE);
                }
            }
            throw RError.getInvalidArgument(ast, "diag");
        }
    }

    public static Triangular UPPER = new Triangular() {

        @Override
        public RArray triangular(int m, int n, boolean diag) {
            int[] content = new int[m * n];
            int takeFromColumn = diag ? 1 : 0;
            for (int j = 0; j < n; j++) {
                for (int i = 0; i < takeFromColumn; i++) {
                    content[j * m + i] = RLogical.TRUE;
                }
                if (takeFromColumn < m) { // FIXME: split into two loops
                    takeFromColumn++;
                }
                // FALSE is the default value
            }
            return RLogical.RLogicalFactory.getFor(content);
        }
    };

    public static Triangular LOWER = new Triangular() {

        @Override
        public RArray triangular(int m, int n, boolean diag) {
            int[] content = new int[m * n];
            int startTakingFrom = diag ? 0 : 1;
            for (int j = 0; j < n; j++) {
                for (int i = startTakingFrom; i < m; i++) {
                    content[j * m + i] = RLogical.TRUE;
                }
                startTakingFrom++;
                // FALSE is the default value
            }
            return RLogical.RLogicalFactory.getFor(content);
        }
    };

    public static class TriangularCallFactory extends CallFactory {
        final Triangular trian;

        public TriangularCallFactory(Triangular trian) {
            this.trian = trian;
        }

        @Override
        public RNode create(ASTNode call, RSymbol[] names, RNode[] exprs) {

            AnalyzedArguments a = BuiltIn.NamedArgsBuiltIn.analyzeArguments(names, exprs, paramNames);
            final boolean[] provided = a.providedParams;
            final int[] paramPositions = a.paramPositions;

            if (!provided[IX]) {
                BuiltIn.missingArg(call, paramNames[IX]);
            }

            if (names.length == 1) {
                return new BuiltIn.BuiltIn1(call, names, exprs) {

                    @Override
                    public final RAny doBuiltIn(Frame frame, RAny argx) {
                        return UPPER.triangular(ast, argx, false);
                    }

                };
            }
            // names.length == 2
            if (!provided[IDIAG]) {
                throw RError.getUnusedArgument(call); // FIXME: should say which argument
            }
            return new BuiltIn.BuiltIn2(call, names, exprs) {
                @Override
                public final RAny doBuiltIn(Frame frame, RAny arg0, RAny arg1) {

                    if (paramPositions[IX] == 0) {
                        return trian.triangular(ast, arg0, arg1);
                    } else {
                        return trian.triangular(ast, arg1, arg0);
                    }
                }

            };
        }
    }

    public static final CallFactory UPPER_FACTORY = new TriangularCallFactory(UPPER);
    public static final CallFactory LOWER_FACTORY = new TriangularCallFactory(LOWER);
}
