package r.nodes.truffle;

import com.oracle.truffle.api.frame.*;
import r.Utils;
import r.data.*;
import r.errors.RError;
import r.nodes.ASTNode;

public class Assignment extends BaseR {

    public static Assignment create(ASTNode orig, RSymbol lhs, RNode rhs, AssignmentNode assignment) {
        if (rhs instanceof Constant) {
            return new Assignment.Const(orig, lhs, rhs, assignment);
        } else {
            return new Assignment(orig, lhs, rhs, assignment);
        }
    }

    /** Assignment node that performs the assignment itself and returns the lhs.
     *
     * If the assignment is in-place the returned value is the same as the lhs argument, otherwise the returned
     * value is the new value.
     */
    public static abstract class AssignmentNode extends BaseR {

        /** Standard constructor.
         */
        public AssignmentNode(ASTNode orig) {
            super(orig);
        }

        /** Override this method to define the assignment operation.
         *
         * @param frame Frame of the execute method.
         * @param lhs Left hand side (assign to)
         * @param rhs Right hand side (assign from)
         * @return Left hand side object after assignment.
         */
        public abstract RAny execute(Frame frame, RAny lhs, RAny rhs, boolean rhsIsConst);

        /** Calls to execute method on frame only are *not* supported.
         */
        @Override
        public Object execute(Frame frame) {
            assert (false) : " calls to execute(frame) method for AssignmentNode is not supported.";
            return null;
        }

    }

    /** LHS of the assignment operator == what to update.
     */
    final RSymbol lhs;

     /** RHS of the assignment operator == new values
     */
    @Child RNode rhs;

    /** Assignment node performing the assignment itself.
     */
    @Child AssignmentNode assignment;


    protected Assignment(ASTNode orig, RSymbol lhs, RNode rhs, AssignmentNode assignment) {
        super(orig);
        this.lhs = lhs;
        this.rhs = adoptChild(rhs);
        this.assignment = adoptChild(assignment);
    }

    /** Copy constructor for the replacement calls.
     */
    protected Assignment(Assignment other) {
        super(other.getAST());
        this.lhs = other.lhs;
        this.rhs = adoptChild(other.rhs);
        this.assignment = adoptChild(other.assignment);
    }

    /** Default execution, rewrites itself either to a top level assignment, or determines the frameslot and replaces
     * to the LocalAssignment.
     */
    @Override
    public Object execute(Frame frame) {
        if (frame != null) {
            FrameSlot frameSlot = RFrameHeader.findVariable(frame,lhs);
            return replace(new Local(this,frameSlot)).execute(frame);
        } else {
            return replace(new TopLevel(this)).execute(frame);
        }
    }

    /** Basic assignment for const rhs values.
     */
    public static class Const extends Assignment {

        protected Const(ASTNode orig, RSymbol lhs, RNode rhs, AssignmentNode assignment) {
            super(orig, lhs, rhs, assignment);
            assert (rhs instanceof Constant) : "for non-constant RHS use Assignment class";
        }

        protected Const(Assignment other) {
            super(other);
        }

        /** Default execution, rewrites itself either to a top level assignment, or determines the frameslot and replaces
         * to the LocalAssignment.
         */
        @Override
        public Object execute(Frame frame) {
            if (frame != null) {
                FrameSlot frameSlot = RFrameHeader.findVariable(frame,lhs);
                return replace(new ConstLocal(this,frameSlot, (RAny) rhs.execute(frame))).execute(frame);
            } else {
                return replace(new ConstTopLevel(this, (RAny) rhs.execute(frame))).execute(frame);
            }
        }
    }

    /** Assigns a local variable.
     *
     * The assignment already knows its frameslot. If the frameslot is null, the node rewrites itself to the general
     * assignment. If the frame is null, the node rewrites itself to the top level assignment.
     */
    protected static class Local extends Assignment {

        /** Frameslot of the lhs variable.
         */
        final FrameSlot frameSlot;

        /** Copy constructor from the assignment node and a frameslot Specification.
         */
        protected Local(Assignment other, FrameSlot frameSlot) {
            super(other);
            this.frameSlot = frameSlot;
        }

        @Override
        public Object execute(Frame frame) {
            if (frame == null)
                return replace(new TopLevel(this)).execute(frame);
            if (frameSlot == null)
                return replace(new Assignment(this)).execute(frame);
            RAny rhsValue = (RAny) rhs.execute(frame);
            RAny lhsValue = (RAny) frame.getObject(frameSlot);
            if (lhsValue == null) {
                // TODO maybe turn this to decompile for smaller methods?
                lhsValue = RFrameHeader.readViaWriteSetSlowPath(frame,lhs);
                if (lhsValue == null)
                    throw RError.getUnknownVariable(getAST(),lhs);
                lhsValue.ref(); // reading from parent, hence need to copy on update
                // ref once will make it shared unless it is stateless (like int sequence)
            }
            RAny newLhs = assignment.execute(frame, lhsValue, rhsValue, false);
            if (lhsValue != newLhs)
                RFrameHeader.writeAtRef(frame,frameSlot,newLhs);
            return rhsValue;
        }
    }

    protected static class ConstLocal extends Local {

        final RAny rhsVal;

        protected ConstLocal(Assignment other, FrameSlot slot,  RAny rhs) {
            super(other, slot);
            rhsVal = rhs;
        }

        @Override
        public Object execute(Frame frame) {
            if (frame == null)
                return replace(new TopLevel(this)).execute(frame);
            if (frameSlot == null)
                return replace(new Const(this)).execute(frame);
            RAny lhsValue = (RAny) frame.getObject(frameSlot);
            if (lhsValue == null) {
                // TODO maybe turn this to decompile for smaller methods?
                lhsValue = RFrameHeader.readViaWriteSetSlowPath(frame,lhs);
                if (lhsValue == null)
                    throw RError.getUnknownVariable(getAST(),lhs);
                lhsValue.ref(); // reading from parent, hence need to copy on update
                // ref once will make it shared unless it is stateless (like int sequence)
            }
            RAny newLhs = assignment.execute(frame, lhsValue, rhsVal, true);
            if (lhsValue != newLhs)
                RFrameHeader.writeAtRef(frame,frameSlot,newLhs);
            return rhsVal;
        }
    }

    protected static class TopLevel extends Assignment {

        protected TopLevel(Assignment other) {
            super(other);
        }

        @Override
        public Object execute(Frame frame) {
            if (frame != null)
                return replace(new Assignment(this)).execute(frame);
            RAny rhsValue = (RAny) rhs.execute(frame);
            RAny lhsValue = lhs.getValue();
            if (lhsValue == null)
                throw RError.getUnknownVariable(getAST(),lhs);
            RAny newLhs = assignment.execute(frame, lhsValue, rhsValue, false);
            if (lhsValue != newLhs)
                RFrameHeader.writeToTopLevelRef(lhs, newLhs);
            return rhsValue;
        }
    }

    protected static class ConstTopLevel extends TopLevel {

        final RAny rhsVal;

        protected ConstTopLevel(Assignment other, RAny rhs) {
            super(other);
            rhsVal = rhs;
        }

        @Override
        public Object execute(Frame frame) {
            if (frame != null)
                return replace(new Const(this)).execute(frame);
            RAny lhsValue = lhs.getValue();
            if (lhsValue == null)
                throw RError.getUnknownVariable(getAST(),lhs);
            RAny newLhs = assignment.execute(frame, lhsValue, rhsVal, true);
            if (lhsValue != newLhs)
                RFrameHeader.writeToTopLevelRef(lhs, newLhs);
            return rhsVal;
        }

    }

}
