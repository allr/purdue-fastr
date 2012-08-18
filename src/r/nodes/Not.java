package r.nodes;

import r.data.*;
import r.data.RLogical.RLogicalFactory;
import r.interpreter.*;

import com.oracle.truffle.runtime.*;

@Precedence(1)
public class Not extends UnaryOperation {

    public static final String OPERATOR = "!";

    Not(Node operand) {
        super(operand);
    }

    @Override
    public RAny execute(REvaluator global, Frame frame) {
        RAny op = getLHS().execute(global, frame);

        return doNot(op.asLogical());
    }

    public RLogical doNot(RLogical value) {
        RLogical result = RLogicalFactory.getEmptyArray(value.size());
        for (int i = 0; i < result.size(); i++) {
            result.set(i, doNotOne(value.getLogical(i)));
        }
        return result;
    }

    private static int doNotOne(int value) {
        switch (value) {
            case RLogical.FALSE:
                return RLogical.TRUE;
            case RLogical.NA:
                return RLogical.NA;
            default:
                return RLogical.FALSE;
        }
    }

    @Override
    public void accept(Visitor v) {
        v.visit(this);
    }

    @Override
    public String getPrettyOperator() {
        return Not.OPERATOR;
    }
}
