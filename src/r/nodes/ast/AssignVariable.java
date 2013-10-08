package r.nodes.ast;

import r.*;
import r.data.*;
import r.errors.*;

public abstract class AssignVariable extends ASTNode {

    final boolean isSuper;
    ASTNode rhs;

    AssignVariable(boolean isSuper, ASTNode expr) {
        this.isSuper = isSuper;
        rhs = updateParent(expr);
    }

    @Override
    public void visit_all(Visitor v) {
        getExpr().accept(v);
    }

    public ASTNode getExpr() {
        return rhs;
    }

    public boolean isSuper() {
        return isSuper;
    }

    public static ASTNode create(boolean isSuper, ASTNode lhs, ASTNode rhs) {
        if (lhs instanceof SimpleAccessVariable) {
            return writeVariable(isSuper, ((SimpleAccessVariable) lhs).symbol, rhs);
        } else if (lhs instanceof AccessVector) {
            return writeVector(isSuper, (AccessVector) lhs, rhs);
        } else if (lhs instanceof FieldAccess) {
            return writeField(isSuper, (FieldAccess) lhs, rhs);
        } else if (lhs instanceof FunctionCall) {
            return writeFunction(isSuper, (FunctionCall) lhs, rhs);
        } else if (lhs instanceof Constant) { // TODO: move this to the parser?
            RAny value = ((Constant) lhs).getValue();
            if (value instanceof RString) {
                RString svalue = (RString) value;
                if (svalue.size() == 1) {
                    String name = svalue.getString(0);
                    return writeVariable(isSuper, RSymbol.getSymbol(name), rhs);
                }
            }
            throw RError.getUnknownObject(rhs); // TODO it's own exception
        }
        Utils.nyi();
        return null;
    }

    public static ASTNode writeVariable(boolean isSuper, RSymbol name, ASTNode rhs) {
        return new SimpleAssignVariable(isSuper, name, rhs);
    }

    public static ASTNode writeVector(boolean isSuper, AccessVector lhs, ASTNode rhs) {
        return new UpdateVector(isSuper, lhs, rhs);
    }

    public static ASTNode writeField(boolean isSuper, FieldAccess lhs, ASTNode rhs) {
        return new UpdateField(isSuper, lhs, rhs);
    }

    public static ASTNode writeFunction(boolean isSuper, FunctionCall lhs, ASTNode rhs) {
        if (lhs.args.size() > 0) {
            ASTNode first = lhs.args.first().getValue();
            if (!(first instanceof SimpleAccessVariable)) {
                return new UpdateExpression(isSuper, lhs, rhs);
            } else {
                lhs.args.add("value", rhs);
                ArgumentList.Default.updateParent(lhs, lhs.args); // TODO: avoid repeated update parent, perhaps convert to array
            }
        }
        lhs.name = RSymbol.getSymbol(lhs.name.pretty() + "<-");
        lhs.isAssignment(true);
        lhs.isSuper(isSuper);
        return lhs;
    }
}
