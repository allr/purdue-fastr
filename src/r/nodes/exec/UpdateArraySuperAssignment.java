package r.nodes.exec;

import r.data.*;
import r.errors.RError;
import r.nodes.ast.*;
import r.runtime.*;

/** A super assignment node. Works similarly to the UpdateArrayAssignment node, but uses the super assignment instead.
 *
 * Requires itself to either const rhs (does not reevaluate rhs on each entry) or non const variant and on its first
 * execution checks the frame to be not null.
 *
 * The const / non-const distinction is done in the create method as in UpdateVariable and no rewrite on runtime is used
 * for this.
 */
public class UpdateArraySuperAssignment extends BaseR {

    final RSymbol lhsSymbol;

    /** LHS of the assignment operator == what to update.
     */
    @Child RNode lhs;

    /** RHS of the assignment operator == new values.
     */
    @Child RNode rhs;

    /** UpdateVariable node performing the assignment itself.
     */
    @Child UpdateArrayAssignment.AssignmentNode assignment;

    /** Writeback node that stores the information to the super frame.
     */
    // TODO this should be more part of this node itself
    @Child RNode writeBack;

    /** new vector, used for the writeBack node.
     */
    RAny newVector;

    @Override
    protected <N extends RNode> N replaceChild(RNode oldNode, N newNode) {
        assert oldNode != null;
        if (lhs == oldNode) {
            lhs = newNode;
            return adoptInternal(newNode);
        }
        if (rhs == oldNode) {
            rhs = newNode;
            return adoptInternal(newNode);
        }
        if (assignment == oldNode) {
            assignment = (r.nodes.exec.UpdateArrayAssignment.AssignmentNode) newNode;
            return adoptInternal(newNode);
        }
        if (writeBack == oldNode) {
            writeBack = newNode;
            return adoptInternal(newNode);
        }
        return super.replaceChild(oldNode, newNode);
    }


    /** Creates the superassignment for given lhs and rhs and specific AssignmentNode.
     *
     * Based on rhs being constant uses the non-const, or const versions of the supperassignment.
     */
    public static UpdateArraySuperAssignment create(ASTNode orig, RSymbol lhsSymbol, RNode lhs, RNode rhs, UpdateArrayAssignment.AssignmentNode assignment) {
        try {
            throw new SpecializationException(null);
        } catch (SpecializationException e) {
            if (rhs instanceof Constant) {
                return new UpdateArraySuperAssignment.Const(orig, lhsSymbol, lhs, rhs, assignment);
            } else {
                return new UpdateArraySuperAssignment(orig, lhsSymbol, lhs, rhs, assignment);
            }
        }
    }

    /** Creates the superassignment node.
     */
    protected UpdateArraySuperAssignment(ASTNode orig, RSymbol lhsSymbol, RNode lhs, RNode rhs, UpdateArrayAssignment.AssignmentNode assignment) {
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
    protected UpdateArraySuperAssignment(UpdateArraySuperAssignment other) {
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
            throw new SpecializationException(null);
        } catch (SpecializationException e) {
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
    protected static class NonConstResolved extends UpdateArraySuperAssignment {

        protected NonConstResolved(UpdateArraySuperAssignment other) {
            super(other);
        }

        @Override
        public Object execute(Frame frame) {
            RAny rhsValue = (RAny) rhs.execute(frame); // note: order is important

            RAny lhsValue = (RAny) lhs.execute(frame.enclosingFrame());

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
    protected static class Const extends UpdateArraySuperAssignment {

        protected Const(UpdateArraySuperAssignment other) {
            super(other);
        }

        protected Const(ASTNode orig, RSymbol lhsSymbol, RNode lhs, RNode rhs, UpdateArrayAssignment.AssignmentNode assignment) {
            super(orig, lhsSymbol, lhs, rhs, assignment);
        }


            @Override
        public Object execute(Frame frame) {
            try {
                throw new SpecializationException(null);
            } catch (SpecializationException e) {
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

            protected Resolved(UpdateArraySuperAssignment other, RAny rhsValue) {
                super(other);
                this.rhsValue = rhsValue;
            }

            @Override
            public Object execute(Frame frame) {

                RAny lhsValue = (RAny) lhs.execute(frame.enclosingFrame());

                newVector = assignment.execute(frame, lhsValue, rhsValue);
                if (newVector != lhsValue) {
                    writeBack.execute(frame);  // FIXME: may ref unnecessarily
                }
                return rhsValue;
            }
        }
    }
}
