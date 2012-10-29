package r.builtins;

import com.oracle.truffle.nodes.*;
import com.oracle.truffle.runtime.*;

import r.*;
import r.Convert;
import r.builtins.BuiltIn.NamedArgsBuiltIn.*;
import r.data.*;
import r.errors.*;
import r.nodes.*;
import r.nodes.truffle.*;
import r.nodes.truffle.FunctionCall;

// FIXME: only a subset of R functionality
public class Apply {
    private static final String[] paramNames = new String[]{"X", "FUN"};

    private static final int IX = 0;
    private static final int IFUN = 1;

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

        public static RAny generic(RContext context, Frame frame, RArray argx, ValueProvider firstArgProvider, FunctionCall callNode, RAny[] partialContent) {
            int xsize = argx.size();
            boolean isList = false;
            RList l = null;
            if (argx instanceof RList) {
                l = (RList) argx;
                isList = true;
            }

            boolean hasLogical = false;
            boolean hasInt = false;
            boolean hasDouble = false;
            boolean hasNull = false;
            boolean notAllScalarLists = false;
            boolean returnList = false;

            RAny[] content;

            if (partialContent == null) {
                content = new RAny[xsize];
            } else {
                content = partialContent;
            }
            for (int i = 0; i < xsize; i++) {
                RAny v;
                if (partialContent == null || content[i] == null) {
                    firstArgProvider.setValue(isList ? l.getRAny(i) : argx.boxedGet(i));
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
                    hasNull = true;
                } else {
                    Utils.nyi("unsupported type");
                }
            }
            if (returnList) {
                if (!notAllScalarLists) {
                    for (int i = 0; i < xsize; i++) {
                        RList v = (RList) content[i];
                        content[i] = v.getRAny(0);
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
                return RDouble.RDoubleFactory.getForArray(values);
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
                return RInt.RIntFactory.getForArray(values);
            }
            if (hasLogical) {
                int[] values = new int[xsize];
                for (int i = 0; i < xsize; i++) {
                    RAny v = content[i];
                    values[i] = Convert.logical2int(((RLogical) v).getLogical(0));
                }
                return RLogical.RLogicalFactory.getForArray(values);
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

        public Specialized createSpecialized(RAny resTemplate) {
            if (resTemplate instanceof RDouble) {
                ApplyFunc a = new ApplyFunc() {
                    @Override
                    public RAny apply(RContext context, Frame frame, RArray argx, ValueProvider firstArgProvider, FunctionCall callNode) throws UnexpectedResultException {
                        int xsize = argx.size();
                        boolean isList = false;
                        RList l = null;
                        if (argx instanceof RList) {
                            l = (RList) argx;
                            isList = true;
                        }
                        double[] content = new double[xsize];
                        for (int i = 0; i < xsize; i++) {
                            firstArgProvider.setValue(isList ? l.getRAny(i) : argx.boxedGet(i));
                            RAny v = (RAny) callNode.execute(context, frame);
                            if (v instanceof RDouble) {
                                content[i] = ((RDouble) v).getDouble(0);
                            } else { // NOTE: can also add Int and Logical support here
                                throw new UnexpectedResultException(new PartialResult(RDouble.RDoubleFactory.getForArray(content), i, v));
                            }
                        }
                        return RDouble.RDoubleFactory.getForArray(content);
                    }
                };
                return new Specialized(ast, argNames, argExprs, callNode, firstArgProvider, closureProvider, xPosition, funPosition, a);
            }
            if (resTemplate instanceof RInt) {
                ApplyFunc a = new ApplyFunc() {
                    @Override
                    public RAny apply(RContext context, Frame frame, RArray argx, ValueProvider firstArgProvider, FunctionCall callNode) throws UnexpectedResultException {
                        int xsize = argx.size();
                        boolean isList = false;
                        RList l = null;
                        if (argx instanceof RList) {
                            l = (RList) argx;
                            isList = true;
                        }
                        int[] content = new int[xsize];
                        for (int i = 0; i < xsize; i++) {
                            firstArgProvider.setValue(isList ? l.getRAny(i) : argx.boxedGet(i));
                            RAny v = (RAny) callNode.execute(context, frame);
                            if (v instanceof RInt) {
                                content[i] = ((RInt) v).getInt(0);
                            } else { // NOTE: can also add Logical support here
                               throw new UnexpectedResultException(new PartialResult(RInt.RIntFactory.getForArray(content), i,  v));
                            }
                        }
                        return RInt.RIntFactory.getForArray(content);
                    }
                };
                return new Specialized(ast, argNames, argExprs, callNode, firstArgProvider, closureProvider, xPosition, funPosition, a);
            }
            if (resTemplate instanceof RLogical) {
                ApplyFunc a = new ApplyFunc() {
                    @Override
                    public RAny apply(RContext context, Frame frame, RArray argx, ValueProvider firstArgProvider, FunctionCall callNode) throws UnexpectedResultException {
                        int xsize = argx.size();
                        boolean isList = false;
                        RList l = null;
                        if (argx instanceof RList) {
                            l = (RList) argx;
                            isList = true;
                        }
                        int[] content = new int[xsize];
                        for (int i = 0; i < xsize; i++) {
                            firstArgProvider.setValue(isList ? l.getRAny(i) : argx.boxedGet(i));
                            RAny v = (RAny) callNode.execute(context, frame);
                            if (v instanceof RLogical) {
                                content[i] = ((RLogical) v).getLogical(0);
                            } else {
                                throw new UnexpectedResultException(new PartialResult(RLogical.RLogicalFactory.getForArray(content), i, v));
                            }
                        }
                        return RLogical.RLogicalFactory.getForArray(content);
                    }
                };
                return new Specialized(ast, argNames, argExprs, callNode, firstArgProvider, closureProvider, xPosition, funPosition, a);
            }
            if (resTemplate instanceof RList) {
                ApplyFunc a = new ApplyFunc() {
                    @Override
                    public RAny apply(RContext context, Frame frame, RArray argx, ValueProvider firstArgProvider, FunctionCall callNode) throws UnexpectedResultException {
                        int xsize = argx.size();
                        boolean isList = false;
                        RList l = null;
                        if (argx instanceof RList) {
                            l = (RList) argx;
                            isList = true;
                        }
                        RAny[] content = new RAny[xsize];
                        boolean returnsList = false;
                        for (int i = 0; i < xsize; i++) {
                            firstArgProvider.setValue(isList ? l.getRAny(i) : argx.boxedGet(i));
                            RAny v = (RAny) callNode.execute(context, frame);
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
                return new Specialized(ast, argNames, argExprs, callNode, firstArgProvider, closureProvider, xPosition, funPosition, a);
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
                RAny res = generic(context, frame, (RArray) argx, firstArgProvider, callNode, null);
                Specialized sn = createSpecialized(res);
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

        public Specialized createGeneric() {
            ApplyFunc a = new ApplyFunc() {
                @Override
                public RAny apply(RContext context, Frame frame, RArray argx, ValueProvider firstArgProvider, FunctionCall callNode) throws UnexpectedResultException {
                    return generic(context, frame, argx, firstArgProvider, callNode, null);
                }
            };
            return new Specialized(ast, argNames, argExprs, callNode, firstArgProvider, closureProvider, xPosition, funPosition, a);
        }

        abstract static class ApplyFunc {
            public abstract RAny apply(RContext context, Frame frame, RArray argx, ValueProvider firstArgProvider, FunctionCall callNode) throws UnexpectedResultException;
        }

        class Specialized extends Sapply {
            final ApplyFunc apply;

            public Specialized(ASTNode call, RSymbol[] names, RNode[] exprs, FunctionCall callNode, ValueProvider firstArgProvider, ValueProvider closureProvider, int xPosition, int funPosition, ApplyFunc apply) {
                super(call, names, exprs, callNode, firstArgProvider, closureProvider, xPosition, funPosition);
                this.apply = apply;
            }

            @Override
            public RAny doApply(RContext context, Frame frame, RAny argx, RAny argfun) {
                try {
                    if (!(argx instanceof RArray)) {
                        Utils.nyi("unsupported type");
                    }
                    if (!(argfun instanceof RClosure)) {
                        // FIXME: add support for builtins, variables
                        throw RError.getNotFunction(ast);
                    }
                    RClosure closure = (RClosure) argfun;
                    closureProvider.setValue(closure);
                    return apply.apply(context, frame, (RArray) argx, firstArgProvider, callNode);
                } catch (UnexpectedResultException e) {
                    RAny[] partialContent = unpackPartial(e.getResult());
                    Specialized sn = createGeneric();
                    replace(sn, "install Specialized<Generic> from Lapply.Specialized");
                    return generic(context, frame, (RArray) argx, firstArgProvider, callNode, partialContent);
                }
            }
        }

    }
}
