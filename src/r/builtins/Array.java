package r.builtins;

import com.oracle.truffle.api.frame.Frame;

import r.*;
import r.builtins.BuiltIn.NamedArgsBuiltIn.*;
import r.data.*;
import r.errors.*;
import r.nodes.*;
import r.nodes.truffle.*;

/**
 * Array builtin.
 *
 * array(data = NA, dim = length(data), dimnames = NULL)
 *
 */
public class Array {
    /**
     * Simple class used to retrieve dimensions array and the size of the underlying vector at
     * once.
     */
    private static class DimAndSize {

        public final int[] dim;
        public final int size;

        public DimAndSize(int[] dim, int size) {
            this.dim = dim;
            this.size = size;
        }
    }

    private static final String[] paramNames = new String[]{"data", "dim", "dimnames"};

    private static final int IDATA = 0;
    private static final int IDIM = 1;
    private static final int IDIMNAMES = 2;

    public static final CallFactory FACTORY = new CallFactory() {

        @Override
        public RNode create(ASTNode call, RSymbol[] names, RNode[] exprs) {
            AnalyzedArguments a = BuiltIn.NamedArgsBuiltIn.analyzeArguments(names, exprs, paramNames);
            final boolean[] provided = a.providedParams;
            final int[] paramPositions = a.paramPositions;
            return new BuiltIn(call, names, exprs) {
                /**
                 * Checks that the provided data is an array and returns it, using boxed NA if the size of
                 * the array is 0. If source is not an array, throws an error.
                 */
                private RArray parseData(RAny source) {
                    if (source instanceof RArray) {
                        return (((RArray) source).size() == 0) ? RLogical.BOXED_NA : (RArray) source;
                    }
                    throw RError.getDataVector(ast);
                }


                /**
                 * Parses the dimensions to the int[] used by the implementation. Remarks:
                 *
                 * - contrary to the help pages, but with accordance to the gnu-r, the product of dimensions
                 * is not tested to be equal to the size of the data vector, nor is a warning emitted if the
                 * product is not a multiple - negative values in dims always produce the negative values
                 * error, not the negative size vector as it does in gnu-r
                 */
                private DimAndSize parseDimensions(RAny dim) {
                    int[] result;
                    int size = 1;
                    if (dim instanceof RInt) {
                        RInt d = (RInt) dim;
                        result = new int[d.size()];
                        for (int i = 0; i < result.length; ++i) {
                            int x = d.getInt(i);
                            if (x < 0)
                                throw RError.getDimsContainNegativeValues(ast);
                            result[i] = x;
                            size *= x;
                        }
                    } else if (dim instanceof RDouble) {
                        RDouble d = (RDouble) dim;
                        result = new int[d.size()];
                        for (int i = 0; i < result.length; ++i) {
                            if (d.isNAorNaN(i)) {
                                throw RError.getNegativeLengthVectorsNotAllowed(ast);
                            }
                            int x = (int) Math.floor(d.getDouble(i));
                            if (x < 0) {
                                throw RError.getDimsContainNegativeValues(ast);
                            }
                            result[i] = x;
                            size *= x;
                        }
                    } else if (dim instanceof RLogical) {
                        RLogical d = (RLogical) dim;
                        result = new int[d.size()];
                        for (int i = 0; i < result.length; ++i) {
                            if (d.isNAorNaN(i)) {
                                throw RError.getNegativeLengthVectorsNotAllowed(ast);
                            }
                            int x = d.getLogical(i);
                            result[i] = x;
                            size *= x;
                        }
                    } else if (dim instanceof RString) {
                        RString d = (RString) dim;
                        result = new int[d.size()];
                        for (int i = 0; i < result.length; ++i) {
                            if (d.isNAorNaN(i)) {
                                throw RError.getNegativeLengthVectorsNotAllowed(ast);
                            }
                            int x;
                            try {
                                x = (int) Math.floor(Double.parseDouble(d.getString(i)));
                            } catch (Throwable e) {
                                throw RError.getNegativeLengthVectorsNotAllowed(ast);
                            }
                            result[i] = x;
                            size *= x;
                        }
                    } else {
                        // otherwise conversion would yield NA and thus we report the negative length
        // vectors not allowed in
                        // accordance with gnu-r
                        throw RError.getNegativeLengthVectorsNotAllowed(ast);
                    }
                    return new DimAndSize(result, size);
                }

                /**
                 * Parses the dimnames. TODO not yet implemented.
                 */
                private String[][] parseDimnames(RAny dimnames, int[] dim) {
                    Utils.nyi("dimnames are not yet implemented");
                    return null;
                }

                /**
                 * Analyze the arguments and call the builder method
                 */
                @Override
                public final RAny doBuiltIn(Frame frame, RAny[] params) {
                    RArray source = provided[IDATA] ? parseData(params[paramPositions[IDATA]]) : RLogical.BOXED_NA;
                    DimAndSize dim = provided[IDIM] ? parseDimensions(params[paramPositions[IDIM]]) : new DimAndSize(new int[]{source.size()}, source.size());
                    String[][] dimnames = provided[IDIMNAMES] ? parseDimnames(params[paramPositions[IDIMNAMES]], dim.dim) : null;
                    return createArrayObject(source, dim.dim, dim.size, dimnames);
                }

                /**
                 * Creates the array object given the already parsed proper arguments. This method is
                 * expected to be called by the specialized versions of the builtin call, if any, and is
                 * therefore final.
                 */
                protected final RAny createArrayObject(RArray source, int[] dim, int size, String[][] dimnames) {
                    // at the moment, we do not support dimnames
                    assert (dimnames == null) : "dimnames not supported yet";
                    // create the array
                    RArray result = Utils.createArray(source, size, dim, null, null);
                    int ssize = source.size();
                    int si = 0;
                    // fill it with source values
                    for (int i = 0; i < size; ++i) {
                        result.set(i, source.get(si));
                        ++si;
                        if (si == ssize) {
                            si = 0;
                        }
                    }
                    return result;
                }

            };
        }
    };

}
