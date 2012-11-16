package r.nodes.truffle;

import r.*;
import r.data.*;
import r.data.RFunction.*;
import r.errors.*;
import r.nodes.*;

import com.oracle.truffle.nodes.*;
import com.oracle.truffle.runtime.*;

// this class is indeed very similar to ReadVariable
// if there is a way to re-factor without incurring performance overhead, it might be worth trying (but unlikely, R has distinct code as well)

public abstract class MatchFunction extends BaseR {

    final RSymbol symbol;

    public MatchFunction(ASTNode ast, RSymbol symbol) {
        super(ast);
        this.symbol = symbol;
    }

    public static MatchFunction getUninitialized(ASTNode ast, RSymbol sym) {
        return new MatchFunction(ast, sym) {

            private Object replaceAndExecute(MatchFunction node, String reason, RContext context, Frame frame) {
                replace(node, reason);
                return node.execute(context, frame);
            }

            @Override
            public final Object execute(RContext context, Frame frame) {

                try {
                    throw new UnexpectedResultException(null);
                } catch (UnexpectedResultException e) {

                    if (frame == null) {
                        return replaceAndExecute(getMatchOnlyFromTopLevel(ast, symbol), "installMatchOnlyFromTopLevel", context, frame);
                    }

                    int pos = RFrame.getPositionInWS(frame, symbol);
                    if (pos >= 0) {
                        return replaceAndExecute(getMatchLocal(ast, symbol, pos), "installMatchLocal", context, frame);
                    }

                    ReadSetEntry rse = RFrame.getRSEntry(frame, symbol);
                    if (rse == null) {
                        return replaceAndExecute(getMatchTopLevel(ast, symbol), "installMatchTopLevel", context, frame);
                    } else {
                        return replaceAndExecute(getMatchEnclosing(ast, symbol, rse.frameHops, rse.framePos), "installMatchEnclosing", context, frame);
                    }
                }
            }
        };
    }

    public static MatchFunction getMatchLocal(ASTNode ast, RSymbol symbol, final int position) {
        return new MatchFunction(ast, symbol) {

            @Override
            public final Object execute(RContext context, Frame frame) {
                RAny val = RFrame.matchViaWriteSet(frame, position, symbol);
                if (val == null) {
                    throw RError.getUnknownFunction(ast, symbol);
                }
                return val;
            }
        };
    }

    public static MatchFunction getMatchEnclosing(ASTNode ast, RSymbol symbol, final int hops, final int position) {
        return new MatchFunction(ast, symbol) {

            @Override
            public final Object execute(RContext context, Frame frame) {
                RAny val = RFrame.matchViaReadSet(frame, hops, position, symbol);
                if (val == null) {
                    throw RError.getUnknownFunction(ast, symbol);
                }
                return val;
            }
        };
    }

    public static MatchFunction getMatchTopLevel(ASTNode ast, RSymbol symbol) {
        return new MatchFunction(ast, symbol) {

            int version;

            @Override
            public final Object execute(RContext context, Frame frame) {
                RAny val; // TODO check if 'version' is enough, I think the good test has to be:
                // if (frame != oldFrame || version != symbol.getVersion()) {
                if (version != symbol.getVersion()) {
                    val = RFrame.matchFromExtension(frame, symbol, null);
                    if (val == null || !(val instanceof RClosure)) {
                        version = symbol.getVersion();
                        // oldFrame = frame;
                        val = symbol.getValue();
                    }
                } else {
                    val = symbol.getValue();
                }
                if (!(val instanceof RClosure)) {
                    throw RError.getUnknownFunction(ast, symbol);
                }
                return val;
            }
        };
    }

    public static MatchFunction getMatchOnlyFromTopLevel(ASTNode ast, RSymbol symbol) {
        return new MatchFunction(ast, symbol) {

            @Override
            public final Object execute(RContext context, Frame frame) {
                assert Utils.check(frame == null);
                RAny val = symbol.getValue();
                if (val == null || !(val instanceof RClosure)) {
                    throw RError.getUnknownFunction(ast, symbol);
                }
                return val;
            }
        };
    }
}
