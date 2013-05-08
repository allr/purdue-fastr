package r.builtins;

import java.util.*;

import com.oracle.truffle.api.CompilerDirectives;
import r.*;
import r.data.*;
import r.data.RComplex.RComplexUtils;
import r.data.RDouble.RDoubleUtils;
import r.errors.*;
import r.nodes.*;
import r.nodes.truffle.*;

import com.oracle.truffle.api.frame.*;

/**
 * "cumsum"
 *
 * <pre>
 * x -- a numeric or complex (not cummin or cummax) object, or an object that can be coerced to one of these.
 * </pre>
 */
// FIXME: could be made much faster with direct access to the arrays (after materialization that is done anyway)
final class Cumsum extends CallFactory {

    static final CallFactory _ = new Cumsum("cumsum", new String[]{"x"}, null);

    private Cumsum(String name, String[] params, String[] required) {
        super(name, params, required);
    }

    public static RComplex cumsum(RComplex x) {
        RComplex input = x.materialize();
        int size = x.size();
        double[] content = new double[2 * size];

        if (size > 0) {
            double raccum = 0;
            double iaccum = 0;
            for (int i = 0; i < size; i++) {
                double real = input.getReal(i);
                double imag = input.getImag(i);
                if (RComplexUtils.eitherIsNA(real, imag)) { return finishComplexWithNAs(content, i); }
                raccum += real;
                iaccum += imag;
                content[2 * i] = raccum;
                content[2 * i + 1] = iaccum;
            }
        }
        return RComplex.RComplexFactory.getFor(content); // drop dimensions
    }

    public static RDouble cumsum(RDouble x) {
        RDouble input = x.materialize();
        int size = x.size();
        double[] content = new double[size];

        if (size > 0) {
            double accum = 0;
            for (int i = 0; i < size; i++) {
                double value = input.getDouble(i);
                if (RDoubleUtils.isNAorNaN(value)) { return finishDoubleWithNAs(content, i); }
                accum += value;
                content[i] = accum;
            }
        }
        return RDouble.RDoubleFactory.getFor(content); // drop dimensions
    }

    private static RComplex finishComplexWithNAs(double[] content, int fromIndex) {
        Arrays.fill(content, 2 * fromIndex, content.length, RDouble.NA);
        return RComplex.RComplexFactory.getFor(content);
    }

    private static RDouble finishDoubleWithNAs(double[] content, int fromIndex) {
        Arrays.fill(content, fromIndex, content.length, RDouble.NA);
        return RDouble.RDoubleFactory.getFor(content);
    }

    public static RInt cumsum(RInt x, ASTNode ast) {
        RInt input = x.materialize();
        int size = x.size();
        int[] content = new int[size];

        if (size > 0) {
            int accum = 0;
            for (int i = 0; i < size; i++) {
                int value = input.getInt(i);
                if (value == RInt.NA) { return finishWithNAs(content, i); }
                accum = Arithmetic.ADD.op(ast, accum, value);
                if (accum == RInt.NA) {
                    RContext.warning(ast, RError.INTEGER_OVERFLOW);
                    return finishWithNAs(content, i);
                }
                content[i] = accum;
            }
        }
        return RInt.RIntFactory.getFor(content); // drop dimensions
    }

    private static RInt finishWithNAs(int[] content, int fromIndex) {
        for (int i = fromIndex; i < content.length; i++) {
            content[i] = RInt.NA;
        }
        return RInt.RIntFactory.getFor(content);
    }

    @Override public RNode create(ASTNode call, RSymbol[] names, RNode[] exprs) {
        check(call, names, exprs);

        return new InitialCumSum(call, names, exprs);
    }


    static class InitialCumSum extends Builtin.Builtin1 {

        public InitialCumSum(ASTNode orig, RSymbol[] argNames, RNode[] argExprs) {
            super(orig, argNames, argExprs);
        }

        public InitialCumSum(InitialCumSum from) {
            super(from.ast, from.argNames, from.argExprs);
        }

        @Override
        public RAny doBuiltIn(Frame frame, RAny arg) {
            if (arg instanceof RDouble) {
                CompilerDirectives.transferToInterpreter();
                return replace(new DoubleCumSum(this)).doBuiltIn(frame, arg);
            } else if (arg instanceof RString) {
                CompilerDirectives.transferToInterpreter();
                return replace(new DoubleOrStringCumSum(this)).doBuiltIn(frame, arg);
            } else {
                CompilerDirectives.transferToInterpreter();
                System.out.println("Transferring to generic from initial, arg is " + arg.getClass().getName());
                return replace(new GenericCumSum(this)).doBuiltIn(frame,arg);
            }
        }
    }
    static class DoubleOrStringCumSum extends InitialCumSum {

        public DoubleOrStringCumSum(InitialCumSum from) {
            super(from);
        }

        @Override
        public RAny doBuiltIn(Frame frame, RAny arg) {
            RDouble dx;
            if (arg instanceof RDouble) {
                dx = (RDouble) arg;
            } else if (arg instanceof RString) {
                dx = Convert.coerceToDoubleWarning((RString) arg, ast);
            } else {
                CompilerDirectives.transferToInterpreter();
                return replace(new GenericCumSum(this)).doBuiltIn(frame,arg);
            }
            return cumsum(dx).setNames(dx.names());
        }
    }

    static class DoubleCumSum extends InitialCumSum {


        public DoubleCumSum(InitialCumSum from) {
            super(from);
        }

        @Override
        public RAny doBuiltIn(Frame frame, RAny arg) {
            if (arg instanceof RDouble) {
                RDouble dx = (RDouble) arg;
                return cumsum(dx).setNames(dx.names());
            } else {
                CompilerDirectives.transferToInterpreter();
                System.out.println("Transferring to generic, arg is "+arg.getClass().getName());
                return replace(new GenericCumSum(this)).doBuiltIn(frame,arg);
            }
        }
    }



    static class GenericCumSum extends InitialCumSum {


        public GenericCumSum(InitialCumSum from) {
            super(from);
        }

        @Override
        public RAny doBuiltIn(Frame frame, RAny x) {
            if (x instanceof RDouble) {
                RDouble dx = (RDouble) x;
                return cumsum(dx).setNames(dx.names());
            } else if (x instanceof RInt) {
                RInt ix = (RInt) x;
                return cumsum(ix, ast).setNames(ix.names());
            } else if (x instanceof RLogical) {
                RLogical lx = (RLogical) x;
                return cumsum(lx.asInt(), ast).setNames(lx.names());
            } else if (x instanceof RComplex) {
                RComplex cx = (RComplex) x;
                return cumsum(cx).setNames(cx.names());
            } else if (x instanceof RRaw) {
                RRaw rx = (RRaw) x;
                return cumsum(rx.asDouble()).setNames(rx.names());
            } else if (x instanceof RNull) {
                return RDouble.EMPTY;
            } else if (x instanceof RString) {
                RString sx = (RString) x;
                RDouble res = cumsum(Convert.coerceToDoubleWarning(sx, ast));
                return res.setNames(sx.names());
            }
            throw Utils.nyi("unsupported type");
        }
    }
}
