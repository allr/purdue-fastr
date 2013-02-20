package r.builtins;

import com.oracle.truffle.api.frame.Frame;
import r.*;
import r.data.*;
import r.errors.RError;
import r.nodes.ASTNode;
import r.nodes.truffle.RNode;

/** The Aperm builtin.
 *
 *
 * aperm(a, perm = NULL, resize = TRUE, ...)
 */
public class Aperm {
    private static final String[] paramNames = new String[]{"a", "perm", "resize"};

    private static final int IA = 0;
    private static final int IPERM = 1;
    private static final int IRESIZE = 2;

    public static final CallFactory FACTORY = new CallFactory() {

        @Override
        public RNode create(ASTNode call, RSymbol[] names, RNode[] exprs) {
            BuiltIn.NamedArgsBuiltIn.AnalyzedArguments a = BuiltIn.NamedArgsBuiltIn.analyzeArguments(names, exprs, paramNames);
            final boolean[] provided = a.providedParams;
            final int[] paramPositions = a.paramPositions;
            if (!provided[IA]) {
                BuiltIn.missingArg(call, paramNames[IA]);
            }



            return new BuiltIn(call, names, exprs) {


                private RArray parseArray(RAny[] params) {
                    RArray ary = (RArray) params[paramPositions[IA]];
                    if (ary.dimensions() == null) {
                        throw RError.getFirstArgMustBeArray(ast);
                    }
                    return ary;
                }

                private int[] parsePermutation(int[] aryDim, RAny[] params) {
                    int[] result = new int[aryDim.length];
                    if (!provided[IPERM]) { // default parm argument is reverse order of the dimensions
                        for (int i = 0; i < result.length; ++i) {
                            result[i] = result.length - i - 1;
                        }
                        return result;
                    } else {
                        boolean[] usedIndices = new boolean[result.length];
                        RArray perm = (RArray) params[paramPositions[IPERM]];
                        if (perm.size() != aryDim.length) {
                            throw RError.getValueIsOfWrongLength(ast, "perm");
                        }
                        if (perm instanceof RInt) {
                            RInt p = (RInt) perm;
                            for (int i = 0; i < result.length; ++i) {
                                int x = p.getInt(i);
                                if ((x < 1) || (x > aryDim.length)) {
                                    throw RError.getValueOutOfRange(ast, "perm");
                                }
                                --x;
                                result[i] = x;
                                if (usedIndices[x] == true) {
                                    throw RError.getInvalidArgument(ast, "perm");
                                } else {
                                    usedIndices[x] = true;
                                }
                            }
                        } else if (perm instanceof RLogical) {
                            RLogical p = (RLogical) perm;
                            for (int i = 0; i < result.length; ++i) {
                                int x = p.getLogical(i);
                                if ((x < 1) || (x > aryDim.length)) {
                                    throw RError.getValueOutOfRange(ast, "perm");
                                }
                                --x;
                                result[i] = x;
                                if (usedIndices[x] == true) {
                                    throw RError.getInvalidArgument(ast, "perm");
                                } else {
                                    usedIndices[x] = true;
                                }
                            }
                        } else if (perm instanceof RDouble) {
                            RDouble p = (RDouble) perm;
                            for (int i = 0; i < result.length; ++i) {
                                int x = (int) Math.floor(p.getDouble(i));
                                if ((x < 1) || (x > aryDim.length)) {
                                    throw RError.getValueOutOfRange(ast, "perm");
                                }
                                --x;
                                result[i] = x;
                                if (usedIndices[x] == true) {
                                    throw RError.getInvalidArgument(ast, "perm");
                                } else {
                                    usedIndices[x] = true;
                                }
                            }

                        } else if (perm instanceof RComplex) {
                            RContext.warning(ast, RError.IMAGINARY_PARTS_DISCARDED_IN_COERCION);
                            RComplex p = (RComplex) perm;
                            for (int i = 0; i < result.length; ++i) {
                                int x = (int) Math.floor(p.getReal(i));
                                if ((x < 1) || (x > aryDim.length)) {
                                    throw RError.getValueOutOfRange(ast, "perm");
                                }
                                --x;
                                result[i] = x;
                                if (usedIndices[x] == true) {
                                    throw RError.getInvalidArgument(ast, "perm");
                                } else {
                                    usedIndices[x] = true;
                                }
                            }
                        } else {
                            Utils.nyi("dimnames not implemented yet");
                        }
                    }
                    return result;
                }

                private boolean parseResize(RAny[] params) {
                    if (!provided[IRESIZE]) {
                        return true;
                    }
                    return params[paramPositions[IRESIZE]].asLogical().getLogical(0) != RLogical.FALSE;
                }

                private void perm(int[] source, int[] dest,  int[] perm) {
                    for (int i = 0; i < perm.length; ++i) {
                        dest[i] = source[perm[i]];
                    }
                }

                private void increment(int[] idx, int[] dim) {
                    int i = 0;
                    while (i < idx.length) {
                        ++idx[i];
                        if (idx[i] < dim[i]) {
                            break;
                        }
                        idx[i] = 0;
                        ++i;
                    }
                }

                private int offset(int[] idx, int[] mults) {
                    int result = 0;
                    for (int i = 0; i < idx.length; ++i) {
                        result += idx[i] * mults[i];
                    }
                    return result;
                }

                private int[] createDimMults(int[] dim) {
                    int[] result = new int[dim.length];
                    result[0] = 1;
                    for (int i = 1; i < result.length; ++i) {
                        result[i] = dim[i - 1] * result[i - 1];
                    }
                    return result;
                }

                private int[] calculateResizedDimension(int[] aryDim, int[] perm) {
                    int[] result = new int[aryDim.length];
                    for (int i = 0; i < perm.length; ++i) {
                        result[i] = aryDim[perm[i]];
                    }
                    return result;
                }

                @Override
                public RAny doBuiltIn(Frame frame, RAny[] params) {
                    RArray ary = parseArray(params);
                    int arySize = ary.size();
                    int[] aryDim = ary.dimensions();
                    int[] perm = parsePermutation(aryDim, params);
                    boolean resize = parseResize(params);
                    int[] resultDim = calculateResizedDimension(aryDim, perm);
                    RArray result = Utils.createArray(ary, arySize, resize ? resultDim : aryDim, ary.names());
                    int[] idx = new int[aryDim.length];
                    int[] resultIdx = new int[aryDim.length];
                    int[] dimMults = createDimMults(resultDim);
                    for (int i = 0; i < arySize; ++i) {
                        perm(idx, resultIdx, perm);
                        int offset = offset(resultIdx, dimMults);
                        result.set(offset, ary.get(i));
                        increment(idx, aryDim);
                    }
                    return result;
                }
            };
        }
    };
}
