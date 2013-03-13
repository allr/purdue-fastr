package r.builtins;

import com.oracle.truffle.api.frame.*;
import com.oracle.truffle.api.nodes.*;

import r.data.*;
import r.nodes.*;
import r.nodes.truffle.*;

class TypeCheck extends CallFactory {

    public abstract static class CheckAction {
        public abstract boolean is(RAny arg);
    }

    private final CheckAction action;

    TypeCheck(String name, CheckAction act) {
        super(name, new String[]{"x"}, null);
        action = act;
    }

    @Override public RNode create(ASTNode call, RSymbol[] names, RNode[] exprs) {
        check(call, names, exprs);
        return new AbstractCall(call, names, exprs) {
            @Override public Object execute(Frame frame) {
                try {
                    return RLogical.RLogicalFactory.getScalar(executeScalarLogical(frame));
                } catch (UnexpectedResultException e) {
                    return e.getResult();
                }
            }

            @Override public int executeScalarLogical(Frame frame) throws UnexpectedResultException {
                RAny value = (RAny) argExprs[0].execute(frame);
                return action.is(value) ? RLogical.TRUE : RLogical.FALSE;
            }

            @Override public int executeScalarNonNALogical(Frame frame) throws UnexpectedResultException {
                RAny value = (RAny) argExprs[0].execute(frame);
                return action.is(value) ? RLogical.TRUE : RLogical.FALSE;
            }
        };
    }
}
