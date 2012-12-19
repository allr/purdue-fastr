package r.builtins;

import com.oracle.truffle.runtime.*;

import r.*;
import r.data.*;
import r.errors.*;
import r.nodes.*;
import r.nodes.truffle.*;
import r.nodes.truffle.Not;
import r.nodes.truffle.UnaryMinus;

public class Operators {

    public static RNode idempotentNumeric(ASTNode ast, RSymbol[] names, RNode[] exprs) {
        return new BuiltIn.BuiltIn1(ast, names, exprs) {

            @Override
            public RAny doBuiltIn(RContext context, Frame frame, RAny arg) {
                if (arg instanceof RDouble || arg instanceof RInt || arg instanceof RLogical) {
                    return arg;
                }
                throw RError.getInvalidArgTypeUnary(ast);
            }
        };
    }

    public static final CallFactory SUB_FACTORY = new CallFactory() {

        @Override
        public RNode create(ASTNode ast, RSymbol[] names, RNode[] exprs) {
            if (exprs.length == 1) {
                return new UnaryMinus.NumericScalar(ast, exprs[0]);
            }
            // exprs.length == 2
            return new Arithmetic(ast, exprs[0], exprs[1], Arithmetic.SUB);
        }
    };

    public static final CallFactory ADD_FACTORY = new CallFactory() {

        @Override
        public RNode create(ASTNode ast, RSymbol[] names, RNode[] exprs) {
            if (exprs.length == 1) {
                return idempotentNumeric(ast, names, exprs); // FIXME: should implement unary plus, anyway
            }
            // exprs.length == 2
            return new Arithmetic(ast, exprs[0], exprs[1], Arithmetic.ADD);
        }
    };

    public static final class ArithmeticCallFactory extends CallFactory {
        final Arithmetic.ValueArithmetic op;

        ArithmeticCallFactory(Arithmetic.ValueArithmetic op) {
            this.op = op;
        }

        @Override
        public RNode create(ASTNode ast, RSymbol[] names, RNode[] exprs) {
            // exprs.length == 2
            return new Arithmetic(ast, exprs[0], exprs[1], op);
        }
    }

    public static final CallFactory MULT_FACTORY = new ArithmeticCallFactory(Arithmetic.MULT);
    public static final CallFactory DIV_FACTORY = new ArithmeticCallFactory(Arithmetic.DIV);
    public static final CallFactory INTEGER_DIV_FACTORY = new ArithmeticCallFactory(Arithmetic.INTEGER_DIV);
    public static final CallFactory MOD_FACTORY = new ArithmeticCallFactory(Arithmetic.MOD);
    public static final CallFactory POW_FACTORY = new ArithmeticCallFactory(Arithmetic.POW);

    public static final CallFactory MAT_MULT_FACTORY = new CallFactory() {

        @Override
        public RNode create(ASTNode ast, RSymbol[] names, RNode[] exprs) {
            // exprs.length == 2
            return new MatrixOperation.MatrixProduct(ast, exprs[0], exprs[1]);
        }
    };

    public static final CallFactory OUTER_MULT_FACTORY = new CallFactory() {

        @Override
        public RNode create(ASTNode ast, RSymbol[] names, RNode[] exprs) {
            // exprs.length == 2
            return new MatrixOperation.OuterProduct(ast, exprs[0], exprs[1]);
        }
    };

    public static final class ComparisonCallFactory extends CallFactory {
        final Comparison.ValueComparison cmp;

        ComparisonCallFactory(Comparison.ValueComparison cmp) {
            this.cmp = cmp;
        }

        @Override
        public RNode create(ASTNode ast, RSymbol[] names, RNode[] exprs) {
            // exprs.length == 2
            return new Comparison(ast, exprs[0], exprs[1], cmp);
        }
    }

    public static final CallFactory EQ_FACTORY = new ComparisonCallFactory(Comparison.getEQ());
    public static final CallFactory NE_FACTORY = new ComparisonCallFactory(Comparison.getNE());
    public static final CallFactory GT_FACTORY = new ComparisonCallFactory(Comparison.getGT());
    public static final CallFactory LT_FACTORY = new ComparisonCallFactory(Comparison.getLT());
    public static final CallFactory GE_FACTORY = new ComparisonCallFactory(Comparison.getGE());
    public static final CallFactory LE_FACTORY = new ComparisonCallFactory(Comparison.getLE());

    public static final CallFactory AND_FACTORY = new CallFactory() {

        @Override
        public RNode create(ASTNode ast, RSymbol[] names, RNode[] exprs) {
            // exprs.length == 2
            return new LogicalOperation.And(ast, exprs[0], exprs[1]);
        }
    };

    public static final CallFactory OR_FACTORY = new CallFactory() {

        @Override
        public RNode create(ASTNode ast, RSymbol[] names, RNode[] exprs) {
            // exprs.length == 2
            return new LogicalOperation.Or(ast, exprs[0], exprs[1]);
        }
    };

    public static final CallFactory ELEMENTWISE_AND_FACTORY = new CallFactory() {

        @Override
        public RNode create(ASTNode ast, RSymbol[] names, RNode[] exprs) {
            // exprs.length == 2
            return ElementwiseLogicalOperation.createUninitialized(ast, exprs[0], ElementwiseLogicalOperation.AND, exprs[1]);
        }
    };

    public static final CallFactory ELEMENTWISE_OR_FACTORY = new CallFactory() {

        @Override
        public RNode create(ASTNode ast, RSymbol[] names, RNode[] exprs) {
            // exprs.length == 2
            return ElementwiseLogicalOperation.createUninitialized(ast, exprs[0], ElementwiseLogicalOperation.OR, exprs[1]);
        }
    };

    public static final CallFactory NOT_FACTORY = new CallFactory() {

        @Override
        public RNode create(ASTNode ast, RSymbol[] names, RNode[] exprs) {
            // exprs.length == 1
            return new Not.LogicalScalar(ast, exprs[0]);
        }
    };

}
