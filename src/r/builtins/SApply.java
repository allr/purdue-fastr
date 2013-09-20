package r.builtins;

import r.*;
import r.builtins.LApply.ArgIterator;
import r.builtins.LApply.CallableProvider;
import r.builtins.LApply.ValueProvider;
import r.data.*;
import r.data.internal.*;
import r.nodes.ast.*;
import r.nodes.exec.*;
import r.nodes.exec.FunctionCall;
import r.runtime.*;

// FIXME: only a subset of R functionality
// TODO: specializations for different argument types done in sapply can be also
// used in lapply
/**
 * "sapply" is a user-friendly version and wrapper of lapply by default returning a vector, matrix or, if
 * simplify="array ", an array if appropriate, by applying simplify2array(). sapply(x, f, simplify=FALSE,
 * USE.NAMES=FALSE) is the same as lapply(x,f).
 *
 * <pre>
 * X -- a vector (atomic or list) or an expression object. Other objects
 *      (including classed objects) will be coerced by base::as.list.
 * FUN -- the function to be applied to each element of X. In the case of functions like
 *        +, %*%, the function name must be backquoted or quoted.
 *  ... -- optional arguments to FUN.
 *  simplify -- logical or character string; should the result be simplified to a vector, matrix or higher dimensional
 *    array if possible? For sapply it must be named and not abbreviated. The default value, TRUE, returns a vector or matrix
 *    if appropriate, whereas if simplify = "array" the result may be an array of "rank" (=length(dim(.))) one higher than
 *    the result of FUN(X[[i]]).
 * USE.NAMES -- logical; if TRUE and if X is character, use X as names for the result unless it had names already.
 *    Since this argument follows ... its name cannot be abbreviated.
 * </pre>
 */

// TODO: dimension names, matrix

// TODO: fix names when result is a list with scalar elements - there should be no names in those elements
// { l <- (sapply(c(a=1,2,3,`c+`=4), function(i) { if (i==1) { c(x=5) } else if (i==2) {c(z=5) } else if (i==3) { c(1) } else { list(`c+`=3) } })) ; l }

final class SApply extends CallFactory {

    static final CallFactory _ = new SApply("sapply", new String[]{"X", "FUN", "...", "simplify", "USE.NAMES"}, new String[]{"X", "FUN"});

    private SApply(String name, String[] params, String[] required) {
        super(name, params, required);
    }

    @Override public RNode create(ASTNode call, RSymbol[] names, RNode[] exprs) {
        ArgumentInfo ia = check(call, names, exprs);
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
            if (ia.position("X") == i || ia.position("FUN") == i) {
                continue;
            }
            cnNames[1 + j] = names[i];
            cnExprs[1 + j] = exprs[i];
            j++;
        }
        final CallableProvider callableProvider = new CallableProvider(call, exprs[ia.position("FUN")]);
        final RNode callNode = FunctionCall.getFunctionCall(call, callableProvider, cnNames, cnExprs);
        return new Sapply(call, names, exprs, callNode, firstArgProvider, callableProvider, ia.position("X"), ia.position("FUN"));
    }

    // TODO: handle names
    public static class Sapply extends Builtin {

        ValueProvider firstArgProvider; // !!!! not a child (a shortcut to the arguments)
        CallableProvider callableProvider; // !!!! not a child (a shortcut to the callable expression of the callNode)
        @Child RNode callNode;
        final int xPosition;
        final int funPosition;

        public Sapply(ASTNode call, RSymbol[] names, RNode[] exprs, RNode callNode, ValueProvider firstArgProvider, CallableProvider callableProvider, int xPosition, int funPosition) {
            super(call, names, exprs);
            this.callableProvider = callableProvider;  // !!! no adopt
            this.firstArgProvider = firstArgProvider;  // !!! no adopt
            this.callNode = adoptChild(callNode);
            this.xPosition = xPosition;
            this.funPosition = funPosition;
        }

        @Override
        protected <N extends RNode> N replaceChild(RNode oldNode, N newNode) {
            assert oldNode != null;
            assert Utils.check(oldNode != firstArgProvider);
            assert Utils.check(oldNode != callableProvider); // this node must not be rewritten
            if (callNode == oldNode) {
                callNode = newNode;
                return adoptInternal(newNode);
            }
            return super.replaceChild(oldNode, newNode);
        }

        // FIXME: this will be slow (a second pass through the results array)
        public static RArray.Names extractNames(ArgIterator argIterator, boolean resultsHaveNames, RAny[] results, int size) {
            boolean argHasNames = argIterator.hasNames();
            if (!argHasNames) {
                if (!resultsHaveNames) { return null; }
                // just names from results
                RSymbol[] symbols = new RSymbol[size];
                for (int i = 0; i < size; i++) {
                    RArray a = (RArray) results[i];
                    RArray.Names n = a.names();
                    if (n != null) {
                        symbols[i] = n.sequence()[0];
                    } else {
                        symbols[i] = RSymbol.EMPTY_SYMBOL;
                    }
                }
                return RArray.Names.create(symbols);
            }
            // argNames != null
            if (!resultsHaveNames) { return argIterator.names(); }
            // merge names
            RString argNames = argIterator.stringNames();
            RSymbol[] symbols = new RSymbol[size];
            for (int i = 0; i < size; i++) {
                String astr = argNames.getString(i);
                RArray a = (RArray) results[i];
                RArray.Names n = a.names();
                if (n != null) {
                    if (astr.length() == 0) {
                        symbols[i] = RSymbol.getSymbol(n.sequence()[0].name());
                    } else {
                        symbols[i] = RSymbol.getSymbol(astr + "." + n.sequence()[0].name());
                    }
                } else {
                    symbols[i] = RSymbol.getSymbol(astr);
                }
            }
            return RArray.Names.create(symbols);
        }

        public static RArray.Names mergeNames(RString argNames, RSymbol[] rnames, int size) {
            RSymbol[] symbols = new RSymbol[size];
            for (int i = 0; i < size; i++) {
                String astr = argNames.getString(i);
                if (astr.length() == 0) {
                    symbols[i] = RSymbol.getSymbol(rnames[i].name());
                } else {
                    symbols[i] = RSymbol.getSymbol(astr + "." + rnames[i].name());
                }
            }
            return RArray.Names.create(symbols);
        }

        // TODO: support rownames, colnames (and names for a matrix result)
        public static RAny generic(Frame frame, ArgIterator argIterator, Sapply sapply, RAny[] partialContent) {

            boolean hasRaw = false;
            boolean hasLogical = false;
            boolean hasInt = false;
            boolean hasDouble = false;
            boolean hasComplex = false;
            boolean hasString = false;
            boolean notAllScalarLists = false;
            boolean hasList = false;
            boolean hasMultipleSizes = false;
            boolean hasNames = false;

            int elementSize = -1;
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
                    v = (RAny) sapply.callNode.execute(frame);
                    content[i] = v;
                } else {
                    v = content[i];
                }
                int vsize;
                if (v instanceof RDouble) {
                    hasDouble = true;
                    notAllScalarLists = true;
                    vsize = ((RDouble) v).size();
                } else if (v instanceof RInt) {
                    hasInt = true;
                    notAllScalarLists = true;
                    vsize = ((RInt) v).size();
                } else if (v instanceof RLogical) {
                    hasLogical = true;
                    notAllScalarLists = true;
                    vsize = ((RLogical) v).size();
                } else if (v instanceof RList) {
                    hasList = true;
                    vsize = ((RList) v).size();
                    if (vsize != 1) {
                        notAllScalarLists = true;
                    }
                } else if (v instanceof RString) {
                    hasString = true;
                    notAllScalarLists = true;
                    vsize = ((RString) v).size();
                } else if (v instanceof RComplex) {
                    hasComplex = true;
                    notAllScalarLists = true;
                    vsize = ((RComplex) v).size();
                } else if (v instanceof RRaw) {
                    hasRaw = true;
                    notAllScalarLists = true;
                    vsize = ((RRaw) v).size();
                } else if (v instanceof RNull) {
                    hasList = true;
                    notAllScalarLists = true;
                    vsize = 0;
                } else {
                    throw Utils.nyi("unsupported type"); // TODO: more things can be in a list
                }
                if (elementSize != -1) {
                    if (vsize != elementSize) {
                        hasMultipleSizes = true;
                    }
                } else {
                    elementSize = vsize;
                }
                if (!hasNames) {
                    RArray.Names names = ((RArray) v).names(); // FIXME: could elide this cast by hand-inlining into code above
                    if (names != null) {
                        hasNames = true;
                    }
                }
            }
            if (elementSize > 1 && !hasMultipleSizes) {
                // result is a matrix
                int[] dimensions = new int[]{elementSize, xsize};
                int resSize = elementSize * xsize;

                if (hasList) {
                    RAny[] values = new RAny[resSize];
                    for (int i = 0; i < xsize; i++) {
                        RList v = content[i].asList();
                        for (int j = 0; j < elementSize; j++) {
                            values[i * elementSize + j] = v.getRAny(j); // shallow but no need to ref here
                        }
                    }
                    return RList.RListFactory.getFor(values, dimensions);
                }
                if (hasString) {
                    String[] values = new String[resSize];
                    for (int i = 0; i < xsize; i++) {
                        RString v = content[i].asString();
                        for (int j = 0; j < elementSize; j++) {
                            values[i * elementSize + j] = v.getString(j);
                        }
                    }
                    return RString.RStringFactory.getFor(values, dimensions, null);
                }
                if (hasComplex) {
                    double[] values = new double[2 * resSize];
                    for (int i = 0; i < xsize; i++) {
                        RComplex v = content[i].asComplex();
                        for (int j = 0; j < elementSize; j++) {
                            int offset = 2 * (i * elementSize + j);
                            values[offset] = v.getReal(j);
                            values[offset + 1] = v.getImag(j);
                        }
                    }
                    return RComplex.RComplexFactory.getFor(values, dimensions, null);
                }
                if (hasDouble) {
                    double[] values = new double[resSize];
                    for (int i = 0; i < xsize; i++) {
                        RDouble v = content[i].asDouble();
                        for (int j = 0; j < elementSize; j++) {
                            values[i * elementSize + j] = v.getDouble(j);
                        }
                    }
                    return RDouble.RDoubleFactory.getFor(values, dimensions, null);
                }
                if (hasInt) {
                    int[] values = new int[resSize];
                    for (int i = 0; i < xsize; i++) {
                        RInt v = content[i].asInt();
                        for (int j = 0; j < elementSize; j++) {
                            values[i * elementSize + j] = v.getInt(j);
                        }
                    }
                    return RInt.RIntFactory.getFor(values, dimensions, null);
                }
                if (hasLogical) {
                    int[] values = new int[resSize];
                    for (int i = 0; i < xsize; i++) {
                        RLogical v = content[i].asLogical();
                        for (int j = 0; j < elementSize; j++) {
                            values[i * elementSize + j] = v.getLogical(j);
                        }
                    }
                    return RLogical.RLogicalFactory.getFor(values, dimensions, null);
                }
                if (hasRaw) {
                    byte[] values = new byte[resSize];
                    for (int i = 0; i < xsize; i++) {
                        RRaw v = content[i].asRaw();
                        for (int j = 0; j < elementSize; j++) {
                            values[i * elementSize + j] = v.getRaw(j);
                        }
                    }
                    return RRaw.RRawFactory.getFor(values, dimensions, null);
                }
                assert Utils.check(false, "unreachable");

            } else {
                // result is a vector (or list) - not a matrix
                if (hasMultipleSizes) { return RList.RListFactory.getFor(content, null, argIterator.names()); // result names not propagated
                }
                if (hasList) {
                    if (!notAllScalarLists) { // all elements are scalar lists
                        if (!hasNames) {
                            for (int i = 0; i < xsize; i++) {
                                RList v = (RList) content[i];
                                content[i] = v.getRAny(0); // shallow but no need to ref here
                            }
                        } else {
                            RSymbol[] symbols = new RSymbol[xsize];
                            for (int i = 0; i < xsize; i++) {
                                RList v = (RList) content[i];
                                content[i] = v.getRAny(0); // shallow but no need to ref here
                                RArray.Names n = v.names();
                                if (n != null) {
                                    symbols[i] = n.sequence()[0];
                                } else {
                                    symbols[i] = RSymbol.EMPTY_SYMBOL;
                                }
                            }
                            if (!argIterator.hasNames()) { return RList.RListFactory.getFor(content, null, RArray.Names.create(symbols)); }
                            return RList.RListFactory.getFor(content, null, mergeNames(argIterator.stringNames(), symbols, xsize));
                        }

                    }
                    return RList.RListFactory.getFor(content, null, extractNames(argIterator, hasNames, content, xsize));
                }

                // this could be written using asXXX (but much slower)
                if (hasString) {
                    String[] values = new String[xsize];
                    for (int i = 0; i < xsize; i++) {
                        RAny v = content[i];
                        if (v instanceof RString) {
                            values[i] = ((RString) v).getString(0);
                        } else if (v instanceof RDouble) {
                            values[i] = Convert.double2string(((RDouble) v).getDouble(0));
                        } else if (v instanceof RInt) {
                            values[i] = Convert.int2string(((RInt) v).getInt(0));
                        } else if (v instanceof RLogical) {
                            values[i] = Convert.logical2string(((RLogical) v).getLogical(0));
                        } else if (v instanceof RComplex) {
                            RComplex cv = (RComplex) v;
                            values[i] = Convert.complex2string(cv.getReal(0), cv.getImag(0));
                        } else {
                            assert Utils.check(v instanceof RRaw);
                            values[i] = Convert.raw2string(((RRaw) v).getRaw(0));
                        }
                    }
                    return RString.RStringFactory.getFor(values, null, extractNames(argIterator, hasNames, content, xsize));
                }
                if (hasComplex) {
                    double[] values = new double[2 * xsize];
                    for (int i = 0; i < xsize; i++) {
                        RAny v = content[i];
                        if (v instanceof RDouble) {
                            values[2 * i] = ((RDouble) v).getDouble(0);
                        } else if (v instanceof RInt) {
                            values[2 * i] = Convert.int2double(((RInt) v).getInt(0));
                        } else if (v instanceof RLogical) {
                            values[2 * i] = Convert.logical2double(((RLogical) v).getLogical(0));
                        } else if (v instanceof RComplex) {
                            RComplex cv = ((RComplex) v);
                            values[2 * i] = cv.getReal(0);
                            values[2 * i + 1] = cv.getImag(0);
                        } else {
                            assert Utils.check(v instanceof RRaw);
                            values[2 * i] = Convert.raw2double(((RRaw) v).getRaw(0));
                        }
                    }
                    return RComplex.RComplexFactory.getFor(values, null, extractNames(argIterator, hasNames, content, xsize));
                }
                if (hasDouble) {
                    double[] values = new double[xsize];
                    for (int i = 0; i < xsize; i++) {
                        RAny v = content[i];
                        if (v instanceof RDouble) {
                            values[i] = ((RDouble) v).getDouble(0);
                        } else if (v instanceof RInt) {
                            values[i] = Convert.int2double(((RInt) v).getInt(0));
                        } else if (v instanceof RLogical) {
                            values[i] = Convert.logical2double(((RLogical) v).getLogical(0));
                        } else {
                            assert Utils.check(v instanceof RRaw);
                            values[i] = Convert.raw2double(((RRaw) v).getRaw(0));
                        }
                    }
                    return RDouble.RDoubleFactory.getFor(values, null, extractNames(argIterator, hasNames, content, xsize));
                }
                if (hasInt) {
                    int[] values = new int[xsize];
                    for (int i = 0; i < xsize; i++) {
                        RAny v = content[i];
                        if (v instanceof RInt) {
                            values[i] = ((RInt) v).getInt(0);
                        } else if (v instanceof RLogical) {
                            values[i] = Convert.logical2int(((RLogical) v).getLogical(0));
                        } else {
                            assert Utils.check(v instanceof RRaw);
                            values[i] = Convert.raw2int(((RRaw) v).getRaw(0));
                        }
                    }
                    return RInt.RIntFactory.getFor(values, null, extractNames(argIterator, hasNames, content, xsize));
                }
                if (hasLogical) {
                    int[] values = new int[xsize];
                    for (int i = 0; i < xsize; i++) {
                        RAny v = content[i];
                        if (v instanceof RLogical) {
                            values[i] = Convert.logical2int(((RLogical) v).getLogical(0));
                        } else {
                            assert Utils.check(v instanceof RRaw);
                            values[i] = Convert.raw2logical(((RRaw) v).getRaw(0));
                        }
                    }
                    return RLogical.RLogicalFactory.getFor(values, null, extractNames(argIterator, hasNames, content, xsize));
                }
                if (hasRaw) {
                    byte[] values = new byte[xsize];
                    for (int i = 0; i < xsize; i++) {
                        RAny v = content[i];
                        values[i] = ((RRaw) v).getRaw(0);
                    }
                    return RRaw.RRawFactory.getFor(values, null, extractNames(argIterator, hasNames, content, xsize));
                }
            }
            return RList.EMPTY;
        }

        public static RAny[] unpackPartial(Object partial) {
            if (partial instanceof RAny[]) { return (RAny[]) partial; }
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
            if (resTemplate instanceof RDouble && ((RDouble) resTemplate).dimensions() == null) {
                ApplyFunc a = new ApplyFunc() {
                    @Override public RAny apply(Frame frame, ArgIterator it, Sapply sapply) throws SpecializationException {
                        int xsize = it.size();
                        double[] content = new double[xsize];
                        for (int i = 0; i < xsize; i++) {
                            it.setNext();
                            RAny v = (RAny) sapply.callNode.execute(frame);
                            if (v instanceof ScalarDoubleImpl) {
                                content[i] = ((ScalarDoubleImpl) v).getDouble();
                            } else { // NOTE: can also add Int and Logical support here
                                throw new SpecializationException(new PartialResult(RDouble.RDoubleFactory.getFor(content), i, v));
                            }
                        }
                        return RDouble.RDoubleFactory.getFor(content, null, it.names());
                    }
                };
                return new Specialized(ast, argNames, argExprs, callNode, firstArgProvider, callableProvider, xPosition, funPosition, argIterator, a, "<=RDouble>");
            }
            if (resTemplate instanceof RInt && ((RInt) resTemplate).dimensions() == null) {
                ApplyFunc a = new ApplyFunc() {
                    @Override public RAny apply(Frame frame, ArgIterator it, Sapply sapply) throws SpecializationException {
                        int xsize = it.size();
                        int[] content = new int[xsize];
                        for (int i = 0; i < xsize; i++) {
                            it.setNext();
                            RAny v = (RAny) sapply.callNode.execute(frame);
                            if (v instanceof ScalarIntImpl) {
                                content[i] = ((ScalarIntImpl) v).getInt();
                            } else { // NOTE: can also add Logical support here
                                throw new SpecializationException(new PartialResult(RInt.RIntFactory.getFor(content), i, v));
                            }
                        }
                        return RInt.RIntFactory.getFor(content, null, it.names());
                    }
                };
                return new Specialized(ast, argNames, argExprs, callNode, firstArgProvider, callableProvider, xPosition, funPosition, argIterator, a, "<=RInt>");
            }
            if (resTemplate instanceof RLogical && ((RLogical) resTemplate).dimensions() == null) {
                ApplyFunc a = new ApplyFunc() {
                    @Override public RAny apply(Frame frame, ArgIterator argIterator, Sapply sapply) throws SpecializationException {
                        int xsize = argIterator.size();
                        int[] content = new int[xsize];
                        for (int i = 0; i < xsize; i++) {
                            argIterator.setNext();
                            RAny v = (RAny) sapply.callNode.execute(frame);
                            if (v instanceof ScalarLogicalImpl) {
                                content[i] = ((ScalarLogicalImpl) v).getLogical();
                            } else {
                                throw new SpecializationException(new PartialResult(RLogical.RLogicalFactory.getFor(content), i, v));
                            }
                        }
                        return RLogical.RLogicalFactory.getFor(content, null, argIterator.names());
                    }
                };
                return new Specialized(ast, argNames, argExprs, callNode, firstArgProvider, callableProvider, xPosition, funPosition, argIterator, a, "<=RLogical>");
            }
            if (resTemplate instanceof RString && ((RString) resTemplate).dimensions() == null) {
                ApplyFunc a = new ApplyFunc() {
                    @Override public RAny apply(Frame frame, ArgIterator argIterator, Sapply sapply) throws SpecializationException {
                        int xsize = argIterator.size();
                        String[] content = new String[xsize];
                        for (int i = 0; i < xsize; i++) {
                            argIterator.setNext();
                            RAny v = (RAny) sapply.callNode.execute(frame);
                            if (v instanceof ScalarStringImpl) {
                                content[i] = ((ScalarStringImpl) v).getString();
                            } else {
                                throw new SpecializationException(new PartialResult(RString.RStringFactory.getFor(content), i, v));
                            }
                        }
                        return RString.RStringFactory.getFor(content, null, argIterator.names());
                    }
                };
                return new Specialized(ast, argNames, argExprs, callNode, firstArgProvider, callableProvider, xPosition, funPosition, argIterator, a, "<=RString>");
            }
            if (resTemplate instanceof RComplex && ((RComplex) resTemplate).dimensions() == null) {
                ApplyFunc a = new ApplyFunc() {
                    @Override public RAny apply(Frame frame, ArgIterator argIterator, Sapply sapply) throws SpecializationException {
                        int xsize = argIterator.size();
                        double[] content = new double[2 * xsize];
                        for (int i = 0; i < xsize; i++) {
                            argIterator.setNext();
                            RAny v = (RAny) sapply.callNode.execute(frame);
                            if (v instanceof ScalarComplexImpl) {
                                ScalarComplexImpl cv = (ScalarComplexImpl) v;
                                content[2 * i] = cv.getReal();
                                content[2 * i + 1] = cv.getImag();
                            } else {
                                throw new SpecializationException(new PartialResult(RComplex.RComplexFactory.getFor(content), i, v));
                            }
                        }
                        return RComplex.RComplexFactory.getFor(content, null, argIterator.names());
                    }
                };
                return new Specialized(ast, argNames, argExprs, callNode, firstArgProvider, callableProvider, xPosition, funPosition, argIterator, a, "<=RComplex>");
            }
            if (resTemplate instanceof RList && ((RList) resTemplate).dimensions() == null) {
                ApplyFunc a = new ApplyFunc() {
                    @Override public RAny apply(Frame frame, ArgIterator argIterator, Sapply sapply) throws SpecializationException {
                        int xsize = argIterator.size();
                        RAny[] content = new RAny[xsize];
                        boolean returnsList = false;
                        for (int i = 0; i < xsize; i++) {
                            argIterator.setNext();
                            RAny v = (RAny) sapply.callNode.execute(frame);
                            content[i] = v;
                            if (!returnsList) {
                                if (v instanceof RList || v instanceof RNull) {
                                    returnsList = true;
                                } else {
                                    assert Utils.check(v instanceof RArray); // TODO: support for adding other types into a list
                                    int size = ((RArray) v).size();
                                    if (size != 1) {
                                        returnsList = true;
                                    }
                                }
                            }
                        }
                        if (!returnsList) { throw new SpecializationException(content); }
                        return RList.RListFactory.getFor(content, null, argIterator.names());
                    }
                };
                return new Specialized(ast, argNames, argExprs, callNode, firstArgProvider, callableProvider, xPosition, funPosition, argIterator, a, "<=RList>");
            }
            if (resTemplate instanceof RRaw && ((RRaw) resTemplate).dimensions() == null) {
                ApplyFunc a = new ApplyFunc() {
                    @Override public RAny apply(Frame frame, ArgIterator argIterator, Sapply sapply) throws SpecializationException {
                        int xsize = argIterator.size();
                        byte[] content = new byte[xsize];
                        for (int i = 0; i < xsize; i++) {
                            argIterator.setNext();
                            RAny v = (RAny) sapply.callNode.execute(frame);
                            if (v instanceof RRaw) {
                                content[i] = ((RRaw) v).getRaw(0);
                            } else {
                                throw new SpecializationException(new PartialResult(RRaw.RRawFactory.getFor(content), i, v));
                            }
                        }
                        return RRaw.RRawFactory.getFor(content, null, argIterator.names());
                    }
                };
                return new Specialized(ast, argNames, argExprs, callNode, firstArgProvider, callableProvider, xPosition, funPosition, argIterator, a, "<=RRaw>");
            }
            return null; // FIXME: should return generic by default?
        }

        public RAny doApply(Frame frame, RAny argx, RAny argfun) {

            try {
                throw new SpecializationException(null);
            } catch (SpecializationException e) {
                if (!(argx instanceof RArray)) { throw Utils.nyi("unsupported type"); }
                callableProvider.matchAndSet(frame, argfun);
                ArgIterator argIterator = ArgIterator.create(argx);
                try {
                    argIterator.reset(firstArgProvider, argx);
                } catch (SpecializationException e1) {
                    throw Utils.nyi("unsupported type");
                }
                RAny res = generic(frame, argIterator, this, null);
                Specialized sn = createSpecialized(res, argIterator);
                if (sn != null) {
                    replace(sn, "install Specialized from Sapply");
                } else {
                    replace(createGeneric(argIterator), "install Generic from Sapply");
                }
                return res;
            }
        }

        @Override public RAny doBuiltIn(Frame frame, RAny[] args) {
            RAny argx = args[xPosition];
            RAny argfun = args[funPosition];
            return doApply(frame, argx, argfun);
        }

        public Specialized createGeneric(ArgIterator argIterator) {
            ApplyFunc a = new ApplyFunc() {
                @Override public RAny apply(Frame frame, ArgIterator argIterator, Sapply sapply) throws SpecializationException {
                    return generic(frame, argIterator, sapply, null);
                }
            };
            return new Specialized(ast, argNames, argExprs, callNode, firstArgProvider, callableProvider, xPosition, funPosition, argIterator, a, "<Generic>");
        }

        abstract static class ApplyFunc {
            public abstract RAny apply(Frame frame, ArgIterator argIterator, Sapply sapply) throws SpecializationException;
        }

        class Specialized extends Sapply {
            final ApplyFunc apply;
            final ArgIterator argIterator;
            final String dbg;

            public Specialized(ASTNode call, RSymbol[] names, RNode[] exprs, RNode callNode, ValueProvider firstArgProvider, CallableProvider callableProvider, int xPosition, int funPosition,
                    ArgIterator argIterator, ApplyFunc apply, String dbg) {
                super(call, names, exprs, callNode, firstArgProvider, callableProvider, xPosition, funPosition);
                this.apply = apply;
                this.argIterator = argIterator;
                this.dbg = dbg;
            }

            @Override public RAny doApply(Frame frame, RAny argx, RAny argfun) {
                callableProvider.matchAndSet(frame, argfun);
                try {
                    argIterator.reset(firstArgProvider, argx);
                } catch (SpecializationException e) {
                    ArgIterator ai = new ArgIterator.Generic();
                    Specialized sn = new Specialized(ast, argNames, argExprs, callNode, firstArgProvider, callableProvider, xPosition, funPosition, ai, apply, dbg);
                    replace(sn, "install Specialized<Generic, ?> from Sapply.Specialized");
                    return sn.doApply(frame, argx, argfun);
                }

                try {
                    return apply.apply(frame, argIterator, this);
                } catch (SpecializationException e) {
                    RAny[] partialContent = unpackPartial(e.getResult());
                    Specialized sn = createGeneric(argIterator);
                    replace(sn, "install Specialized<?, Generic> from Sapply.Specialized");
                    return generic(frame, argIterator, sn, partialContent); // NOTE: not passing this, because sn now has the callnode
                }
            }
        }
    }
}
