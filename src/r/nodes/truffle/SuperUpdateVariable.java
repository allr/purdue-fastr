package r.nodes.truffle;

import com.oracle.truffle.api.frame.*;
import com.oracle.truffle.api.nodes.UnexpectedResultException;
import r.data.*;
import r.errors.RError;
import r.nodes.ASTNode;

/** A super assignment node. Works similarly to the UpdateVariable node, but uses the super assignment instead.
 *
 * Reqrites itself to either const rhs (does not reevaluate rhs on each entry) or non const variant and on its first
 * execution checks the frame to be not null.
 *
 * The const / non-const distincition is done in the create method as in UpdateVariable and no rewrite on runtime is used
 * for this.
 */
public class SuperUpdateVariable extends BaseR {

    final RSymbol lhsSymbol;

    /** LHS of the assignment operator == what to update.
     */
    @Child RNode lhs;

    /** RHS of the assignment operator == new values.
     */
    @Child RNode rhs;

    /** UpdateVariable node performing the assignment itself.
     */
    @Child UpdateVariable.AssignmentNode assignment;

    /** Writeback node that stores the information to the super frame.
     */
    // TODO this should be more part of this node itself
    @Child RNode writeBack;

    /** new vector, used for the writeBack node.
     */
    RAny newVector;


    /** Creates the superassignment for given lhs and rhs and specific AssignmentNode.
     *
     * Based on rhs being constant uses the non-const, or const versions of the supperassignment.
     */
    public static SuperUpdateVariable create(ASTNode orig, RSymbol lhsSymbol, RNode lhs, RNode rhs, UpdateVariable.AssignmentNode assignment) {
        try {
            throw new UnexpectedResultException(null);
        } catch (UnexpectedResultException e) {
            if (rhs instanceof Constant) {
                return new SuperUpdateVariable.Const(orig, lhsSymbol, lhs, rhs, assignment);
            } else {
                return new SuperUpdateVariable(orig, lhsSymbol, lhs, rhs, assignment);
            }
        }
    }

    /** Creates the superassignment node.
     */
    protected SuperUpdateVariable(ASTNode orig, RSymbol lhsSymbol, RNode lhs, RNode rhs, UpdateVariable.AssignmentNode assignment) {
        super(orig);
        this.lhsSymbol = lhsSymbol;
        this.lhs = adoptChild(lhs);
        this.rhs = adoptChild(rhs);
        this.assignment = adoptChild(assignment);
        RNode node = adoptChild(new BaseR(ast) {
            @Override
            public final Object execute(Frame frame) {
                return newVector;
            }
        });
        this.writeBack = adoptChild(SuperWriteVariable.getUninitialized(ast, lhsSymbol, node));
    }

    /** Copy constructor for the replacement calls.
     */
    protected SuperUpdateVariable(SuperUpdateVariable other) {
        super(other.getAST());
        this.lhsSymbol = other.lhsSymbol;
        this.lhs = adoptChild(other.lhs);
        this.rhs = adoptChild(other.rhs);
        this.assignment = adoptChild(other.assignment);
        RNode node = adoptChild(new BaseR(ast) {
            @Override
            public final Object execute(Frame frame) {
                return newVector;
            }
        });
        this.writeBack = adoptChild(SuperWriteVariable.getUninitialized(ast, lhsSymbol, node));
    }

    /** Non-const rhs execution checks the frame being initialized and if ok rewrites to the NonConstResolved node.
     */
    @Override
    public Object execute(Frame frame) {
        try {
            throw new UnexpectedResultException(null);
        } catch (UnexpectedResultException e) {
            if (frame == null) {  // FIXME: turn this guard into node rewriting, it only has to be done once
                throw RError.getUnknownVariable(ast, lhsSymbol);
            }
            return replace(new NonConstResolved(this)).execute(frame);
        }
    }

    /** Frame resolved (not null) version of the non const super assignment.
     *
     * Obtains the lhs, executes the assignment and if the result differs, writebacks the new value.
     */
    protected static class NonConstResolved extends SuperUpdateVariable {

        protected NonConstResolved(SuperUpdateVariable other) {
            super(other);
        }

        @Override
        public Object execute(Frame frame) {
            RAny rhsValue = (RAny) rhs.execute(frame); // note: order is important

            RAny lhsValue = (RAny) lhs.execute(RFrameHeader.enclosingFrame(frame));

            newVector = assignment.execute(frame, lhsValue, rhsValue);
            if (newVector != lhsValue) {
                writeBack.execute(frame);  // FIXME: may ref unnecessarily
            }
            return rhsValue;
        }
    }

    /** Const super assignment.
     *
     * Checks the frame is not null, then evaluates the rhs once and converts to the const resolved node.
     */
    protected static class Const extends SuperUpdateVariable {

        protected Const(SuperUpdateVariable other) {
            super(other);
        }

        protected Const(ASTNode orig, RSymbol lhsSymbol, RNode lhs, RNode rhs, UpdateVariable.AssignmentNode assignment) {
            super(orig, lhsSymbol, lhs, rhs, assignment);
        }


            @Override
        public Object execute(Frame frame) {
            try {
                throw new UnexpectedResultException(null);
            } catch (UnexpectedResultException e) {
                if (frame == null) { // TODO does this really has to be done once? (eval, etc? )
                    throw RError.getUnknownVariable(ast, lhsSymbol);
                }
                return replace(new Resolved(this, (RAny) rhs.execute(frame))).execute(frame);
            }
        }

        /** Const resolved node. The frame is known not to be null and the rhs is const so not reevaluated.
         */
        protected static class Resolved extends Const {

            final RAny rhsValue;

            protected Resolved(SuperUpdateVariable other, RAny rhsValue) {
                super(other);
                this.rhsValue = rhsValue;
            }

            @Override
            public Object execute(Frame frame) {

                RAny lhsValue = (RAny) lhs.execute(RFrameHeader.enclosingFrame(frame));

                newVector = assignment.execute(frame, lhsValue, rhsValue);
                if (newVector != lhsValue) {
                    writeBack.execute(frame);  // FIXME: may ref unnecessarily
                }
                return rhsValue;
            }
        }
    }





}
