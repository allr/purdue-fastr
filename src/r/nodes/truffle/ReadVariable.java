package r.nodes.truffle;

import r.*;
import r.data.*;
import r.data.RFrameDescriptor.ReadSetEntry;
import r.errors.*;
import r.nodes.*;

public abstract class ReadVariable extends BaseRNode {

    final RSymbol symbol;

    public ReadVariable(ASTNode orig, RSymbol sym) {
        super(orig);
        symbol = sym;
    }

    public static ReadVariable getUninitialized(ASTNode orig, RSymbol sym) {
        return new ReadVariable(orig, sym) {

            @Override
            public Object execute(RContext context, RFrame frame) {
                ReadVariable node;
                int pos;
                ReadSetEntry rse;
                if (frame == null) {
                    node = getReadOnlyFromTopLevel(getAST(), symbol);
                } else if ((pos = frame.getPositionInWS(symbol)) >= 0) {
                    node = getReadLocal(getAST(), symbol, pos);
                } else if ((rse = frame.getRSEntry(symbol)) == null) {
                    node = getReadTopLevel(getAST(), symbol); // TODO this should be removed or at least asserted false !
                } else {
                    node = getReadEnclosing(getAST(), symbol, rse.frameHops, rse.framePos);
                }
                replace(node, null);
                return node.execute(context, frame);
            }
        };
    }

    public static ReadVariable getReadLocal(ASTNode orig, RSymbol sym, final int position) {
        return new ReadVariable(orig, sym) {

            @Override
            public Object execute(RContext context, RFrame frame) {
                RAny val = frame.readViaWriteSet(position, symbol);
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
            public Object execute(RContext context, RFrame frame) {
                RAny val = frame.readViaReadSet(hops, position, symbol);
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
            public Object execute(RContext context, RFrame frame) {
                RAny val; // TODO check if 'version' is enough, I think the good test has to be:
                // if (frame != oldFrame || version != symbol.getVersion()) {
                if (version != symbol.getVersion()) {
                    val = frame.readFromExtension(symbol, null);
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
            public Object execute(RContext context, RFrame frame) {
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
