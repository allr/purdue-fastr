package r.nodes.truffle;

import com.oracle.truffle.runtime.Frame;

import r.*;
import r.data.*;
import r.data.RFunction.ReadSetEntry;
import r.errors.*;
import r.nodes.*;

public abstract class ReadVariable extends BaseR {

    final RSymbol symbol;

    public ReadVariable(ASTNode orig, RSymbol sym) {
        super(orig);
        symbol = sym;
    }

    public static ReadVariable getUninitialized(ASTNode orig, RSymbol sym) {
        return new ReadVariable(orig, sym) {

            @Override
            public Object execute(RContext context, Frame frame) {
                ReadVariable node;
                int pos;
                ReadSetEntry rse;
                String reason;

                if (frame == null) {
                    node = getReadOnlyFromTopLevel(getAST(), symbol);
                    reason = "installReadOnlyFromTopLevelNode";
                } else if ((pos = RFrame.getPositionInWS(frame, symbol)) >= 0) {
                    node = getReadLocal(getAST(), symbol, pos);
                    reason = "installReadLocalNode";
                } else if ((rse = RFrame.getRSEntry(frame, symbol)) == null) {
                        // note: this can happen although we thought initially it shouldn't (why did we think that?)
                    node = getReadTopLevel(getAST(), symbol);
                    reason = "installReadTopLevel";
                } else {
                    node = getReadEnclosing(getAST(), symbol, rse.frameHops, rse.framePos);
                    reason = "installReadEnclosingNode";
                }
                replace(node, reason);
                return node.execute(context, frame);
            }
        };
    }

    public static ReadVariable getReadLocal(ASTNode orig, RSymbol sym, final int position) {
        return new ReadVariable(orig, sym) {

            @Override
            public Object execute(RContext context, Frame frame) {
                RAny val = RFrame.readViaWriteSet(frame, position, symbol);
                if (val == null) {
                    throw RError.getUnknownVariable(getAST());
                }
                return val;
            }
        };
    }

    public static ReadVariable getReadEnclosing(ASTNode orig, RSymbol sym, final int hops, final int position) {
        return new ReadVariable(orig, sym) {

            @Override
            public Object execute(RContext context, Frame frame) {
                RAny val = RFrame.readViaReadSet(frame, hops, position, symbol);
                if (val == null) {
                    throw RError.getUnknownVariable(getAST());
                }
                return val;
            }
        };
    }

    public static ReadVariable getReadTopLevel(ASTNode orig, RSymbol sym) {
        return new ReadVariable(orig, sym) {

            int version;

            @Override
            public Object execute(RContext context, Frame frame) {
                RAny val; // TODO check if 'version' is enough, I think the good test has to be:
                // if (frame != oldFrame || version != symbol.getVersion()) {
                if (version != symbol.getVersion()) {
                    val = RFrame.readFromExtension(frame, symbol, null);
                    if (val == null) {
                        version = symbol.getVersion();
                        // oldFrame = frame;
                        val = symbol.getValue();
                    }
                } else {
                    val = symbol.getValue();
                }
                if (val == null) {
                    throw RError.getUnknownVariable(getAST());
                }
                return val;
            }
        };
    }

    public static ReadVariable getReadOnlyFromTopLevel(ASTNode orig, RSymbol sym) {
        return new ReadVariable(orig, sym) {

            @Override
            public Object execute(RContext context, Frame frame) {
                assert Utils.check(frame == null);
                RAny val = symbol.getValue();
                if (val == null) {
                    throw RError.getUnknownVariable(getAST());
                }
                return val;
            }
        };
    }
}
