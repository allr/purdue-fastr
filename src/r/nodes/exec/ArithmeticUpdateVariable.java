package r.nodes.exec;

import r.Utils;
import r.data.*;
import r.data.internal.*;
import r.nodes.ast.*;
import r.runtime.*;

import com.oracle.truffle.api.nodes.*;


public abstract class ArithmeticUpdateVariable extends BaseR {

    final RSymbol varName;

    public ArithmeticUpdateVariable(SimpleAssignVariable ast) {
        super(ast);
        varName = ast.getSymbol();
    }

    // recovery method for a constant add x <- x + c or x <- c + x
    protected Object constantAddReplaceAndExecute(Frame frame) {

        SimpleAssignVariable assignAST = (SimpleAssignVariable) ast;
        Add addAST = (Add) assignAST.getExpr();

        Arithmetic rhsNode = null;
        if (addAST.getLHS() instanceof SimpleAccessVariable) {
            // x <- x + c
            SimpleAccessVariable accessAST = (SimpleAccessVariable) addAST.getLHS();
            r.nodes.ast.Constant constantAST = (r.nodes.ast.Constant) addAST.getRHS();

            rhsNode = new Arithmetic(addAST,
                            ReadVariable.getUninitialized(accessAST, varName),
                            new Constant(constantAST, constantAST.getValue()),
                            Arithmetic.ADD);
        } else {
            // x <- c + x
            SimpleAccessVariable accessAST = (SimpleAccessVariable) addAST.getRHS();
            r.nodes.ast.Constant constantAST = (r.nodes.ast.Constant) addAST.getLHS();

            rhsNode = new Arithmetic(addAST,
                            new Constant(constantAST, constantAST.getValue()),
                            ReadVariable.getUninitialized(accessAST, varName),
                            Arithmetic.ADD);
        }
        RNode assignmentNode = WriteVariable.getUninitialized(assignAST, varName, rhsNode);
        replace(assignmentNode, "install generic Arithmetic node from ArithmeticUpdateVariable");
        return assignmentNode.execute(frame); // could read the variable the second time, but that does not matter
    }

    // x <- x + 1L   or x <- 1L + x, only for x local, ScalarIntImpl, otherwise rewrites
    public static class ScalarIntLocalIncrement extends ArithmeticUpdateVariable {

        final int slot;

        public ScalarIntLocalIncrement(SimpleAssignVariable ast, int slot) {
            super(ast);
            this.slot = slot;
        }

        @Override
        public Object execute(Frame frame) {
            try {
                Object value = frame.getObjectForcingPromises(slot);
                if (value != null && value instanceof ScalarIntImpl) {
                    int i = ((ScalarIntImpl) value).getInt();
                    int newi = i + 1;
                    if (i != RInt.NA && newi != RInt.NA) {
                        ScalarIntImpl res = new ScalarIntImpl(newi);
                        frame.set(slot, res);  // NOTE: cannot modify a ScalarIntImpl (once written to a frame)
                        return res;
                    }
                }
                throw new UnexpectedResultException(null);
            } catch (UnexpectedResultException e) {
                return constantAddReplaceAndExecute(frame);
            }
        }
    }

    // recovery method for a constant add x <- x - c
    protected Object constantSubReplaceAndExecute(Frame frame) {

        SimpleAssignVariable assignAST = (SimpleAssignVariable) ast;
        Sub subAST = (Sub) assignAST.getExpr();

        Arithmetic rhsNode = null;
        if (subAST.getLHS() instanceof SimpleAccessVariable) {
            // x <- x - c
            SimpleAccessVariable accessAST = (SimpleAccessVariable) subAST.getLHS();
            r.nodes.ast.Constant constantAST = (r.nodes.ast.Constant) subAST.getRHS();

            rhsNode = new Arithmetic(subAST,
                            ReadVariable.getUninitialized(accessAST, varName),
                            new Constant(constantAST, constantAST.getValue()),
                            Arithmetic.SUB);
        }
        RNode assignmentNode = WriteVariable.getUninitialized(assignAST, varName, rhsNode);
        replace(assignmentNode, "install generic Arithmetic node from ArithmeticUpdateVariable");
        return assignmentNode.execute(frame); // could read the variable the second time, but that does not matter
    }

    // x <- x - 1L,  only for x local, ScalarIntImpl, otherwise rewrites
    public static class ScalarIntLocalDecrement extends ArithmeticUpdateVariable {

        final int slot;

        public ScalarIntLocalDecrement(SimpleAssignVariable ast, int slot) {
            super(ast);
            this.slot = slot;
        }

        @Override
        public Object execute(Frame frame) {
            try {
                Object value = frame.getObjectForcingPromises(slot);
                if (value != null && value instanceof ScalarIntImpl) {
                    int i = ((ScalarIntImpl) value).getInt();
                    int newi = i - 1;
                    if (i != RInt.NA && newi != RInt.NA) {
                        ScalarIntImpl res = new ScalarIntImpl(newi);
                        frame.set(slot, res);  // NOTE: cannot modify a ScalarIntImpl (once written to a frame)
                        return res;
                    }
                }
                throw new UnexpectedResultException(null);
            } catch (UnexpectedResultException e) {
                return constantSubReplaceAndExecute(frame);
            }
        }
    }
}
