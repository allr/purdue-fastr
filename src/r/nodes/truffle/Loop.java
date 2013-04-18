package r.nodes.truffle;

import r.*;
import r.data.*;
import r.data.internal.*;
import r.errors.*;
import r.nodes.*;
import r.Truffle.*;

public abstract class Loop extends BaseR {

    @Child RNode body;

    private static final boolean DEBUG_LO = false;

    Loop(ASTNode ast, RNode body) {
        super(ast);
        this.body = adoptChild(body);
    }

    public static final class BreakException extends ControlFlowException {
        public static BreakException instance = new BreakException();
        private static final long serialVersionUID = -7381797804423147124L;
    }

    public static final class ContinueException extends ControlFlowException {
        public static ContinueException instance = new ContinueException();
        private static final long serialVersionUID = -5960047826708655261L;
    }

    public static class Break extends BaseR {
        public Break(ASTNode ast) {
            super(ast);
        }

        @Override public final RAny execute(Frame frame) {
            throw BreakException.instance;
        }
    }

    public static class Next extends BaseR {
        public Next(ASTNode ast) {
            super(ast);
        }

        @Override public final RAny execute(Frame frame) {
            throw ContinueException.instance;
        }
    }

    public static class Repeat extends Loop {
        public Repeat(ASTNode ast, RNode body) {
            super(ast, body);
        }

        @Override public final RAny execute(Frame frame) {
            try {
                if (DEBUG_LO) Utils.debug("loop - entering repeat loop");
                for (;;) {
                    try {
                        body.execute(frame);
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

        @Child RNode cond;

        public While(ASTNode ast, RNode cond, RNode body) {
            super(ast, body);
            this.cond = adoptChild(cond);
        }

        @Override public final RAny execute(Frame frame) {
            try {
                if (DEBUG_LO) Utils.debug("loop - entering while loop");
                for (;;) {
                    try {
                        int condVal;
                        try {
                            condVal = cond.executeScalarLogical(frame);
                        } catch (UnexpectedResultException e) {
                            RAny result = (RAny) e.getResult();
                            ConvertToLogicalOne castNode = ConvertToLogicalOne.createNode(cond, result);
                            replaceChild(cond, castNode);
                            condVal = castNode.executeScalarLogical(result);
                        }
                        if (condVal == RLogical.FALSE) {
                            break;
                        }
                        if (condVal == RLogical.NA) { throw RError.getUnexpectedNA(ast); }
                        body.execute(frame);
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

        @Child RNode range;
        final RSymbol cvar;

        For(ASTNode ast, RSymbol cvar, RNode range, RNode body) {
            super(ast, body);
            this.range = adoptChild(range);
            this.cvar = cvar;
        }

        // when a range is a sequence of integers
        public static class IntSequenceRange extends For {
            public IntSequenceRange(ASTNode ast, RSymbol cvar, RNode range, RNode body) {
                super(ast, cvar, range, body);
            }

            @Override public RAny execute(Frame frame) {

                try {
                    throw new UnexpectedResultException(null);
                } catch (UnexpectedResultException e) {
                    Specialized sn;
                    String dbg;
                    if (frame == null) {
                        sn = createToplevel(ast, cvar, range, body);
                        dbg = "install IntSequenceRange.TopLevel from IntSequenceRange (uninitialized)";
                    } else {
                        sn = create(ast, cvar, range, body, RFrameHeader.findVariable(frame, cvar));
                        dbg = "install IntSequenceRange from IntSequenceRange (uninitialized)";
                    }
                    replace(sn, dbg);
                    return sn.execute(frame);
                }
            }

            public abstract static class Specialized extends IntSequenceRange {
                public Specialized(ASTNode ast, RSymbol cvar, RNode range, RNode body) {
                    super(ast, cvar, range, body);
                }

                @Override public final RAny execute(Frame frame) {
                    RAny rval = (RAny) range.execute(frame);
                    try {
                        if (!(rval instanceof IntImpl.RIntSequence)) { throw new UnexpectedResultException(null); }
                        IntImpl.RIntSequence sval = (IntImpl.RIntSequence) rval;
                        int size = sval.size();
                        try {
                            return execute(frame, sval, size);
                        } catch (BreakException be) {}
                    } catch (UnexpectedResultException e) {
                        Generic gn;
                        if (frame == null) {
                            gn = Generic.createToplevel(ast, cvar, range, body);
                        } else {
                            gn = Generic.create(ast, cvar, range, body, RFrameHeader.findVariable(frame, cvar));
                        }
                        replace(gn, "install Generic from IntSequenceRange");
                        return gn.execute(frame, rval);
                    }
                    return RNull.getNull();
                }

                public abstract RAny execute(Frame frame, IntImpl.RIntSequence sval, int size);
            }

            public static Specialized createToplevel(ASTNode ast, RSymbol cvar, RNode range, RNode body) {
                return new Specialized(ast, cvar, range, body) {
                    @Override public final RAny execute(Frame frame, IntImpl.RIntSequence sval, int size) {
                        final int from = sval.from();
                        final int to = sval.to();
                        final int step = sval.step();
                        try {
                            for (int i = from;; i += step) {
                                RFrameHeader.writeToTopLevelNoRef(cvar, RInt.RIntFactory.getScalar(i));
                                try {
                                    body.execute(frame);
                                } catch (ContinueException ce) {}
                                if (i == to) {
                                    break;
                                }
                            }
                        } catch (BreakException be) {}
                        return RNull.getNull();
                    }
                };
            }

            public static Specialized create(ASTNode ast, RSymbol cvar, RNode range, RNode body, final FrameSlot slot) {
                return new Specialized(ast, cvar, range, body) {
                    @Override public final RAny execute(Frame frame, IntImpl.RIntSequence sval, int size) {
                        final int from = sval.from();
                        final int to = sval.to();
                        final int step = sval.step();
                        try {
                            for (int i = from;; i += step) {
                                // no ref needed because scalars do not have reference counts
                                RFrameHeader.writeAtNoRef(frame, slot, RInt.RIntFactory.getScalar(i));
                                try {
                                    body.execute(frame);
                                } catch (ContinueException ce) {}
                                if (i == to) {
                                    break;
                                }
                            }
                        } catch (BreakException be) {}
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

            @Override public final RAny execute(Frame frame) {
                RAny rval = (RAny) range.execute(frame);
                return execute(frame, rval);
            }

            public RAny execute(Frame frame, RAny rval) {
                try {
                    throw new UnexpectedResultException(null);
                } catch (UnexpectedResultException e) {
                    Generic gn;
                    String dbg;
                    if (frame == null) {
                        gn = createToplevel(ast, cvar, range, body);
                        dbg = "install Generic.TopLevel from Generic (uninitialized)";
                    } else {
                        gn = create(ast, cvar, range, body, RFrameHeader.findVariable(frame, cvar));
                        dbg = "install Generic from Generic (uninitialized)";
                    }
                    replace(gn, dbg);
                    return gn.execute(frame, rval);
                }
            }

            public static Generic createToplevel(ASTNode ast, RSymbol cvar, RNode range, RNode body) {
                return new Generic(ast, cvar, range, body) {
                    @Override public final RAny execute(Frame frame, RAny rval) {
                        if (!(rval instanceof RArray)) { throw RError.getInvalidForSequence(ast); }
                        RArray arange = (RArray) rval;
                        int size = arange.size();
                        try {
                            for (int i = 0; i < size; i++) {
                                RAny vvalue = arange.boxedGet(i);
                                RFrameHeader.writeToTopLevelRef(cvar, vvalue); // FIXME: ref is only needed if the value is a list
                                try {
                                    body.execute(frame);
                                } catch (ContinueException ce) {}
                            }
                        } catch (BreakException be) {}
                        return RNull.getNull();
                    }
                };
            }

            public static Generic create(ASTNode ast, RSymbol cvar, RNode range, RNode body, final FrameSlot slot) {
                return new Generic(ast, cvar, range, body) {
                    @Override public final RAny execute(Frame frame, RAny rval) {
                        if (!(rval instanceof RArray)) { throw RError.getInvalidForSequence(ast); }
                        RArray arange = (RArray) rval;
                        int size = arange.size();
                        try {
                            for (int i = 0; i < size; i++) {
                                RAny vvalue = arange.boxedGet(i);
                                RFrameHeader.writeAtRef(frame, slot, vvalue);
                                try {
                                    body.execute(frame);
                                } catch (ContinueException ce) {}
                            }
                        } catch (BreakException be) {}
                        return RNull.getNull();
                    }
                };
            }
        }
    }
}
