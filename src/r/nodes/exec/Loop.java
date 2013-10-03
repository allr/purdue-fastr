package r.nodes.exec;

import r.*;
import r.data.*;
import r.data.internal.*;
import r.errors.*;
import r.nodes.ast.*;
import r.runtime.*;

// FIXME: local slot for functions can be looked up statically, so the code can be simplified accordingly
// but wait, if we start doing anything smarter about executing language objects, then the non-static look-up may again end-up useful, yet
// will have to be extended

public abstract class Loop extends BaseR {

    @Child RNode body;

    private static final boolean DEBUG_LO = false;

    Loop(ASTNode ast, RNode body) {
        super(ast);
        this.body = adoptChild(body);
    }

    @Override
    protected <N extends RNode> N replaceChild(RNode oldNode, N newNode) {
        assert oldNode != null;
        if (body == oldNode) {
            body = newNode;
            return adoptInternal(newNode);
        }
        return super.replaceChild(oldNode, newNode);
    }

    public static final class BreakException extends RuntimeException {
        public static final BreakException instance = new BreakException();
        private static final long serialVersionUID = -7381797804423147124L;
    }

    public static final class ContinueException extends RuntimeException {
        public static final ContinueException instance = new ContinueException();
        private static final long serialVersionUID = -5960047826708655261L;
    }

    public static class Break extends BaseR {
        public Break(ASTNode ast) {
            super(ast);
        }

        @Override
        public final RAny execute(Frame frame) {
            throw BreakException.instance;
        }
    }

    public static class Next extends BaseR {
        public Next(ASTNode ast) {
            super(ast);
        }

        @Override
        public final RAny execute(Frame frame) {
            throw ContinueException.instance;
        }
    }

    public static class Repeat extends Loop {
        public Repeat(ASTNode ast, RNode body) {
            super(ast, body);
        }

        @Override
        public final RAny execute(Frame frame) {
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

        @Override
        public final RAny execute(Frame frame) {
            try {
                if (DEBUG_LO) Utils.debug("loop - entering while loop");
                for (;;) {
                    try {
                        int condVal;
                        try {
                            condVal = cond.executeScalarLogical(frame);
                        } catch (SpecializationException e) {
                            RAny result = (RAny) e.getResult();
                            ConvertToLogicalOne castNode = ConvertToLogicalOne.createAndInsertNode(cond, result);
                            cond = adoptChild(castNode);
                            condVal = castNode.executeScalarLogical(result);
                        }
                        if (condVal == RLogical.FALSE) {
                            break;
                        }
                        if (condVal == RLogical.NA) {
                            throw RError.getUnexpectedNA(ast);
                        }
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

        @Override
        protected <N extends RNode> N replaceChild(RNode oldNode, N newNode) {
            assert oldNode != null;
            if (cond == oldNode) {
                cond = newNode;
                return adoptInternal(newNode);
            }
            return super.replaceChild(oldNode, newNode);
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

        @Override
        protected <N extends RNode> N replaceChild(RNode oldNode, N newNode) {
            assert oldNode != null;
            if (range == oldNode) {
                range = newNode;
                return adoptInternal(newNode);
            }
            return super.replaceChild(oldNode, newNode);
        }

//        public static final class NestedLocalIntSequenceRange extends BaseR {
//            @Child RNode range;
//            @Child RNode innerRange;
//            @Child RNode innerBody;
//
//            final FrameSlot cvarSlot;
//            final FrameSlot innerCvarSlot;
//
//            public NestedLocalIntSequenceRange(ASTNode ast, FrameSlot cvarSlot, RNode range, FrameSlot innerCvarSlot, RNode innerRange, RNode innerBody) {
//                super(ast);
//                this.cvarSlot = cvarSlot;
//                this.range = adoptChild(range);
//                this.innerCvarSlot = innerCvarSlot;
//                this.innerRange = adoptChild(innerRange);
//                this.innerBody = adoptChild(innerBody);
//            }
//
//            @Override
//            public final Object execute(Frame frame) {
//
//                Object rangeVal = range.execute(frame);
//                Object innerRangeVal = innerRange.execute(frame); // TODO: this has to be evaluated repeatedly if not invariant
//
//                try {
//                    if (!(rangeVal instanceof RIntSequence && innerRangeVal instanceof RIntSequence)) {
//                        throw new UnexpectedResultException(null);
//                    }
//                    RIntSequence srng = (RIntSequence) rangeVal;
//                    RIntSequence sinnerRng = (RIntSequence) innerRangeVal;
//                    final int from = srng.from();
//                    final int to = srng.to();
//                    final int step = srng.step();
//                    final int innerFrom = sinnerRng.from();
//                    final int innerTo = sinnerRng.to();
//                    final int innerStep = sinnerRng.step();
//
//                    if (from > to || step != 1 || from != 1) {
//                        throw new UnexpectedResultException(null);
//                    }
//                    if (innerFrom > innerTo || innerStep != 1 || innerFrom != 1) {
//                        throw new UnexpectedResultException(null);
//                    }
//
//                    for (int i = 1; i <= to; i++) {
//                        RFrameHeader.writeAtNoRef(frame, cvarSlot, RInt.RIntFactory.getScalar(i));
//                        try {
//                            for (int j = 1; j <= innerTo; j++) {
//                                RFrameHeader.writeAtNoRef(frame, innerCvarSlot, RInt.RIntFactory.getScalar(j));
//                                try {
//                                    innerBody.execute(frame);
//                                } catch (ContinueException ce) { }
//                            }
//                        } catch (BreakException be) { }
//                    }
//                    return RNull.getNull();
//                } catch (UnexpectedResultException e) {
//                    pushBack(range, rangeVal);
//                    pushBack(innerRange, innerRangeVal);
//                    r.nodes.For outerAST = (r.nodes.For) ast;
//                    r.nodes.For innerAST = (r.nodes.For) BuildExecutableTree.skipTrivialSequences(outerAST.getBody());
//                    RNode inner = IntSequenceRange.create(innerAST, innerAST.getCVar(), innerRange, innerBody, innerCvarSlot);
//                    RNode outer = IntSequenceRange.create(outerAST, outerAST.getCVar(), range, inner, cvarSlot);
//                    return outer.execute(frame);
//                }
//            }
//        }

        // when a range is a sequence of integers
        public static class IntSequenceRange extends For {
            public IntSequenceRange(ASTNode ast, RSymbol cvar, RNode range, RNode body) {
                super(ast, cvar, range, body);
            }

            @Override
            public Object execute(Frame frame) {

                try {
                    throw new SpecializationException(null);
                } catch (SpecializationException e) {
                    RNode sn;
                    String dbg;
                    if (frame == null) {
                        sn = createToplevel(ast, cvar, range, body);
                        dbg = "install IntSequenceRange.TopLevel from IntSequenceRange (uninitialized)";
                    } else {
                        int slot = frame.findVariable(cvar);
                        if (slot != -1) {
                            sn = createSimple(ast, cvar, range, body, slot);
                            dbg = "install IntSequenceRange.Simple from IntSequenceRange (uninitialized)";
                        } else {
                            sn = createDynamic(ast, cvar, range, body);
                            dbg = "install IntSequenceRange.Dynamic from IntSequenceRange (uninitialized)";
                        }
                    }
                    replace(sn, dbg);
                    return sn.execute(frame);
                }
            }

            @Override
            protected <N extends RNode> N replaceChild(RNode oldNode, N newNode) {
                assert oldNode != null;
                if (range == oldNode) {
                    range = newNode;
                    return adoptInternal(newNode);
                }
                return super.replaceChild(oldNode, newNode);
            }

            public abstract static class Specialized extends IntSequenceRange {
                public Specialized(ASTNode ast, RSymbol cvar, RNode range, RNode body) {
                    super(ast, cvar, range, body);
                }

                @Override
                public final RAny execute(Frame frame) {
                    RAny rval = (RAny) range.execute(frame);
                    try {
                        if (!IntImpl.RIntSequence.isInstance(rval)) {
                            throw new SpecializationException(null);
                        }
                        IntImpl.RIntSequence sval = IntImpl.RIntSequence.cast(rval);
                        int size = sval.size();
                        return execute(frame, sval, size);
                    } catch (SpecializationException e) {
                        Generic gn;
                        if (frame == null) {
                            gn = Generic.createToplevel(ast, cvar, range, body);
                        } else {
                            int slot = frame.findVariable(cvar);
                            if (slot != -1) {
                                gn = Generic.create(ast, cvar, range, body, slot);
                            } else {
                                gn = Generic.createDynamic(ast, cvar, range, body);
                            }
                        }
                        replace(gn, "install Generic from IntSequenceRange");
                        return gn.execute(frame, rval);
                    }
                }

                public abstract RAny execute(Frame frame, IntImpl.RIntSequence sval, int size);
            }

            public static Specialized createToplevel(ASTNode ast, RSymbol cvar, RNode range, RNode body) {
                return new Specialized(ast, cvar, range, body) {
                    @Override
                    public final RAny execute(Frame frame, IntImpl.RIntSequence sval, int size) {
                        final int from = sval.from();
                        final int to = sval.to();
                        final int step = sval.step();
                        try {
                            for (int i = from;; i += step) {
                                Frame.writeToTopLevelNoRef(cvar, RInt.RIntFactory.getScalar(i));
                                try {
                                    body.execute(frame);
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

            // for a sequence l:k, l <= k
            // FIXME: could make this even simpler with a bit of analysis (removal of break, continue catch blocks)
            public static RNode createSimple(ASTNode ast, RSymbol cvar, RNode range, RNode body, final int slot) {
                return new IntSequenceRange(ast, cvar, range, body) {
                    @Override
                    public final RAny execute(Frame frame) {
                        RAny rval = (RAny) range.execute(frame);
                        try {
                            if (!IntImpl.RIntSequence.isInstance(rval)) {
                                throw new SpecializationException(null);
                            }
                            IntImpl.RIntSequence sval = IntImpl.RIntSequence.cast(rval);
                            final int from = sval.from();
                            final int to = sval.to();
                            final int step = sval.step();
                            if (from > to || step != 1 || from != 1) {
                                throw new SpecializationException(null);
                            }
                            try {
                                for (int i = 1; i <= to; i++) {
                                    // no ref needed because scalars do not have reference counts
                                    frame.writeAtNoRef(slot, RInt.RIntFactory.getScalar(i));
                                    try {
                                        body.execute(frame);
                                    } catch (ContinueException ce) { }
                                }
                            } catch (BreakException be) { }
                            return RNull.getNull();
                        } catch (SpecializationException e) {
                            if (IntImpl.RIntSequence.isInstance(rval)) {
                                IntImpl.RIntSequence sval = IntImpl.RIntSequence.cast(rval);
                                Specialized sn = Specialized.create(ast, cvar, range, body, slot);
                                replace(sn, "install Specialized from IntSequenceRange.Simple");
                                return sn.execute(frame, sval, sval.size());
                            } else {
                                Generic gn = Generic.create(ast, cvar, range, body, slot);
                                replace(gn, "install Generic from IntSequenceRange.Simple");
                                return gn.execute(frame, rval);
                            }
                        }
                    }
                };
            }

            public static Specialized create(ASTNode ast, RSymbol cvar, RNode range, RNode body, final int slot) {
                return new Specialized(ast, cvar, range, body) {
                    @Override
                    public final RAny execute(Frame frame, IntImpl.RIntSequence sval, int size) {
                        final int from = sval.from();
                        final int to = sval.to();
                        final int step = sval.step();
                        try {
                            for (int i = from;; i += step) {
                                // no ref needed because scalars do not have reference counts
                                frame.writeAtNoRef(slot, RInt.RIntFactory.getScalar(i));
                                try {
                                    body.execute(frame);
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

            public static Specialized createDynamic(ASTNode ast, RSymbol cvar, RNode range, RNode body) {
                return new Specialized(ast, cvar, range, body) {
                    @Override
                    public final RAny execute(Frame frame, IntImpl.RIntSequence sval, int size) {
                        final int from = sval.from();
                        final int to = sval.to();
                        final int step = sval.step();
                        try {
                            for (int i = from;; i += step) {
                                // no ref needed because scalars do not have reference counts
                                // TODO: this is super-inefficient
                                frame.writeToExtension(cvar, RInt.RIntFactory.getScalar(i));
                                try {
                                    body.execute(frame);
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

        public static class IntSimpleRangeFor extends For {
            public IntSimpleRangeFor(ASTNode ast, RSymbol cvar, RNode range, RNode body) {
                super(ast, cvar, range, body);
            }

            @Override
            public Object execute(Frame frame) {

                try {
                    throw new SpecializationException(null);
                } catch (SpecializationException e) {
                    RNode sn;
                    String dbg;
                    if (frame == null) {
                        sn = createToplevel(ast, cvar, range, body);
                        dbg = "install IntSimpleRangeFor.TopLevel from IntSimpleRangeFor (uninitialized)";
                    } else {
                        int slot = frame.findVariable(cvar);
                        if (slot != -1) {
                            sn = createSimple(ast, cvar, range, body, slot);
                            dbg = "install IntSimpleRangeFor.Simple from IntSimpleRangeFor (uninitialized)";
                        } else {
                            sn = createDynamic(ast, cvar, range, body);
                            dbg = "install IntSimpleRangeFor.Dynamic from IntSimpleRangeFor (uninitialized)";
                        }
                    }
                    replace(sn, dbg);
                    return sn.execute(frame);
                }
            }

            @Override
            protected <N extends RNode> N replaceChild(RNode oldNode, N newNode) {
                assert oldNode != null;
                if (range == oldNode) {
                    range = newNode;
                    return adoptInternal(newNode);
                }
                return super.replaceChild(oldNode, newNode);
            }

            public abstract static class Specialized extends IntSimpleRangeFor {
                public Specialized(ASTNode ast, RSymbol cvar, RNode range, RNode body) {
                    super(ast, cvar, range, body);
                }

                @Override
                public final RAny execute(Frame frame) {
                    RAny rval = (RAny) range.execute(frame);
                    try {
                        if (!IntImpl.RIntSimpleRange.isInstance(rval)) {
                            throw new SpecializationException(null);
                        }
                        IntImpl.RIntSimpleRange sval = IntImpl.RIntSimpleRange.cast(rval);
                        int to = sval.to();
                        return execute(frame, to);
                    } catch (SpecializationException e) {
                        Generic gn;
                        if (frame == null) {
                            gn = Generic.createToplevel(ast, cvar, range, body);
                        } else {
                            int slot = frame.findVariable(cvar);
                            if (slot != -1) {
                                gn = Generic.create(ast, cvar, range, body, slot);
                            } else {
                                gn = Generic.createDynamic(ast, cvar, range, body);
                            }
                        }
                        replace(gn, "install Generic from IntSequenceRange");
                        return gn.execute(frame, rval);
                    }
                }

                public abstract RAny execute(Frame frame, int to);
            }

            public static Specialized createToplevel(ASTNode ast, RSymbol cvar, RNode range, RNode body) {
                return new Specialized(ast, cvar, range, body) {
                    @Override
                    public final RAny execute(Frame frame, int to) {
                        try {
                            for (int i = 1; i <= to; i++) {
                                Frame.writeToTopLevelNoRef(cvar, RInt.RIntFactory.getScalar(i));
                                try {
                                    body.execute(frame);
                                } catch (ContinueException ce) { }
                            }
                        } catch (BreakException be) { }
                        return RNull.getNull();
                    }
                };
            }

            public static RNode createSimple(ASTNode ast, RSymbol cvar, RNode range, RNode body, final int slot) {
                return new IntSequenceRange(ast, cvar, range, body) {
                    @Override
                    public final RAny execute(Frame frame) {
                        RAny rval = (RAny) range.execute(frame);
                        try {
                            if (!IntImpl.RIntSimpleRange.isInstance(rval)) {
                                throw new SpecializationException(null);
                            }
                            IntImpl.RIntSimpleRange sval = IntImpl.RIntSimpleRange.cast(rval);
                            int to = sval.to();
                            try {
                                for (int i = 1; i <= to; i++) {
                                    // no ref needed because scalars do not have reference counts
                                    frame.writeAtNoRef(slot, RInt.RIntFactory.getScalar(i));
                                    try {
                                        body.execute(frame);
                                    } catch (ContinueException ce) { }
                                }
                            } catch (BreakException be) { }
                            return RNull.getNull();
                        } catch (SpecializationException e) {
                            if (IntImpl.RIntSequence.isInstance(rval)) {
                                IntImpl.RIntSequence sval = IntImpl.RIntSequence.cast(rval);
                                Specialized sn = IntSequenceRange.Specialized.create(ast, cvar, range, body, slot);
                                replace(sn, "install IntSequenceRange.Specialized from IntSimpleRangeFor.Simple");
                                return sn.execute(frame, sval, sval.size());
                            } else {
                                Generic gn = Generic.create(ast, cvar, range, body, slot);
                                replace(gn, "install Generic from IntSimpleRangeFor.Simple");
                                return gn.execute(frame, rval);
                            }
                        }
                    }
                };
            }

            public static Specialized createDynamic(ASTNode ast, RSymbol cvar, RNode range, RNode body) {
                return new Specialized(ast, cvar, range, body) {
                    @Override
                    public final RAny execute(Frame frame, int to) {
                        try {
                            for (int i = 1; i <= 10; i++) {
                                // no ref needed because scalars do not have reference counts
                                // TODO: this is super-inefficient
                                frame.writeToExtension(cvar, RInt.RIntFactory.getScalar(i));
                                try {
                                    body.execute(frame);
                                } catch (ContinueException ce) { }
                            }
                        } catch (BreakException be) { }
                        return RNull.getNull();
                    }
                };
            }
        }

        // works for any type of loop range
        public abstract static class Generic extends For {

            public Generic(ASTNode ast, RSymbol cvar, RNode range, RNode body) {
                super(ast, cvar, range, body);
            }

            @Override
            public final RAny execute(Frame frame) {
                RAny rval = (RAny) range.execute(frame);
                return execute(frame, rval);
            }

            public abstract RAny execute(Frame frame, RAny rval);

            public static Generic createToplevel(ASTNode ast, RSymbol cvar, RNode range, RNode body) {
                return new Generic(ast, cvar, range, body) {
                    @Override
                    public final RAny execute(Frame frame, RAny rval) {
                        if (!(rval instanceof RArray)) {
                            throw RError.getInvalidForSequence(ast);
                        }
                        RArray arange = (RArray) rval;
                        int size = arange.size();
                        try {
                            for (int i = 0; i < size; i++) {
                                RAny vvalue = arange instanceof RList ? ((RList) arange).getRAny(i) : arange.boxedGet(i);
                                Frame.writeToTopLevelRef(cvar, vvalue); // FIXME: ref is only needed if the value is a list
                                try {
                                    body.execute(frame);
                                } catch (ContinueException ce) { }
                            }
                        } catch (BreakException be) { }
                        return RNull.getNull();
                    }
                };
            }

            public static Generic create(ASTNode ast, RSymbol cvar, RNode range, RNode body, final int slot) {
                return new Generic(ast, cvar, range, body) {
                    @Override
                    public final RAny execute(Frame frame, RAny rval) {
                        if (!(rval instanceof RArray)) {
                            throw RError.getInvalidForSequence(ast);
                        }
                        RArray arange = (RArray) rval;
                        int size = arange.size();
                        try {
                            for (int i = 0; i < size; i++) {
                                RAny vvalue = arange.boxedGet(i);
                                frame.writeAtRef(slot, vvalue);
                                try {
                                    body.execute(frame);
                                } catch (ContinueException ce) { }
                            }
                        } catch (BreakException be) { }
                        return RNull.getNull();
                    }
                };
            }

            public static Generic createDynamic(ASTNode ast, RSymbol cvar, RNode range, RNode body) {
                return new Generic(ast, cvar, range, body) {
                    @Override
                    public final RAny execute(Frame frame, RAny rval) {
                        if (!(rval instanceof RArray)) {
                            throw RError.getInvalidForSequence(ast);
                        }
                        RArray arange = (RArray) rval;
                        int size = arange.size();
                        try {
                            for (int i = 0; i < size; i++) {
                                RAny vvalue = arange.boxedGet(i);
                                frame.writeToExtension(cvar, vvalue); // TODO: this is inefficient
                                try {
                                    body.execute(frame);
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
