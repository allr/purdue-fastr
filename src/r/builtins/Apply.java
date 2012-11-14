package r.builtins;

import com.oracle.truffle.nodes.*;
import com.oracle.truffle.runtime.*;

import r.*;
import r.Convert;
import r.builtins.BuiltIn.NamedArgsBuiltIn.*;
import r.data.*;
import r.data.internal.*;
import r.errors.*;
import r.nodes.*;
import r.nodes.truffle.*;
import r.nodes.truffle.FunctionCall;

// FIXME: only a subset of R functionality
// TODO: specializations for different argument types done in sapply can be also used in lapply

public class Apply {
    private static final String[] paramNames = new String[]{"X", "FUN"};

    private static final int IX = 0;
    private static final int IFUN = 1;

    private static final boolean DEBUG_AP = false;

    public static class ValueProvider extends BaseR {
        RAny value;

        public ValueProvider(ASTNode ast) {
            super(ast);
        }

        @Override
        public final Object execute(RContext context, Frame frame) {
            return value;
        }

        public void setValue(RAny value) {
            this.value = value;
        }
    }

    public static final CallFactory LAPPLY_FACTORY = new CallFactory() {

        @Override
        public RNode create(ASTNode call, RSymbol[] names, RNode[] exprs) {
            AnalyzedArguments a = BuiltIn.NamedArgsBuiltIn.analyzeArguments(names, exprs, paramNames);
            int[] paramPositions = a.paramPositions;

            // lapply will create a call node, let's prepare names and expressions (first is the variable)

            int cnArgs = 1 + names.length - 2; // "-2" because both FUN and X are required
            RSymbol[] cnNames = new RSymbol[cnArgs];
            RNode[] cnExprs = new RNode[cnArgs];
            cnNames[0] = null;
            ValueProvider firstArgProvider = new ValueProvider(call);
            cnExprs[0] = firstArgProvider;
            int j = 0;
            for (int i = 0; i < names.length; i++) {
                if (paramPositions[IX] == i || paramPositions[IFUN] == i) {
                    continue;
                }
                cnNames[1 + j] = names[i];
                cnExprs[1 + j] = exprs[i];
                j++;
            }

            // FIXME: this won't allow calling builtins or giving function by name
            final ValueProvider closureProvider = new ValueProvider(null);
            final FunctionCall callNode = FunctionCall.getFunctionCall(call, closureProvider, cnNames, cnExprs);
            return new Lapply(call, names, exprs, callNode, firstArgProvider, closureProvider, paramPositions[IX], paramPositions[IFUN]);
        }
    };

    public static final CallFactory SAPPLY_FACTORY = new CallFactory() {

        @Override
        public RNode create(ASTNode call, RSymbol[] names, RNode[] exprs) {
            AnalyzedArguments a = BuiltIn.NamedArgsBuiltIn.analyzeArguments(names, exprs, paramNames);
            int[] paramPositions = a.paramPositions;

            // for now this initialization is copy-paste from lapply, but a full version of sapply would be different
            // sapply will create a call node, let's prepare names and expressions (first is the variable)

            int cnArgs = 1 + names.length - 2; // "-2" because both FUN and X are required
            RSymbol[] cnNames = new RSymbol[cnArgs];
            RNode[] cnExprs = new RNode[cnArgs];
            cnNames[0] = null;
            ValueProvider firstArgProvider = new ValueProvider(call);
            cnExprs[0] = firstArgProvider;
            int j = 0;
            for (int i = 0; i < names.length; i++) {
                if (paramPositions[IX] == i || paramPositions[IFUN] == i) {
                    continue;
                }
                cnNames[1 + j] = names[i];
                cnExprs[1 + j] = exprs[i];
                j++;
            }

            // FIXME: this won't allow calling builtins or giving function by name
            final ValueProvider closureProvider = new ValueProvider(null);
            final FunctionCall callNode = FunctionCall.getFunctionCall(call, closureProvider, cnNames, cnExprs);
            return new Sapply(call, names, exprs, callNode, firstArgProvider, closureProvider, paramPositions[IX], paramPositions[IFUN]);
        }
    };

    public static class Lapply extends BuiltIn {

        @Stable ValueProvider firstArgProvider;
        @Stable ValueProvider closureProvider;
        @Stable FunctionCall callNode;
        final int xPosition;
        final int funPosition;

        public Lapply(ASTNode call, RSymbol[] names, RNode[] exprs, FunctionCall callNode, ValueProvider firstArgProvider, ValueProvider closureProvider, int xPosition, int funPosition) {
            super(call, names, exprs);
            this.closureProvider = updateParent(closureProvider);
            this.firstArgProvider = updateParent(firstArgProvider);
            this.callNode = updateParent(callNode);
            this.xPosition = xPosition;
            this.funPosition = funPosition;
        }

        public static RAny generic(RContext context, Frame frame, RAny argx, ValueProvider firstArgProvider, FunctionCall callNode) {
            if (!(argx instanceof RArray)) {
                Utils.nyi("unsupported type");
            }
            RArray x = (RArray) argx;
            int xsize = x.size();
            boolean isList = false;
            RList l = null;
            if (x instanceof RList) {
                l = (RList) x;
                isList = true;
            }
            RAny[] content = new RAny[xsize];
            for (int i = 0; i < xsize; i++) {
                firstArgProvider.setValue(isList ? l.getRAny(i) : x.boxedGet(i));
                content[i] = (RAny) callNode.execute(context, frame);
            }
            return RList.RListFactory.getForArray(content);
        }

        public Specialized createSpecialized(RAny argxTemplate) {
            if (argxTemplate instanceof RList) {
                ApplyFunc a = new ApplyFunc() {
                    @Override
                    public RAny apply(RContext context, Frame frame, RAny argx, ValueProvider firstArgProvider, FunctionCall callNode) throws UnexpectedResultException {
                        if (!(argx instanceof RList))  {
                            throw new UnexpectedResultException(null);
                        }
                        RList x = (RList) argx;
                        int xsize = x.size();
                        RAny[] content = new RAny[xsize];
                        for (int i = 0; i < xsize; i++) {
                            firstArgProvider.setValue(x.getRAny(i));
                            content[i] = (RAny) callNode.execute(context, frame);
                        }
                        return RList.RListFactory.getForArray(content);
                    }
                };
                return new Specialized(ast, argNames, argExprs, callNode, firstArgProvider, closureProvider, xPosition, funPosition, a);
            }
            if (argxTemplate instanceof RArray) {
                ApplyFunc a = new ApplyFunc() {
                    @Override
                    public RAny apply(RContext context, Frame frame, RAny argx, ValueProvider firstArgProvider, FunctionCall callNode) throws UnexpectedResultException {
                        if (argx instanceof RList || !(argx instanceof RArray))  {
                            throw new UnexpectedResultException(null);
                        }
                        RArray x = (RArray) argx;
                        int xsize = x.size();
                        RAny[] content = new RAny[xsize];
                        for (int i = 0; i < xsize; i++) {
                            firstArgProvider.setValue(x.boxedGet(i));
                            content[i] = (RAny) callNode.execute(context, frame);
                        }
                        return RList.RListFactory.getForArray(content);                    }
                };
                return new Specialized(ast, argNames, argExprs, callNode, firstArgProvider, closureProvider, xPosition, funPosition, a);
            }
            return null;
        }

        public Specialized createGeneric() {
            ApplyFunc a = new ApplyFunc() {
                @Override
                public RAny apply(RContext context, Frame frame, RAny argx, ValueProvider firstArgProvider, FunctionCall callNode) throws UnexpectedResultException {
                    return generic(context, frame, argx, firstArgProvider, callNode);
                }
            };
            return new Specialized(ast, argNames, argExprs, callNode, firstArgProvider, closureProvider, xPosition, funPosition, a);
        }

        public RAny doApply(RContext context, Frame frame, RAny argx, RAny argfun) {
            try {
                throw new UnexpectedResultException(null);
            } catch (UnexpectedResultException e) {
                Specialized sn = createSpecialized(argx);
                if (sn != null) {
                    replace(sn, "install Specialized from Lapply");
                    return sn.doApply(context, frame, argx, argfun);
                } else {
                    sn = createGeneric();
                    replace(sn, "install Specialized<Generic> from Lapply");
                    return sn.doApply(context, frame, argx, argfun);
                }
            }
        }

        @Override
        public RAny doBuiltIn(RContext context, Frame frame, RAny[] args) {
            RAny argx = args[xPosition];
            RAny argfun = args[funPosition];
            return doApply(context, frame, argx, argfun);
        }

        abstract static class ApplyFunc {
            public abstract RAny apply(RContext context, Frame frame, RAny argx, ValueProvider firstArgProvider, FunctionCall callNode) throws UnexpectedResultException;
        }

        class Specialized extends Lapply {
            final ApplyFunc apply;

            public Specialized(ASTNode call, RSymbol[] names, RNode[] exprs, FunctionCall callNode, ValueProvider firstArgProvider, ValueProvider closureProvider, int xPosition, int funPosition, ApplyFunc apply) {
                super(call, names, exprs, callNode, firstArgProvider, closureProvider, xPosition, funPosition);
                this.apply = apply;
            }

            @Override
            public RAny doApply(RContext context, Frame frame, RAny argx, RAny argfun) {
                try {
                    if (!(argfun instanceof RClosure)) {
                        // FIXME: add support for builtins, variables
                        throw RError.getNotFunction(ast);
                    }
                    RClosure closure = (RClosure) argfun;
                    closureProvider.setValue(closure);
                    return apply.apply(context, frame, argx, firstArgProvider, callNode);
                } catch (UnexpectedResultException e) {
                    Specialized sn = createGeneric();
                    replace(sn, "install Specialized<Generic> from Lapply.Specialized");
                    return sn.doApply(context, frame, argx, argfun);
                }
            }
        }
    }

        // FIXME: should move these iterators to RArray ?
    public abstract static class ArgIterator {
        ValueProvider argProvider;
        int size;

        public abstract void reset(ValueProvider provider, RAny source) throws UnexpectedResultException;
        public abstract void setNext();
        public int size() {
            return size;
        }
        public static ArgIterator create(RAny sourceTemplate) {
            if (sourceTemplate instanceof IntImpl.RIntSequence) {
                return new IntSequence();
            }
            if (sourceTemplate instanceof RList) {
                return new List();
            }
            if (sourceTemplate instanceof RArray) {
                return new NonlistArray();
            }
            return new Generic(); // this will fail later when called
        }

        public static final class IntSequence extends ArgIterator {
            int to;
            int step;
            int next;

            @Override
            public void reset(ValueProvider provider, RAny source) throws UnexpectedResultException {
                if (!(source instanceof IntImpl.RIntSequence)) {
                    throw new UnexpectedResultException(null);
                }
                IntImpl.RIntSequence seq = (IntImpl.RIntSequence) source;
                this.argProvider = provider;
                next = seq.from();
                to = seq.to();
                step = seq.step();
                size = seq.size();
            }

            @Override
            public void setNext() {
                argProvider.setValue(RInt.RIntFactory.getScalar(next));
                next += step;
            }
        }

        public static final class NonlistArray extends ArgIterator {
            RArray array;
            int i;

            @Override
            public void reset(ValueProvider provider, RAny source) throws UnexpectedResultException {
                if (source instanceof RList || !(source instanceof RArray)) {
                    throw new UnexpectedResultException(null);
                }
                this.argProvider = provider;
                this.array = (RArray) source;
                i = 0;
                size = array.size();
            }

            @Override
            public void setNext() {
                argProvider.setValue(array.boxedGet(i));
                i++;
            }
        }

        public static final class List extends ArgIterator {
            RList list;
            int i;

            @Override
            public void reset(ValueProvider provider, RAny source) throws UnexpectedResultException {
                if (!(source instanceof RList)) {
                    throw new UnexpectedResultException(null);
                }
                this.argProvider = provider;
                this.list = (RList) source;
                i = 0;
                size = list.size();
            }

            @Override
            public void setNext() {
                argProvider.setValue(list.getRAny(i));
                i++;
            }
        }

        public static final class Generic extends ArgIterator {
            RArray array;
            int i;

            @Override
            public void reset(ValueProvider provider, RAny source) {
                if (!(source instanceof RArray)) {
                    Utils.nyi("unsupported type");
                }
                this.argProvider = provider;
                this.array = (RArray) source;
                i = 0;
                size = array.size();
            }

            @Override
            public void setNext() {
                argProvider.setValue(array.boxedGet(i));
                i++;
            }
        }
    }

    public static class Sapply extends BuiltIn {

        @Stable ValueProvider firstArgProvider;
        @Stable ValueProvider closureProvider;
        @Stable FunctionCall callNode;
        final int xPosition;
        final int funPosition;

        public Sapply(ASTNode call, RSymbol[] names, RNode[] exprs, FunctionCall callNode, ValueProvider firstArgProvider, ValueProvider closureProvider, int xPosition, int funPosition) {
            super(call, names, exprs);
            this.closureProvider = updateParent(closureProvider);
            this.firstArgProvider = updateParent(firstArgProvider);
            this.callNode = updateParent(callNode);
            this.xPosition = xPosition;
            this.funPosition = funPosition;
        }

        public static RAny generic(RContext context, Frame frame, ArgIterator argIterator, Sapply sapply, RAny[] partialContent) {

            boolean hasLogical = false;
            boolean hasInt = false;
            boolean hasDouble = false;
            boolean notAllScalarLists = false;
            boolean returnList = false;

            RAny[] content;
            int xsize = argIterator.size();

            if (partialContent == null) {
                content = new RAny[xsize];
            } else {
                content = partialContent;
            }
            for (int i = 0; i < xsize; i++) {
                RAny v;
                if (partialContent == null || content[i] == null) {
                    argIterator.setNext();
                    FunctionCall callNode = sapply.callNode;
                    v = (RAny) callNode.execute(context, frame);
                    content[i] = v;
                } else {
                    v = content[i];
                }
                if (returnList && notAllScalarLists) {
                    continue;
                }
                if (v instanceof RDouble) {
                    hasDouble = true;
                    notAllScalarLists = true;
                    if (!returnList && ((RDouble) v).size() != 1) {
                        returnList = true;
                    }
                } else if (v instanceof RInt) {
                    hasInt = true;
                    notAllScalarLists = true;
                    if (!returnList && ((RInt) v).size() != 1) {
                        returnList = true;
                    }
                } else if (v instanceof RLogical) {
                    hasLogical = true;
                    notAllScalarLists = true;
                    if (!returnList && ((RLogical) v).size() != 1) {
                        returnList = true;
                    }
                } else if (v instanceof RList) {
                    returnList = true;
                    if (!notAllScalarLists && ((RList) v).size() != 1) {
                        notAllScalarLists = true;
                    }
                } else if (v instanceof RNull) {
                    returnList = true;
                    notAllScalarLists = true;
                } else {
                    Utils.nyi("unsupported type");
                }
            }
            if (returnList) {
                if (!notAllScalarLists) {
                    for (int i = 0; i < xsize; i++) {
                        RList v = (RList) content[i];
                        content[i] = v.getRAny(0); // shallow but no need to ref here
                    }
                }
                return RList.RListFactory.getForArray(content);
            }
            if (hasDouble) {
                double[] values = new double[xsize];
                for (int i = 0; i < xsize; i++) {
                    RAny v = content[i];
                    if (v instanceof RDouble) {
                        values[i] = ((RDouble) v).getDouble(0);
                    } else if (v instanceof RInt) {
                        values[i] = Convert.int2double(((RInt) v).getInt(0));
                    } else {
                        values[i] = Convert.logical2double(((RLogical) v).getLogical(0));
                    }
                }
                return RDouble.RDoubleFactory.getFor(values);
            }
            if (hasInt) {
                int[] values = new int[xsize];
                for (int i = 0; i < xsize; i++) {
                    RAny v = content[i];
                    if (v instanceof RInt) {
                        values[i] = ((RInt) v).getInt(0);
                    } else {
                        values[i] = Convert.logical2int(((RLogical) v).getLogical(0));
                    }
                }
                return RInt.RIntFactory.getFor(values);
            }
            if (hasLogical) {
                int[] values = new int[xsize];
                for (int i = 0; i < xsize; i++) {
                    RAny v = content[i];
                    values[i] = Convert.logical2int(((RLogical) v).getLogical(0));
                }
                return RLogical.RLogicalFactory.getFor(values);
            }
            return RList.EMPTY;
        }

        public static RAny[] unpackPartial(Object partial) {
            if (partial instanceof RAny[]) {
                return (RAny[]) partial;
            }
            PartialResult p = (PartialResult) partial;
            int size = p.content.size();
            RAny[] res = new RAny[size];
            RArray content = p.content;
            int csize = p.contentSize;
            int i = 0;
            for (; i < csize; i++) {
                res[i] = content.boxedGet(i);
            }
            res[i] = p.lastValue;
            return res;
        }

        public class PartialResult {
            public final RArray content;
            public final int contentSize;
            public final RAny lastValue;

            PartialResult(RArray content, int contentSize, RAny lastValue) {
                this.content = content;
                this.contentSize = contentSize;
                this.lastValue = lastValue;
            }
        }

        public Specialized createSpecialized(RAny resTemplate, ArgIterator argIterator) {
            if (resTemplate instanceof RDouble) {
                ApplyFunc a = new ApplyFunc() {
                    @Override
                    public RAny apply(RContext context, Frame frame, ArgIterator argIterator, Sapply sapply) throws UnexpectedResultException {
                        int xsize = argIterator.size();
                        double[] content = new double[xsize];
                        for (int i = 0; i < xsize; i++) {
                            argIterator.setNext();
                            RAny v = (RAny) sapply.callNode.execute(context, frame);
                            if (v instanceof RDouble) {
                                content[i] = ((RDouble) v).getDouble(0);
                            } else { // NOTE: can also add Int and Logical support here
                                throw new UnexpectedResultException(new PartialResult(RDouble.RDoubleFactory.getFor(content), i, v));
                            }
                        }
                        return RDouble.RDoubleFactory.getFor(content);
                    }
                };
                return new Specialized(ast, argNames, argExprs, callNode, firstArgProvider, closureProvider, xPosition, funPosition, argIterator, a, "<=RDouble>");
            }
            if (resTemplate instanceof RInt) {
                ApplyFunc a = new ApplyFunc() {
                    @Override
                    public RAny apply(RContext context, Frame frame, ArgIterator argIterator, Sapply sapply) throws UnexpectedResultException {
                        int xsize = argIterator.size();
                        int[] content = new int[xsize];
                        for (int i = 0; i < xsize; i++) {
                            argIterator.setNext();
                            RAny v = (RAny) sapply.callNode.execute(context, frame);
                            if (v instanceof RInt) {
                                content[i] = ((RInt) v).getInt(0);
                            } else { // NOTE: can also add Logical support here
                               throw new UnexpectedResultException(new PartialResult(RInt.RIntFactory.getFor(content), i,  v));
                            }
                        }
                        return RInt.RIntFactory.getFor(content);
                    }
                };
                return new Specialized(ast, argNames, argExprs, callNode, firstArgProvider, closureProvider, xPosition, funPosition, argIterator, a, "<=RInt>");
            }
            if (resTemplate instanceof RLogical) {
                ApplyFunc a = new ApplyFunc() {
                    @Override
                    public RAny apply(RContext context, Frame frame, ArgIterator argIterator, Sapply sapply) throws UnexpectedResultException {
                        int xsize = argIterator.size();
                        int[] content = new int[xsize];
                        for (int i = 0; i < xsize; i++) {
                            argIterator.setNext();
                            RAny v = (RAny) sapply.callNode.execute(context, frame);
                            if (v instanceof RLogical) {
                                content[i] = ((RLogical) v).getLogical(0);
                            } else {
                                throw new UnexpectedResultException(new PartialResult(RLogical.RLogicalFactory.getFor(content), i, v));
                            }
                        }
                        return RLogical.RLogicalFactory.getFor(content);
                    }
                };
                return new Specialized(ast, argNames, argExprs, callNode, firstArgProvider, closureProvider, xPosition, funPosition, argIterator, a, "<=RLogical>");
            }
            if (resTemplate instanceof RList) {
                ApplyFunc a = new ApplyFunc() {
                    @Override
                    public RAny apply(RContext context, Frame frame, ArgIterator argIterator, Sapply sapply) throws UnexpectedResultException {
                        int xsize = argIterator.size();
                        RAny[] content = new RAny[xsize];
                        boolean returnsList = false;
                        for (int i = 0; i < xsize; i++) {
                            argIterator.setNext();
                            RAny v = (RAny) sapply.callNode.execute(context, frame);
                            content[i] = v;
                            if (!returnsList) {
                                if (v instanceof RList || v instanceof RNull) {
                                    returnsList = true;
                                } else {
                                    if (v instanceof RArray) {
                                        int size = ((RArray) v).size();
                                        if (size != 1) {
                                            returnsList = true;
                                        }
                                    }
                                }
                            }
                        }
                        if (!returnsList) {
                            throw new UnexpectedResultException(content);
                        }
                        return RList.RListFactory.getForArray(content);
                    }
                };
                return new Specialized(ast, argNames, argExprs, callNode, firstArgProvider, closureProvider, xPosition, funPosition, argIterator, a, "<=RList>");
            }
            Utils.nyi("unsupported type");
            return null;
        }

        public RAny doApply(RContext context, Frame frame, RAny argx, RAny argfun) {

            try {
                throw new UnexpectedResultException(null);
            } catch (UnexpectedResultException e) {
                if (!(argx instanceof RArray)) {
                    Utils.nyi("unsupported type");
                }
                if (!(argfun instanceof RClosure)) {
                    // FIXME: add support for builtins, variables
                    throw RError.getNotFunction(ast);
                }
                RClosure closure = (RClosure) argfun;
                closureProvider.setValue(closure);
                ArgIterator argIterator = ArgIterator.create(argx);
                try {
                    argIterator.reset(firstArgProvider, argx);
                } catch (UnexpectedResultException e1) {
                    Utils.nyi("unsupported type");
                    return null;
                }
                RAny res = generic(context, frame, argIterator, this, null);
                Specialized sn = createSpecialized(res, argIterator);
                if (DEBUG_AP) Utils.debug("apply - rewrote initial generic to " + argIterator.toString() + " " + sn.dbg);
                replace(sn, "install Specialized from Sapply");
                return res;
            }
        }

        @Override
        public RAny doBuiltIn(RContext context, Frame frame, RAny[] args) {
            RAny argx = args[xPosition];
            RAny argfun = args[funPosition];
            return doApply(context, frame, argx, argfun);
        }

        public Specialized createGeneric(ArgIterator argIterator) {
            ApplyFunc a = new ApplyFunc() {
                @Override
                public RAny apply(RContext context, Frame frame, ArgIterator argIterator, Sapply sapply) throws UnexpectedResultException {
                    return generic(context, frame, argIterator, sapply, null);
                }
            };
            return new Specialized(ast, argNames, argExprs, callNode, firstArgProvider, closureProvider, xPosition, funPosition, argIterator, a, "<Generic>");
        }

        abstract static class ApplyFunc {
            public abstract RAny apply(RContext context, Frame frame, ArgIterator argIterator, Sapply sapply) throws UnexpectedResultException;
        }

        class Specialized extends Sapply {
            final ApplyFunc apply;
            final ArgIterator argIterator;
            final String dbg;

            public Specialized(ASTNode call, RSymbol[] names, RNode[] exprs, FunctionCall callNode, ValueProvider firstArgProvider, ValueProvider closureProvider, int xPosition, int funPosition, ArgIterator argIterator, ApplyFunc apply, String dbg) {
                super(call, names, exprs, callNode, firstArgProvider, closureProvider, xPosition, funPosition);
                this.apply = apply;
                this.argIterator = argIterator;
                this.dbg = dbg;
            }

            @Override
            public RAny doApply(RContext context, Frame frame, RAny argx, RAny argfun) {
                if (!(argfun instanceof RClosure)) {
                    // FIXME: add support for builtins, variables
                    throw RError.getNotFunction(ast);
                }
                RClosure closure = (RClosure) argfun;
                closureProvider.setValue(closure);
                try {
                    argIterator.reset(firstArgProvider, argx);
                } catch (UnexpectedResultException e) {
                    ArgIterator ai = new ArgIterator.Generic();
                    Specialized sn = new Specialized(ast, argNames, argExprs, callNode, firstArgProvider, closureProvider, xPosition, funPosition, ai, apply, dbg);
                    if (DEBUG_AP) Utils.debug("apply - Specialized " + argIterator.toString() + " " + dbg  + " failed, rewriting to " + ai.toString() + dbg );
                    replace(sn, "install Specialized<Generic, ?> from Sapply.Specialized");
                    return sn.doApply(context, frame, argx, argfun);
                }

                try {
                    return apply.apply(context, frame, argIterator, this);
                } catch (UnexpectedResultException e) {
                    RAny[] partialContent = unpackPartial(e.getResult());
                    Specialized sn = createGeneric(argIterator);
                    if (DEBUG_AP) Utils.debug("apply - Specialized " + argIterator.toString() + " " + dbg + " failed, rewriting to " + sn.argIterator.toString() + " " + sn.dbg);
                    replace(sn, "install Specialized<?, Generic> from Sapply.Specialized");
                    return generic(context, frame, argIterator, this, partialContent);
                }
            }
        }

    }
}
