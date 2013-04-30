package r.nodes.truffle;

import com.oracle.truffle.api.frame.*;
import com.oracle.truffle.api.nodes.UnexpectedResultException;

import r.*;
import r.data.*;
import r.errors.RError;
import r.nodes.ASTNode;

// implements the assignment part of an array update
//
// for vector update xSymbol [ indexNode ] <- newValueNode
//   lhs is xSymbol
//   rhs is newValueNode
//
//   UpdateVariable will call assignmentNode.execute( currentValue(xSymbol), newValue )
//     assignmentNode.execute returns the new value of x
//   UpdateVariable then assigns newValueOfX to xSymbol

// TODO: BUG BUG this approach leads to incorrect order of evaluation, in R, indexes are evaluated before the lhs (variable to update),
// but here they are evaluated last.
//
// this snippet triggers the error:
//  { z <- 4L ; m <- matrix(1:6, nrow=2) ; x <- m ; m[1, (m[1,3] <- (z <- z + 1L) ) - 7L] <- (z <- z * 2L) ; m }


public abstract class UpdateArrayAssignment extends BaseR {

    public static UpdateArrayAssignment create(ASTNode ast, RSymbol varName, RFunction enclosingFunction, FrameSlot varSlot, RNode rhs, AssignmentNode assignment) {

        boolean topLevel = enclosingFunction == null;
        if (topLevel) {
            if (rhs instanceof Constant) {
                return new ConstTopLevel(ast, varName, rhs, assignment, ((Constant) rhs).value());
            } else {
                return new TopLevel(ast, varName, rhs, assignment);
            }
        } else {
            assert Utils.check(varSlot != null);
            return new LocalInitial(ast, varName, varSlot, rhs, assignment);
        }
    }

    /**
     * UpdateVariable node that performs the assignment itself and returns the lhs.
     * <p/>
     * If the assignment is in-place the returned value is the same as the lhs argument, otherwise the returned value is
     * the new value.
     */
    public abstract static class AssignmentNode extends BaseR {

        /** Standard constructor. */
        public AssignmentNode(ASTNode orig) {
            super(orig);
        }

        /**
         * Override this method to define the assignment operation.
         *
         * @param frame Frame of the execute method.
         * @param lhs   Left hand side (assign to)
         * @param rhs   Right hand side (assign from)
         * @return Left hand side object after assignment.
         */
        public abstract RAny execute(Frame frame, RAny lhs, RAny rhs);

        /** Calls to execute method on frame only are *not* supported. */
        @Override
        public Object execute(Frame frame) {
            Utils.nyi("unreachable");
            return null;
        }

    }

    /** LHS of the assignment operator == what to update. */
    final RSymbol varName;

    /** RHS of the assignment operator == new values. */
    @Child RNode rhs;

    /** UpdateVariable node performing the assignment itself. */
    @Child AssignmentNode assignment;


    protected UpdateArrayAssignment(ASTNode orig, RSymbol varName, RNode rhs, AssignmentNode assignment) {
        super(orig);
        this.varName = varName;
        this.rhs = adoptChild(rhs);
        this.assignment = adoptChild(assignment);
    }

    // update with a local slot, rewrites once (if) that local slot contains a value
    protected static class LocalInitial extends UpdateArrayAssignment {

        final FrameSlot varSlot;

        protected LocalInitial(ASTNode ast, RSymbol varName, FrameSlot varSlot, RNode rhs, AssignmentNode assignment) {
            super(ast, varName, rhs, assignment);
            this.varSlot = varSlot;
        }

        @Override
        public Object execute(Frame frame) {

            RAny rhsValue = (RAny) rhs.execute(frame);
            RAny lhsValue = (RAny) frame.getObject(varSlot);
            if (lhsValue == null) {
                // TODO maybe turn this to decompile for smaller methods?
                lhsValue = RFrameHeader.readViaWriteSetSlowPath(frame, varName);
                if (lhsValue == null) {
                    throw RError.getUnknownVariable(getAST(), varName);
                }
                lhsValue.ref(); // reading from parent, hence need to copy on update
                // ref once will make it shared unless it is stateless (like int sequence)
            }
            RAny newLhs = assignment.execute(frame, lhsValue, rhsValue);
            if (lhsValue != newLhs) {
                RFrameHeader.writeAtRef(frame, varSlot, newLhs);
            }

            try {
                if (lhsValue != null) {
                    throw new UnexpectedResultException(null);
                }
            } catch (UnexpectedResultException e) {
                replace(new LocalSimple(ast, varName, varSlot, rhs, assignment));
            }
            return rhsValue;
        }
    }

    // update with a local slot that actually has a valid value, rewrites if it does not
    protected static class LocalSimple extends UpdateArrayAssignment {

        final FrameSlot varSlot;

        protected LocalSimple(ASTNode ast, RSymbol varName, FrameSlot varSlot, RNode rhs, AssignmentNode assignment) {
            super(ast, varName, rhs, assignment);
            this.varSlot = varSlot;
        }

        @Override
        public Object execute(Frame frame) {

            RAny rhsValue = (RAny) rhs.execute(frame);
            RAny lhsValue = (RAny) frame.getObject(varSlot);

            try {
                if (lhsValue == null) {
                   throw new UnexpectedResultException(null);
                }
            } catch (UnexpectedResultException e) {
                return replace(rhs, rhsValue, new LocalGeneric(ast, varName, varSlot, rhs, assignment), frame);
            }

            RAny newLhs = assignment.execute(frame, lhsValue, rhsValue);
            if (lhsValue != newLhs) {
                RFrameHeader.writeAtRef(frame, varSlot, newLhs);
            }
            return rhsValue;
        }
    }

    protected static class LocalGeneric extends UpdateArrayAssignment {

        final FrameSlot varSlot;

        protected LocalGeneric(ASTNode ast, RSymbol varName, FrameSlot varSlot, RNode rhs, AssignmentNode assignment) {
            super(ast, varName, rhs, assignment);
            this.varSlot = varSlot;
        }

        @Override
        public Object execute(Frame frame) {

            RAny rhsValue = (RAny) rhs.execute(frame);
            RAny lhsValue = (RAny) frame.getObject(varSlot);
            if (lhsValue == null) {
                // TODO maybe turn this to decompile for smaller methods?
                lhsValue = RFrameHeader.readViaWriteSetSlowPath(frame, varName);
                if (lhsValue == null) {
                    throw RError.getUnknownVariable(getAST(), varName);
                }
                lhsValue.ref(); // reading from parent, hence need to copy on update
                // ref once will make it shared unless it is stateless (like int sequence)
            }
            RAny newLhs = assignment.execute(frame, lhsValue, rhsValue);
            if (lhsValue != newLhs) {
                RFrameHeader.writeAtRef(frame, varSlot, newLhs);
            }
            return rhsValue;
        }
    }

    protected static final class TopLevel extends UpdateArrayAssignment {

        protected TopLevel(ASTNode orig, RSymbol varName, RNode rhs, AssignmentNode assignment) {
            super(orig, varName, rhs, assignment);
        }

        @Override
        public Object execute(Frame frame) {
            RAny rhsValue = (RAny) rhs.execute(frame);
            RAny lhsValue = varName.getValue();
            if (lhsValue == null) {
                throw RError.getUnknownVariable(getAST(), varName);
            }
            RAny newLhs = assignment.execute(frame, lhsValue, rhsValue);
            if (lhsValue != newLhs) {
                RFrameHeader.writeToTopLevelRef(varName, newLhs);
            }
            return rhsValue;
        }
    }

    protected static final class ConstTopLevel extends UpdateArrayAssignment {

        final RAny rhsVal;

        protected ConstTopLevel(ASTNode orig, RSymbol varName, RNode rhs, AssignmentNode assignment, RAny rhsVal) {
            super(orig, varName, rhs, assignment);
            this.rhsVal = rhsVal;
        }

        @Override
        public Object execute(Frame frame) {
            RAny lhsValue = varName.getValue();
            if (lhsValue == null) {
                throw RError.getUnknownVariable(getAST(), varName);
            }
            RAny newLhs = assignment.execute(frame, lhsValue, rhsVal);
            if (lhsValue != newLhs) {
                RFrameHeader.writeToTopLevelRef(varName, newLhs);
            }
            return rhsVal;
        }

    }

}
