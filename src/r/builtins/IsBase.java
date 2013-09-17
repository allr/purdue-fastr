package r.builtins;

import com.oracle.truffle.api.nodes.*;

import r.data.*;
import r.nodes.ast.*;
import r.nodes.exec.*;
import r.runtime.*;

abstract class IsBase extends CallFactory {

    abstract boolean is(RAny arg);

    IsBase(String name) {
        super(name, new String[]{"x"}, null);
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
                return is(value) ? RLogical.TRUE : RLogical.FALSE;
            }

            @Override public int executeScalarNonNALogical(Frame frame) throws UnexpectedResultException {
                RAny value = (RAny) argExprs[0].execute(frame);
                return is(value) ? RLogical.TRUE : RLogical.FALSE;
            }
        };
    }
}
