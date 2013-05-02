package r.nodes.truffle;

import com.oracle.truffle.api.CompilerDirectives;
import r.*;
import r.builtins.*;
import r.data.*;
import r.data.RFunction.*;
import r.errors.*;
import r.nodes.*;

import com.oracle.truffle.api.frame.*;
import com.oracle.truffle.api.nodes.*;

// TODO: re-visit this with eval in mind

// this class is indeed very similar to ReadVariable
// if there was a way to re-factor without incurring performance overhead, it might be worth trying (but unlikely, R has distinct code as well)

public abstract class MatchCallable extends BaseR {

    final RSymbol symbol;
    public MatchCallable(ASTNode ast, RSymbol symbol) {
        super(ast);
        this.symbol = symbol;
    }

    public static RCallable matchNonVariable(ASTNode ast, RSymbol symbol) { // TODO: get rid of this (it is now in EnvironmentImpl.match)
        // builtins
        RBuiltIn builtIn = Primitives.getBuiltIn(symbol, null);
        if (builtIn != null) {
            return builtIn;
        } else {
            throw RError.getUnknownFunction(ast, symbol);
        }
    }

    public static RCallable matchGeneric(ASTNode ast, Frame frame, RSymbol symbol) {
        RCallable res = RFrameHeader.match(frame, symbol);
        if (res != null) {
            return res;
        }
        throw RError.getUnknownFunction(ast, symbol);
    }


    public static MatchCallable getUninitialized(ASTNode ast, RSymbol sym) {
        return new MatchCallable(ast, sym) {

            private Object replaceAndExecute(MatchCallable node, String reason, Frame frame) {
                replace(node, reason);
                return node.execute(frame);
            }

            @Override
            public final Object execute(Frame frame) {

                try {
                    throw new UnexpectedResultException(null);
                } catch (UnexpectedResultException e) {

                    if (frame == null) {
                        return replaceAndExecute(getMatchOnlyFromTopLevel(ast, symbol), "installMatchOnlyFromTopLevel", frame);
                    }

                    FrameSlot slot = RFrameHeader.findVariable(frame, symbol);
                    if (slot != null) {
                        return replaceAndExecute(getMatchLocal(ast, symbol, slot), "installMatchLocal", frame);
                    }

                    EnclosingSlot rse = RFrameHeader.readSetEntry(frame, symbol);
                    if (rse == null) {
                        return replaceAndExecute(getMatchTopLevel(ast, symbol), "installMatchTopLevel", frame);
                    } else {
                        return replaceAndExecute(getMatchEnclosing(ast, symbol, rse.hops, rse.slot), "installMatchEnclosing", frame);
                    }
                }
            }
        };
    }

    public static MatchCallable getMatchLocal(ASTNode ast, RSymbol symbol, final FrameSlot slot) {
        return new MatchCallable(ast, symbol) {

            @Override
            public final Object execute(Frame frame) {
                RAny val = RFrameHeader.matchViaWriteSet(frame, slot, symbol);
                if (val == null) {
                    throw RError.getUnknownFunction(ast, symbol);
                }
                return val;
            }
        };
    }

    public static MatchCallable getMatchEnclosing(ASTNode ast, RSymbol symbol, final int hops, final FrameSlot slot) {
        return new MatchCallable(ast, symbol) {

            @Override
            public final Object execute(Frame frame) {
                RAny val = RFrameHeader.matchViaReadSet(frame, hops, slot, symbol);
                if (val == null) {
                    throw RError.getUnknownFunction(ast, symbol);
                }
                return val;
            }
        };
    }

    /** GRAAL - rewritten for a clearer fastpath matcher for the functions when symbols are not dirty. Also got rid of
     * the anonymous class.
     */
    public static MatchCallable getMatchTopLevel(ASTNode ast, RSymbol symbol) {
        return new TopLevelMatcher(ast,symbol);
/*        return new MatchCallable(ast, symbol) {

            int version;

            @Override
            public final Object execute(Frame frame) {
                RAny val; // TODO check if 'version' is enough, I think the good test has to be:
                // if (frame != oldFrame || version != symbol.getVersion()) {
                if (version != symbol.getVersion()) {
                    val = RFrameHeader.matchFromExtensionEntry(frame, symbol);
                    if (val != null) {
                        return val;
                    }
                    version = symbol.getVersion();
                    // oldFrame = frame;
                    val = symbol.getValue();

                } else {
                    val = symbol.getValue();
                }
                if (val == null || !(val instanceof RCallable)) {
                    if (Primitives.STATIC_LOOKUP) {
                        throw RError.getUnknownFunction(ast, symbol);
                    } else {
                        return matchNonVariable(ast, symbol);
                    }
                }
                return val;
            }
        };*/
    }

    public static class TopLevelMatcher extends MatchCallable {

        int version;

        public TopLevelMatcher(ASTNode ast, RSymbol symbol) {
            super(ast, symbol);
        }

        public TopLevelMatcher(TopLevelMatcher from) {
            super(from.ast, from.symbol);
            version = from.version;
        }

        @Override
        public Object execute(Frame frame) {
            RAny val; // TODO check if 'version' is enough, I think the good test has to be:
            // if (frame != oldFrame || version != symbol.getVersion()) {
            if (version != symbol.getVersion()) {
                val = RFrameHeader.matchFromExtensionEntry(frame, symbol);
                if (val != null) {
                    return val;
                }
                version = symbol.getVersion();
                // oldFrame = frame;
                val = symbol.getValue();
            } else {
                val = symbol.getValue();
            }
            if (val == null || !(val instanceof RCallable)) {
                if (Primitives.STATIC_LOOKUP) {
                    throw RError.getUnknownFunction(ast, symbol);
                } else {
                    CompilerDirectives.transferToInterpreter();
                    //replace(new GenericTopLevelMatcher(this));
                    replace(new FastPathBuiltinMatcher(this));
                    return matchNonVariable(ast, symbol);
                }
            } else {
                CompilerDirectives.transferToInterpreter();
                replace(new FastPathTopLevelMatcher(this));
            }
            return val;
        }
    }

    public static class FastPathBuiltinMatcher extends TopLevelMatcher {

        public FastPathBuiltinMatcher(TopLevelMatcher from) {
            super(from);
        }
        @Override
        public Object execute(Frame frame) {
            // if (frame != oldFrame || version != symbol.getVersion()) {
            if (version != symbol.getVersion()) {
                CompilerDirectives.transferToInterpreter();
                return replace(new GenericTopLevelMatcher(this)).execute(frame);
            }
            if (Primitives.STATIC_LOOKUP) {
                throw RError.getUnknownFunction(ast, symbol);
            } else {
                return matchNonVariable(ast, symbol);
            }
        }


    }

    public static class FastPathTopLevelMatcher extends TopLevelMatcher {

        public FastPathTopLevelMatcher(TopLevelMatcher from) {
            super(from);
        }

        @Override
        public Object execute(Frame frame) {
            if (version != symbol.getVersion()) {
                CompilerDirectives.transferToInterpreter();
                replace(new GenericTopLevelMatcher(this));
            }
            RAny val = symbol.getValue();
            if ((val == null) || !(val instanceof RCallable)) {
                CompilerDirectives.transferToInterpreter();
                replace(new GenericTopLevelMatcher(this));
            }
            return val;
        }
    }

    public static class GenericTopLevelMatcher extends TopLevelMatcher {

        public GenericTopLevelMatcher(TopLevelMatcher from) {
            super(from);
        }

        @Override
        public Object execute(Frame frame) {
            RAny val; // TODO check if 'version' is enough, I think the good test has to be:
            // if (frame != oldFrame || version != symbol.getVersion()) {
            if (version != symbol.getVersion()) {
                val = RFrameHeader.matchFromExtensionEntry(frame, symbol);
                if (val != null) {
                    return val;
                }
                version = symbol.getVersion();
                // oldFrame = frame;
                val = symbol.getValue();
            } else {
                val = symbol.getValue();
            }
            if (val == null || !(val instanceof RCallable)) {
                if (Primitives.STATIC_LOOKUP) {
                    throw RError.getUnknownFunction(ast, symbol);
                } else {
                    return matchNonVariable(ast, symbol);
                }
            } else {
                return val;
            }
        }

    }


    public static MatchCallable getMatchOnlyFromTopLevel(ASTNode ast, RSymbol symbol) {
        return new MatchCallable(ast, symbol) {

            @Override
            public final Object execute(Frame frame) {
                assert Utils.check(frame == null);
                RAny val = symbol.getValue();
                if (val == null || !(val instanceof RCallable)) {
                    if (Primitives.STATIC_LOOKUP) {
                        throw RError.getUnknownFunction(ast, symbol);
                    } else {
                        return matchNonVariable(ast, symbol);
                    }
                }
                return val;
            }
        };
    }
}
