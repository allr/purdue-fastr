package r.nodes.exec;

import java.util.*;

import r.data.*;
import r.errors.*;
import r.nodes.ast.*;
import r.runtime.*;

// FIXME: this is a very unoptimized version
// FIXME: could optimize "%in% names" using the hash-map stored in Names
public class InOperation extends BaseR {
    @Child RNode left;
    @Child RNode right;

    public InOperation(ASTNode ast, RNode left, RNode right) {
        super(ast);
        this.left = adoptChild(left);
        this.right = adoptChild(right);
    }

    @Override public final Object execute(Frame frame) {
        RAny leftValue = (RAny) left.execute(frame);
        RAny rightValue = (RAny) right.execute(frame);
        return execute(leftValue, rightValue);
    }

    @Override protected <N extends RNode> N replaceChild(RNode oldNode, N newNode) {
        assert oldNode != null;
        if (left == oldNode) {
            left = newNode;
            return adoptInternal(newNode);
        }
        if (right == oldNode) {
            right = newNode;
            return adoptInternal(newNode);
        }
        return super.replaceChild(oldNode, newNode);
    }

    // TODO: this could be customized to elide cast view creation, boxing when creating a hash map
    // FIXME: could use some primitive collections library, e.g. Trove or PCJ?
    public Object execute(RAny lhs, RAny rhs) {

        RArray typedX;
        RArray typedTable;

        // note: R also converts raw to string, but using int seems functionally equivalent
        // note: this requires list to string conversion
        if (lhs instanceof RString || rhs instanceof RString || lhs instanceof RList || rhs instanceof RList) {
            typedX = lhs.asString();
            typedTable = rhs.asString();
        } else if (lhs instanceof RComplex || rhs instanceof RComplex) {
            typedX = lhs.asComplex();
            typedTable = rhs.asComplex();
        } else if (lhs instanceof RDouble || rhs instanceof RDouble) {
            typedX = lhs.asDouble();
            typedTable = rhs.asDouble();
        } else if (lhs instanceof RInt || rhs instanceof RInt || lhs instanceof RRaw || rhs instanceof RRaw) {
            typedX = lhs.asInt();
            typedTable = rhs.asInt();
        } else if (lhs instanceof RLogical && rhs instanceof RLogical) {
            typedX = lhs.asLogical();
            typedTable = rhs.asLogical();
        } else {
            throw RError.getMatchVectorArgs(ast);
        }

        int xsize = typedX.size();
        int tableSize = typedTable.size();

        if (xsize == 1) {
            Object x = typedX.get(0);
            for (int i = 0; i < tableSize; i++) {
                Object v = typedTable.get(i);
                if (x.equals(v)) { return RLogical.BOXED_TRUE; }
            }
            return RLogical.BOXED_FALSE;
        }
        int[] content = new int[xsize];
        HashSet<Object> set = new HashSet<>(tableSize);
        for (int i = 0; i < tableSize; i++) {
            Object v = typedTable.get(i);
            set.add(v);
        }
        for (int i = 0; i < xsize; i++) {
            Object x = typedX.get(i);
            content[i] = set.contains(x) ? RLogical.TRUE : RLogical.FALSE;
        }
        return RLogical.RLogicalFactory.getFor(content);
    }

}
