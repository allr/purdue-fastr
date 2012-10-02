package r.nodes.truffle;

import com.oracle.truffle.nodes.*;
import com.oracle.truffle.nodes.control.*;
import com.oracle.truffle.runtime.*;

import r.*;
import r.data.*;
import r.data.internal.*;
import r.errors.*;
import r.nodes.*;


public abstract class Loop extends BaseR {

    @Stable RNode body;

    private static final boolean DEBUG_LO = false;

    Loop(ASTNode ast, RNode body) {
        super(ast);
        this.body = updateParent(body);
    }

    public static class Break extends BaseR {
        public Break(ASTNode ast) {
            super(ast);
        }

        @Override
        public RAny execute(RContext context, Frame frame) {
            throw new BreakException();
        }
    }

    public static class Next extends BaseR {
        public Next(ASTNode ast) {
            super(ast);
        }

        @Override
        public RAny execute(RContext context, Frame frame) {
            throw new ContinueException();
        }
    }

    public static class Repeat extends Loop {
        public Repeat(ASTNode ast, RNode body) {
            super(ast, body);
        }

        @Override
        public RAny execute(RContext context, Frame frame) {
            try {
                if (DEBUG_LO) Utils.debug("loop - entering repeat loop");
                for (;;) {
                    try {
                        body.execute(context, frame);
                    } catch (ContinueException ce) {
                        if (DEBUG_LO) Utils.debug("loop - repeat loop received continue exception");
                    }

                }
            } catch (BreakException be) {
                if (DEBUG_LO) Utils.debug("loop - repeat loop received break exception");
            }
            return RNull.getNull();
        }
    }

    public static class While extends Loop {

        @Stable RNode cond;
        public While(ASTNode ast, RNode cond, RNode body) {
            super(ast, body);
            this.cond = cond;
        }

        @Override
        public RAny execute(RContext context, Frame frame) {
            try {
                if (DEBUG_LO) Utils.debug("loop - entering while loop");
                for (;;) {
                    try {
                        int condVal;
                        try {
                            condVal = cond.executeLogicalOne(context, frame);
                        } catch (UnexpectedResultException e) {
                            RAny result = (RAny) e.getResult();
                            ConvertToLogicalOne castNode = ConvertToLogicalOne.createNode(cond, result);
                            replaceChild(cond, castNode);
                            condVal = castNode.executeLogicalOne(context, result);
                        }
                        if (condVal == RLogical.FALSE) {
                            break;
                        }
                        if (condVal == RLogical.NA) {
                            throw RError.getUnexpectedNA(ast);
                        }
                        body.execute(context, frame);
                    } catch (ContinueException ce) {
                        if (DEBUG_LO) Utils.debug("loop - while loop received continue exception");
                    }

                }
            } catch (BreakException be) {
                if (DEBUG_LO) Utils.debug("loop - while loop received break exception");
            }
            return RNull.getNull();
        }
    }

    public abstract static class For extends Loop {

        @Stable RNode range;
        RSymbol cvar;

        For(ASTNode ast, RSymbol cvar, RNode range, RNode body) {
            super(ast, body);
            this.range = updateParent(range);
            this.cvar = cvar;
        }

        // when a range is a sequence of integeres
        public static class IntSequenceRange extends For {
            public IntSequenceRange(ASTNode ast, RSymbol cvar, RNode range, RNode body) {
                super(ast, cvar, range, body);
            }

            @Override
            public RAny execute(RContext context, Frame frame) {
                Specialized sn;
                String dbg;
                if (frame == null) {
                    sn = createToplevel(ast, cvar, range, body);
                    dbg = "install IntSequenceRange.TopLevel from IntSequenceRange (uninitialized)";
                } else {
                    sn = create(ast, cvar, range, body);
                    dbg = "install IntSequenceRange from IntSequenceRange (uninitialized)";
                }
                replace(sn, dbg);
                return sn.execute(context, frame);
            }

            public abstract static class Specialized extends IntSequenceRange {
                public Specialized(ASTNode ast, RSymbol cvar, RNode range, RNode body) {
                    super(ast, cvar, range, body);
                }

                @Override
                public RAny execute(RContext context, Frame frame) {
                    RAny rval = (RAny) range.execute(context, frame);
                    try {
                        if (!(rval instanceof IntImpl.RIntSequence)) {
                            throw new UnexpectedResultException(null);
                        }
                        IntImpl.RIntSequence sval = (IntImpl.RIntSequence) rval;
                        int size = sval.size();
                        try {
                            return execute(context, frame, sval, size);
                        } catch (BreakException be) { }
                    } catch (UnexpectedResultException e) {
                        Generic gn;
                        if (frame == null) {
                            gn = Generic.createToplevel(ast, cvar, range, body);
                        } else {
                            gn = Generic.create(ast, cvar, range, body);
                        }
                        replace(gn, "install Generic from IntSequenceRange");
                        return gn.execute(context, frame, rval);
                    }
                    return RNull.getNull();
                }

                public abstract RAny execute(RContext context, Frame frame, IntImpl.RIntSequence sval, int size);
            }

            public static Specialized createToplevel(ASTNode ast, RSymbol cvar, RNode range, RNode body) {
                return new Specialized(ast, cvar, range, body) {
                    @Override
                    public RAny execute(RContext context, Frame frame, IntImpl.RIntSequence sval, int size) {
                        final int from = sval.from();
                        final int to = sval.to();
                        final int step = sval.step();
                        try {
                            for (int i = from;; i += step) {
                                RFrame.writeInTopLevel(cvar, RInt.RIntFactory.getScalar(i));
                                try {
                                    body.execute(context, frame);
                                } catch (ContinueException ce) { }
                                if (i == to) {
                                    break;
                                }
                            }
                        } catch (BreakException be) { }
                        return RNull.getNull();
                    }
                };
            }

            public static Specialized create(ASTNode ast, RSymbol cvar, RNode range, RNode body) {
                return new Specialized(ast, cvar, range, body) {
                    @Override
                    public RAny execute(RContext context, Frame frame, IntImpl.RIntSequence sval, int size) {
                        final int from = sval.from();
                        final int to = sval.to();
                        final int step = sval.step();
                        final int pos = RFrame.getPositionInWS(frame, cvar);
                        try {
                            for (int i = from;; i += step) {
                                RFrame.writeAt(frame, pos, RInt.RIntFactory.getScalar(i));
                                try {
                                    body.execute(context, frame);
                                } catch (ContinueException ce) { }
                                if (i == to) {
                                    break;
                                }
                            }
                        } catch (BreakException be) { }
                        return RNull.getNull();
                    }
                };
            }
        }

        // works for any type of loop range
        public static class Generic extends For {

            public Generic(ASTNode ast, RSymbol cvar, RNode range, RNode body) {
                super(ast, cvar, range, body);
            }

            @Override
            public RAny execute(RContext context, Frame frame) {
                RAny rval = (RAny) range.execute(context, frame);
                return execute(context, frame, rval);
            }

            public RAny execute(RContext context, Frame frame, RAny rval) {
                Generic gn;
                String dbg;
                if (frame == null) {
                    gn = createToplevel(ast, cvar, range, body);
                    dbg = "install Generic.TopLevel from Generic (uninitialized)";
                } else {
                    gn = create(ast, cvar, range, body);
                    dbg = "install Generic from Generic (uninitialized)";
                }
                replace(gn, dbg);
                return gn.execute(context, frame, rval);
            }

            public static Generic createToplevel(ASTNode ast, RSymbol cvar, RNode range, RNode body) {
                return new Generic(ast, cvar, range, body) {
                    @Override
                    public RAny execute(RContext context, Frame frame, RAny rval) {
                        if (!(rval instanceof RArray)) {
                            throw RError.getInvalidForSequence(ast);
                        }
                        RArray arange = (RArray) rval;
                        int size = arange.size();
                        try {
                            for (int i = 0; i < size; i++) {
                                RAny vvalue = arange.boxedGet(i);
                                RFrame.writeInTopLevel(cvar, vvalue);
                                try {
                                    body.execute(context, frame);
                                } catch (ContinueException ce) { }
                            }
                        } catch (BreakException be) { }
                        return RNull.getNull();
                    }
                };
            }

            public static Generic create(ASTNode ast, RSymbol cvar, RNode range, RNode body) {
                return new Generic(ast, cvar, range, body) {
                    @Override
                    public RAny execute(RContext context, Frame frame, RAny rval) {
                        if (!(rval instanceof RArray)) {
                            throw RError.getInvalidForSequence(ast);
                        }
                        RArray arange = (RArray) rval;
                        int size = arange.size();
                        try {
                            int pos = RFrame.getPositionInWS(frame,cvar);
                            for (int i = 0; i < size; i++) {
                                RAny vvalue = arange.boxedGet(i);
                                RFrame.writeAt(frame, pos, vvalue);
                                try {
                                    body.execute(context, frame);
                                } catch (ContinueException ce) { }
                            }
                        } catch (BreakException be) { }
                        return RNull.getNull();
                    }
                };
            }
        }
    }
}
